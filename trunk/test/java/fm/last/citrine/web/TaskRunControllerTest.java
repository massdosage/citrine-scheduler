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
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import fm.last.citrine.model.TaskRun;
import fm.last.citrine.service.LogFileManager;
import fm.last.citrine.service.TaskManager;
import fm.last.citrine.service.TaskRunManager;

public class TaskRunControllerTest {

  private TaskRunController taskRunController = new TaskRunController();

  @Mock
  private TaskRunManager mockTaskRunManager;
  @Mock
  private TaskManager mockTaskManager;
  @Mock
  private LogFileManager mockLogFileManager;

  private MockHttpServletRequest mockRequest = new MockHttpServletRequest();

  private MockHttpServletResponse mockResponse = new MockHttpServletResponse();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    taskRunController.setTaskManager(mockTaskManager);
    taskRunController.setTaskRunManager(mockTaskRunManager);
    taskRunController.setLogFileManager(mockLogFileManager);
  }

  @Test(expected = ServletException.class)
  public void testDeleteNoTaskId() throws Exception {
    long taskRunId = 8889;
    mockRequest.addParameter(Constants.PARAM_TASK_RUN_ID, String.valueOf(taskRunId));
    taskRunController.delete(mockRequest, mockResponse);
  }

  @Test
  public void testDelete() throws Exception {
    long taskRunId = 8889;
    mockRequest.addParameter(Constants.PARAM_TASK_RUN_ID, String.valueOf(taskRunId));
    long taskId = 345;
    mockRequest.addParameter(Constants.PARAM_TASK_ID, String.valueOf(taskId));
    ModelAndView modelAndView = taskRunController.delete(mockRequest, mockResponse);
    verify(mockTaskRunManager).delete(taskRunId);
    assertEquals("task_runs_list", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(taskId, model.get("taskId"));
    assertEquals(0, (((List<TaskRun>) model.get("taskRuns")).size()));
    assertEquals(0, (((Map<Long, String>) model.get("taskRunLogs")).size()));
    assertEquals(null, model.get("selectedGroupName"));
  }

  @Test(expected = ServletException.class)
  public void testStopNoTaskId() throws Exception {
    long taskRunId = 8889;
    mockRequest.addParameter(Constants.PARAM_TASK_RUN_ID, String.valueOf(taskRunId));
    taskRunController.stop(mockRequest, mockResponse);
  }

  @Test
  public void testStop() throws Exception {
    long taskRunId = 8889;
    mockRequest.addParameter(Constants.PARAM_TASK_RUN_ID, String.valueOf(taskRunId));
    long taskId = 345;
    mockRequest.addParameter(Constants.PARAM_TASK_ID, String.valueOf(taskId));
    ModelAndView modelAndView = taskRunController.stop(mockRequest, mockResponse);
    verify(mockTaskRunManager).stop(taskRunId);
    assertEquals("task_runs_list", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(taskId, model.get("taskId"));
    assertEquals(0, (((List<TaskRun>) model.get("taskRuns")).size()));
    assertEquals(0, (((Map<Long, String>) model.get("taskRunLogs")).size()));
    assertEquals(null, model.get("selectedGroupName"));
  }

  // TODO: test list with varying inputs

}
