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
package fm.last.syscommand;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * Class that executes system commands, a separate instance should be used per Process created. Typical use would
 * involve creating an instance, then optionally setting the working directory and environment variables, then calling
 * start(), then waitForProcess(), and then inspecting/using the value returned and the error and system output set on
 * the observer.
 */
public class SysCommandExecutor {

  private static Logger log = Logger.getLogger(SysCommandExecutor.class);
  private File workingDirectory = null;

  /**
   * Enum representing the output type (system out or error).
   */
  public static enum OutputType {
    OUT, ERR
  };

  private Map<String, String> environment = new HashMap<String, String>();

  private SysExecutorObserver sysOutObserver;
  private SysExecutorObserver sysErrObserver;

  private AsyncStreamReader commandOutputThread = null;
  private AsyncStreamReader commandErrorThread = null;

  public static final long DEFAULT_SYS_OUT_WAIT_TIME = 10000;
  public static final long DEFAULT_SYS_ERR_WAIT_TIME = 10000;

  private long sysOutWaitTime = DEFAULT_SYS_OUT_WAIT_TIME;
  private long sysErrWaitTime = DEFAULT_SYS_ERR_WAIT_TIME;

  private Process process;

  /**
   * Constructs a new SysCommandExecutor which will use the passed observers to drain the system out and system error
   * streams.
   * 
   * @param sysOutObserver Observer that will receive data from system out.
   * @param sysErrObserver Observer that will receieve data from system error.
   */
  public SysCommandExecutor(SysExecutorObserver sysOutObserver, SysExecutorObserver sysErrObserver) {
    this.sysOutObserver = sysOutObserver;
    this.sysErrObserver = sysErrObserver;
  }

  /**
   * Constructs a new SysCommandExecutor which will use the passed observer to drain both the system out and system
   * error streams.
   * 
   * @param observer Observer that will receive data from system out and system error.
   */
  public SysCommandExecutor(SysExecutorObserver observer) {
    this(observer, observer);
  }

  /**
   * Constructs a new SysCommandExecutor which will not drain the system out or system error streams. Note that this may
   * cause the Process to block or even deadlock.
   */
  public SysCommandExecutor() {
    this(null, null);
  }

  /**
   * Creates and starts executing a process represented by the passed command. Note: each part of the command (i.e.
   * program name and its arguments) must be a separate item in the list.
   * 
   * @param command Command to execute and its arguments.
   * @return The executing process.
   * @throws Exception If an error occurs starting the process or the stream monitoring threads.
   */
  public Process start(List<String> command) throws Exception {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    if (workingDirectory != null) {
      processBuilder.directory(workingDirectory);
    }
    if (environment.size() > 0) {
      Map<String, String> processEnvironment = processBuilder.environment();
      for (Entry<String, String> entry : environment.entrySet()) {
        processEnvironment.put(entry.getKey(), entry.getValue());
      }
    }
    process = processBuilder.start();
    // start output and error read threads
    startStreamReadThreads(process.getInputStream(), process.getErrorStream());
    return process;
  }

  /**
   * Creates and starts executing a process represented by the passed command. Note: each part of the command (i.e.
   * program name and its arguments) must be a separate value i.e. call this method like so:
   * 
   * <pre>
   * start(&quot;ls&quot;, &quot;-al&quot;)
   * </pre>
   * 
   * and not like so:
   * 
   * <pre>
   * start(&quot;ls -al&quot;)
   * </pre>
   * 
   * @param command Command to execute and its arguments.
   * @return The executing process.
   * @throws Exception If an error occurs starting the process or the stream monitoring threads.
   */
  public Process start(String... command) throws Exception {
    return start(Arrays.asList(command));
  }

  /**
   * Stops the process created by runCommand(), also stops the threads monitoring the processes' output streams.
   * 
   * @return The command's exit status or -1 if an error occurs.
   */
  public int destroyProcess() {
    if (process == null) {
      return -1;
    }
    process.destroy();
    return waitForProcess();
  }

