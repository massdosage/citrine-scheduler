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

import static fm.last.citrine.scheduler.SchedulerConstants.BEAN_FACTORY;
import static fm.last.citrine.scheduler.SchedulerConstants.TASK_BEAN_NAME;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.BeanFactory;

/**
 * Simple Job wrapper that retrieves the name of the job to run and runs it using values stored in the job context.
 */
public class BeanJob implements InterruptableJob {

  private static Logger log = Logger.getLogger(BeanJob.class);
  private org.quartz.Job jobBean;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    try {
      String jobBeanName = jobDataMap.getString(TASK_BEAN_NAME);
      BeanFactory beanFactory = (BeanFactory) jobDataMap.get(BEAN_FACTORY);
      jobBean = (Job) beanFactory.getBean(jobBeanName);
      log.debug("Executing job bean with name: " + jobBeanName);
      jobBean.execute(context);
    } catch (Exception e) {
      throw new JobExecutionException("Exception occurred running job bean", e);
    }
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    if (jobBean != null && jobBean instanceof InterruptableJob) {
      ((InterruptableJob) jobBean).interrupt();
    }
  }

}
