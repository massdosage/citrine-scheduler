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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import fm.last.citrine.scheduler.SchedulerManager;
import fm.last.citrine.service.TaskManager;

/**
 * Controller for the admin page.
 */
public class AdminController extends MultiActionController {

  private static Logger log = Logger.getLogger(AdminController.class);

  private TaskManager taskManager;
  private SchedulerManager schedulerManager;

  private String buildVersion;

  private String buildDateTime;

  @Override
  protected void initServletContext(ServletContext servletContext) {
    super.initServletContext(servletContext);
    try {
      InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
      Manifest manifest = new Manifest(inputStream);
      Attributes attributes = manifest.getMainAttributes();
      buildVersion = attributes.getValue("Build-Version");
      buildDateTime = attributes.getValue("Build-DateTime");
      log.info("Citrine Build-Version: " + attributes.getValue("Build-Version"));
      log.info("Citrine Build-DateTime: " + attributes.getValue("Build-DateTime"));
    } catch (Exception e) {
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

  public void setTaskManager(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  public void setSchedulerManager(SchedulerManager schedulerManager) {
    this.schedulerManager = schedulerManager;
  }

}
