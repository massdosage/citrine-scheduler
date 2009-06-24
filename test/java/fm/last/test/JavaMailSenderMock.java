package fm.last.test;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Mock mail sender that just keeps track of number of times requests were made to send messages.
 */
public class JavaMailSenderMock implements MailSender {

  private int messageCount;

  @Override
  public void send(SimpleMailMessage simplemailmessage) throws MailException {
    messageCount++;
  }

  @Override
  public void send(SimpleMailMessage[] asimplemailmessage) throws MailException {
    messageCount++;
  }

  /**
   * Get the number of messages sent.
   * 
   * @return The number of messages sent.
   */
  public int getMessageCount() {
    return messageCount;
  }

  /**
   * Set the number of messages sent.
   * 
   * @param messageCount New message count.
   */
  public void setMessageCount(int messageCount) {
    this.messageCount = messageCount;
  }

}
