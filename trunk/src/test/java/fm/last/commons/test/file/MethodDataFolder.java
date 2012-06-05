package fm.last.commons.test.file;

import java.io.File;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public final class MethodDataFolder extends AbstractDataFolder {

  private final File parent;

  public MethodDataFolder() {
    parent = new File("src" + File.separator + "test" + File.separator + "data");
  }

  @Override
  public final Statement apply(final Statement base, FrameworkMethod method, Object target) {
    Test testAnnotation = method.getAnnotation(Test.class);
    if (testAnnotation == null) {
      return base;
    }
    final String methodName = method.getName();
    final Class<?> targetClass = target.getClass();
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        folder = new File(parent, targetClass.getName().replaceAll("\\.", File.separator) + File.separator + methodName);
        base.evaluate();
      }
    };
  }

}
