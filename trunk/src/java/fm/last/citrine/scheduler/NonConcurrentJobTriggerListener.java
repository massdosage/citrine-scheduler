/*
 * Copyright 2009 Last.fm
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
package fm.last.citrine.scheduler;

import static fm.last.citrine.scheduler.SchedulerConstants.TASK_ID;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.Task;
import fm.last.citrine.model.TaskRun;
import fm.last.citrine.service.TaskManager;
import fm.last.citrine.service.TaskRunManager;

/**
 * A TriggerListener implementation that prevents Quartz Jobs belonging to the same Job in the database from running at
 * the same time.
 */
public class NonConcurrentJobTriggerListener implements TriggerListener {

  private static Logger log = Logger.getLogger(NonConcurrentJobTriggerListener.class);

  private TaskRunManager taskRunManager;
  private TaskManager taskManager;

  @Override
  public String getName() {
    return "Citrine Trigger Listener";
  }

  @Override
  public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode) {

  }

  @Override
  public void triggerFired(Trigger trigger, JobExecutionContext context) {

  }

  @Override
  public void triggerMisfired(Trigger trigger) {

  }

  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    long taskId = jobDataMap.getLong(TASK_ID);
    if (taskRunManager.isRunning(taskId)) { // task is already running, veto this run
      log.warn("Task " + taskId + " is already running, vetoing TaskRun");
      Task task = taskManager.get(taskId);
      if (task.isErrorIfRunning()) { // only create aborted run and set status if error if running
        TaskRun taskRun = new TaskRun(Status.ABORTED, new Date(), new Date(), null, null, null, taskId);
        taskRunManager.save(taskRun); // save it first so we get an id
        taskRunManager.setStatus(taskRun, Status.ABORTED); // now send notification of status change
      }
      return true;
    }
    return false;
  }

  /**
   * @param taskRunManager the taskRunManager to set
   */
  public void setTaskRunManager(TaskRunManager taskRunManager) {
    this.taskRunManager = taskRunManager;
  }

  /**
   * @param taskManager the taskManager to set
   */
  public void setTaskManager(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

}
