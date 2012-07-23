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
package fm.last.citrine.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.transaction.TransactionConfiguration;

import fm.last.citrine.model.Notification;
import fm.last.citrine.model.TableConstants;
import fm.last.citrine.model.Task;
import fm.last.test.BaseSpringTestCase;

/**
 * Unit test case for the TaskDAO.
 */
@TransactionConfiguration(defaultRollback = false)
public class TaskDAOTest extends BaseSpringTestCase {

  private static Logger log = Logger.getLogger(TaskDAOTest.class);

  private String defaultTimerSchedule = "0 15 10 ? * 6L 3002-3005"; // a timer schedule string that should never run

  // Tasks which are used for child/parent tests
  private Task task1;
  private Task task2;
  private Task task3;
  private Task task4;

  @Resource
  private TaskDAO taskDAO;

  @After
  @Before
  public void cleanup() {
    super.cleanupTaskTables();
  }

  /**
   * Convenience method for creating a set of Task where Task 1 has 2 children - 2 and 3, and 2 and 3 in turn have the
   * same child, 4.
   */
  private void createChildParentTasks() {
    task1 = new Task("TaskDAOTest1", "childTest", "1", false, true, "1", defaultTimerSchedule);
    taskDAO.save(task1);
    task2 = new Task("TaskDAOTest2", "childTest", "2", false, true, "2", defaultTimerSchedule);
    taskDAO.save(task2);
    task3 = new Task("TaskDAOTest3", "childTest", "3", false, true, "3", defaultTimerSchedule);
    taskDAO.save(task3);
    task4 = new Task("TaskDAOTest4", "childTest", "4", false, true, "4", defaultTimerSchedule);
    taskDAO.save(task4);

    task1.addChildTask(task2);
    task1.addChildTask(task3);
    taskDAO.save(task1);

    task2.addChildTask(task4);
    taskDAO.save(task2);
    task3.addChildTask(task4);
    taskDAO.save(task3);
  }

  @Test
  public void testSaveGetAndUpdate() {
    Task task = new Task("name", "groupName", "sysExecTask", true, true, "command", defaultTimerSchedule);
    taskDAO.save(task);
    assertTrue(task.getId() > 0);
    assertEquals(0, task.getVersion());
    assertEquals(true, task.isEnabled());

    Task retrieved = taskDAO.get(task.getId());
    assertEquals(task, retrieved);

    task.setEnabled(false);
    taskDAO.save(task);

    retrieved = taskDAO.get(task.getId());
    assertEquals(false, task.isEnabled());
    assertNotNull(task.getNotification());
  }

  @Test
  public void testSaveGetAndUpdate_AllFields() {
    Task task = new Task("name", "groupName", "sysExecTask", true, true, "command", defaultTimerSchedule);
    task.setPriority(100);
    task.setDescription("description");
    taskDAO.save(task);
    assertTrue(task.getId() > 0);
    assertEquals(0, task.getVersion());

    Task retrieved = taskDAO.get(task.getId());
    assertEquals(task, retrieved);
  }

  @Test
  public void testGetTasks() {
    assertEquals(0, taskDAO.getTasks().size()); // no Tasks in DB to start with
    Task task = new Task("name", "groupName", "sysExecTask", false, true, "command", defaultTimerSchedule);
    taskDAO.save(task);
    assertEquals(1, taskDAO.getTasks().size());

    Task taskB = new Task("name2", "groupName2", "sysExecTask2", false, true, "command2", defaultTimerSchedule);
    taskDAO.save(taskB);

    List<Task> retrieved = taskDAO.getTasks();
    assertEquals(2, retrieved.size());

    // now check that they are ordered by id asb
    long previousId = -1;
    for (Task retrievedTask : retrieved) {
      long currentId = retrievedTask.getId();
      assertTrue(currentId > previousId);
      previousId = currentId;
    }
  }

  @Test
  public void testDelete() {
    Task task = new Task("name", "groupName", "sysExecTask", false, true, "command", defaultTimerSchedule);
    taskDAO.save(task);
    assertEquals(1, taskDAO.getTasks().size());
    taskDAO.delete(task);
    assertEquals(0, taskDAO.getTasks().size());
  }

