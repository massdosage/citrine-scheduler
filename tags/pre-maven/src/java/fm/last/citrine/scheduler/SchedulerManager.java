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
package fm.last.citrine.scheduler;

import static fm.last.citrine.scheduler.SchedulerConstants.BEAN_FACTORY;
import static fm.last.citrine.scheduler.SchedulerConstants.TASK_BEAN_NAME;
import static fm.last.citrine.scheduler.SchedulerConstants.TASK_COMMAND;
import static fm.last.citrine.scheduler.SchedulerConstants.TASK_ID;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.TriggerUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import fm.last.citrine.model.Task;
import fm.last.commons.io.LastFileUtils;

/**
 * Manager class that is responsible for handling scheduling of Tasks.
 */
public class SchedulerManager implements BeanFactoryAware {

  private static Logger log = Logger.getLogger(SchedulerManager.class);

  private static final String SUFFIX_IMMEDIATE = "-immediate";

  private Scheduler scheduler;
  private Class<Task> jobClass;

  private BeanFactory beanFactory;

  /**
   * Constructs a new Scheduler that will be responsible for scheduling Jobs of the passed class definition.
   * 
   * @param jobClassName Job class name as a String.
   */
  public SchedulerManager(Scheduler scheduler, String jobClassName) {
    try {
      Manifest manifest = LastFileUtils.getManifest(this.getClass());
      Attributes attributes = manifest.getMainAttributes();
      log.info("Citrine Build-Version: " + attributes.getValue("Build-Version"));
      log.info("Citrine Build-DateTime: " + attributes.getValue("Build-DateTime"));
    } catch (IOException e) {
      log.warn("Error getting Citrine version info: " + e.getMessage());
    }

    try {
      this.scheduler = scheduler;
      jobClass = (Class<Task>) Class.forName(jobClassName);
      log.info("Starting scheduler...");
      scheduler.start();
      log.info("Scheduler started");
    } catch (SchedulerException e) {
      throw new ScheduleException("Error creating scheduler", e);
    } catch (ClassNotFoundException e) {
      throw new ScheduleException("Invalid taskClassName: " + jobClassName, e);
    }
  }

  /**
   * Creates a Quartz JobDetail object for the passed citrine Task.
   * 
   * @param task Task to create JobDetail for.
   * @return JobDetail for the passed Task.
   */
  private JobDetail createJobDetail(Task task) {
    if (beanFactory == null) {
      throw new IllegalStateException("Bean factory is null");
    }
    JobDetail jobDetail = new JobDetail(String.valueOf(task.getId()), task.getGroupName(), jobClass);
    JobDataMap jobDataMap = jobDetail.getJobDataMap();
    jobDataMap.put(TASK_ID, task.getId());
    jobDataMap.put(TASK_COMMAND, task.getCommand());
    // put the name of the task to run and the factory to use to retrieve it into the map to be used
    // by TaskBean later
    jobDataMap.put(TASK_BEAN_NAME, task.getBeanName());
    jobDataMap.put(BEAN_FACTORY, beanFactory);
    return jobDetail;
  }

  /**
   * Unschedules the passed Task.
   * 
   * @param task Task to unschedule.
   */
  public void unscheduleTask(Task task) {
    try {
      log.info("Unscheduling task with id " + task.getId());
      scheduler.unscheduleJob(String.valueOf(task.getId()), task.getGroupName());
    } catch (SchedulerException e) {
      throw new ScheduleException("Error unscheduling task with id " + task.getId(), e);
    }
  }

