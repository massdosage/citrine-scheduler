package fm.last.commons.test.file;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;

public abstract class SuperclassBeforeTemporaryFolderIntegrationTest {

  static final String DATA_FILE = "data.txt";
  static final String FOLDER= "folder";

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  File dataFile;
  File dataFolder;

  @Before
  public void init() throws IOException {
    dataFile = folder.newFile(DATA_FILE);
    dataFolder = folder.newFolder(FOLDER);
  }

}
