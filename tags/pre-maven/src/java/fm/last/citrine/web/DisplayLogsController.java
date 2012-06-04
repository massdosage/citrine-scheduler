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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import fm.last.citrine.service.LogFileManager;

/**
 * Controller that is responsible for listing log files and displaying their contents.
 */
public class DisplayLogsController extends MultiActionController {
  
  protected static final String PARAM_LOG_FILE = "logFile";

  private LogFileManager logFileManager;

  /**
   * Number of bytes from end of log file to display.
   */
  private long tailBytes = 100000;

  /**
   * Lists all log files.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
    return new ModelAndView("logs_list", "logFiles", logFileManager.findAllLogFiles());
  }

  /**
   * Displays the contents of a particular log file.
   * 
   * @param request
   * @param response
   * @return A ModelAndView to render.
   * @throws Exception
   */
  public ModelAndView display(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String logFileName = request.getParameter(PARAM_LOG_FILE);
    String contents = logFileManager.tail(logFileName, tailBytes);
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("contents", contents);
    // task id will only be set if we activate this controller from the task runs view
    Long taskId = RequestUtils.getLongValue(request, Constants.PARAM_TASK_ID, false);
    model.put("taskId", taskId);
    model.put(Constants.PARAM_SELECTED_GROUP_NAME, request.getParameter(Constants.PARAM_SELECTED_GROUP_NAME));
    return new ModelAndView("log_display", model);
  }

  public long getTailBytes() {
    return tailBytes;
  }

  public void setTailBytes(long tailBytes) {
    this.tailBytes = tailBytes;
  }

  /**
   * @param logFileManager the logFileManager to set
   */
  public void setLogFileManager(LogFileManager logFileManager) {
    this.logFileManager = logFileManager;
  }

}
