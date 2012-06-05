package fm.last.commons.test.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

public class RootDataFolderIntegrationTest {

  @Rule
  public DataFolder folder = new RootDataFolder();

  @Test
  public void integration() throws IOException {
    File actualFolder = folder.getFolder();
    assertTrue(actualFolder.exists());
    assertTrue(actualFolder.canRead());
    assertTrue(actualFolder.getAbsolutePath().endsWith("src/test/data"));
  }

}
