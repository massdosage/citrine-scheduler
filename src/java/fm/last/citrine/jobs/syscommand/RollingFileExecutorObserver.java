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
package fm.last.citrine.jobs.syscommand;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.RootLogger;

import fm.last.io.FileUtils;
import fm.last.syscommand.SysExecutorObserver;

/**
 * SysExecutorObserver implementation that appends the SysOut and SysErr strings to a log file.
 */
public class RollingFileExecutorObserver implements SysExecutorObserver {

  /**
   * This logs standard application messages as setup in main log4j config ONLY.
   */
  private static Logger log = Logger.getLogger(RollingFileExecutorObserver.class);

  private static final String DEFAULT_CONVERSION_PATTERN = "%d{ISO8601} %m%n";

  /**
   * This Logger will log to a custom file per batch run ONLY.
   */
  private Logger observerLogger;

  /**
   * The appender which writes to custom files per batch run.
   */
  private RollingFileAppender appender;

  /**
   * The id of the JobRun (used to determine the name of the log file).
   */
  private long jobRunId;

  /**
   * The conversion pattern to be used for the batch run custom log messages.
   */
  private String conversionPattern = DEFAULT_CONVERSION_PATTERN;

  /**
   * The base folder under which log files will be created.
   */
  private String baseLogPath = System.getProperty("java.io.tmpdir");

  /**
   * Set to true if at least one message from System.err was logged.
   */
  private boolean errLogged = false;

  /**
   * Set to true if at least one message from System.out was logged.
   */
  private boolean outLogged = false;

  /**
   * The number of backup files to keep (e.g. when file exceeds length, a new one will be created and the previous one
   * will be backed up).
   */
  private int maxBackupIndex = 100;

  /**
   * The number of bytes in the file to "tail" when the job is finished and the results of Sysout and Syserr are
   * retrieved.
   */
  private int tailBytes = 0;

  public RollingFileExecutorObserver() {
  }

  /**
   * Performs any necessary initialisation.
   */
  private void init() {
    if (appender == null) {
      // need to create a new log4j hierarchy so values in log4j.xml are ignored
      observerLogger = new Hierarchy(new RootLogger(Level.DEBUG)).getLogger(this.getClass().getName());
      PatternLayout layout = new PatternLayout();
      layout.setConversionPattern(conversionPattern);
      try {
        appender = new RollingFileAppender(layout, baseLogPath + jobRunId + ".log");
        appender.setMaxBackupIndex(maxBackupIndex);
        log.info("Log file for this run will be located at: " + appender.getFile());
        observerLogger.addAppender(appender);
      } catch (IOException e) {
        log.error(e);
      }
    }
  }

  /**
   * This appender does not log out and err to different files, so for now this returns output of a run by retrieving
   * the last "n" bytes from the log file.
   * 
   * @return
   */
  private String getOutput() {
    String returnValue = appender.getFile();
    if (tailBytes > 0) {
      try {
        returnValue += ":\n" + FileUtils.tail(new File(appender.getFile()), tailBytes);
      } catch (IOException e) {
        log.error("Error getting output", e);
      }
    }
    return returnValue;
  }

  @Override
  public String getSysErr() {
    if (errLogged) {
      return getOutput();
    }
    return null;
  }

  @Override
  public String getSysOut() {
    if (outLogged) {
      return getOutput();
    }
    return null;
  }

  @Override
  public void sysErr(String sysErr) {
    init();
    observerLogger.error(sysErr);
    errLogged = true;
  }

  @Override
  public void sysOut(String sysOut) {
    init();
    observerLogger.info(sysOut);
    outLogged = true;
  }

  @Override
  public void close() {
    if (appender != null) {
      appender.close();
    }
  }

  public void setJobRunId(long jobRunId) {
    this.jobRunId = jobRunId;
  }

  public String getConversionPattern() {
    return conversionPattern;
  }

  public void setConversionPattern(String conversionPattern) {
    this.conversionPattern = conversionPattern;
  }

  public String getBaseLogPath() {
    return baseLogPath;
  }

  public void setBaseLogPath(String baseLogPath) {
    this.baseLogPath = baseLogPath;
  }

  public int getMaxBackupIndex() {
    return maxBackupIndex;
  }

  public void setMaxBackupIndex(int maxBackupIndex) {
    this.maxBackupIndex = maxBackupIndex;
  }

  public int getTailBytes() {
    return tailBytes;
  }

  public void setTailBytes(int tailBytes) {
    this.tailBytes = tailBytes;
  }

}