  @Test
  public void testFindByGroup() {
    Task taskA = new Task("name", "groupA", "sysExecTask", false, true, "command", defaultTimerSchedule);
    taskDAO.save(taskA);
    Task taskB1 = new Task("name2", "groupB", "sysExecTask2", false, true, "command2", defaultTimerSchedule);
    taskDAO.save(taskB1);
    Task taskB2 = new Task("name3", "groupB", "sysExecTask3", false, true, "command3", defaultTimerSchedule);
    taskDAO.save(taskB2);

    List<Task> retrieved = taskDAO.findByGroup(taskA.getGroupName());
    assertEquals(1, retrieved.size());
    assertEquals(taskA, retrieved.get(0));

    retrieved = taskDAO.findByGroup(taskB1.getGroupName());
    assertEquals(2, retrieved.size());
    for (Task task : retrieved) {
      assertTrue(taskB1.equals(task) || taskB2.equals(task));
    }
  }

  @Test
  public void testSaveDependentTask() {
    createChildParentTasks();

    // check that 1 has no parents and has 2,3 as children
    Task retrieved = taskDAO.get(task1.getId());
    assertEquals(0, retrieved.getParentTasks().size());
    assertEquals(2, retrieved.getChildTasks().size());
    for (Task child : retrieved.getChildTasks()) {
      assertTrue(task2.getId() == child.getId() || task3.getId() == child.getId());
    }

    // assert that 1 is parent of 2 and 4 is it's child
    retrieved = taskDAO.get(task2.getId());
    assertEquals(1, retrieved.getParentTasks().size());
    assertEquals(task1.getId(), retrieved.getParentTasks().iterator().next().getId());
    assertEquals(1, retrieved.getChildTasks().size());
    assertEquals(task4.getId(), retrieved.getChildTasks().iterator().next().getId());

    // assert that 1 is parent of 3 and 4 is it's child
    retrieved = taskDAO.get(task3.getId());
    assertEquals(1, retrieved.getParentTasks().size());
    assertEquals(task1.getId(), retrieved.getParentTasks().iterator().next().getId());
    assertEquals(1, retrieved.getChildTasks().size());
    assertEquals(task4.getId(), retrieved.getChildTasks().iterator().next().getId());

    // assert that 4 has no children and that 2 and 3 are its parents
    retrieved = taskDAO.get(task4.getId());
    assertEquals(2, retrieved.getParentTasks().size());
    for (Task parent : retrieved.getParentTasks()) {
      assertTrue(task2.getId() == parent.getId() || task3.getId() == parent.getId());
    }
    assertEquals(0, retrieved.getChildTasks().size());
  }

  /*
   * Test deleting 3 -> 4 should now only have 2 as parent, 1 should only have 3 as child; delete 1 -> 2 should have no
   * parent, 4 as child; delete 4 -> 2 should have no children; delete 2 -> join table should be empty
   */
  @Test
  public void testDeleteDependentTasks() {
    createChildParentTasks();

    taskDAO.delete(task3);
    Task retrieved = taskDAO.get(task1.getId());
    assertEquals(1, retrieved.getChildTasks().size());
    assertEquals(task2.getId(), retrieved.getChildTasks().iterator().next().getId());
    retrieved = taskDAO.get(task4.getId());
    assertEquals(1, retrieved.getParentTasks().size());
    assertEquals(task2.getId(), retrieved.getParentTasks().iterator().next().getId());

    taskDAO.delete(task1);
    retrieved = taskDAO.get(task2.getId());
    assertEquals(0, retrieved.getParentTasks().size());
    assertEquals(1, retrieved.getChildTasks().size());
    assertEquals(task4.getId(), retrieved.getChildTasks().iterator().next().getId());

    taskDAO.delete(task4);
    retrieved = taskDAO.get(task2.getId());
    assertEquals(0, retrieved.getParentTasks().size());
    assertEquals(0, retrieved.getChildTasks().size());

    taskDAO.delete(task2);
    assertEquals(0, taskDAO.getTasks().size());

    // double check to make sure that all links are gone from join table
    assertEquals(0, simpleJdbcTemplate.queryForInt("select count(*) from " + TableConstants.TABLE_TASK_CHILD_TASK));
  }

