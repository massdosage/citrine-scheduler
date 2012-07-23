package fm.last.commons.test.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

abstract class AbstractDataFolder implements DataFolder {

  File folder;

  AbstractDataFolder() {
  }

  @Override
  public File getFolder() throws IOException {
    if (!folder.exists()) {
      throw new FileNotFoundException(folder.getAbsolutePath());
    }
    if (!folder.canRead()) {
      throw new IOException("Cannot read '" + folder.getAbsolutePath() + "'");
    }
    if (!folder.isDirectory()) {
      throw new IOException("Path is not a directory '" + folder.getAbsolutePath() + "'");
    }
    return folder;
  }

  @Override
  public File getFile(String path) throws IOException {
    return new File(getFolder(), path);
  }

}
