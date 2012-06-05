package fm.last.commons.test.file;

import java.io.File;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public final class RootDataFolder extends AbstractDataFolder {

  public RootDataFolder() {
    this(new String[] {});
  }

  public RootDataFolder(String... children) {
    StringBuilder path = new StringBuilder();
    path.append("src");
    path.append(File.separator);
    path.append("test");
    path.append(File.separator);
    path.append("data");
    for (String child : children) {
      path.append(File.separator);
      path.append(child);
    }
    folder = new File(path.toString());
  }

  @Override
  public Statement apply(Statement base, FrameworkMethod method, Object target) {
    return base;
  }

}
