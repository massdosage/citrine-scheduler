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
    StringBuilder messageText = new StringBuilder();

    String logUrl = getDisplayLogUrl(taskRun);
    if (logUrl!=null) {
      messageText.append("\nSee: ").append(logUrl).append("\n");
    }
    
    if (!StringUtils.isEmpty(taskRun.getStackTrace())) {
      messageText.append("\nStackTrace:\n").append(taskRun.getStackTrace()).append("\n");
    }
    if (!StringUtils.isEmpty(taskRun.getSysErr())) {
      messageText.append("\nSysErr:\n").append(taskRun.getSysErr()).append("\n");
    }
    if (!StringUtils.isEmpty(taskRun.getSysOut())) {
      messageText.append("\nSysOut:\n").append(taskRun.getSysOut()).append("\n");
    }
    msg.setText(messageText.toString());

    log.warn(messageText.toString());

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
