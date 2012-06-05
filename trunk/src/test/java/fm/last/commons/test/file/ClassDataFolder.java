package fm.last.commons.test.file;

import java.io.File;
import java.lang.annotation.Annotation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public final class ClassDataFolder extends AbstractDataFolder {

  private final File parent;

  public ClassDataFolder() {
    parent = new File("src" + File.separator + "test" + File.separator + "data");
  }

  @SuppressWarnings("unchecked")
  @Override
  public final Statement apply(final Statement base, FrameworkMethod method, Object target) {
    if (notAnnotatedWithAny(method, Test.class, Before.class, After.class)) {
      return base;
    }
    Class<?> targetClass = target.getClass();
    folder = new File(parent, targetClass.getName().replaceAll("\\.", File.separator));
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        base.evaluate();
      }
    };
  }

  private boolean notAnnotatedWithAny(FrameworkMethod method, Class<? extends Annotation>... annotationClasses) {
    for (Class<? extends Annotation> annotationClass : annotationClasses) {
      Annotation annotationInstance = method.getAnnotation(annotationClass);
      if (annotationInstance != null) {
        return false;
      }
    }
    return true;
  }

}
