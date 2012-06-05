package fm.last.commons.test.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class RootSqlFolderTest {

  @Test
  public void getRootFolder() throws IOException {
    RootSqlFolder sqlFolder = new RootSqlFolder();
    File folder = sqlFolder.getFolder();
    assertTrue(folder.exists());
  }

  @Test(expected = FileNotFoundException.class)
  public void getNonExistentSubFolder() throws IOException {
    RootSqlFolder sqlFolder = new RootSqlFolder("sub");
    sqlFolder.getFolder();
  }
}
