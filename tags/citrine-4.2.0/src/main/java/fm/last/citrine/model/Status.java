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
package fm.last.citrine.model;

/**
 * Enumeration representing the various Status values that a TaskRun can have.
 */
public enum Status {
  UNKNOWN(0), INITIALISING(5), /* not currently used */
  RUNNING(10), /* job is running */
  CANCELLING(15), /* received request to cancel by user */
  CANCELLED(20), /* cancelled by user */
  INTERRUPTED(21), /* interrupted (e.g. during shutdown) */
  ABORTED(21), /* some condition caused it to abort running (e.g. was already running when triggered) */
  FAILED(30), /* finished with an error */
  SUCCESS(40); /* finished with no errors */

  private int value;

  Status(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
