package fm.last.test;

import java.io.File;
import java.io.IOException;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;

/**
 * Base test case.
 */
public class LastTestCase {

  private static Logger log = Logger.getLogger(LastTestCase.class);

  protected static final String TEST_PATH = "test";
  protected static final String TEST_DATA_PATH = TEST_PATH + "/data";
  protected static final String TEST_TMP_PATH = TEST_PATH + "/tmp";

  /**
   * The base test data folder (i.e. test/data);
   */
  protected File baseTestDataFolder = new File(TEST_DATA_PATH);

  /**
   * A test data folder for a specific test (only valid for tests which put their data in a folder under "data" which
   * matches their fully qualified class name).
   */
  protected File testDataFolder = new File(baseTestDataFolder, getClass().getName().replaceAll("\\.", "/"));

  /**
   * A temporary folder which tests can use to write data while they run, will be cleaned inbetween each test.
   */
  protected File testTempFolder = new File(TEST_TMP_PATH);

  protected boolean cleanupTempFolder = true;

  /**
   * Cleanup the temp folder which tests can use to write data.
   * 
   * @throws IOException
   */
  @After
  public void cleanupTempFolder() throws IOException {
    if (cleanupTempFolder) {
      log.debug("Deleting " + testTempFolder.getAbsolutePath());
      FileUtils.deleteDirectory(testTempFolder);
    }
  }
  
  /**
   * Fails the test because of the passed error.
   * 
   * @param t Throwable that should cause test to fail.
   */
  protected void fail(Throwable t) {
    t.printStackTrace();
    throw new AssertionFailedError(t.getMessage());
  }
  
}
