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
package fm.last.citrine.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for Task.
 */
public class TaskTest {

  @Test
  public void testGetParentIds_NoParents() {
    Task task1 = new Task();
    task1.setId(1);
    assertEquals(0, task1.getParentTaskIds(true).size());

    Task task2 = new Task();
    task2.setId(2);
    task1.addChildTask(task2);
    assertEquals(0, task1.getParentTaskIds(true).size());
  }

  @Test
  public void testGetParentIds_SingleParent() {
    Task task1 = new Task();
    task1.setId(1);

    Task task2 = new Task();
    task2.setId(2);
    task1.addChildTask(task2);
    assertEquals(1, task2.getParentTaskIds(true).size());
    assertTrue(task2.getParentTaskIds(true).contains(task1.getId()));
  }

  @Test
  public void testGetParentIds_MutlipleParents() {
    Task task1 = new Task("t1");
    task1.setId(1);
    Task task2 = new Task("t2");
    task2.setId(2);
    Task task3 = new Task("t3");
    task3.setId(3);

    task1.addChildTask(task3);
    task2.addChildTask(task3);

    assertEquals(2, task3.getParentTaskIds(true).size());
    assertTrue(task3.getParentTaskIds(true).contains(task1.getId()));
    assertTrue(task3.getParentTaskIds(true).contains(task2.getId()));

    assertEquals(0, task1.getParentTaskIds(true).size());
    assertEquals(0, task2.getParentTaskIds(true).size());
  }

  @Test
  public void testGetParentIds_MutlipleParentHierarchy() {
    Task task1 = new Task("t1");
    task1.setId(1);
    Task task2 = new Task("t2");
    task2.setId(2);
    Task task3 = new Task("t3");
    task3.setId(3);
    Task task4 = new Task("t4");
    task4.setId(4);
    Task task5 = new Task("t5");
    task5.setId(5);
    Task task6 = new Task("t6");
    task6.setId(6);

    task1.addChildTask(task2);
    task1.addChildTask(task3);
    task2.addChildTask(task4);
    task4.addChildTask(task5);
    task3.addChildTask(task5);
    task6.addChildTask(task5);

    // now check that the parents of each task are what we expect

    assertEquals(0, task1.getParentTaskIds(true).size());

    assertEquals(1, task2.getParentTaskIds(true).size());
    assertTrue(task2.getParentTaskIds(true).contains(task1.getId()));

    assertEquals(1, task3.getParentTaskIds(true).size());
    assertTrue(task3.getParentTaskIds(true).contains(task1.getId()));

    assertEquals(2, task4.getParentTaskIds(true).size());
    assertTrue(task4.getParentTaskIds(true).contains(task1.getId()));
    assertTrue(task4.getParentTaskIds(true).contains(task2.getId()));
    assertEquals(1, task4.getParentTaskIds(false).size());
    assertTrue(task4.getParentTaskIds(false).contains(task2.getId()));

    assertEquals(5, task5.getParentTaskIds(true).size());
    assertTrue(task5.getParentTaskIds(true).contains(task4.getId()));
    assertTrue(task5.getParentTaskIds(true).contains(task2.getId()));
    assertTrue(task5.getParentTaskIds(true).contains(task1.getId()));
    assertTrue(task5.getParentTaskIds(true).contains(task3.getId()));
    assertTrue(task5.getParentTaskIds(true).contains(task6.getId()));
    assertEquals(3, task5.getParentTaskIds(false).size());
    assertTrue(task5.getParentTaskIds(false).contains(task4.getId()));
    assertTrue(task5.getParentTaskIds(false).contains(task3.getId()));
    assertTrue(task5.getParentTaskIds(false).contains(task6.getId()));
  }

}
