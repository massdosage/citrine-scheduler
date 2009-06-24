package fm.last.citrine.notification;

import org.apache.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import fm.last.citrine.model.Notification;
import fm.last.citrine.model.TaskRun;
import fm.last.util.StringUtils;

/**
 * Notifier which sends notifications via e-mail.
 */
public class EMailNotifier extends BaseNotifier {

  private static Logger log = Logger.getLogger(EMailNotifier.class);

  private MailSender mailSender;

  private SimpleMailMessage templateMessage;

  @Override
  public void notify(Notification notification, TaskRun taskRun, String taskName) {
    SimpleMailMessage message = createMessage(notification.getRecipients(), taskRun, taskName);
    log.debug("Sending notification for TaskRun " + taskRun.getId());
    try {
      this.mailSender.send(message);
    } catch (MailException e) {
      log.error("Error sending e-mail notification", e);
    }
  }

  /**
   * Creates a mail message which is ready to be sent.
   * 
   * @param recipients Message recipients.
   * @param taskRun Task run containing various values which will be put into message.
   * @param taskName The task name.
   * @return A prepared mail message.
   */
  private SimpleMailMessage createMessage(String recipients, TaskRun taskRun, String taskName) {
    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
    if (!StringUtils.isEmpty(recipients)) {
      // override default recipients set in application context
      String[] recipientArray = recipients.split(",");
      msg.setTo(recipientArray);
    }
    msg.setSubject("[citrine] '" + taskName + "' finished with Status " + taskRun.getStatus() + " for TaskRun "
        + taskRun.getId());
    StringBuffer messageText = new StringBuffer();

    if (!StringUtils.isEmpty(taskRun.getStackTrace())) {
      messageText.append("\nStackTrace:\n" + taskRun.getStackTrace() + "\n");
    }
    if (!StringUtils.isEmpty(taskRun.getSysErr())) {
      messageText.append("\nSysErr:\n" + taskRun.getSysErr() + "\n");
    }
    if (!StringUtils.isEmpty(taskRun.getSysOut())) {
      messageText.append("\nSysOut:\n" + taskRun.getSysOut() + "\n");
    }
    msg.setText(messageText.toString());
    return msg;
  }

  public MailSender getMailSender() {
    return mailSender;
  }

  public void setMailSender(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  public SimpleMailMessage getTemplateMessage() {
    return templateMessage;
  }

  public void setTemplateMessage(SimpleMailMessage templateMessage) {
    this.templateMessage = templateMessage;
  }

}
