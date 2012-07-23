/*
 * Copyright 2011 Last.fm
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
package fm.last.citrine.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.Task;
import fm.last.citrine.model.TaskRun;
import fm.last.citrine.scheduler.SchedulerManager;
import fm.last.citrine.scheduler.SchedulerStatus;
import fm.last.citrine.service.TaskManager;
import fm.last.citrine.service.TaskRunManager;

public class TaskControllerTest {

  private final TaskController taskController = new TaskController();

  @Mock
  private TaskManager mockTaskManager;
  @Mock
  private TaskRunManager mockTaskRunManager;
  @Mock
  private SchedulerManager mockSchedulerManager;

  private final MockHttpServletRequest mockRequest = new MockHttpServletRequest();

  private final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    when(mockSchedulerManager.getStatus()).thenReturn(SchedulerStatus.STARTED);

    taskController.setTaskManager(mockTaskManager);
    taskController.setTaskRunManager(mockTaskRunManager);
    taskController.setSchedulerManager(mockSchedulerManager);
  }

  @Test
  public void testListNoTasksOrGroupNames() throws Exception {
    Map<String, Object> model = getModel();
    assertEquals(SchedulerStatus.STARTED, model.get("schedulerStatus"));
    assertEquals(Constants.GROUP_NAME_ALL, model.get("selectedGroupName"));
    assertEquals(0, ((Set<String>) model.get("groupNames")).size());
    assertEquals(0, ((Map<Long, String>) model.get("recentStatus")).size());
    assertEquals(0, ((List<Task>) model.get("tasks")).size());
    assertEquals(0, ((Map<Long, String>) model.get("lastRun")).size());
  }

  @Test
  public void testTask() throws Exception {
    List<Task> tasks = new ArrayList<Task>();
    tasks.add(new Task("task1"));
    when(mockTaskManager.getTasks()).thenReturn(tasks);
    Map<String, Object> model = getModel();
    assertEquals(SchedulerStatus.STARTED, model.get("schedulerStatus"));
    assertEquals(Constants.GROUP_NAME_ALL, model.get("selectedGroupName"));
    Map<Long, String> recentStatusMap = (Map<Long, String>) model.get("recentStatus");
    assertEquals(1, recentStatusMap.size());
    assertEquals(Status.UNKNOWN.toString().toLowerCase(), recentStatusMap.get(0L));
    List<Task> tasksMapMap = (List<Task>) model.get("tasks");
    assertEquals(1, tasksMapMap.size());
    assertEquals(tasks.get(0), tasksMapMap.get(0));
    Map<Long, String> lastRunMap = (Map<Long, String>) model.get("lastRun");
    assertEquals(1, lastRunMap.size());
    assertEquals("Never", lastRunMap.get(0L));
  }

  @Test
  public void testTaskDisabled() throws Exception {
    List<Task> tasks = new ArrayList<Task>();
    Task task = new Task("task1");
    task.setEnabled(false);
    tasks.add(task);
    when(mockTaskManager.getTasks()).thenReturn(tasks);
    Map<String, Object> model = getModel();
    Map<Long, String> recentStatusMap = (Map<Long, String>) model.get("recentStatus");
    assertEquals(1, recentStatusMap.size());
    assertEquals(TaskController.TASK_STATUS_DISABLED, recentStatusMap.get(0L));
  }

  @Test
  public void testLastRun() throws Exception {
    List<Task> tasks = new ArrayList<Task>();
    Task task = new Task("task1");
    tasks.add(task);
    when(mockTaskManager.getTasks()).thenReturn(tasks);
    DateTime startDate = new DateTime().minusDays(1);
    TaskRun taskRun = new TaskRun(startDate.toDate(), startDate.plusHours(2).toDate(), "", "", "", task.getId());
    when(mockTaskRunManager.getMostRecent(task.getId())).thenReturn(taskRun);
    Map<String, Object> model = getModel();
    Map<Long, String> lastRunMap = (Map<Long, String>) model.get("lastRun");
    assertEquals(1, lastRunMap.size());
    assertEquals("1 day ago", lastRunMap.get(0L));
  }

  private Map<String, Object> getModel() throws Exception {
    ModelAndView modelAndView = taskController.list(mockRequest, mockResponse);
    assertEquals("tasks_list", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    return model;
  }

  // TODO: test with a selected group name
  // TODO: test with the "all" selected group name
  // TODO: select with no group name returning tasks with group names
  // TODO: test with a long description that should get truncated

}
