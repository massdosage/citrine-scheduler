package fm.last.commons.test;

import java.lang.reflect.Field;

/**
 * If your project is using Spring, see the ReflectionTestUtils class for this and similar methods
 */
public final class TestingAccessibilityUtil {

  private TestingAccessibilityUtil() {
  }

  public static void setField(String name, Object target, Object value) {
    try {
      Class<?> targetClass = target.getClass();
      Field field = targetClass.getDeclaredField(name);
      boolean unlocked = false;
      if (!field.isAccessible()) {
        field.setAccessible(true);
        unlocked = true;
      }
      field.set(target, value);
      if (unlocked) {
        field.setAccessible(false);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
