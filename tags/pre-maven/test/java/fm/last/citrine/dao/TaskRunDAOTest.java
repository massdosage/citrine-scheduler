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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.transaction.TransactionConfiguration;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.TableConstants;
import fm.last.citrine.model.TaskRun;
import fm.last.test.BaseSpringTestCase;

/**
 * Unit Test case for the TaskRunDAO.
 */
@TransactionConfiguration(defaultRollback = false)
public class TaskRunDAOTest extends BaseSpringTestCase {

  private static Logger log = Logger.getLogger(TaskRunDAOTest.class);

  @Resource
  private TaskRunDAO taskRunDAO;

  @Before
  public void cleanup() {
    deleteFromTables(TableConstants.TABLE_TASK_RUNS);
  }

  @Test
  public void testSaveGetAndUpdate() {
    TaskRun taskRun = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    taskRunDAO.save(taskRun);
    assertTrue(taskRun.getId() > 0);
    assertEquals(0, taskRun.getVersion());

    TaskRun retrieved = taskRunDAO.get(taskRun.getId());
    assertEquals(taskRun, retrieved);

    taskRun.setSysErr("err2");
    taskRunDAO.save(taskRun);

    retrieved = taskRunDAO.get(taskRun.getId());
    assertEquals("err2", retrieved.getSysErr());
  }

  @Test
  public void testFindByTaskId() {
    assertEquals(0, taskRunDAO.findByTaskId(1).size()); // initially should be none in db

    // save two task runs against id 1
    TaskRun taskRun = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    taskRunDAO.save(taskRun);
    TaskRun taskRun2 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    taskRunDAO.save(taskRun2);
    // save one task run against id 2
    TaskRun taskRun3 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 2);
    taskRunDAO.save(taskRun3);

    List<TaskRun> task1Tasks = taskRunDAO.findByTaskId(1);
    assertEquals(2, task1Tasks.size());
    // test ordering
    assertEquals(taskRun2.getId(), task1Tasks.get(0).getId());
    assertEquals(taskRun.getId(), task1Tasks.get(1).getId());

