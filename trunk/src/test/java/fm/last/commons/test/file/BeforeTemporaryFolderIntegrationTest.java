package fm.last.commons.test.file;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BeforeTemporaryFolderIntegrationTest {

  private static final String DATA_FILE = "data.txt";

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private File dataFile;

  @Before
  public void init() throws IOException {
    dataFile = folder.newFile(DATA_FILE);
  }

  @Test
  public void integration() throws IOException {
    // create a file with no root path, will get created relative to project root
    File projectFile = new File("projectFile.dat");

    String projectFileBasePath = projectFile.getAbsolutePath().split(File.separator)[1];
    String dataFileBasePath = dataFile.getAbsolutePath().split(File.separator)[1];

    // all we can really check is that the root of our test files isn't the same as the root to the project
    assertFalse(projectFileBasePath.equals(dataFileBasePath));
  }

}
