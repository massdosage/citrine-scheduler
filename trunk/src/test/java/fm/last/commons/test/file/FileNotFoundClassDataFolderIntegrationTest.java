package fm.last.commons.test.file;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

public class FileNotFoundClassDataFolderIntegrationTest {

  @Rule
  public DataFolder folder = new ClassDataFolder();

  @Test(expected = FileNotFoundException.class)
  public void integration() throws IOException {
    folder.getFolder();
  }

}
