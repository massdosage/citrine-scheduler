package fm.last.commons.test.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BeforeClassDataFolderIntegrationTest {

  private static final String DATA_FILE = "data.txt";

  @Rule
  public DataFolder folder = new ClassDataFolder();

  private File dataFile;

  @Before
  public void init() throws IOException {
    dataFile = folder.getFile(DATA_FILE);
  }

  @Test
  public void integration() throws IOException {
    assertTrue(dataFile.exists());
    assertTrue(dataFile.canRead());
    assertTrue(dataFile.getAbsolutePath().endsWith(
        getClass().getName().replaceAll("\\.", File.separator) + File.separator + DATA_FILE));
  }

}
