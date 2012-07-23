package fm.last.commons.test.file;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;

public abstract class SuperclassBeforeClassDataFolderIntegrationTest {

  static final String DATA_FILE = "data.txt";

  @Rule
  public DataFolder folder = new ClassDataFolder();

  File dataFile;

  @Before
  public void init() throws IOException {
    dataFile = folder.getFile(DATA_FILE);
  }

}