  /**
   * Runs the passed Task immediately, regardless of any schedule settings.
   * 
   * @param task Task to run.
   */
  public void runTaskNow(Task task) {
    JobDetail jobDetail = createJobDetail(task);
    // modify group name otherwise this has potential to clash with other scheduled run of this job
    jobDetail.setGroup(jobDetail.getGroup() + SUFFIX_IMMEDIATE);
    Trigger trigger = TriggerUtils.makeImmediateTrigger(String.valueOf(task.getId()), 0, 1);
    log.info("Scheduling task with id " + task.getId() + " to run now");
    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      throw new ScheduleException("Error scheduling task with id " + task.getId(), e);
    }
  }

  /**
   * Schedule a task to be run.
   * 
   * @param task Task to be scheduled.
   * @param unschedule Whether any existing scheduled runs for this Task should be unscheduled.
   */
  public void scheduleTask(Task task, boolean unschedule) {
    try {
      if (unschedule) {
        unscheduleTask(task);
      }
      if (task.isEnabled() && !StringUtils.isEmpty(task.getTimerSchedule())) {
        JobDetail jobDetail = createJobDetail(task);
        log.info("Scheduling task with id " + task.getId() + " and schedule: " + task.getTimerSchedule());
        CronTrigger cronTrigger = new CronTrigger(String.valueOf(task.getId()), task.getGroupName(), task
            .getTimerSchedule());
        scheduler.scheduleJob(jobDetail, cronTrigger);
      }
    } catch (SchedulerException e) {
      throw new ScheduleException("Error scheduling task with id " + task.getId(), e);
    } catch (ParseException e) {
      throw new ScheduleException("Error scheduling task with id " + task.getId(), e);
    }
  }

  /**
   * Gets the IDs of the Tasks which are currently running.
   * 
   * @return A list of currently running Task IDs.
   */
  public List<Long> getCurrentlyRunningTaskIds() {
    List<Long> taskIds = new ArrayList<Long>();
    try {
      List<JobExecutionContext> runningJobs = scheduler.getCurrentlyExecutingJobs();
      for (JobExecutionContext context : runningJobs) {
        taskIds.add((Long) context.getJobDetail().getJobDataMap().get(TASK_ID));
      }
    } catch (SchedulerException e) {
      throw new ScheduleException("Error getting current running tasks", e);
    }
    return taskIds;
  }

  /**
   * Resets a Task in the scheduler - this deletes the job from the scheduler, then reschedules it.
   * 
   * @param task Task to reset.
   */
  public void resetTask(Task task) {
    log.info("Resetting task with id " + task.getId());
    try {
      scheduler.deleteJob(String.valueOf(task.getId()), task.getGroupName() + SUFFIX_IMMEDIATE);
      scheduler.deleteJob(String.valueOf(task.getId()), task.getGroupName());
      scheduleTask(task, true);
    } catch (SchedulerException e) {
      throw new ScheduleException("Error resetting task with id " + task.getId(), e);
    }
  }

  /**
   * Shuts down the scheduler.
   */
  public void shutdown() {
    log.info("Shutting down scheduler...");
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      throw new ScheduleException("Error shutting down scheduler", e);
    }
    log.info("Scheduler shutdown");
  }

  /**
   * Prepares the scheduler for shutdown by putting it in standby mode. After calling this method all requests to run
   * tasks will be ignored.
   */
  public void prepareForShutdown() {
    log.info("Preparing scheduler for shutdown...");
    try {
      scheduler.standby();
    } catch (SchedulerException e) {
      throw new ScheduleException("Error preparing scheduler for shutdown", e);
    }
    log.info("Prepare finished");
  }

  /**
   * Determines the current status of the scheduler.
   * 
   * @return The current status of the scheduler.
   */
  public SchedulerStatus getStatus() {
    try {
      if (scheduler.isShutdown()) {
        return SchedulerStatus.SHUTDOWN;
      }
      if (scheduler.isInStandbyMode()) {
        if (getCurrentlyRunningTaskIds().isEmpty()) {
          return SchedulerStatus.READY_FOR_SHUTDOWN;
        }
        return SchedulerStatus.PREPARING_FOR_SHUTDOWN;
      }
      if (scheduler.isStarted()) {
        return SchedulerStatus.STARTED;
      }
    } catch (SchedulerException e) {
      throw new ScheduleException("Error determining scheduler status", e);
    }
    return SchedulerStatus.UNKNOWN;
  }

  /**
   * Adds a JobListener to the scheduler.
   * 
   * @param listener Listener to add.
   */
  public void setJobListener(JobListener listener) {
    try {
      scheduler.addGlobalJobListener(listener);
    } catch (SchedulerException e) {
      throw new ScheduleException("Error adding job listener", e);
    }
  }

  /**
   * Adds a TriggerListener to the scheduler.
   * 
   * @param listener Listener to add.
   */
  public void setTriggerListener(TriggerListener listener) {
    try {
      scheduler.addGlobalTriggerListener(listener);
    } catch (SchedulerException e) {
      throw new ScheduleException("Error adding trigger listener", e);
    }
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

}
