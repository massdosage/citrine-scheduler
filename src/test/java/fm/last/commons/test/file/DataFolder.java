package fm.last.commons.test.file;

import java.io.File;
import java.io.IOException;

import org.junit.rules.MethodRule;

public interface DataFolder extends MethodRule {

  File getFolder() throws IOException;

  File getFile(String path) throws IOException;

}
