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

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.impl.calendar.WeeklyCalendar;
import org.quartz.spi.TriggerFiredBundle;

import fm.last.citrine.model.TaskRun;
import fm.last.citrine.scheduler.SchedulerConstants;
import fm.last.citrine.service.TaskRunManager;
import fm.last.test.BaseSpringTestCase;

/**
 * Unit test case for the AdminJob.
 */
public class AdminJobTest extends BaseSpringTestCase {

  @Resource
  private AdminJob adminJob;

  @Resource
  private Scheduler scheduler;

  @Resource
  private TaskRunManager taskRunManager;

  private JobExecutionContext context;

  private JobDetail jobDetail = new JobDetail();

  @Before
  public void setUp() {
    super.setUp();
    TriggerFiredBundle bundle = new TriggerFiredBundle(jobDetail, new SimpleTrigger(), new WeeklyCalendar(), false,
        new Date(), new Date(), new Date(), new Date());
    context = new JobExecutionContext(scheduler, bundle, adminJob);
  }

  @Test(expected = JobExecutionException.class)
  public void testNoCommand() throws JobExecutionException {
    adminJob.execute(context);
  }

  @Test(expected = JobExecutionException.class)
  public void testEmptyCommand() throws JobExecutionException {
    jobDetail.getJobDataMap().put(SchedulerConstants.TASK_COMMAND, "");
    adminJob.execute(context);
  }

  @Test(expected = JobExecutionException.class)
  public void testMissingArgument() throws JobExecutionException {
    jobDetail.getJobDataMap().put(SchedulerConstants.TASK_COMMAND, AdminJob.COMMAND_CLEAR_TASK_RUNS);
    adminJob.execute(context);
  }

  @Test(expected = JobExecutionException.class)
  public void testInvalidCommand() throws JobExecutionException {
    jobDetail.getJobDataMap().put(SchedulerConstants.TASK_COMMAND, "BLA BLA");
    adminJob.execute(context);
  }

  @Test(expected = JobExecutionException.class)
  public void testDeleteTaskRunsBefore() throws JobExecutionException {
    DateTime runTime = new DateTime().minusDays(5);
    int taskId = 1;
    TaskRun taskRun = new TaskRun(runTime.toDate(), runTime.toDate(), "", "", "", taskId);
    taskRunManager.save(taskRun);
    // delete everything older than 1 day, shouldn't delete anything
    jobDetail.getJobDataMap().put(SchedulerConstants.TASK_COMMAND, "1");
    adminJob.execute(context);
    assertEquals(1, taskRunManager.findByTaskId(taskId));

    jobDetail.getJobDataMap().put(SchedulerConstants.TASK_COMMAND, "6");
    adminJob.execute(context);
    assertEquals(0, taskRunManager.findByTaskId(taskId));
  }

}
