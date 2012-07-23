package fm.last.commons.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

/**
 * Base test case.
 * 
 * @deprecated JUnit and <code>fm.last.commons.test.file</code> now provides the functionality in this class
 */
@Deprecated
public abstract class LastTestCase {

  private static Logger log = Logger.getLogger(LastTestCase.class);

  /**
   * @deprecated You shouldn't need direct access to the root test folder. Consider any of the rules in
   *             <code>fm.last.commons.test.file</code> instead for your temporary and permanent test file needs.
   */
  @Deprecated
  protected static final String TEST_PATH = "test";

  /**
   * @deprecated This functionality is now provided by <code>fm.last.commons.test.file.RootDataFolder</code>. Please use
   *             these instead.
   */
  @Deprecated
  protected static final String TEST_DATA_PATH = TEST_PATH + "/data";

  /**
   * @deprecated JUnit now provides the functionality with <code>org.junit.rules.TemporaryFolder</code>, please use this
   *             instead.
   */
  @Deprecated
  protected static final String TEST_TMP_PATH = TEST_PATH + "/tmp";

  /**
   * The base test data folder (i.e. test/data);
   * 
   * @deprecated This functionality is now provided by <code>fm.last.commons.test.file.RootDataFolder</code>. Please use
   *             these instead.
   */
  @Deprecated
  protected File baseTestDataFolder = new File(TEST_DATA_PATH);

  /**
   * A test data folder for a specific test (only valid for tests which put their data in a folder under "data" which
   * matches their fully qualified class name).
   * 
   * @deprecated This functionality is now provided by <code>fm.last.commons.test.file.ClassDataFolder</code>. Please
   *             use these instead.
   */
  @Deprecated
  protected File testDataFolder = new File(baseTestDataFolder, getClass().getName().replaceAll("\\.", "/"));

  /**
   * A temporary folder which tests can use to write data while they run, will be cleaned inbetween each test.
   * 
   * @deprecated JUnit now provides the functionality with <code>org.junit.rules.TemporaryFolder</code>, please use this
   *             instead.
   */
  @Deprecated
  protected File testTempFolder = new File(TEST_TMP_PATH);

  /**
   * @deprecated File dependencies between tests are a bad idea
   */
  @Deprecated
  protected boolean cleanupTempFolder = true;

  /**
   * @deprecated JUnit now provides the functionality with <code>org.junit.rules.TemporaryFolder</code>, please use this
   *             instead.
   */
  @Deprecated
  protected boolean createTempFolder = true;

  /**
   * Cleanup the temp folder which tests can use to write data.
   * 
   * @deprecated JUnit now provides the functionality with <code>org.junit.rules.TemporaryFolder</code>, please use this
   *             instead.
   */
  @Deprecated
  @After
  public void cleanupTempFolder() throws IOException {
    if (cleanupTempFolder) {
      log.debug("Deleting " + testTempFolder.getAbsolutePath());
      FileUtils.deleteDirectory(testTempFolder);
    }
  }

  /**
   * * @deprecated JUnit now provides the functionality with
   * <code>org.junit.rules.TemporaryFolder.newFolder(String folderName)</code>, please use this instead.
   */
  @Deprecated
  @Before
  public void createTempFolder() throws IOException {
    if (createTempFolder && !testTempFolder.exists()) {
      if (!testTempFolder.mkdirs()) {
        throw new IOException("Failed to create " + testTempFolder.getAbsolutePath());
      }
    }
  }

  /**
   * * @deprecated Please use <code>org.junit.rules.TemporaryFolder.newFolder(String folderName)</code> and
   * <code>java.io.File.createTempFile(String prefix, String suffix)</code> or similar.
   */
  @Deprecated
  protected File createTemporaryFile() throws IOException {
    File tempFile = new File(testTempFolder, RandomStringUtils.randomAlphanumeric(12) + ".txt");
    if (!tempFile.createNewFile()) {
      if (!tempFile.exists()) {
        throw new IOException("Unable to create file '" + tempFile.getAbsolutePath() + "'");
      }
    }
    return tempFile;
  }

}
