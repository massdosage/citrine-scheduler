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
package fm.last.citrine.notification;

import org.apache.commons.lang.StringUtils;

import fm.last.citrine.model.Notification;
import fm.last.citrine.model.Status;
import fm.last.citrine.model.TaskRun;

/**
 * Base class for Notifier implementations.
 */
public abstract class BaseNotifier implements Notifier {

  // not great that this is hardcoded here, not sure how to make this configurable
  private static final String VIEW_LOG_PATH = "logs.do?action=display&logFile=$.log";
  protected String baseCitrineUrl;

  public String getBaseCitrineUrl() {
    return baseCitrineUrl;
  }

  public void setBaseCitrineUrl(String baseCitrineUrl) {
    baseCitrineUrl = StringUtils.trimToNull(baseCitrineUrl);
    if (baseCitrineUrl == null) {
      return;
    }
    if (!baseCitrineUrl.endsWith("/")) {
      baseCitrineUrl += "/";
    }
    this.baseCitrineUrl = baseCitrineUrl;
  }

  protected String getDisplayLogUrl(TaskRun taskRun) {
    if (StringUtils.trimToNull(baseCitrineUrl) == null) {
      return null;
    }
    return baseCitrineUrl + VIEW_LOG_PATH.replace("$", String.valueOf(taskRun.getId()));
  }

  /**
   * Returns whether a notification should be sent or not.
   * 
   * @param notification The notification.
   * @param status The new status.
   * @return Whether a notification should be sent or not.
   */
  protected boolean shouldNotify(Notification notification, Status status) {
    // first check if we actually have someone to send notifications to
    if (notification != null) {
      // now check status and whether we should notify on that status
      if (Status.SUCCESS.equals(status) && notification.isNotifyOnSuccess()) {
        return true;
      }
      if (status.getValue() < Status.SUCCESS.getValue() && notification.isNotifyOnFailure()) {
        return true;
      }
    }
    return false;
  }

  /**
   * If necessary, sends a notification message for the passed TaskRun
   * 
   * @param notification The notification.
   * @param taskRun The task run to send the notification for.
   * @param taskName The task name to send the notification for.
   */
  public void sendNotification(Notification notification, TaskRun taskRun, String taskName) {
    if (shouldNotify(notification, taskRun.getStatus())) {
      notify(notification, taskRun, taskName);
    }
  }

  /**
   * Performs the implementation specific notification.
   * 
   * @param notification The notification.
   * @param taskRun The task run to send the notification for.
   * @param taskName The task name to send the notificaton for.
   */
  public abstract void notify(Notification notification, TaskRun taskRun, String taskName);

}
