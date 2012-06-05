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

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.commons.test.file.ClassDataFolder;
import fm.last.commons.test.file.DataFolder;

/**
 * Unit test case for our test assertions.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAssertTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Rule
  public DataFolder dataFolder = new ClassDataFolder();

  @Mock
  private DataSource dataSource;

  @Mock
  private Connection connection;
  
  @Test(expected = java.lang.AssertionError.class)
  public void assertFilesEqualWithDifferentFiles() throws IOException {
    File file1 = new File(dataFolder.getFolder(), "file1.txt");
    File file2 = new File(dataFolder.getFolder(), "file2.txt");
    TestAssert.assertFileEquals(file1, file2);
  }

  @Test
  public void assertFilesEqualWithSameFile() throws IOException {
    File file1 = new File(dataFolder.getFolder(), "file1.txt");
    TestAssert.assertFileEquals(file1, file1);
  }

  @Test
  public void assertFilesEqualWithSameFileInDifferentFolders() throws IOException {
    File file1 = new File(dataFolder.getFolder(), "file1.txt");
    File file2 = temporaryFolder.newFile("copy.txt");
    FileUtils.copyFile(file1, file2);
    TestAssert.assertFileEquals(file1, file2);
  }

  @Test
  public void assertFilesNotEqual() throws IOException {
    File file1 = new File(dataFolder.getFolder(), "file1.txt");
    File file2 = new File(dataFolder.getFolder(), "file2.txt");
    TestAssert.assertFilesNotEqual(file1, file2);
  }

  @Test(expected = java.lang.AssertionError.class)
  public void assertFilesNotEqualWithSameFile() throws IOException {
    File file1 = new File(dataFolder.getFolder(), "file1.txt");
    File file2 = new File(dataFolder.getFolder(), "file1.txt");
    TestAssert.assertFilesNotEqual(file1, file2);
  }

  @Test
  public void assertValues() {
    String value = "value";
    TestAssert.assertValues(value, new String[] { value, value, value });
  }

  @Test(expected = java.lang.AssertionError.class)
  public void assertValuesNotEqual() {
    String value = "value";
    TestAssert.assertValues(value, new String[] { value, "value2", value });
  }

  @Test
  public void assertTestDatabase() throws SQLException {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getCatalog()).thenReturn("test" + TestAssert.TEST_DB_SUFFIX);
    TestAssert.assertTestDatabase(dataSource);
  }

  @Test(expected = IllegalArgumentException.class)
  public void assertWrongTestDatabase() throws SQLException {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.getCatalog()).thenReturn("test");
    TestAssert.assertTestDatabase(dataSource);
  }
}
