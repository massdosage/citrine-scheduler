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
package fm.last.citrine.jobs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fm.last.citrine.jobs.syscommand.RollingFileExecutorObserver;
import fm.last.commons.test.LastTestCase;
import fm.last.syscommand.SysCommandExecutor;

/**
 * Unit test case for the SystemExecJob.
 */
public class SystemExecJobTest extends LastTestCase {

  private SystemExecJob execJob = new SystemExecJob();
  private RollingFileExecutorObserver observer = new RollingFileExecutorObserver();
  private SysCommandExecutor executor = new SysCommandExecutor();

  @Before
  public void setUp() {
    observer.setBaseLogPath(testTempFolder.getAbsolutePath() + "/");
    execJob.setExecutor(executor);
    execJob.setObserver(observer);
  }

  @Test
  public void testExec() throws Exception {
    execJob.execute("ls -al");
    assertNotNull(execJob.getCommandOutput());
    assertNull(execJob.getCommandError());
  }

  @Test
  public void testInterrupt() throws Exception {
    Thread thread = new Thread(new SystemExecJobRunner(execJob, "sleep 15"));
    thread.start();
    Thread.sleep(1000); // give the job a chance to get going
    execJob.interrupt();
    Thread.sleep(1000); // give the job a chance to die
    assertFalse(thread.isAlive());
  }

  /**
   * Simple class to run the SystemExecJob in a separate thread so it can be interrupted.
   */
  class SystemExecJobRunner implements Runnable {

    private SystemExecJob job;
    private String command;

    public SystemExecJobRunner(SystemExecJob job, String command) {
      this.job = job;
      this.command = command;
    }

    @Override
    public void run() {
      try {
        job.execute(command);
      } catch (Exception e) {
        Assert.fail(e.getMessage());
      }
    }

  }

}
