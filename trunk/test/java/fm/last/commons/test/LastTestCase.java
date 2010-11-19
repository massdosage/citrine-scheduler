/*
 * Copyright 2010 Last.fm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
 */
public abstract class LastTestCase {

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

  protected boolean createTempFolder = true;

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

  @Before
  public void createTempFolder() throws IOException {
    if (createTempFolder && !testTempFolder.exists()) {
      if (!testTempFolder.mkdirs()) {
        throw new IOException("Failed to create " + testTempFolder.getAbsolutePath());
      }
    }
  }

  protected File createTemporaryFile() throws IOException {
    File temp = new File(testTempFolder, RandomStringUtils.randomAlphanumeric(12) + ".txt");
    temp.createNewFile();
    return temp;
  }

}
