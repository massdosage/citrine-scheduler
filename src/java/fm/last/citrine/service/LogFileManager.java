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
package fm.last.citrine.service;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

/**
 * Manages Citrine task run log files.
 */
public interface LogFileManager {

  /**
   * Finds all task run log file names.
   * 
   * @return A list of all the task run log file names.
   */
  public List<String> findAllLogFiles();

  /**
   * Tails the passed task run log file.
   * 
   * @param logFileName The name of the task run log file.
   * @param tailBytes The number of bytes to read backward from the end of the file.
   * @return The requested tail end of the file.
   * @throws IOException If an error occurs tailing the file.
   */
  public String tail(String logFileName, long tailBytes) throws IOException;

  /**
   * Deletes all log files older than the passed date.
   * 
   * @param deleteBefore DateTime to delete log files before.
   * @throws IOException If an error occurs deleting the log files.
   */
  public void deleteBefore(DateTime deleteBefore) throws IOException;

  /**
   * Determines whether a log file with the passed name exists.
   * 
   * @param logFileName Name of the log file.
   * @return Whether the log file exists or not.
   */
  public boolean exists(String logFileName);

}
