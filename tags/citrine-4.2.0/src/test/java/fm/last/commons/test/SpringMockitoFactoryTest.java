package fm.last.commons.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Observer;

import org.junit.Test;
import org.springframework.mail.MailMessage;

public class SpringMockitoFactoryTest {

  @Test
  public void setType() {
    SpringMockitoFactory factory = new SpringMockitoFactory();
    factory.setType(String.class);
    assertEquals(String.class, factory.getObjectType());
    factory.setType(DateFormat.class);
    assertEquals(DateFormat.class, factory.getObjectType());
  }
  
  @Test
  public void setSpyOn() throws Exception {
    SpringMockitoFactory factory = new SpringMockitoFactory();
    factory.setSpyOn(new ArrayList());
    assertEquals(ArrayList.class, factory.getObjectType());
    assertTrue(factory.getObject() instanceof ArrayList);
  }

  @Test
  public void getObject1() throws Exception {
    SpringMockitoFactory factory = new SpringMockitoFactory();
    factory.setType(Observer.class);
    assertTrue(factory.getObject() instanceof Observer);
  }

  @Test
  public void getObject2() throws Exception {
    SpringMockitoFactory factory = new SpringMockitoFactory();
    factory.setType(MailMessage.class);
    assertTrue(factory.getObject() instanceof MailMessage);
  }

}