  @Test
  public void testGetCandidateChildren() {
    createChildParentTasks();
    Task task5 = new Task("5", task4.getGroupName(), "5", false, true, "5", defaultTimerSchedule);
    taskDAO.save(task5);
    Task task6 = new Task("6", "differentGroup", "6", false, true, "6", defaultTimerSchedule);
    taskDAO.save(task6);

    // nobody else is in the same group as 6, so should be no candidates
    Set<Task> candidates = taskDAO.findCandidateChildren(task6);
    assertEquals(0, candidates.size());

    candidates = taskDAO.findCandidateChildren(task4);
    assertEquals(1, candidates.size());
    assertTrue(candidates.contains(task5));

    // now join 4 and 5
    task4.addChildTask(task5);
    taskDAO.save(task4);

    candidates = taskDAO.findCandidateChildren(task4);
    assertEquals(0, candidates.size());
    candidates = taskDAO.findCandidateChildren(task5);
    assertEquals(0, candidates.size());

    candidates = taskDAO.findCandidateChildren(task2);
    assertEquals(2, candidates.size());
    assertTrue(candidates.contains(task5));
    assertTrue(candidates.contains(task3));

    candidates = taskDAO.findCandidateChildren(task3);
    assertEquals(2, candidates.size());
    assertTrue(candidates.contains(task5));
    assertTrue(candidates.contains(task2));

    candidates = taskDAO.findCandidateChildren(task1);
    assertEquals(2, candidates.size());
    assertTrue(candidates.contains(task5));
    assertTrue(candidates.contains(task4));
  }

  @Test
  public void testGetCandidateChildrenParentOfParent() {
    Task child1 = new Task("child1", "g", "1", false, true, "1", defaultTimerSchedule);
    taskDAO.save(child1);
    Task child2 = new Task("child2", "g", "2", false, true, "2", defaultTimerSchedule);
    taskDAO.save(child2);
    Task child3 = new Task("child3", "g", "3", false, true, "3", defaultTimerSchedule);
    taskDAO.save(child3);
    Task parent = new Task("parent", "g", "4", false, true, "4", defaultTimerSchedule);
    taskDAO.save(parent);
    Task parentParent = new Task("parentparent", "g", "5", false, true, "5", defaultTimerSchedule);
    taskDAO.save(parentParent);

    parent.addChildTask(child1);
    parent.addChildTask(child2);
    parent.addChildTask(child3);
    taskDAO.save(parent);

    Set<Task> candidateChildren = taskDAO.findCandidateChildren(parentParent);
    assertEquals(4, candidateChildren.size());
    assertTrue(candidateChildren.contains(child1));
    assertTrue(candidateChildren.contains(child2));
    assertTrue(candidateChildren.contains(child3));
    assertTrue(candidateChildren.contains(parent));
  }

  @Test
  public void testSaveGetAndUpdate_Notification() {
    Task task = new Task("name", "groupName", "sysExecTask", true, true, "command", defaultTimerSchedule);
    Notification notification = new Notification("recipients", false, false);
    task.setNotification(notification);
    taskDAO.save(task);
    assertTrue(task.getId() > 0);
    assertEquals(0, task.getVersion());

    Task retrieved = taskDAO.get(task.getId());
    assertEquals(task, retrieved);
    assertEquals(notification, retrieved.getNotification());
  }

  @Test
  public void testGetGroupNames() {
    Task task1 = new Task("child1", "g1", "1", false, true, "1", defaultTimerSchedule);
    taskDAO.save(task1);
    Task task2 = new Task("child2", "g2", "2", false, true, "2", defaultTimerSchedule);
    taskDAO.save(task2);
    Task task3 = new Task("child3", "g1", "3", false, true, "3", defaultTimerSchedule);
    taskDAO.save(task3);
    Task task4 = new Task("parent", "g3", "4", false, true, "4", defaultTimerSchedule);
    taskDAO.save(task4);
    Set<String> groupNames = taskDAO.getGroupNames();
    assertEquals(3, groupNames.size());
    // convert from set back to list so we can easily check ordering without having to iterate etc.
    List<String> groupList = new ArrayList<String>(groupNames);
    assertEquals(task1.getGroupName(), groupList.get(0));
    assertEquals(task2.getGroupName(), groupList.get(1));
    assertEquals(task4.getGroupName(), groupList.get(2));
  }
  
  @Test
  public void testGetNonExistent() {
    assertNull(taskDAO.get(9999));
  }

}
