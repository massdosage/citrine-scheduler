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
   * @return
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
   * @return
   * @throws Exception
   */
  public ModelAndView display(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String logFileName = request.getParameter("logFile");
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
