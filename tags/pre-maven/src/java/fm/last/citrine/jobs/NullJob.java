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
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

/**
 * A job implementation that does nothing. Can be used as a placeholder in parent/child relationships etc.
 */
public class NullJob implements InterruptableJob {

  private static Logger log = Logger.getLogger(NullJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.debug("Null Job called");
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    log.debug("Interrupt called");
  }

}
