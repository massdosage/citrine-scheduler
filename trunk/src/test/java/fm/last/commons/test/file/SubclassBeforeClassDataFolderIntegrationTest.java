package fm.last.commons.test.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class SubclassBeforeClassDataFolderIntegrationTest extends SuperclassBeforeClassDataFolderIntegrationTest {

  @Before
  public void init2() throws IOException {
    assertFileExists();
  }

  @Test
  public void integration() throws IOException {
    assertFileExists();
  }

  private void assertFileExists() {
    assertTrue(dataFile.exists());
    assertTrue(dataFile.canRead());
    assertTrue(dataFile.getAbsolutePath().endsWith(
        getClass().getName().replaceAll("\\.", File.separator) + File.separator + DATA_FILE));
  }

}
