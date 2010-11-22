package fm.last.citrine.web;

import static org.junit.Assert.assertEquals;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class RequestUtilsTest {

  private MockHttpServletRequest servletRequest = new MockHttpServletRequest();

  @Test(expected = ServletException.class)
  public void getLongValueNoParam() throws ServletException {
    RequestUtils.getLongValue(servletRequest, "someParam");
  }

  @Test
  public void getLongValue() throws ServletException {
    servletRequest.addParameter("someParam", "100");
    assertEquals(100, (long) RequestUtils.getLongValue(servletRequest, "someParam"));
  }

  @Test(expected = ServletException.class)
  public void getLongValueRequiredNoParam() throws ServletException {
    RequestUtils.getLongValue(servletRequest, "someParam", true);
  }

  @Test
  public void getLongValueRequiredParam() throws ServletException {
    servletRequest.addParameter("someParam", "100");
    assertEquals(100, (long) RequestUtils.getLongValue(servletRequest, "someParam", true));
  }

  @Test
  public void getLongValueNotRequiredNoParam() throws ServletException {
    assertEquals(null, RequestUtils.getLongValue(servletRequest, "someParam", false));
  }

}
