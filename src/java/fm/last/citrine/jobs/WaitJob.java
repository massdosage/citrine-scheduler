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

import static fm.last.citrine.scheduler.SchedulerConstants.TASK_COMMAND;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Very simple job implemetation which waits for the amount of time in the passed command string (this must 
 * be convertible to a long).
 */
public class WaitJob implements Job {

  private static Logger log = Logger.getLogger(WaitJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    String command = jobDataMap.getString(TASK_COMMAND);
    long waitTime = Long.parseLong(command);
    log.debug("Waiting for " + waitTime);
    try {
      Thread.sleep(waitTime);
    } catch (InterruptedException e) {
      log.error(e);
    }
  }

}
