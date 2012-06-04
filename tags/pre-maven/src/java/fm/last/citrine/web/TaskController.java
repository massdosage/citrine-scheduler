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

import static fm.last.citrine.web.Constants.PARAM_TASK_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.Task;
import fm.last.citrine.model.TaskRun;
import fm.last.citrine.scheduler.SchedulerManager;
import fm.last.citrine.service.TaskManager;
import fm.last.citrine.service.TaskRunManager;

/**
 * Controller that handles listing and running of Tasks.
 */
public class TaskController extends MultiActionController {

  private static Logger log = Logger.getLogger(TaskController.class);

  private TaskManager taskManager;
  private TaskRunManager taskRunManager;

  private SchedulerManager schedulerManager;

  /**
   * Fetches a List of tasks and adds group-related entries to the model based on the passed group name.
   * 
   * @param selectedGroupName Currently selected group name.
   * @param model Model to add group-related entries to.
   * @return A List of Tasks related to the passed selected group name.
   */
  private List<Task> handleGroupNames(String selectedGroupName, Map<String, Object> model) {
    List<Task> tasks = null;
    Set<String> groupNames = new TreeSet<String>();
    if (StringUtils.isEmpty(selectedGroupName) || (Constants.GROUP_NAME_ALL.equals(selectedGroupName))) {
      tasks = taskManager.getTasks();
      // no need to query for group names as we have all tasks, so just iterate thru them
      for (Task task : tasks) {
        groupNames.add(task.getGroupName());
      }
      selectedGroupName = Constants.GROUP_NAME_ALL;
    } else {
      // we only have selected group name, need to get rest of them for dropdown
      groupNames = taskManager.getGroupNames();
      if (groupNames.contains(selectedGroupName)) {
        tasks = taskManager.findByGroup(selectedGroupName);
      } else { // selected group no longer exists, revert to all
        selectedGroupName = Constants.GROUP_NAME_ALL;
        tasks = taskManager.getTasks();
      }
    }
    model.put(Constants.PARAM_SELECTED_GROUP_NAME, selectedGroupName);
    model.put("groupNames", groupNames);
    return tasks;
  }

  /**
   * Performs processing on the passed Tasks in order to prepare them for viewing.
   * 
   * @param tasks List of tasks to process.
   * @param model Model to add task-related entries to.
   */
  private void processTasks(List<Task> tasks, Map<String, Object> model) {
    Map<Long, String> taskRunStatus = new HashMap<Long, String>();
    for (Task task : tasks) {

      // limit the description text based in the gui (we could also do this via displaytag)
      String description = task.getDescription();
      if (description != null) {
        int periodIndex = description.indexOf(".");
        if (periodIndex > 0) { // only display up to first "."
          task.setDescription(description.substring(0, periodIndex));
        }
      }

      // get the most recent status for each task
      TaskRun mostRecentTaskRun = taskRunManager.getMostRecent(task.getId());
      if (mostRecentTaskRun != null && mostRecentTaskRun.getStatus() != null) {
        taskRunStatus.put(task.getId(), mostRecentTaskRun.getStatus().toString().toLowerCase());
      } else {
        taskRunStatus.put(task.getId(), Status.UNKNOWN.toString().toLowerCase());
      }
    }
    model.put("recentStatus", taskRunStatus);
    model.put("tasks", tasks);
  }

  /**
   * Lists tasks.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("schedulerStatus", schedulerManager.getStatus());
    List<Task> tasks = handleGroupNames(request.getParameter(Constants.PARAM_SELECTED_GROUP_NAME), model);
    processTasks(tasks, model);
    return new ModelAndView("tasks_list", model);
  }

  /**
   * Handles a request to run a particular task.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView run(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long taskId = RequestUtils.getLongValue(request, PARAM_TASK_ID);
    log.debug("Received request to run task " + taskId);
    Task task = taskManager.get(taskId);
    schedulerManager.runTaskNow(task);
    Thread.sleep(1000); // bit of a hack, but allows TaskRun to be created so view should contain it
    // total hack to have path to .do here, but unsure how else to redirect there
    return new ModelAndView(new RedirectView("task_runs.do?action=list&" + Constants.PARAM_TASK_ID + "=" + taskId + "&"
        + Constants.PARAM_SELECTED_GROUP_NAME + "=" + request.getParameter(Constants.PARAM_SELECTED_GROUP_NAME)));
  }

  /**
   * Handles a request to reset a particular task.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView reset(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long taskId = RequestUtils.getLongValue(request, PARAM_TASK_ID);
    log.debug("Received request to reset task " + taskId);
    Task task = taskManager.get(taskId);
    schedulerManager.resetTask(task);
    return new ModelAndView(new RedirectView("tasks.do?" + Constants.PARAM_SELECTED_GROUP_NAME + "="
        + request.getParameter(Constants.PARAM_SELECTED_GROUP_NAME)));
  }

  public TaskManager getTaskManager() {
    return taskManager;
  }

  public void setTaskManager(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  public SchedulerManager getSchedulerManager() {
    return schedulerManager;
  }

  public void setSchedulerManager(SchedulerManager schedulerManager) {
    this.schedulerManager = schedulerManager;
  }

  public TaskRunManager getTaskRunManager() {
    return taskRunManager;
  }

  public void setTaskRunManager(TaskRunManager jobRunManager) {
    this.taskRunManager = jobRunManager;
  }

}
