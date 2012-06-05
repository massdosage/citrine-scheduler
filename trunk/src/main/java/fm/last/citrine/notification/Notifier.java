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

import fm.last.citrine.model.Notification;
import fm.last.citrine.model.TaskRun;

/**
 * Interface for classes responsible for sending citrine-related notifications.
 */
public interface Notifier {

  /**
   * Send an notification message for the passed TaskRun.
   * 
   * @param notification Notification object.
   * @param taskRun TaskRun which the notification should be sent for.
   * @param taskName Name of task associated with TaskRun.
   */
  public void sendNotification(Notification notification, TaskRun taskRun, String taskName);

}
