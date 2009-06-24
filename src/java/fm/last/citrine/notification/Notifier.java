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