  /**
   * Wait for the process created by runCommand() to finish running of its own accord. Also stops the threads monitoring
   * the processes' output streams.
   * 
   * @return The command's exit status or -1 if an error occurs.
   */
  public int waitForProcess() {
    // wait for command execution to terminate
    int exitStatus = -1;
    if (process == null) { // a process wasn't started or created, so just return -1 right away
      return exitStatus;
    }
    try {
      exitStatus = process.waitFor();
    } catch (InterruptedException e) {
      log.error(e);
    } finally {
      // notify output and error read threads to stop reading
      stopStreamReadThreads();
    }
    return exitStatus;
  }

  /**
   * Starts the threads which will monitor the process' output streams.
   * 
   * @param processOut The process' system output stream.
   * @param processErr The process' system error stream.
   */
  private void startStreamReadThreads(InputStream processOut, InputStream processErr) {
    if (sysOutObserver != null) {
      commandOutputThread = new AsyncStreamReader(processOut, sysOutObserver, OutputType.OUT);
      commandOutputThread.start();
    }

    if (sysErrObserver != null) {
      commandErrorThread = new AsyncStreamReader(processErr, sysErrObserver, OutputType.ERR);
      commandErrorThread.start();
    }
  }

  /**
   * Stops a stream reader thread.
   * 
   * @param reader The reader to stop.
   * @param timeout The amount of time to wait for the reader to stop.
   */
  private void stopStreamReadThread(AsyncStreamReader reader, long timeout) {
    if (reader != null) {
      reader.setRunning(false);
      try {
        reader.join(timeout); // give thread a chance to continue reading process output
      } catch (InterruptedException e) {
        log.error(e);
      }
      if (reader.isAlive()) {
        reader.interrupt();
      }
    }
  }

  /**
   * Stops the threads which are monitoring the processes' output streams.
   */
  public void stopStreamReadThreads() {
    stopStreamReadThread(commandOutputThread, sysOutWaitTime);
    stopStreamReadThread(commandErrorThread, sysErrWaitTime);
  }

  public void setWorkingDirectory(File workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public long getSysOutWaitTime() {
    return sysOutWaitTime;
  }

  public void setSysOutWaitTime(long sysOutWaitTime) {
    this.sysOutWaitTime = sysOutWaitTime;
  }

  public long getSysErrWaitTime() {
    return sysErrWaitTime;
  }

  public void setSysErrWaitTime(long sysErrWaitTime) {
    this.sysErrWaitTime = sysErrWaitTime;
  }

  /**
   * @return the environment
   */
  public Map<String, String> getEnvironment() {
    return environment;
  }

  /**
   * @param environment the environment to set
   */
  public void setEnvironment(Map<String, String> environment) {
    this.environment = environment;
  }

  /**
   * Adds an environment variable.
   * 
   * @param key The environment variable name.
   * @param value The environment variable value.
   * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for <tt>key</tt>.
   *         (A <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt> with
   *         <tt>key</tt>, if the implementation supports <tt>null</tt> values.)
   */
  public String addEnvironmentVariable(String key, String value) {
    return this.environment.put(key, value);
  }

  /**
   * @return the sysOutObserver
   */
  public SysExecutorObserver getSysOutObserver() {
    return sysOutObserver;
  }

  /**
   * @param sysOutObserver the sysOutObserver to set
   */
  public void setSysOutObserver(SysExecutorObserver sysOutObserver) {
    this.sysOutObserver = sysOutObserver;
  }

  /**
   * @return the sysErrObserver
   */
  public SysExecutorObserver getSysErrObserver() {
    return sysErrObserver;
  }

  /**
   * @param sysErrObserver the sysErrObserver to set
   */
  public void setSysErrObserver(SysExecutorObserver sysErrObserver) {
    this.sysErrObserver = sysErrObserver;
  }

}
