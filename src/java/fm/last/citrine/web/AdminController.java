package fm.last.citrine.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import fm.last.citrine.scheduler.SchedulerManager;
import fm.last.citrine.service.TaskManager;
import fm.last.io.FileUtils;

/**
 * Controller for the admin page.
 */
public class AdminController extends MultiActionController {

  private static Logger log = Logger.getLogger(AdminController.class);

  private TaskManager taskManager;
  private SchedulerManager schedulerManager;

  private String buildVersion;

  private String buildDateTime;

  public AdminController() {
    Manifest manifest;
    try {
      manifest = FileUtils.getManifest(this.getClass());
      Attributes attributes = manifest.getMainAttributes();
      buildVersion = attributes.getValue("Build-Version");
      buildDateTime = attributes.getValue("Build-DateTime");
    } catch (IOException e) {
      log.error("Error determining build version", e);
    }
  }

  public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("buildVersion", buildVersion);
    model.put("buildDateTime", buildDateTime);
    model.put("currentTasks", taskManager.getCurrentlyRunningTasks());
    model.put("schedulerStatus", schedulerManager.getStatus());
    return new ModelAndView("admin", model);
  }

  public ModelAndView prepareForShutdown(HttpServletRequest request, HttpServletResponse response) throws Exception {
    schedulerManager.prepareForShutdown();
    return new ModelAndView(new RedirectView("admin.do")); 
  }

  /**
   * @param taskManager the taskManager to set
   */
  public void setTaskManager(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  /**
   * @param schedulerManager the schedulerManager to set
   */
  public void setSchedulerManager(SchedulerManager schedulerManager) {
    this.schedulerManager = schedulerManager;
  }

}
