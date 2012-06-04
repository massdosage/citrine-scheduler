/*
 * Copyright 2010 Last.fm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fm.last.citrine.service;

import static fm.last.citrine.scheduler.SchedulerConstants.SYS_ERR;
import static fm.last.citrine.scheduler.SchedulerConstants.SYS_OUT;
import static fm.last.citrine.scheduler.SchedulerConstants.TASK_ID;
import static fm.last.citrine.scheduler.SchedulerConstants.TASK_RUN_ID;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import fm.last.citrine.dao.TaskDAO;
import fm.last.citrine.dao.TaskRunDAO;
import fm.last.citrine.model.Status;
import fm.last.citrine.model.Task;
import fm.last.citrine.model.TaskRun;
import fm.last.citrine.notification.Notifier;
import fm.last.citrine.scheduler.SchedulerManager;

/**
 * TaskRunManager implementation.
 */
public class TaskRunManagerImpl implements TaskRunManager {

  private static Logger log = Logger.getLogger(TaskRunManagerImpl.class);

  private TaskRunDAO taskRunDAO;
  private TaskDAO taskDAO;
  private Notifier notifier;

  /**
   * Map of running tasks. Key is the task id, value is the JobExecutionContext.
   */
  private Map<Long, JobExecutionContext> runningTasks = Collections
      .synchronizedMap(new HashMap<Long, JobExecutionContext>());

  private SchedulerManager schedulerManager;

  private TaskManager taskManager;

  /**
   * Constructs a new TaskRunManager which will use the passed DAO to communicate with the TaskRun storage.
   * 
   * @param taskRunDAO TaskRun DAO.
   */
  public TaskRunManagerImpl(TaskRunDAO taskRunDAO) {
    this.taskRunDAO = taskRunDAO;
    taskRunDAO.setInterruptedStatus(); // on startup set state for any previously running TaskRuns to interrupted
  }

  // from JobListener interface
  @Override
  public String getName() {
    return "Citrine Job Listener";
  }

  // from JobListener interface
  @Override
  public void jobExecutionVetoed(JobExecutionContext context) {

  }

  // from JobListener interface
  @Override
  public void jobToBeExecuted(JobExecutionContext context) {
    startTaskRun(context);
  }

