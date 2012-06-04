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

import static fm.last.citrine.web.Constants.PARAM_TASK_RUN_ID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import fm.last.citrine.model.TaskRun;
import fm.last.citrine.service.TaskRunManager;

/**
 * Controller that handles the display of various values (stack trace, sysout, syserr) of a TaskRun that are typically
 * too big to be displayed in the main TaskRuns list view.
 */
public class DisplayTaskRunMessageController extends MultiActionController {

  private static Logger log = Logger.getLogger(DisplayTaskRunMessageController.class);

  private TaskRunManager taskRunManager;

  /**
   * Retrieves the task run ID from the passed request.
   * 
   * @param request Request.
   * @return The task run ID set in the request.
   * @throws ServletException If the task run ID parameter was not set on the request.
   */
  private long getTaskRunId(HttpServletRequest request) throws ServletException {
    String idString = request.getParameter(PARAM_TASK_RUN_ID);
    if (idString != null) {
      return Long.parseLong(idString);
    } else {
      throw new ServletException(PARAM_TASK_RUN_ID + " required");
    }
  }

  /**
   * Display a TaskRun's stack trace.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView displayStack(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long taskRunId = getTaskRunId(request);
    TaskRun taskRun = taskRunManager.get(taskRunId);
    String message = taskRun.getStackTrace();
    return new ModelAndView("display_task_run_message", "message", message);
  }

  /**
   * Display a TaskRun's System Out output.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView displaySysOut(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long jobRunId = getTaskRunId(request);
    TaskRun taskRun = taskRunManager.get(jobRunId);
    String message = taskRun.getSysOut();
    return new ModelAndView("display_task_run_message", "message", message);
  }

  /**
   * Display a TaskRun's System Error output.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView displaySysErr(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long taskRunId = getTaskRunId(request);
    TaskRun taskRun = taskRunManager.get(taskRunId);
    String message = taskRun.getSysErr();
    return new ModelAndView("display_task_run_message", "message", message);
  }

  public TaskRunManager getTaskRunManager() {
    return taskRunManager;
  }

  public void setTaskRunManager(TaskRunManager taskRunManager) {
    this.taskRunManager = taskRunManager;
  }

}
