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
package fm.last.citrine.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import fm.last.citrine.model.Task;
import fm.last.citrine.scheduler.SchedulerManager;
import fm.last.citrine.scheduler.SchedulerStatus;
import fm.last.citrine.service.TaskManager;

public class AdminControllerTest {

  private AdminController adminController = new AdminController();

  @Mock
  private TaskManager mockTaskManager;

  @Mock
  private SchedulerManager mockSchedulerManager;

  private MockHttpServletRequest mockRequest = new MockHttpServletRequest();

  private MockHttpServletResponse mockResponse = new MockHttpServletResponse();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    adminController.setTaskManager(mockTaskManager);
    adminController.setSchedulerManager(mockSchedulerManager);
  }

  @Test
  public void testList() throws Exception {
    List<Task> runningTasks = new ArrayList<Task>();
    Task task = new Task();
    runningTasks.add(task);
    when(mockTaskManager.getCurrentlyRunningTasks()).thenReturn(runningTasks);
    when(mockSchedulerManager.getStatus()).thenReturn(SchedulerStatus.STARTED);
    ModelAndView modelAndView = adminController.list(mockRequest, mockResponse);
    assertEquals("admin", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(4, model.size());
    assertEquals(runningTasks, model.get("currentTasks"));
    assertEquals(SchedulerStatus.STARTED, model.get("schedulerStatus"));
  }

  @Test
  public void testPrepareForShutdown() throws Exception {
    ModelAndView modelAndView = adminController.prepareForShutdown(mockRequest, mockResponse);
    verify(mockSchedulerManager).prepareForShutdown();
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("admin.do", view.getUrl());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(0, model.size());
  }

}
