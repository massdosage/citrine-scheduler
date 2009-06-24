package fm.last.citrine.notification;

import fm.last.citrine.model.Notification;
import fm.last.citrine.model.Status;
import fm.last.citrine.model.TaskRun;

/**
 * Base class for Notifier implementations.
 */
public abstract class BaseNotifier implements Notifier {

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
