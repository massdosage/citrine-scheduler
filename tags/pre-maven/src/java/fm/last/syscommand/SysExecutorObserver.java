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

/**
 * Interface for classes which observer the running of a command via a SysCommandExecutor.
 */
public interface SysExecutorObserver {

  /**
   * Process the passed string which was sent to System.Out.
   * 
   * @param sysOut System.out string.
   */
  public void sysOut(String sysOut);

  /**
   * Process the passed string which was sent to System.Err.
   * 
   * @param sysErr System.err string.
   */
  public void sysErr(String sysErr);

  /**
   * Gets the String sent to System.Out (not all observers need return this, e.g. if they don't store the string set).
   * 
   * @return The strings sent to System.out or null if nothing sent or the particular observer implementation doesn't
   *         store this.
   */
  public String getSysOut();

  /**
   * Gets the String sent to System.Err (not all observers need return this, e.g. if they don't store the string set).
   * 
   * @return The strings sent to System.err or null if nothing sent or the particular observer implementation doesn't
   *         store this.
   */
  public String getSysErr();

  /**
   * Called when the observer is no longer needed so that it can close any resources it may have open.
   */
  public void close();

}
