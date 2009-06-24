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
import static fm.last.citrine.scheduler.SchedulerConstants.TASK_RUN_ID;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import fm.last.citrine.jobs.syscommand.RollingFileExecutorObserver;
import fm.last.syscommand.SysCommandExecutor;
import fm.last.syscommand.SysCommandUtils;
import fm.last.syscommand.SysExecutorObserver;

/**
 * Citrine job that runs "System Exec" commands (i.e. using a command shell).
 */
public class SystemExecJob implements InterruptableJob {

  private static Logger log = Logger.getLogger(SystemExecJob.class);

  private SysCommandExecutor executor;

  private RollingFileExecutorObserver observer;

  private String commandOutput;

  private String commandError;

  private long taskRunId = -1;

  public SystemExecJob() {
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    this.taskRunId = jobDataMap.getLong(TASK_RUN_ID);
    try {
      execute(jobDataMap.getString(TASK_COMMAND));
    } catch (Exception e) {
      throw new JobExecutionException("Exception occurred running command", e);
    } finally {
      jobDataMap.put(SYS_OUT, commandOutput);
      jobDataMap.put(SYS_ERR, commandError);
    }
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    int returnCode = executor.destroyProcess();
    log.info("Stopped task run " + taskRunId + " exit status " + returnCode);
  }

  /**
   * Executes the passed command.
   * 
   * @param commandString Command to execute.
   * @throws Exception If an error occurs running the command.
   */
  public void execute(String commandString) throws Exception {
    if (observer != null) {
      observer.setJobRunId(taskRunId);
      executor.setSysErrObserver((SysExecutorObserver) observer);
      executor.setSysOutObserver((SysExecutorObserver) observer);
    }
    List<String> command = SysCommandUtils.convertCommand(commandString);
    log.info("Running " + command);
    executor.start(command);
    int exitStatus = executor.waitForProcess();
    log.info("Job finished with exit status " + exitStatus);
    if (observer != null) {
      this.commandOutput = observer.getSysOut();
      this.commandError = observer.getSysErr();
      observer.close();
    }
    if (exitStatus != 0) {
      throw new IllegalStateException("Command returned exit code " + exitStatus);
    }
  }

  public SysCommandExecutor getExecutor() {
    return executor;
  }

  public void setExecutor(SysCommandExecutor executor) {
    this.executor = executor;
  }

  public RollingFileExecutorObserver getObserver() {
    return observer;
  }

  public void setObserver(RollingFileExecutorObserver observer) {
    this.observer = observer;
  }

  /**
   * @return the commandOutput
   */
  public String getCommandOutput() {
    return commandOutput;
  }

  /**
   * @return the commandError
   */
  public String getCommandError() {
    return commandError;
  }

}
