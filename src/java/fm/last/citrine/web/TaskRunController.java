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
import static fm.last.citrine.web.Constants.PARAM_TASK_RUN_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.Task;
import fm.last.citrine.model.TaskRun;
import fm.last.citrine.service.LogFileManager;
import fm.last.citrine.service.TaskManager;
import fm.last.citrine.service.TaskRunManager;

/**
 * Controller that handles the listing and deleting of TaskRuns.
 */
public class TaskRunController extends MultiActionController {

  private static Logger log = Logger.getLogger(TaskRunController.class);
  private static final String PARAM_PAGE = "page";
  public static final int DEFAULT_PAGE_SIZE = 20;
  private int pageSize = DEFAULT_PAGE_SIZE;

  private TaskRunManager taskRunManager;
  private TaskManager taskManager;
  private LogFileManager logFileManager;

  /**
   * Lists all the TaskRuns for a particular Task.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long taskId = RequestUtils.getLongValue(request, PARAM_TASK_ID);
    int currentPage = 0;
    String pageString = request.getParameter(PARAM_PAGE);
    if (!StringUtils.isEmpty(pageString)) {
      currentPage = Integer.parseInt(pageString);
    }

    List<TaskRun> taskRuns = taskRunManager.findByTaskId(taskId, currentPage * pageSize, pageSize);
    Map<Long, String> taskRunLogs = getTaskRunLogs(taskRuns);
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("taskRuns", taskRuns);
    model.put("taskId", taskId);
    model.put("taskRunLogs", taskRunLogs);
    model.put("page", currentPage);

    Task task = taskManager.get(taskId);
    if (task != null) {
      model.put("taskName", task.getName());
    }

    if (taskRuns.size() == pageSize) { // a bit crude, but will do for now, should really compare with count(*)
      model.put("morepages", true);
    }

    model.put(Constants.PARAM_SELECTED_GROUP_NAME, request.getParameter(Constants.PARAM_SELECTED_GROUP_NAME));
    return new ModelAndView("task_runs_list", model);
  }

  /**
   * Creates a Map where the key is the TaskRun id and the value is a string indicating what kind of log file view is
   * available for the TaskRun. "display" means the TaskRun is in progress and therefore the HTML display view of the
   * log is preferred, "raw" means the TaskRun is complete and a link to the raw log file is preferred. If there are no
   * log files available for a TaskRun then no entry is added to the Map.
   * 
   * @param taskRuns List of TaskRuns to find log file views for.
   * @return A Map containing TaskRunId=LogFileDisplayString entries.
   */
  private Map<Long, String> getTaskRunLogs(List<TaskRun> taskRuns) {
    Map<Long, String> taskRunLogs = new HashMap<Long, String>();
    for (TaskRun taskRun : taskRuns) {
      if (logFileManager.exists(taskRun.getId() + ".log")) {
        if (Status.RUNNING.equals(taskRun.getStatus())) {
          taskRunLogs.put(taskRun.getId(), "display"); // for running jobs, link to html display
        } else {
          taskRunLogs.put(taskRun.getId(), "raw"); // for finished jobs link to raw log file
        }
      }
    }
    return taskRunLogs;
  }

  /**
   * Deletes a particular TaskRun.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long taskRunId = RequestUtils.getLongValue(request, PARAM_TASK_RUN_ID);
    taskRunManager.delete(taskRunId);
    return list(request, response);
  }

  /**
   * Stops a particular TaskRun.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView stop(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long taskRunId = RequestUtils.getLongValue(request, PARAM_TASK_RUN_ID);
    taskRunManager.stop(taskRunId);
    return list(request, response);
  }

  public void setTaskRunManager(TaskRunManager taskRunManager) {
    this.taskRunManager = taskRunManager;
  }

  /**
   * @return the pageSize
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * @param pageSize the pageSize to set
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * @param taskManager the taskManager to set
   */
  public void setTaskManager(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  /**
   * @param logFileManager the logFileManager to set
   */
  public void setLogFileManager(LogFileManager logFileManager) {
    this.logFileManager = logFileManager;
  }

}
