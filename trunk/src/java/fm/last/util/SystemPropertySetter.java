package fm.last.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class for setting system properties.
 */
public class SystemPropertySetter {

  /**
   * Sets the contents of the passed map as system properties. The key is the system property name and the value is the
   * property value.
   * 
   * @param propertyValues A map of system property keys and values to be set.
   */
  public SystemPropertySetter(Map<String, String> propertyValues) {
    for (Entry<String, String> entry : propertyValues.entrySet()) {
      System.setProperty(entry.getKey(), entry.getValue());
    }
  }

}
