package fm.last.commons.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestingAccessibilityUtilTest {

  private class TestBean {
    private String value;
    private String value2;

    public String getValue() {
      return value;
    }

    public String getValue2() {
      return value2;
    }

    public void setValue(String value) {
      this.value = value;
    }

  }

  @Test
  public void setFieldWithSetter() {
    TestBean bean = new TestBean();
    String newValue = "new value";
    assertEquals(null, bean.getValue());
    TestingAccessibilityUtil.setField("value", bean, newValue);
    assertEquals(newValue, bean.getValue());
    newValue = "new value2";
    TestingAccessibilityUtil.setField("value", bean, newValue);
    assertEquals(newValue, bean.getValue());
  }

  @Test
  public void setFieldNoSetter() {
    TestBean bean = new TestBean();
    String newValue = "new value";
    assertEquals(null, bean.getValue2());
    TestingAccessibilityUtil.setField("value2", bean, newValue);
    assertEquals(newValue, bean.getValue2());
    newValue = "new value2";
    TestingAccessibilityUtil.setField("value2", bean, newValue);
    assertEquals(newValue, bean.getValue2());
  }

  @Test(expected = RuntimeException.class)
  public void setFieldNoField() {
    TestBean bean = new TestBean();
    TestingAccessibilityUtil.setField("nonExistent", bean, "someValue");
  }

}