  // from JobListener interface
  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
    finishTaskRun(context, exception);
  }

  /**
   * Creates a or updates a TaskRun object in storage.
   * 
   * @param taskRun TaskRun to save.
   */
  public void save(TaskRun taskRun) {
    taskRunDAO.save(taskRun);
  }

  /**
   * Retrieves a TaskRun by its primary key.
   * 
   * @param id TaskRun id.
   * @return TaskRun identified by the passed id.
   */
  public TaskRun get(long id) {
    return taskRunDAO.get(id);
  }

  /**
   * Finds TaskRuns that belong to a certain Task.
   * 
   * @param taskId The Task ID.
   * @return List of matching TaskRuns.
   */
  public List<TaskRun> findByTaskId(long taskId) {
    return taskRunDAO.findByTaskId(taskId);
  }

  @Override
  public List<TaskRun> findByTaskId(long taskId, int firstResult, int maxResults) {
    return taskRunDAO.findByTaskId(taskId, firstResult, maxResults);
  }

  /**
   * Deletes the TaskRun identified by the passed ID.
   * 
   * @param taskRunId ID of the TaskRun to delete.
   */
  public void delete(long taskRunId) {
    taskRunDAO.delete(taskRunId);
  }

  @Override
  public void deleteBefore(DateTime before) {
    taskRunDAO.deleteBefore(before);
  }

  @Override
  public boolean isRunning(long taskId) {
    return runningTasks.containsKey(taskId);
  }

  @Override
  public boolean stop(long taskRunId) {
    TaskRun taskRun = taskRunDAO.get(taskRunId);
    long taskId = taskRun.getTaskId();

    JobExecutionContext context = runningTasks.get(taskId);
    if (context == null) { // there is no task run
      return false;
    }
    Job runningJob = context.getJobInstance();
    if (runningJob instanceof InterruptableJob) {
      try {
        log.info("Interrupting TaskRun " + taskRunId + " for Task " + taskId);
        setStatus(taskRun, Status.CANCELLING);
        taskRunDAO.save(taskRun);

        ((InterruptableJob) runningJob).interrupt();
        runningTasks.remove(taskId);

        return true;
      } catch (UnableToInterruptJobException e) {
        log.error("Unable to interrupt TaskRun " + taskRunId + " for Task " + taskId, e);
      }
    }
    return false;
  }

  @Override
  public void shutdown() {
    // TODO: replace with calls to actually interrupt TaskRuns in map (and test)
    taskRunDAO.setInterruptedStatus();
  }

  @Override
  public void setStatus(TaskRun taskRun, Status status) {
    taskRun.setStatus(status);
    if (status.compareTo(Status.CANCELLED) >= 0 && status.compareTo(Status.SUCCESS) <= 0) {
      Task task = taskDAO.get(taskRun.getTaskId());
      if (task == null) {
        log.fatal("Could not send a notification for task run " + taskRun.getTaskId() + ", no owning task found");
        return;
      }
      notifier.sendNotification(task.getNotification(), taskRun, task.getName());
    }
  }

  /**
   * Marks a TaskRun as running.
   * 
   * @param context The job's execution context.
   */
  private void startTaskRun(JobExecutionContext context) {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    long taskId = jobDataMap.getLong(TASK_ID);
    TaskRun taskRun = new TaskRun(new Date(), null, null, null, null, taskId);
    setStatus(taskRun, Status.RUNNING);
    save(taskRun); // saving it will get the task run an id
    jobDataMap.put(TASK_RUN_ID, taskRun.getId());
    runningTasks.put(taskId, context);
  }

  /**
   * Mark a TaskRun as finished.
   * 
   * @param context The job's execution context.
   * @param exception Exception that caused the TaskRun to fail, else null if the TaskRun succeeded.
   */
  private void finishTaskRun(JobExecutionContext context, JobExecutionException exception) {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    if (jobDataMap.get(TASK_RUN_ID) != null) { // it could be null (if vetoed ?)
      TaskRun finishedRun = saveFinishedTaskRun(jobDataMap, exception);
      runningTasks.remove(finishedRun.getTaskId());
      handleFinishedRun(finishedRun); // kick off child jobs if necessary
    }
  }

  /**
   * Updates the database with the state of the finished TaskRun.
   * 
   * @param jobDataMap Map containing information about the TaskRun.
   * @param exception Exception that caused the TaskRun to fail, else null if the TaskRun succeeded.
   * @return The saved TaskRun.
   */
  private TaskRun saveFinishedTaskRun(JobDataMap jobDataMap, JobExecutionException exception) {
    long taskRunId = jobDataMap.getLong(TASK_RUN_ID);
    TaskRun taskRun = get(taskRunId);
    taskRun.setEndDate(new Date());
    taskRun.setSysOut(jobDataMap.getString(SYS_OUT));
    taskRun.setSysErr(jobDataMap.getString(SYS_ERR));
    if (exception == null) {
      if (Status.CANCELLING.equals(taskRun.getStatus())) {
        setStatus(taskRun, Status.CANCELLED);
      } else {
        setStatus(taskRun, Status.SUCCESS);
      }
    } else {
      log.error(exception);
      StringWriter writer = new StringWriter();
      exception.printStackTrace(new PrintWriter(writer));
      String stackTrace = writer.toString();
      taskRun.setStackTrace(stackTrace);
      if (Status.CANCELLING.equals(taskRun.getStatus())) {
        setStatus(taskRun, Status.CANCELLED);
      } else {
        setStatus(taskRun, Status.FAILED);
      }
    }
    save(taskRun);
    return taskRun;
  }

  /**
   * Handle a finished TaskRun by checking if any child TaskRuns should now be triggered.
   * 
   * @param finishedRun TaskRun that has just completed.
   */
  private void handleFinishedRun(TaskRun finishedRun) {
    Task finishedTask = taskManager.get(finishedRun.getTaskId());
    if (finishedTask.hasChild()) { // do we need to bother with checking whether to run chilren
      // run a child if parent ran successfully or it failed but stop on error is false
      if (Status.SUCCESS.equals(finishedRun.getStatus())
          || (Status.FAILED.equals(finishedRun.getStatus()) && !finishedTask.isStopOnError())) {
        runChildTasks(finishedTask, finishedRun);
      }
    }
  }

  /**
   * Runs the Children of a certain Task, if necessary. Child Tasks not enabled are skipped (as will any children of
   * theirs). Child Tasks that still have running parents will also be skipped, they will be triggered by the same
   * process when the parent finishes.
   * 
   * @param finishedRun
   */
  private void runChildTasks(Task finished, TaskRun finishedRun) {
    for (Task child : finished.getChildTasks()) {
      if (!child.isEnabled()) {
        log.debug("Child task " + child.getId() + " not enabled, skipping");
      } else {

        boolean runningParent = false;
        for (Task parent : child.getParentTasks()) {
          if (isRunning(parent.getId())) {
            runningParent = true;
            break;
          }
        }

        if (!runningParent) {
          // NOTE: there is a potential synchronisation issue for tasks which run very quickly
          // for example, if a task has 2 parents and 1 of them finishes and we get into the start of this method and
          // then 2nd parent finishes, above code will find no running parents and will continue below
          // and we will run children. While this is happening, handleFinishedRun() for 2nd parent will be
          // triggered which will also try run children, the check on TaskRun start times should prevent
          // children from being run in this case
          TaskRun lastChildRun = getMostRecent(child.getId());
          if (lastChildRun != null && lastChildRun.getStartDate().compareTo(finishedRun.getEndDate()) >= 0) {
            log.error("Task " + child.getId() + " appears to have run already, ignoring");
          } else {
            if (isRunning(child.getId())) { // could be that child is still running from a previous run
              log.warn("Child still/already running, aborting run for task " + child.getId());
              TaskRun taskRun = new TaskRun(new Date(), null, null, null, null, child.getId());
              setStatus(taskRun, Status.ABORTED);
              save(taskRun);
            } else {
              log.info("Running child task " + child.getId());
              schedulerManager.runTaskNow(child);
            }
          }
        } else {
          log.info("At least one parent of task " + finished.getId()
              + " was still running, task will be run when parent completes");
        }
      }
    }
  }

  @Override
  public TaskRun getMostRecent(long taskId) {
    return taskRunDAO.getMostRecentTaskRun(taskId);
  }

  /**
   * @return the schedulerManager
   */
  public SchedulerManager getSchedulerManager() {
    return schedulerManager;
  }

  /**
   * @param schedulerManager the schedulerManager to set
   */
  public void setSchedulerManager(SchedulerManager schedulerManager) {
    this.schedulerManager = schedulerManager;
    schedulerManager.setJobListener(this);
  }

  public TaskDAO getTaskDAO() {
    return taskDAO;
  }

  public void setTaskDAO(TaskDAO taskDAO) {
    this.taskDAO = taskDAO;
  }

  /**
   * @return the taskManager
   */
  public TaskManager getTaskManager() {
    return taskManager;
  }

  /**
   * @param taskManager the taskManager to set
   */
  public void setTaskManager(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  /**
   * @return the notifier
   */
  public Notifier getNotifier() {
    return notifier;
  }

  /**
   * @param notifier the notifier to set
   */
  public void setNotifier(Notifier notifier) {
    this.notifier = notifier;
  }

}
