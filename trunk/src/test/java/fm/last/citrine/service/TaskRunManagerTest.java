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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.spi.TriggerFiredBundle;

import fm.last.citrine.jobs.NullJob;
import fm.last.citrine.model.Notification;
import fm.last.citrine.model.Status;
import fm.last.citrine.model.Task;
import fm.last.citrine.model.TaskRun;
import fm.last.citrine.scheduler.SchedulerConstants;
import fm.last.test.BaseSpringTestCase;
import fm.last.test.JavaMailSenderMock;

/**
 * Unit test case for the TaskRunManager.
 */
public class TaskRunManagerTest extends BaseSpringTestCase {

  @Resource
  private Scheduler scheduler;

  @Resource
  private TaskRunManager taskRunManager;

  @Resource
  private TaskManager taskManager;

  @Resource
  private JavaMailSenderMock mailSender;

  private long testTaskId;

  private Task testTask = new Task("taskRunManagerTestTask", "testGroup", "", false, true, "", "");

  @Before
  public void setUp() {
    super.setUp();
    super.cleanupTaskTables();
    taskManager.save(testTask);
    testTaskId = testTask.getId();
  }

  @Test
  public void testSaveGetAndUpdate() {
    TaskRun taskRun = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    taskRunManager.save(taskRun);
    assertTrue(taskRun.getId() > 0);
    assertEquals(0, taskRun.getVersion());

    TaskRun retrieved = taskRunManager.get(taskRun.getId());
    assertEquals(taskRun, retrieved);

    taskRun.setSysErr("err2");
    taskRunManager.save(taskRun);

    retrieved = taskRunManager.get(taskRun.getId());
    assertEquals("err2", retrieved.getSysErr());
  }

  @Test
  public void testFindByTaskId() {
    assertEquals(0, taskRunManager.findByTaskId(1).size()); // initially should be none in db

    // save two task runs against id 1
    TaskRun taskRun = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    taskRunManager.save(taskRun);
    TaskRun taskRun2 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    taskRunManager.save(taskRun2);
    // save one taskrun against id 2
    TaskRun taskRun3 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 2);
    taskRunManager.save(taskRun3);

