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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.springframework.test.annotation.DirtiesContext;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.Task;
import fm.last.citrine.model.TaskRun;
import fm.last.citrine.service.TaskManager;
import fm.last.citrine.service.TaskRunManager;
import fm.last.test.BaseSpringTestCase;

/**
 * Unit test case for the SchedulerManager.
 */
public class SchedulerManagerTest extends BaseSpringTestCase {

  private static Logger log = Logger.getLogger(SchedulerManagerTest.class);

  private static final String BEAN_WAIT_JOB = "waitJob";

  private static final int DEFAULT_TEST_TIMEOUT = 60000;

  @Resource
  private SchedulerManager schedulerManager;

  @Resource
  private TaskManager taskManager;

  @Resource
  private TaskRunManager taskRunManager;

  private String defaultTimerSchedule = "0 15 10 ? * 6L 3002-3005"; // a timer schedule string that should never run

  private String testGroup = "testGroup";

  // an enabled Task with stopOnError set to true that is guaranteed to fail
  private Task failTask = new Task("name", testGroup, "failTask", true, true, "true", "");

  /**
   * Cleaning up the tables also needs to happen *after* to prevent the scheduler from starting tasks before this test
   * runs.
   * 
   * @throws SchedulerException
   * @throws InterruptedException
   */
  @Before
  @After
  public void cleanup() throws SchedulerException, InterruptedException {
    super.cleanupTaskTables();
  }

  /**
   * Utility method to give a Task a chance to finish running. Uses a preconfigured default number of attempts.
   * 
   * @param task Task to wait for.
   * @throws InterruptedException If this Thread is interrupted while waiting.
   */
  private void waitForTask(Task task) throws InterruptedException {
    waitForTask(task, 20);
  }

  /**
   * Utility method to give a Task a chance to finish running. Uses a preconfigured default number of attempts.
   * 
   * @param task Task to wait for.
   * @param maxAttempts Maximum number of times to wait for Task.
   * @throws InterruptedException If this Thread is interrupted while waiting.
   */
  private void waitForTask(Task task, int maxAttempts) throws InterruptedException {
    int attempts = 0;
    while (attempts < maxAttempts) {
      log.debug("Waiting for Task " + task.getId() + " (" + task.getName() + ")");
      Thread.sleep(3000); // give task time to run
      List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
      if (taskRuns.size() == 0) { // task run hasn't even been created yet
        log.debug("No task runs found");
      } else {
        if (!taskRunManager.isRunning(task.getId())) {
          return; // no longer running, get outta here
        }
      }
      attempts++;
    }
  }

