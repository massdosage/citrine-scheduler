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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import fm.last.citrine.model.Task;
import fm.last.citrine.scheduler.SchedulerManager;
import fm.last.citrine.scheduler.SchedulerStatus;
import fm.last.citrine.service.TaskManager;
import fm.last.citrine.service.TaskRunManager;

public class TaskControllerTest {

  private TaskController taskController = new TaskController();

  @Mock
  private TaskManager mockTaskManager;
  @Mock
  private TaskRunManager mockTaskRunManager;
  @Mock
  private SchedulerManager mockSchedulerManager;

  private MockHttpServletRequest mockRequest = new MockHttpServletRequest();

  private MockHttpServletResponse mockResponse = new MockHttpServletResponse();

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
    ModelAndView modelAndView = taskController.list(mockRequest, mockResponse);
    assertEquals("tasks_list", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(SchedulerStatus.STARTED, model.get("schedulerStatus"));
    assertEquals(Constants.GROUP_NAME_ALL, model.get("selectedGroupName"));
    assertEquals(0, ((Set<String>) model.get("groupNames")).size());
    assertEquals(0, ((Map<Long, String>) model.get("recentStatus")).size());
    assertEquals(0, ((List<Task>) model.get("tasks")).size());
  }

  // TODO: test with a selected group name
  // TODO: test with the "all" selected group name
  // TODO: select with no group name returning tasks with group names
  // TODO: test with a long description that should get truncated
  // TODO: test most recent status


}
