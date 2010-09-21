package fm.last.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test case for the SystemPropertySetter.
 */
public class SystemPropertySetterTest {

  @Test
  public void test() {
    String key = "fm.last.test.key";
    String value = "foo";
    Map<String, String> propertyMap = new HashMap<String, String>();
    propertyMap.put(key, value);
    assertNull(System.getProperty(key));
    new SystemPropertySetter(propertyMap);
    assertEquals(value, System.getProperty(key));
  }

}
