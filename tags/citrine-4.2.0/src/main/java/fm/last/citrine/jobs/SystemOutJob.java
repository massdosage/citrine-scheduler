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
package fm.last.citrine.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Simple Job implementation that prints to sysout when run (used mainly for testing).
 */
public class SystemOutJob implements Job {
  
  private static Logger log = Logger.getLogger(SystemOutJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    System.out.println("Job execute called");
    try {
      Thread.sleep(1000); //sleep for a second to fake actually doing some work
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
