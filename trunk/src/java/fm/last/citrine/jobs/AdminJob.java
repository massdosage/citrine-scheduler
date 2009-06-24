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
package fm.last.citrine.jobs;

import static fm.last.citrine.scheduler.SchedulerConstants.SYS_ERR;
import static fm.last.citrine.scheduler.SchedulerConstants.SYS_OUT;
import static fm.last.citrine.scheduler.SchedulerConstants.TASK_COMMAND;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fm.last.citrine.service.LogFileManager;
import fm.last.citrine.service.TaskRunManager;

/**
 * Job implementation for performing common Citrine admin tasks.
 */
public class AdminJob implements Job {

  private static Logger log = Logger.getLogger(AdminJob.class);

  public static final String COMMAND_CLEAR_TASK_RUNS = "clear_task_runs";
  public static final String COMMAND_CLEAR_LOG_FILES = "clear_log_files";

  private TaskRunManager taskRunManager;
  private LogFileManager logFileManager;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    String commandString = jobDataMap.getString(TASK_COMMAND);
    if (StringUtils.isEmpty(commandString)) {
      throw new JobExecutionException("Received empty admin command");
    }
    String[] command = commandString.split("\\s+");
    if (command.length != 2) {
      throw new JobExecutionException("Did not receive command and param");
    }
    String commandType = command[0];
    String argument = command[1];
    if (COMMAND_CLEAR_TASK_RUNS.equals(commandType)) {
      int days = Integer.parseInt(argument);
      DateTime deleteBefore = new DateTime().minusDays(days);
      taskRunManager.deleteBefore(deleteBefore);
      jobDataMap.put(SYS_OUT, "Deleted task runs on and before " + deleteBefore);
    } else if (COMMAND_CLEAR_LOG_FILES.equals(commandType)) {
      int days = Integer.parseInt(argument);
      DateTime deleteBefore = new DateTime().minusDays(days);
      try {
        logFileManager.deleteBefore(deleteBefore);
        jobDataMap.put(SYS_OUT, "Deleted log files older than " + deleteBefore);
      } catch (IOException e) {
        log.error("Error deleting log files", e);
        jobDataMap.put(SYS_ERR, "Error deleting log files " + e.getMessage());
      }
    } else {
      throw new JobExecutionException("Invalid command type '" + commandType + "'");
    }
  }

  /**
   * @param taskRunManager the taskRunManager to set
   */
  public void setTaskRunManager(TaskRunManager taskRunManager) {
    this.taskRunManager = taskRunManager;
  }

  /**
   * @param logFileManager the logFileManager to set
   */
  public void setLogFileManager(LogFileManager logFileManager) {
    this.logFileManager = logFileManager;
  }
  
}
