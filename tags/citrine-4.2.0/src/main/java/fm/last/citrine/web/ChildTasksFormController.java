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

import static fm.last.citrine.web.Constants.PARAM_CANCEL;
import static fm.last.citrine.web.Constants.PARAM_TASK_ID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import fm.last.citrine.model.Task;
import fm.last.citrine.service.TaskManager;

/**
 * Controller which manages the links between a Task and it's child Tasks.
 */
public class ChildTasksFormController extends SimpleFormController {

  private static Logger log = Logger.getLogger(ChildTasksFormController.class);

  private TaskManager taskManager;

  private String getSuccessView(String selectedGroupName) {
    if (StringUtils.isEmpty(selectedGroupName)) {
      selectedGroupName = Constants.GROUP_NAME_ALL;
    }
    return "tasks.do?" + Constants.PARAM_SELECTED_GROUP_NAME + "=" + selectedGroupName;
  }

  @Override
  protected Map<String, Object> referenceData(HttpServletRequest request, Object command, Errors errors)
    throws Exception {
    Map<String, Object> referenceData = new HashMap<String, Object>();
    Task task = ((TaskChildCandidatesDTO) command).getTask();
    referenceData.put("childTasks", task.getChildTasks()); // existing child tasks
    referenceData.put("candidateChildTasks", taskManager.findCandidateChildren(task)); // potential child tasks
    referenceData.put(Constants.PARAM_SELECTED_GROUP_NAME, request.getParameter(Constants.PARAM_SELECTED_GROUP_NAME));
    return referenceData;
  }

  @Override
  protected Object formBackingObject(HttpServletRequest request) throws ServletException {
    String idString = request.getParameter(PARAM_TASK_ID);
    TaskChildCandidatesDTO backingObject = null;
    if (request.getParameter(PARAM_CANCEL) == null && null != idString && !idString.equals("") && !idString.equals("0")) {
      long taskId = Long.parseLong(idString);
      Task task = taskManager.get(taskId);
      backingObject = new TaskChildCandidatesDTO(task);
    } else {
      backingObject = new TaskChildCandidatesDTO();
    }
    String selectedGroupName = request.getParameter(Constants.PARAM_SELECTED_GROUP_NAME);
    if (selectedGroupName != null && !(Constants.GROUP_NAME_ALL.equals(selectedGroupName))) {
      backingObject.setSelectedGroupName(selectedGroupName);
    }
    return backingObject;
  }

  @Override
  public ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command,
      BindException errors) throws Exception {
    if (request.getParameter(PARAM_CANCEL) != null) {
      TaskChildCandidatesDTO dto = ((TaskChildCandidatesDTO) command);
      return new ModelAndView(new RedirectView(getSuccessView(dto.getSelectedGroupName())));
    }
    return super.processFormSubmission(request, response, command, errors);
  }

  @Override
  public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
      BindException errors) {
    TaskChildCandidatesDTO dto = ((TaskChildCandidatesDTO) command);
    Task task = taskManager.get(dto.getTask().getId());

    Set<Task> newChildTasks = new HashSet<Task>(); // list which will replace existing child tasks
    if (dto.getCandidateChildTaskIds() != null) {
      for (long candidateTaskId : dto.getCandidateChildTaskIds()) {
        newChildTasks.add(taskManager.get(candidateTaskId));
      }
    }

    if (dto.getChildTaskIds() != null) {
      for (long candidateTaskId : dto.getChildTaskIds()) {
        newChildTasks.add(taskManager.get(candidateTaskId));
      }
    }

    task.setChildTasks(newChildTasks);
    taskManager.save(task);
    return new ModelAndView(new RedirectView(getSuccessView(dto.getSelectedGroupName())));
  }

  /**
   * @return the jobManager
   */
  public TaskManager getTaskManager() {
    return taskManager;
  }

  /**
   * @param jobManager the jobManager to set
   */
  public void setTaskManager(TaskManager jobManager) {
    this.taskManager = jobManager;
  }

}
