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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import fm.last.syscommand.SysCommandExecutor.OutputType;

/**
 * A Thread implementation that reads asynchronously from an InputStream. Callers *must* call setRunning(false) when
 * they are done with this, otherwise it will continue to try reading from the stream indefinitely.
 */
public class AsyncStreamReader extends Thread {

  private static Logger log = Logger.getLogger(AsyncStreamReader.class);
  private InputStream inputStream = null;

  private OutputType outputType;

  private SysExecutorObserver observer;

  private boolean running = false;

  /**
   * Constructs a new instance, no monitoring of the inputstream will occur until start() is called.
   * 
   * @param inputStream The input stream to read from.
   * @param observer SysExecutorObserver that will receive output from standard out or err.
   * @param outputType The type of output being read.
   */
  public AsyncStreamReader(InputStream inputStream, SysExecutorObserver observer, OutputType outputType) {
    this.inputStream = inputStream;
    this.observer = observer;
    this.outputType = outputType;
  }

  /**
   * Thread's run method (called via start()).
   */
  public void run() {
    running = true;
    try {
      readCommandOutput();
    } catch (Exception e) {
      log.error("Error running stream reader", e);
    }
    running = false;
  }

  /**
   * The method which does the main work of reading from the input stream.
   * 
   * @throws IOException If an error occurs reading from the stream.
   */
  private void readCommandOutput() throws IOException {
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;
    try {
      inputStreamReader = new InputStreamReader(inputStream);
      bufferedReader = new BufferedReader(inputStreamReader);
      String line = null;
      while (running) {
        while ((line = readLine(bufferedReader)) != null) {
          if (OutputType.OUT.equals(outputType)) {
            observer.sysOut(line);
          } else {
            observer.sysErr(line);
          }
        }

        if (line == null) {
          // no ouput from process, this happens with process streams, after a wait
          // there might actually be some more, i.e. null does not necessarily mean stream is empty
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            log.error(e);
          }
        }
      }
    } finally {
      IOUtils.closeQuietly(bufferedReader);
      IOUtils.closeQuietly(inputStreamReader);
    }
  }

  private String readLine(BufferedReader bufferedReader) throws IOException {
    try {
      return bufferedReader.readLine();
    } catch (IOException e) {
      // see http://code.google.com/p/citrine-scheduler/issues/detail?id=32 - ugly but no known alternative
      if (e.getMessage().contains("Bad file descriptor")) {
        log.warn("Nothing to read from stream, returning null");
        return null;
      } else {
        throw e;
      }
    }
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

}
