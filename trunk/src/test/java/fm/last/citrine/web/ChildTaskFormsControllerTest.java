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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import fm.last.citrine.model.Task;
import fm.last.citrine.service.TaskManager;

public class ChildTaskFormsControllerTest {

  private ChildTasksFormController childTasksFormController = new ChildTasksFormController();

  @Mock
  private TaskManager mockTaskManager;

  private MockHttpServletRequest mockRequest = new MockHttpServletRequest();

  private MockHttpServletResponse mockResponse = new MockHttpServletResponse();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    childTasksFormController.setTaskManager(mockTaskManager);
  }

  @Test
  public void testCancel() throws Exception {
    mockRequest.setParameter(Constants.PARAM_CANCEL, "true");
    TaskChildCandidatesDTO dto = new TaskChildCandidatesDTO();
    dto.setSelectedGroupName("testGroupName");
    ModelAndView modelAndView = childTasksFormController.processFormSubmission(mockRequest, mockResponse, dto, null);
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("tasks.do?selectedGroupName=testGroupName", view.getUrl());
    assertEquals(0, modelAndView.getModel().size());
  }

  @Test
  public void testProcessFormSubmissionNoChildTasks() throws Exception {
    long taskId = 100;
    Task task = new Task("task100");
    task.setId(taskId);
    when(mockTaskManager.get(taskId)).thenReturn(task);

    TaskChildCandidatesDTO dto = new TaskChildCandidatesDTO();
    dto.setTask(task);
    dto.setSelectedGroupName("testGroupName");
    BindException bindException = new BindException(dto, "bla");
    ModelAndView modelAndView = childTasksFormController.processFormSubmission(mockRequest, mockResponse, dto,
        bindException);
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("tasks.do?selectedGroupName=testGroupName", view.getUrl());
    assertEquals(0, modelAndView.getModel().size());
  }

  @Test
  public void testProcessFormSubmissionCandidateChildTasksOnly() throws Exception {
    long taskId = 100;
    Task task = new Task("task100");
    task.setId(taskId);
    when(mockTaskManager.get(taskId)).thenReturn(task);

    TaskChildCandidatesDTO dto = new TaskChildCandidatesDTO();
    dto.setTask(task);
    dto.setSelectedGroupName("testGroupName");

    long candidateTaskId = 200;
    Task candidateTask = new Task("task200");
    candidateTask.setId(candidateTaskId);
    when(mockTaskManager.get(candidateTaskId)).thenReturn(candidateTask);
    Set<Long> candidateTaskIds = new HashSet<Long>();
    candidateTaskIds.add(candidateTaskId);
    dto.setCandidateChildTaskIds(candidateTaskIds);

    BindException bindException = new BindException(dto, "bla");
    ModelAndView modelAndView = childTasksFormController.processFormSubmission(mockRequest, mockResponse, dto,
        bindException);
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("tasks.do?selectedGroupName=testGroupName", view.getUrl());
    assertEquals(0, modelAndView.getModel().size());

    Set<Task> childTasks = task.getChildTasks();
    assertEquals(1, childTasks.size());
    assertEquals(candidateTask, childTasks.iterator().next());
  }

  @Test
  public void testProcessFormSubmissionChildTasksOnly() throws Exception {
    long taskId = 100;
    Task task = new Task("task100");
    task.setId(taskId);
    when(mockTaskManager.get(taskId)).thenReturn(task);

    TaskChildCandidatesDTO dto = new TaskChildCandidatesDTO();
    dto.setTask(task);
    dto.setSelectedGroupName("testGroupName");

    long childTaskId = 300;
    Task childTask = new Task("task300");
    childTask.setId(childTaskId);
    when(mockTaskManager.get(childTaskId)).thenReturn(childTask);
    Set<Long> childTaskIds = new HashSet<Long>();
    childTaskIds.add(childTaskId);
    dto.setChildTaskIds(childTaskIds);

    BindException bindException = new BindException(dto, "bla");
    ModelAndView modelAndView = childTasksFormController.processFormSubmission(mockRequest, mockResponse, dto,
        bindException);
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("tasks.do?selectedGroupName=testGroupName", view.getUrl());
    assertEquals(0, modelAndView.getModel().size());

    Set<Task> childTasks = task.getChildTasks();
    assertEquals(1, childTasks.size());
    assertEquals(childTask, childTasks.iterator().next());
  }

  @Test
  public void testProcessFormSubmissionCandidateAndChildTasks() throws Exception {
    long taskId = 100;
    Task task = new Task("task100");
    task.setId(taskId);
    when(mockTaskManager.get(taskId)).thenReturn(task);

    TaskChildCandidatesDTO dto = new TaskChildCandidatesDTO();
    dto.setTask(task);
    dto.setSelectedGroupName("testGroupName");

    long candidateTaskId = 200;
    Task candidateTask = new Task("task200");
    candidateTask.setId(candidateTaskId);
    when(mockTaskManager.get(candidateTaskId)).thenReturn(candidateTask);
    Set<Long> candidateTaskIds = new HashSet<Long>();
    candidateTaskIds.add(candidateTaskId);
    dto.setCandidateChildTaskIds(candidateTaskIds);

    long childTaskId = 300;
    Task childTask = new Task("task300");
    childTask.setId(childTaskId);
    when(mockTaskManager.get(childTaskId)).thenReturn(childTask);
    Set<Long> childTaskIds = new HashSet<Long>();
    childTaskIds.add(childTaskId);
    dto.setChildTaskIds(childTaskIds);

    BindException bindException = new BindException(dto, "bla");
    ModelAndView modelAndView = childTasksFormController.processFormSubmission(mockRequest, mockResponse, dto,
        bindException);
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("tasks.do?selectedGroupName=testGroupName", view.getUrl());
    assertEquals(0, modelAndView.getModel().size());

    Set<Task> childTasks = task.getChildTasks();
    assertEquals(2, childTasks.size());
    assertTrue(childTasks.contains(childTask));
    assertTrue(childTasks.contains(candidateTask));
  }

}
