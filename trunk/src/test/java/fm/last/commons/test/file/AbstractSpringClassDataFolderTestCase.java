package fm.last.commons.test.file;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;

@Ignore
public abstract class AbstractSpringClassDataFolderTestCase {

  static final String DATA_FILE = "data.txt";

  @Rule
  public DataFolder folder = new ClassDataFolder();

  File dataFile;

  @Before
  public void init() throws IOException {
    assertNotNull(folder.getFolder());
    dataFile = folder.getFile(DATA_FILE);
  }
}
