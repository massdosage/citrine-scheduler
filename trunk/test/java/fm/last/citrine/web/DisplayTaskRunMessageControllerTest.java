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
import static org.mockito.Mockito.when;

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
import fm.last.citrine.service.TaskRunManager;

public class DisplayTaskRunMessageControllerTest {

  private DisplayTaskRunMessageController displayTaskRunMessageController = new DisplayTaskRunMessageController();

  @Mock
  private TaskRunManager mockTaskRunManager;

  private MockHttpServletRequest mockRequest = new MockHttpServletRequest();

  private MockHttpServletResponse mockResponse = new MockHttpServletResponse();

  private TaskRun taskRun = new TaskRun();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    displayTaskRunMessageController.setTaskRunManager(mockTaskRunManager);
    taskRun.setId(1000);
    taskRun.setStackTrace("strack trace");
    taskRun.setSysOut("sys out");
    taskRun.setSysErr("sys err");
    when(mockTaskRunManager.get(taskRun.getId())).thenReturn(taskRun);

  }

  @Test(expected = ServletException.class)
  public void testDisplayStackNoTaskRunId() throws Exception {
    displayTaskRunMessageController.displayStack(mockRequest, mockResponse);
  }

  @Test(expected = ServletException.class)
  public void testDisplaySysOutNoTaskRunId() throws Exception {
    displayTaskRunMessageController.displaySysOut(mockRequest, mockResponse);
  }

  @Test(expected = ServletException.class)
  public void testDisplaySysErrNoTaskRunId() throws Exception {
    displayTaskRunMessageController.displaySysErr(mockRequest, mockResponse);
  }

  @Test
  public void testDisplayStack() throws Exception {
    mockRequest.addParameter(Constants.PARAM_TASK_RUN_ID, String.valueOf(taskRun.getId()));
    ModelAndView modelAndView = displayTaskRunMessageController.displayStack(mockRequest, mockResponse);
    assertEquals("display_task_run_message", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(1, model.size());
    assertEquals(taskRun.getStackTrace(), model.get("message"));
  }

  @Test
  public void testDisplaySysOut() throws Exception {
    mockRequest.addParameter(Constants.PARAM_TASK_RUN_ID, String.valueOf(taskRun.getId()));
    ModelAndView modelAndView = displayTaskRunMessageController.displaySysOut(mockRequest, mockResponse);
    assertEquals("display_task_run_message", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(1, model.size());
    assertEquals(taskRun.getSysOut(), model.get("message"));
  }

  @Test
  public void testDisplaySysErr() throws Exception {
    mockRequest.addParameter(Constants.PARAM_TASK_RUN_ID, String.valueOf(taskRun.getId()));
    ModelAndView modelAndView = displayTaskRunMessageController.displaySysErr(mockRequest, mockResponse);
    assertEquals("display_task_run_message", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(1, model.size());
    assertEquals(taskRun.getSysErr(), model.get("message"));
  }


}