  /**
   * Tests running a task that fails due to invalid command.
   * 
   * @throws InterruptedException
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_InvalidCommand() throws InterruptedException {
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "invalidcommand", defaultTimerSchedule);
    taskManager.save(task);
    schedulerManager.runTaskNow(task);
    waitForTask(task);
    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.FAILED, taskRun.getStatus());

    // only stack trace should be filled in
    assertNotNull(taskRun.getStackTrace());
    assertNull(taskRun.getSysOut());
    assertNull(taskRun.getSysErr());
  }

  /**
   * Tests running a task that runs, but its command generates an error.
   * 
   * @throws InterruptedException
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_FailureWhileRunning() throws InterruptedException {
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "ls -l23frx", defaultTimerSchedule);
    taskManager.save(task);
    schedulerManager.runTaskNow(task);
    waitForTask(task);
    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.FAILED, taskRun.getStatus());

    // stack trace and sys err should be filled in
    assertNotNull(taskRun.getStackTrace());
    assertTrue(StringUtils.isEmpty(taskRun.getSysOut()));
    assertFalse("Expected a string but got '" + taskRun.getSysErr() + "'", StringUtils.isEmpty(taskRun.getSysErr()));
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_Success() throws InterruptedException {
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "sleep 5", defaultTimerSchedule);
    taskManager.save(task);
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // give task time to get started
    assertTrue(taskRunManager.isRunning(task.getId()));
    waitForTask(task);
    assertFalse(taskRunManager.isRunning(task.getId()));
    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.SUCCESS, taskRun.getStatus());

    // we slept so there should be no output at all
    assertNull(taskRun.getStackTrace());
    assertTrue(StringUtils.isEmpty(taskRun.getSysOut()));
    assertTrue(StringUtils.isEmpty(taskRun.getSysErr()));
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunSameTaskSimultaneously() throws InterruptedException {
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "sleep 5", defaultTimerSchedule);
    taskManager.save(task);
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // give task time to get started
    assertTrue(taskRunManager.isRunning(task.getId()));

    try {
      schedulerManager.runTaskNow(task);
      fail("Should not be able to run the same task simultaneously");
    } catch (ScheduleException e) {
      // expected
    }

    waitForTask(task);
    assertFalse(taskRunManager.isRunning(task.getId()));
    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.SUCCESS, taskRun.getStatus());

    // now that task has finished, try run it again
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // give task time to get started
    assertTrue(taskRunManager.isRunning(task.getId()));
    waitForTask(task);
    assertFalse(taskRunManager.isRunning(task.getId()));
    taskRuns = taskRunManager.findByTaskId(task.getId());
    assertEquals(2, taskRuns.size());
    assertEquals(Status.SUCCESS, taskRuns.get(0).getStatus());
    assertEquals(Status.SUCCESS, taskRuns.get(1).getStatus());
  }

  /**
   * Tests whether two instances of the task can be run at the same time, using via calls to runTaskNow(). Note that
   * this is different to a task being set to run while another job is running due to a cron trigger.
   * 
   * @throws InterruptedException
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_AbortOnRunning() throws InterruptedException {
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "sleep 3", defaultTimerSchedule);
    taskManager.save(task);
    schedulerManager.runTaskNow(task);
    try { // try run task again, quartz should not allow this
      schedulerManager.runTaskNow(task);
    } catch (ScheduleException e) {
      // expected
    }
    waitForTask(task);
    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0); // most recent job run should be first in list, i.e. the one that aborted
    assertEquals(Status.SUCCESS, taskRun.getStatus());
    assertTrue(StringUtils.isEmpty(taskRun.getStackTrace()));
    assertTrue(StringUtils.isEmpty(taskRun.getSysOut()));
    assertTrue(StringUtils.isEmpty(taskRun.getSysErr()));
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_Success_WithChild() throws InterruptedException {
    Task task1 = new Task("name", "groupName", "sysExecJob", false, true, "ls", defaultTimerSchedule);
    taskManager.save(task1);
    Task task2 = new Task("name2", "groupName", "sysExecJob", true, true, "ls", "");
    taskManager.save(task2);
    task1.addChildTask(task2);
    taskManager.save(task1);

    schedulerManager.runTaskNow(task1);
    waitForTask(task2);

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task1.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.SUCCESS, taskRun.getStatus());

    taskRuns = taskRunManager.findByTaskId(task2.getId());
    assertEquals(1, taskRuns.size());
    taskRun = taskRuns.get(0);
    assertEquals(Status.SUCCESS, taskRun.getStatus());
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_Failure_WithChild_StopOnErrorTrue() throws InterruptedException {
    failTask.setStopOnError(true);
    taskManager.save(failTask);
    Task task2 = new Task("name2", "groupName", "sysExecJob", true, true, "ls", "");
    taskManager.save(task2);
    failTask.addChildTask(task2);
    taskManager.save(failTask);

    schedulerManager.runTaskNow(failTask);
    waitForTask(failTask);

    Thread.sleep(1000);

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(failTask.getId());
    assertEquals(1, taskRuns.size());
    TaskRun jobRun = taskRuns.get(0);
    assertEquals(Status.FAILED, jobRun.getStatus());

    assertEquals(0, taskRunManager.findByTaskId(task2.getId()).size());
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_Failure_WithChild_StopOnErrorFalse() throws InterruptedException {
    // set stopOnError for parent to false, child should run even though parent fails
    failTask.setStopOnError(false);
    taskManager.save(failTask);
    Task task2 = new Task("name2", "groupName", "sysExecJob", true, true, "ls", "");
    taskManager.save(task2);
    failTask.addChildTask(task2);
    taskManager.save(failTask);

    schedulerManager.runTaskNow(failTask);
    waitForTask(task2); // job2 should run even though failJob fails

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(failTask.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.FAILED, taskRun.getStatus());

    assertEquals(1, taskRunManager.findByTaskId(task2.getId()).size());
  }

  /**
   * Tests running a task "diamond" where job 1 has children 2,3 and they both have child 4.
   * 
   * @throws InterruptedException
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_ChildDiamond() throws InterruptedException {
    Task task1 = new Task("name", "groupName", "sysExecJob", false, true, "ls", defaultTimerSchedule);
    taskManager.save(task1);
    Task task2 = new Task("name2", "groupName", "sysExecJob", false, true, "ls", "");
    taskManager.save(task2);
    task1.addChildTask(task2);
    taskManager.save(task1);

    schedulerManager.runTaskNow(task1);
    waitForTask(task1);

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task1.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.SUCCESS, taskRun.getStatus());

    // wait once for task2 just to make sure it hasn't actually run
    waitForTask(task2, 1);

    // task 2 is disabled, so even though it is child of 1, it should not run
    taskRuns = taskRunManager.findByTaskId(task2.getId());
    assertEquals(0, taskRuns.size());
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testChildStillRunning() throws InterruptedException {
    Task parentTask = new Task("parent", "groupName", "sysExecJob", false, true, "ls", defaultTimerSchedule);
    taskManager.save(parentTask);
    Task childTask = new Task("child", "groupName", "sysExecJob", true, true, "sleep 10", "");
    taskManager.save(childTask);
    parentTask.addChildTask(childTask);
    taskManager.save(parentTask);

    schedulerManager.runTaskNow(parentTask);
    waitForTask(parentTask);
    Thread.sleep(1000); // give task 2 a chance to get going
    assertTrue(taskRunManager.isRunning(childTask.getId()));

    schedulerManager.runTaskNow(parentTask); // now run task 1 again
    waitForTask(parentTask);
    waitForTask(childTask);

    // now run 1 again, should all go fine
    schedulerManager.runTaskNow(parentTask);
    waitForTask(childTask);

    List<TaskRun> parentTaskRuns = taskRunManager.findByTaskId(parentTask.getId());
    assertEquals(3, parentTaskRuns.size());
    assertEquals(Status.SUCCESS, parentTaskRuns.get(0).getStatus());
    assertEquals(Status.SUCCESS, parentTaskRuns.get(1).getStatus());
    assertEquals(Status.SUCCESS, parentTaskRuns.get(2).getStatus());

    List<TaskRun> childTaskRuns = taskRunManager.findByTaskId(childTask.getId());
    assertEquals(3, childTaskRuns.size());
    assertEquals(Status.SUCCESS, childTaskRuns.get(0).getStatus());
    assertEquals(Status.ABORTED, childTaskRuns.get(1).getStatus());
    assertEquals(Status.SUCCESS, childTaskRuns.get(2).getStatus());
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_Success_WithChildDisabled() throws InterruptedException {
    String group = "diamondTest";
    Task task1 = new Task("task1", group, BEAN_WAIT_JOB, true, true, "3000", "");
    taskManager.save(task1);
    Task task2 = new Task("task2", group, BEAN_WAIT_JOB, true, true, "100", "");
    taskManager.save(task2);
    Task task3 = new Task("task3", group, BEAN_WAIT_JOB, true, true, "6000", "");
    taskManager.save(task3);
    Task task4 = new Task("task4", group, BEAN_WAIT_JOB, true, true, "1000", "");
    taskManager.save(task4);

    // now create our task "diamond"
    task1.addChildTask(task2);
    task1.addChildTask(task3);
    taskManager.save(task1);
    task2.addChildTask(task4);
    taskManager.save(task2);
    task3.addChildTask(task4);
    taskManager.save(task3);

    schedulerManager.runTaskNow(task1);
    Thread.sleep(1000); // wait enough that task1 should have started but not yet finished
    assertEquals(1, taskRunManager.findByTaskId(task1.getId()).size());
    assertEquals(0, taskRunManager.findByTaskId(task3.getId()).size());
    assertEquals(0, taskRunManager.findByTaskId(task2.getId()).size());

    waitForTask(task1);
    // now tasks 2 and 3 should get kicked off
    Thread.sleep(1000); // wait a bit for this to happen
    assertEquals(1, taskRunManager.findByTaskId(task2.getId()).size());
    assertEquals(1, taskRunManager.findByTaskId(task3.getId()).size());
    assertEquals(0, taskRunManager.findByTaskId(task4.getId()).size()); // should not be triggered yet

    waitForTask(task4); // once 2 and 3 are finished, 4 should get triggered
    assertEquals(1, taskRunManager.findByTaskId(task1.getId()).size());
    assertEquals(1, taskRunManager.findByTaskId(task2.getId()).size());
    assertEquals(1, taskRunManager.findByTaskId(task3.getId()).size());
    assertEquals(1, taskRunManager.findByTaskId(task4.getId()).size());
  }

  /**
   * Tests running the diamond, where task 2 fails, 3 succeeds, due to "AND" nature of stopOnError, task 4 should still
   * run.
   * 
   * @throws InterruptedException
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_ChildDiamond_StopOnError_2Fails() throws InterruptedException {
    String group = "diamondTest";
    Task task1 = new Task("task1", group, BEAN_WAIT_JOB, true, true, "3000", "");
    taskManager.save(task1);
    // set task 2 to fail
    Task task2 = new Task("task2", group, "failJob", true, true, "true", "");
    taskManager.save(task2);
    Task task3 = new Task("task3", group, BEAN_WAIT_JOB, true, true, "5000", "");
    taskManager.save(task3);
    Task task4 = new Task("task4", group, BEAN_WAIT_JOB, true, true, "1000", "");
    taskManager.save(task4);

    // now create our task "diamond"
    task1.addChildTask(task2);
    task1.addChildTask(task3);
    taskManager.save(task1);
    task2.addChildTask(task4);
    taskManager.save(task2);
    task3.addChildTask(task4);
    taskManager.save(task3);

    schedulerManager.runTaskNow(task1);
    waitForTask(task4); // task 4 should run

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task2.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.FAILED, taskRun.getStatus());

    taskRuns = taskRunManager.findByTaskId(task4.getId());
    assertEquals(1, taskRuns.size());
    taskRun = taskRuns.get(0);
    assertEquals(Status.SUCCESS, taskRun.getStatus());
  }

  /**
   * Tests running the diamond, where task 2 and 3 failsdue to "AND" nature of stopOnError, task 4 should not run.
   * 
   * @throws InterruptedException
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testRunNow_ChildDiamond_StopOnError_2And3Fail() throws InterruptedException {
    String group = "diamondTest";
    Task task1 = new Task("task1", group, BEAN_WAIT_JOB, true, true, "3000", "");
    taskManager.save(task1);
    // set tasks 2 and 3 to fail
    Task task2 = new Task("task2", group, "failJob", true, true, "true", "");
    taskManager.save(task2);
    Task task3 = new Task("task3", group, "failJob", true, true, "true", "");
    taskManager.save(task3);
    Task task4 = new Task("task4", group, BEAN_WAIT_JOB, true, true, "1000", "");
    taskManager.save(task4);

    // now create our task "diamond"
    task1.addChildTask(task2);
    task1.addChildTask(task3);
    taskManager.save(task1);
    task2.addChildTask(task4);
    taskManager.save(task2);
    task3.addChildTask(task4);
    taskManager.save(task3);

    schedulerManager.runTaskNow(task1);
    waitForTask(task2);
    waitForTask(task3);

    assertEquals(0, taskRunManager.findByTaskId(100).size());
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testStopTaskRun() throws InterruptedException {
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "sleep 15", defaultTimerSchedule);
    taskManager.save(task);
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // give job time to get started
    long taskRunId = taskRunManager.getMostRecent(task.getId()).getId();
    assertTrue(taskRunManager.isRunning(task.getId()));

    taskRunManager.stop(taskRunId);
    Thread.sleep(1000); // give job time to die
    assertFalse(taskRunManager.isRunning(task.getId()));
    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.CANCELLED, taskRun.getStatus());

    assertNotNull(taskRun.getStackTrace());
    assertTrue(StringUtils.isEmpty(taskRun.getSysOut()));
    assertTrue(StringUtils.isEmpty(taskRun.getSysErr()));
  }

  /**
   * Test that scheduling a task to run using a cron trigger works.
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testTimedScheduledRun() throws InterruptedException {
    Task task = new Task("testTimerScheduledRun", "groupName", "sysExecJob");
    task.setErrorIfRunning(true);
    task.setCommand("sleep 1");
    task.setTimerSchedule("*/10 * * * * ?");
    taskManager.save(task);
    waitForTask(task);

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertEquals(1, taskRuns.size());
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.SUCCESS, taskRun.getStatus());
  }

  /**
   * Test scheduling a task that will run for long enough that other runs are triggered while it is running. The task is
   * set to error if running, so the other runs should show up as aborted.
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  @DirtiesContext
  public void testTaskConflict_ErrorIfRunningTrue() throws InterruptedException {
    Task task = new Task("errorIfRunningTask", "groupName", "sysExecJob");
    task.setErrorIfRunning(true);
    task.setCommand("sleep 10");
    task.setTimerSchedule("*/3 * * * * ?");
    taskManager.save(task);
    waitForTask(task);

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertTrue(taskRuns.size() >= 3); // should be at least 1 success run and two aborted
    int successCount = 0;
    int abortCount = 0;
    for (TaskRun taskRun : taskRuns) {
      // there might be more than 1 successful run depending on timing above, all others should be aborted
      if (Status.SUCCESS.equals(taskRun.getStatus())) {
        successCount++;
      } else if (Status.ABORTED.equals(taskRun.getStatus())) {
        abortCount++;
      }
    }
    assertTrue(abortCount > successCount); // should be more runs aborted while task ran successfully
  }

  /**
   * Test scheduling a task that will run for long enough that other runs are triggered while it is running. The task is
   * set NOT to error if running, so the other runs should not show up in task runs table at all.
   */
  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  @DirtiesContext
  public void testTaskConflict_ErrorIfRunningFalse() throws InterruptedException {
    Task task = new Task("noErrorIfRunningTask", "groupName", "sysExecJob");
    task.setErrorIfRunning(false);
    task.setCommand("sleep 10");
    task.setTimerSchedule("*/3 * * * * ?");
    taskManager.save(task);
    waitForTask(task);

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    assertTrue(taskRuns.size() >= 1); // should be at least 1 run, maybe more depending on timing
    for (TaskRun taskRun : taskRuns) { // all runs should be marked as success (all aborts ignored)
      assertEquals(Status.SUCCESS, taskRun.getStatus());
    }
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  public void testGetCurrentlyRunningTaskIds() throws InterruptedException, SchedulerException {
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "sleep 5", defaultTimerSchedule);
    taskManager.save(task);
    assertEquals(0, schedulerManager.getCurrentlyRunningTaskIds().size());
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // give task time to get started
    assertTrue(taskRunManager.isRunning(task.getId()));
    List<Long> taskIds = schedulerManager.getCurrentlyRunningTaskIds();
    assertEquals(1, taskIds.size());
    assertEquals(task.getId(), (long) taskIds.get(0));
    waitForTask(task);
    assertEquals(0, schedulerManager.getCurrentlyRunningTaskIds().size());
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  @DirtiesContext
  public void testReset() throws InterruptedException {
    Task task = new Task("testReset", "groupName", "sysExecJob");
    task.setErrorIfRunning(true);
    task.setCommand("sleep 1");
    task.setTimerSchedule("*/10 * * * * ?");
    taskManager.save(task); // save triggers schedule
    waitForTask(task);

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(task.getId());
    int runCount = taskRuns.size();
    assertTrue(runCount >= 1); // in most cases will be 1, but might get triggered again before we get here
    TaskRun taskRun = taskRuns.get(0);
    assertEquals(Status.SUCCESS, taskRun.getStatus());

    schedulerManager.resetTask(task); // this will delete the job and reschedule it
    Thread.sleep(5000); // wait for job to run at least one more time to check reschedule worked
    waitForTask(task);

    taskRuns = taskRunManager.findByTaskId(task.getId());
    assertTrue(taskRuns.size() > runCount); // should have run at least one more time
  }

  @Test(timeout = DEFAULT_TEST_TIMEOUT)
  @DirtiesContext
  public void testPrepareForShutdown() throws InterruptedException {
    assertEquals(SchedulerStatus.STARTED, schedulerManager.getStatus());
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "sleep 5", defaultTimerSchedule);
    taskManager.save(task);
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // give task time to get started
    assertTrue(taskRunManager.isRunning(task.getId()));
    schedulerManager.prepareForShutdown();
    // task is still running, so should still be in "preparing for shutdown" mode
    assertEquals(SchedulerStatus.PREPARING_FOR_SHUTDOWN, schedulerManager.getStatus());
    waitForTask(task);
    // task no longer running, ready
    assertEquals(SchedulerStatus.READY_FOR_SHUTDOWN, schedulerManager.getStatus());
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // give task time to get started
    assertFalse(taskRunManager.isRunning(task.getId())); // scheduler should not run anything anymore
    assertEquals(SchedulerStatus.READY_FOR_SHUTDOWN, schedulerManager.getStatus());
    schedulerManager.shutdown();
    assertEquals(SchedulerStatus.SHUTDOWN, schedulerManager.getStatus());
  }

}