    int currentMaxResults = taskRunDAO.getMaxTaskRunResults();
    taskRunDAO.setMaxTaskRunResults(1); // set limit of results returned to 1
    task1Tasks = taskRunDAO.findByTaskId(1);
    assertEquals(1, task1Tasks.size()); // now only 1 should be returned instead of 2
    taskRunDAO.setMaxTaskRunResults(currentMaxResults); // reset back to original value
  }

  @Test
  public void testDelete() {
    TaskRun taskRun = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    taskRunDAO.save(taskRun);
    assertEquals(1, taskRunDAO.findByTaskId(1).size());
    taskRunDAO.delete(taskRun.getId());
    assertEquals(0, taskRunDAO.findByTaskId(1).size());
  }

  @Test
  public void testDeleteByTaskId() {
    TaskRun taskRun1 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun2 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun3 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 2);
    taskRunDAO.save(taskRun1);
    taskRunDAO.save(taskRun2);
    taskRunDAO.save(taskRun3);
    taskRunDAO.deleteByTaskId(1);
    assertEquals(0, taskRunDAO.findByTaskId(1).size());
    assertEquals(1, taskRunDAO.findByTaskId(2).size());
  }

  @Test
  public void testIsRunning() {
    // for task id 1, put all finished status
    TaskRun taskRun1 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun2 = new TaskRun(Status.CANCELLED, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun3 = new TaskRun(Status.FAILED, new Date(), new Date(), "out", "err", "stack", 1);

    // for task id 2, put 1 run with status running
    TaskRun taskRun4 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 2);
    TaskRun taskRun5 = new TaskRun(Status.RUNNING, new Date(), new Date(), "out", "err", "stack", 2);

    // for task id 2, put 1 run with status initialising
    TaskRun taskRun6 = new TaskRun(Status.INITIALISING, new Date(), new Date(), "out", "err", "stack", 3);

    taskRunDAO.save(taskRun1);
    taskRunDAO.save(taskRun2);
    taskRunDAO.save(taskRun3);
    taskRunDAO.save(taskRun4);
    taskRunDAO.save(taskRun5);
    taskRunDAO.save(taskRun6);

    assertFalse(taskRunDAO.isRunning(1));
    assertTrue(taskRunDAO.isRunning(2));
    assertTrue(taskRunDAO.isRunning(3));
  }

  @Test
  public void testInterruptRunningTasks() {
    TaskRun taskRun1 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun2 = new TaskRun(Status.CANCELLED, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun3 = new TaskRun(Status.FAILED, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun4 = new TaskRun(Status.INTERRUPTED, new Date(), new Date(), "out", "err", "stack", 2);
    TaskRun taskRun5 = new TaskRun(Status.RUNNING, new Date(), new Date(), "out", "err", "stack", 2);
    TaskRun taskRun6 = new TaskRun(Status.INITIALISING, new Date(), new Date(), "out", "err", "stack", 3);
    TaskRun taskRun7 = new TaskRun(Status.CANCELLING, new Date(), new Date(), "out", "err", "stack", 3);
    taskRunDAO.save(taskRun1);
    taskRunDAO.save(taskRun2);
    taskRunDAO.save(taskRun3);
    taskRunDAO.save(taskRun4);
    taskRunDAO.save(taskRun5);
    taskRunDAO.save(taskRun6);
    taskRunDAO.save(taskRun7);
    taskRunDAO.setInterruptedStatus();
    assertEquals(Status.SUCCESS, taskRunDAO.get(taskRun1.getId()).getStatus());
    assertEquals(Status.CANCELLED, taskRunDAO.get(taskRun2.getId()).getStatus());
    assertEquals(Status.FAILED, taskRunDAO.get(taskRun3.getId()).getStatus());
    assertEquals(Status.INTERRUPTED, taskRunDAO.get(taskRun4.getId()).getStatus());
    assertEquals(Status.INTERRUPTED, taskRunDAO.get(taskRun5.getId()).getStatus());
    assertEquals(Status.INTERRUPTED, taskRunDAO.get(taskRun6.getId()).getStatus());
    assertEquals(Status.INTERRUPTED, taskRunDAO.get(taskRun7.getId()).getStatus());
  }

  /**
   * Tests getting the most recent TaskRun for a task where there are no TaskRuns.
   */
  @Test
  public void testGetMostRecentTaskRun_None() {
    assertEquals(null, taskRunDAO.getMostRecentTaskRun(1));
  }

  /**
   * Tests getting the most recent TaskRun for a Task.
   */
  @Test
  public void testGetMostRecentTaskRun() {
    Calendar calendar = Calendar.getInstance();
    Date now = new Date();
    TaskRun taskRun1 = new TaskRun(Status.RUNNING, now, null, null, null, null, 1);
    calendar.set(2005, 11, 27);
    TaskRun taskRun2 = new TaskRun(Status.SUCCESS, calendar.getTime(), calendar.getTime(), "out", "err", "stack", 1);
    calendar.set(2006, 11, 27);
    TaskRun taskRun3 = new TaskRun(Status.FAILED, calendar.getTime(), calendar.getTime(), "out", "err", "stack", 1);
    // same start date but different task id
    TaskRun taskRun4 = new TaskRun(Status.RUNNING, now, null, "out", "err", "stack", 2);
    taskRunDAO.save(taskRun2);
    taskRunDAO.save(taskRun1); // put save in middle so it's id is not necessarily first/last
    taskRunDAO.save(taskRun3);
    taskRunDAO.save(taskRun4);
    TaskRun retrieved = taskRunDAO.getMostRecentTaskRun(1);
    assertEquals(taskRun1, retrieved);
  }

  @Test
  public void testFindByTaskId_FirstAndMax() {
    TaskRun taskRun1 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun2 = new TaskRun(Status.CANCELLED, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun3 = new TaskRun(Status.FAILED, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun4 = new TaskRun(Status.SUCCESS, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun5 = new TaskRun(Status.RUNNING, new Date(), new Date(), "out", "err", "stack", 1);
    TaskRun taskRun6 = new TaskRun(Status.INITIALISING, new Date(), new Date(), "out", "err", "stack", 1);

    taskRunDAO.save(taskRun1);
    taskRunDAO.save(taskRun2);
    taskRunDAO.save(taskRun3);
    taskRunDAO.save(taskRun4);
    taskRunDAO.save(taskRun5);
    taskRunDAO.save(taskRun6);
    // first do some sanity checks
    assertEquals(6, taskRunDAO.findByTaskId(1, 0, 6).size()); // get all
    assertEquals(6, taskRunDAO.findByTaskId(1, 0, 100).size()); // set max to higher than number in db
    assertEquals(4, taskRunDAO.findByTaskId(1, 2, 10).size()); // get a few inbetween

    // now lets inspect one list of results in more detail
    List<TaskRun> taskRuns = taskRunDAO.findByTaskId(1, 2, 2);
    assertEquals(2, taskRuns.size());
    // now check that this contains task runs 4 and 3 (ordered desc by id)
    assertEquals(taskRun4.getId(), taskRuns.get(0).getId());
    assertEquals(taskRun3.getId(), taskRuns.get(1).getId());
  }

  @Test
  public void testDeleteBefore() {
    DateTime now = new DateTime();
    DateTime threeDaysAgo = now.minusDays(3);
    DateTime tenDaysAgo = now.minusDays(10);
    DateTime twentyDaysAgo = now.minusDays(20);
    DateTime thirtyDaysAgo = now.minusDays(30);

    int taskId = 1;
    TaskRun taskRun1 = new TaskRun(Status.SUCCESS, now.toDate(), now.toDate(), "out", "err", "stack", taskId);
    TaskRun taskRun2 = new TaskRun(Status.SUCCESS, threeDaysAgo.toDate(), now.toDate(), "out", "err", "stack", taskId);
    TaskRun taskRun3 = new TaskRun(Status.SUCCESS, tenDaysAgo.toDate(), threeDaysAgo.toDate(), "out", "err", "stack",
        taskId);
    TaskRun taskRun4 = new TaskRun(Status.SUCCESS, twentyDaysAgo.toDate(), tenDaysAgo.toDate(), "out", "err", "stack",
        taskId);

    taskRunDAO.save(taskRun1);
    taskRunDAO.save(taskRun2);
    taskRunDAO.save(taskRun3);
    taskRunDAO.save(taskRun4);

    assertEquals(4, taskRunDAO.findByTaskId(taskId).size());

    // delete before a date that matches no task runs
    taskRunDAO.deleteBefore(thirtyDaysAgo);
    assertEquals(4, taskRunDAO.findByTaskId(taskId).size());

    // delete before a date that matches no task run end date (and only task run 4's start date)
    taskRunDAO.deleteBefore(twentyDaysAgo);
    assertEquals(4, taskRunDAO.findByTaskId(taskId).size());

    // delete before a date that matches taskRun4 end date
    taskRunDAO.deleteBefore(tenDaysAgo);
    List<TaskRun> retrieved = taskRunDAO.findByTaskId(taskId);
    assertEquals(3, retrieved.size());
    assertTrue(retrieved.contains(taskRun1));
    assertTrue(retrieved.contains(taskRun2));
    assertTrue(retrieved.contains(taskRun3));

    // delete before a date that matches the rest
    taskRunDAO.deleteBefore(now);
    retrieved = taskRunDAO.findByTaskId(taskId);
    assertEquals(0, retrieved.size());
  }

}