    assertEquals(2, taskRunManager.findByTaskId(1).size());
  }

  /**
   * Tests that changing a TaskRun's status ends up with the expected number of notifications being sent.
   */
  @Test
  public void testSetTaskRunStatus() {
    Task task = new Task("name", "group", "");
    task.setNotification(new Notification());
    taskManager.save(task);
    TaskRun taskRun = new TaskRun(new Date(), new Date(), "out", "err", "stack", task.getId());
    
    int mailCount = 0;
    // no mail should be sent for following
    taskRunManager.setStatus(taskRun, Status.INITIALISING);
    assertEquals(mailCount, mailSender.getMessageCount());
    assertEquals(Status.INITIALISING, taskRun.getStatus());
    taskRunManager.setStatus(taskRun, Status.RUNNING);
    assertEquals(mailCount, mailSender.getMessageCount());
    assertEquals(Status.RUNNING, taskRun.getStatus());
    taskRunManager.setStatus(taskRun, Status.CANCELLING);
    assertEquals(mailCount, mailSender.getMessageCount());
    assertEquals(Status.CANCELLING, taskRun.getStatus());

    // mail should be sent for following
    taskRunManager.setStatus(taskRun, Status.SUCCESS);
    assertEquals(++mailCount, mailSender.getMessageCount());
    assertEquals(Status.SUCCESS, taskRun.getStatus());
    taskRunManager.setStatus(taskRun, Status.CANCELLED);
    assertEquals(++mailCount, mailSender.getMessageCount());
    assertEquals(Status.CANCELLED, taskRun.getStatus());
    taskRunManager.setStatus(taskRun, Status.INTERRUPTED);
    assertEquals(++mailCount, mailSender.getMessageCount());
    assertEquals(Status.INTERRUPTED, taskRun.getStatus());
    taskRunManager.setStatus(taskRun, Status.ABORTED);
    assertEquals(++mailCount, mailSender.getMessageCount());
    assertEquals(Status.ABORTED, taskRun.getStatus());
    taskRunManager.setStatus(taskRun, Status.FAILED);
    assertEquals(++mailCount, mailSender.getMessageCount());
    assertEquals(Status.FAILED, taskRun.getStatus());
  }

  @Test
  public void testShutdown() {
    TaskRun taskRun = new TaskRun(Status.RUNNING, new Date(), new Date(), null, null, null, 1);
    taskRunManager.save(taskRun);
    taskRun = new TaskRun(Status.INITIALISING, new Date(), new Date(), null, null, null, 1);
    taskRunManager.save(taskRun);
    taskRunManager.shutdown();
    List<TaskRun> taskRuns = taskRunManager.findByTaskId(1);
    assertEquals(2, taskRuns.size());
    assertEquals(Status.INTERRUPTED, taskRuns.get(0).getStatus());
    assertEquals(Status.INTERRUPTED, taskRuns.get(1).getStatus());
  }

  /**
   * Creates a basic JobExecutionContext which can be used for triggering the starting and stopping of tasks.
   * 
   * @return A basic JobExecutionContext.
   */
  private JobExecutionContext createJobExecutionContext(Task task) {
    JobDetail jobDetail = new JobDetail();
    JobDataMap jobDataMap = jobDetail.getJobDataMap();
    jobDataMap.put(SchedulerConstants.TASK_ID, task.getId());
    Job testJob = new NullJob();
    JobExecutionContext context = new JobExecutionContext(scheduler, new TriggerFiredBundle(jobDetail,
        new SimpleTrigger(), null, false, new Date(), null, null, null), testJob);
    return context;
  }

  @Test
  public void testStartAndFinish_Success() {
    JobExecutionContext context = createJobExecutionContext(testTask);
    taskRunManager.jobToBeExecuted(context);

    TaskRun taskRun = taskRunManager.getMostRecent(testTaskId);
    assertTrue(taskRunManager.isRunning(taskRun.getTaskId()));

    taskRunManager.jobWasExecuted(context, null);
    assertFalse(taskRunManager.isRunning(taskRun.getTaskId()));
    taskRun = taskRunManager.get(taskRun.getId());
    assertEquals(Status.SUCCESS, taskRun.getStatus());
  }

  @Test
  public void testStartAndFinish_Failure() {
    JobExecutionContext context = createJobExecutionContext(testTask);
    taskRunManager.jobToBeExecuted(context);

    TaskRun taskRun = taskRunManager.getMostRecent(testTaskId);
    assertTrue(taskRunManager.isRunning(taskRun.getTaskId()));

    taskRunManager.jobWasExecuted(context, new JobExecutionException());
    assertFalse(taskRunManager.isRunning(taskRun.getTaskId()));
    taskRun = taskRunManager.get(taskRun.getId());
    assertEquals(Status.FAILED, taskRun.getStatus());
  }

  @Test
  public void testStartAndCancel() {
    JobExecutionContext context = createJobExecutionContext(testTask);
    taskRunManager.jobToBeExecuted(context);

    TaskRun taskRun = taskRunManager.getMostRecent(testTaskId);
    assertTrue(taskRunManager.isRunning(taskRun.getTaskId()));

    taskRunManager.stop(taskRun.getId());
    taskRun = taskRunManager.get(taskRun.getId());
    assertEquals(Status.CANCELLING, taskRun.getStatus());
    assertFalse(taskRunManager.isRunning(taskRun.getTaskId()));

    taskRunManager.jobWasExecuted(context, new JobExecutionException());
    assertFalse(taskRunManager.isRunning(taskRun.getTaskId()));
    taskRun = taskRunManager.get(taskRun.getId());
    assertEquals(Status.CANCELLED, taskRun.getStatus());
  }

  @Test
  public void testStartAndFinish_SuccessWithDisabledChild() throws InterruptedException {
    Task childTask = new Task("taskRunManagerTestChildTask", "testGroup", "", false, true, "", "");
    taskManager.save(childTask);
    testTask.addChildTask(childTask);
    taskManager.save(testTask);

    JobExecutionContext context = createJobExecutionContext(testTask);
    taskRunManager.jobToBeExecuted(context);
    taskRunManager.jobWasExecuted(context, null);
    Thread.sleep(1000); // give scheduler time to start child

    // child is not enabled so no task run should be created for it when parent finishes
    assertNull(taskRunManager.getMostRecent(childTask.getId()));
  }

  @Test
  public void testStartAndFinish_SuccessWithEnabledChild() throws InterruptedException {
    Task childTask = new Task("taskRunManagerTestChildTask", "testGroup", "nullJob", true, true, "", "");
    taskManager.save(childTask);
    testTask.addChildTask(childTask);
    taskManager.save(testTask);

    JobExecutionContext context = createJobExecutionContext(testTask);
    taskRunManager.jobToBeExecuted(context);
    taskRunManager.jobWasExecuted(context, null);
    Thread.sleep(1000); // give scheduler time to start child

    assertNotNull(taskRunManager.getMostRecent(childTask.getId())); // child enabled so it should be triggered
  }

  @Test
  public void testStartAndFinish_FailureWithChild() throws InterruptedException {
    Task childTask = new Task("taskRunManagerTestChildTask", "testGroup", "nullJob", true, true, "", "");
    taskManager.save(childTask);
    testTask.addChildTask(childTask);
    taskManager.save(testTask);

    JobExecutionContext context = createJobExecutionContext(testTask);
    taskRunManager.jobToBeExecuted(context);
    taskRunManager.jobWasExecuted(context, new JobExecutionException());
    Thread.sleep(1000); // give scheduler time to start child

    assertNull(taskRunManager.getMostRecent(childTask.getId())); // parent failed so child should not run
  }

  @Test
  public void testStartAndFinish_FailureNoStopOnErrorWithChild() throws InterruptedException {
    testTask.setStopOnError(false);
    taskManager.save(testTask);

    Task childTask = new Task("taskRunManagerTestChildTask", "testGroup", "nullJob", true, true, "", "");
    taskManager.save(childTask);
    testTask.addChildTask(childTask);
    taskManager.save(testTask);

    JobExecutionContext context = createJobExecutionContext(testTask);
    taskRunManager.jobToBeExecuted(context);
    taskRunManager.jobWasExecuted(context, new JobExecutionException());
    Thread.sleep(1500); // give scheduler time to start child

    // parent failed but not set to stop on error so child should run
    assertNotNull(taskRunManager.getMostRecent(childTask.getId()));
  }

  @Test
  public void testStartAndCancel_WithChild() throws InterruptedException {
    testTask.setStopOnError(false); // ensure there is no other reason for parent stopping child
    taskManager.save(testTask);

    Task childTask = new Task("taskRunManagerTestChildTask", "testGroup", "nullJob", true, true, "", "");
    taskManager.save(childTask);
    testTask.addChildTask(childTask);
    taskManager.save(testTask);

    JobExecutionContext context = createJobExecutionContext(testTask);
    taskRunManager.jobToBeExecuted(context);

    TaskRun taskRun = taskRunManager.getMostRecent(testTaskId);
    assertTrue(taskRunManager.isRunning(taskRun.getTaskId()));

    taskRunManager.stop(taskRun.getId());
    taskRun = taskRunManager.get(taskRun.getId());
    taskRunManager.jobWasExecuted(context, new JobExecutionException());
    assertFalse(taskRunManager.isRunning(taskRun.getTaskId()));

    Thread.sleep(1000); // give scheduler time to start child

    // parent was cancelled, so no child should run
    assertNull(taskRunManager.getMostRecent(childTask.getId()));
  }

}
