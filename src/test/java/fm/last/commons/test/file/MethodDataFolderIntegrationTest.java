package fm.last.commons.test.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

public class MethodDataFolderIntegrationTest {

  @Rule
  public DataFolder folder = new MethodDataFolder();

  @Test
  public void helloWorld() throws IOException {
    File actualFolder = folder.getFolder();
    assertTrue(actualFolder.exists());
    assertTrue(actualFolder.canRead());
    assertTrue(actualFolder.getAbsolutePath().endsWith(
        getClass().getName().replaceAll("\\.", File.separator) + File.separator + "helloWorld"));
  }

  @Test(expected = FileNotFoundException.class)
  public void nonExistent() throws IOException {
    folder.getFolder();
  }

}
