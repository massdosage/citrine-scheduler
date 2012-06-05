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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.Task;
import fm.last.citrine.model.TaskRun;
import fm.last.citrine.scheduler.SchedulerManager;
import fm.last.test.BaseSpringTestCase;

/**
 * Unit test case for the TaskManager.
 */
public class TaskManagerTest extends BaseSpringTestCase {

  private String defaultTimerSchedule = "0 15 10 ? * 6L 3002-3005"; // a timer schedule string that should never run

  private static Logger log = Logger.getLogger(TaskManagerTest.class);

  // tasks which are used for child/parent tests
  private Task task1;
  private Task task2;
  private Task task3;
  private Task task4;

  @Resource
  private TaskManager taskManager;

  @Resource
  private TaskRunManager taskRunManager;

  @Resource
  private SchedulerManager schedulerManager;

  @Before
  @After
  public void cleanup() {
    super.cleanupTaskTables();
  }

  /**
   * Convenience method for creating a set of tasks where task 1 has 2 children - 2 and 3, and 2 and 3 in turn have the
   * same child, 4.
   */
  private void createChildParentTasks() {
    task1 = new Task("1", "childTest", "1", false, true, "1", defaultTimerSchedule);
    taskManager.save(task1);
    task2 = new Task("2", "childTest", "2", false, true, "2", defaultTimerSchedule);
    taskManager.save(task2);
    task3 = new Task("3", "childTest", "3", false, true, "3", defaultTimerSchedule);
    taskManager.save(task3);
    task4 = new Task("4", "childTest", "4", false, true, "4", defaultTimerSchedule);
    taskManager.save(task4);

    task1.addChildTask(task2);
    task1.addChildTask(task3);
    taskManager.save(task1);

    task2.addChildTask(task4);
    taskManager.save(task2);
    task3.addChildTask(task4);
    taskManager.save(task3);
  }

  @Test
  public void testSaveGetAndUpdate() {
    Task task = new Task("name", "groupName", "sysExecTask", false, true, "command", defaultTimerSchedule);
    taskManager.save(task);
    assertTrue(task.getId() > 0);
    assertEquals(0, task.getVersion());

    Task retrieved = taskManager.get(task.getId());
    assertEquals(task, retrieved);

    task.setCommand("command2");
    taskManager.save(task);

    retrieved = taskManager.get(task.getId());
    assertEquals("command2", retrieved.getCommand());
  }

  @Test
  public void testGetTasks() {
    assertEquals(0, taskManager.getTasks().size()); // no jobs in DB to start with
    Task task = new Task("name", "groupName", "sysExecTask", false, true, "command", defaultTimerSchedule);
    taskManager.save(task);
    assertEquals(1, taskManager.getTasks().size());

    Task task2 = new Task("name2", "groupName2", "sysExecTask2", false, true, "command2", defaultTimerSchedule);
    taskManager.save(task2);

    assertEquals(2, taskManager.getTasks().size());
  }

  @Test
  public void testDelete() {
    Task task1 = new Task("name", "groupName", "sysExecTask", false, true, "command", defaultTimerSchedule);
    taskManager.save(task1);
    Task task2 = new Task("name", "groupName", "sysExecTask", false, true, "command", defaultTimerSchedule);
    taskManager.save(task2);

    TaskRun taskRun1 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", task1.getId());
    TaskRun taskRun2 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", task1.getId());
    TaskRun taskRun3 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", task2.getId());
    taskRunManager.save(taskRun1);
    taskRunManager.save(taskRun2);
    taskRunManager.save(taskRun3);

    taskManager.delete(task1);

    assertEquals(1, taskManager.getTasks().size());
    assertEquals(0, taskRunManager.findByTaskId(task1.getId()).size());
    assertEquals(1, taskRunManager.findByTaskId(task2.getId()).size());

    taskManager.delete(task2);
    assertEquals(0, taskManager.getTasks().size());
    assertEquals(0, taskRunManager.findByTaskId(task2.getId()).size());
  }

  @Test
  public void testFindTasksInSameGroup() {
    Task taskA = new Task("name", "groupA", "sysExecTask", false, true, "command", defaultTimerSchedule);
    taskManager.save(taskA);
    Task taskB1 = new Task("name2", "groupB", "sysExecTask2", false, true, "command2", defaultTimerSchedule);
    taskManager.save(taskB1);
    Task taskB2 = new Task("name3", "groupB", "sysExecTask3", false, true, "command3", defaultTimerSchedule);
    taskManager.save(taskB2);

    List<Task> retrieved = taskManager.findTasksInSameGroup(taskA);
    assertEquals(0, retrieved.size());

    retrieved = taskManager.findTasksInSameGroup(taskB1);
    assertEquals(1, retrieved.size());
    assertEquals(taskB2, retrieved.get(0));
  }

  @Test
  public void testSaveDependentJob() {
    createChildParentTasks();

    // check that 1 has no parents and has 2,3 as children
    Task retrieved = taskManager.get(task1.getId());
    assertEquals(0, retrieved.getParentTasks().size());
    assertEquals(2, retrieved.getChildTasks().size());
    for (Task child : retrieved.getChildTasks()) {
      assertTrue(task2.getId() == child.getId() || task3.getId() == child.getId());
    }

    // assert that 1 is parent of 2 and 4 is it's child
    retrieved = taskManager.get(task2.getId());
    assertEquals(1, retrieved.getParentTasks().size());
    assertEquals(task1.getId(), retrieved.getParentTasks().iterator().next().getId());
    assertEquals(1, retrieved.getChildTasks().size());
    assertEquals(task4.getId(), retrieved.getChildTasks().iterator().next().getId());

    // assert that 1 is parent of 3 and 4 is it's child
    retrieved = taskManager.get(task3.getId());
    assertEquals(1, retrieved.getParentTasks().size());
    assertEquals(task1.getId(), retrieved.getParentTasks().iterator().next().getId());
    assertEquals(1, retrieved.getChildTasks().size());
    assertEquals(task4.getId(), retrieved.getChildTasks().iterator().next().getId());

    // assert that 4 has no children and that 2 and 3 are its parents
    retrieved = taskManager.get(task4.getId());
    assertEquals(2, retrieved.getParentTasks().size());
    for (Task parent : retrieved.getParentTasks()) {
      assertTrue(task2.getId() == parent.getId() || task3.getId() == parent.getId());
    }
    assertEquals(0, retrieved.getChildTasks().size());
  }

  @Test
  public void testSave_NoTimerSchedule() {
    Task task = new Task("taskName", "groupName", "beanName");
    taskManager.save(task);
    Task retrieved = taskManager.get(task.getId());
    assertEquals(task, retrieved);
  }

  @Test
  public void testSave_InvalidTimerSchedule() {
    Task task = new Task("taskName", "groupName", "beanName");
    task.setTimerSchedule("0 15 * ? * ? *");
    try {
      taskManager.save(task);
      fail("Should not be able to save task with invalid timer schedule");
    } catch (Exception e) {

    }
    assertNull(taskManager.get(task.getId())); // invalid timer schedule, task should not be saved
  }

  @Test(timeout = 15000)
  public void testGetCurrentRunningTaskIds() throws InterruptedException {
    Task task = new Task("name", "groupName", "sysExecJob", false, true, "sleep 5", defaultTimerSchedule);
    taskManager.save(task);
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // give task time to get started
    assertTrue(taskRunManager.isRunning(task.getId()));
    List<Task> tasks = taskManager.getCurrentlyRunningTasks();
    assertEquals(1, tasks.size());
    assertEquals(task, tasks.get(0));
  }

}
