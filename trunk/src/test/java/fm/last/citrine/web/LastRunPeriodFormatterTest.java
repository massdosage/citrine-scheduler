/*
 * Copyright 2012 Last.fm
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
package fm.last.citrine.web;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import fm.last.citrine.model.TaskRun;

public class LastRunPeriodFormatterTest {

  private final LastRunPeriodFormatter formatter = new LastRunPeriodFormatter();

  @Test
  public void testFormatterNormal() {
    DateTime startDate = new DateTime();
    startDate = startDate.minusYears(1).minusMonths(12).minusHours(124).minusMinutes(25).minusSeconds(1);
    TaskRun mostRecentTaskRun = new TaskRun(startDate.toDate(), startDate.plusHours(8).toDate(), "", "", "", 1);
    String printLastRun = formatter.printLastRun(mostRecentTaskRun);
    assertEquals("2 years, 5 days, 4 hours and 25 minutes ago", printLastRun);
  }

  @Test
  public void testFormatterPluralForm() {
    DateTime startDate = new DateTime();
    startDate = startDate.minusYears(2).minusMonths(2).minusDays(2).minusHours(2).minusMinutes(2);
    TaskRun mostRecentTaskRun = new TaskRun(startDate.toDate(), startDate.plusHours(8).toDate(), "", "", "", 1);
    String printLastRun = formatter.printLastRun(mostRecentTaskRun);
    assertEquals("2 years, 2 months, 2 days, 2 hours and 2 minutes ago", printLastRun);
  }

  @Test
  public void testFormatterSingularForm() {
    DateTime startDate = new DateTime();
    startDate = startDate.minusYears(1).minusMonths(1).minusDays(1).minusHours(1).minusMinutes(1);
    TaskRun mostRecentTaskRun = new TaskRun(startDate.toDate(), startDate.plusHours(8).toDate(), "", "", "", 1);
    String printLastRun = formatter.printLastRun(mostRecentTaskRun);
    assertEquals("1 year, 1 month, 1 day, 1 hour and 1 minute ago", printLastRun);
  }

  @Test
  public void testNoAndSeparator() {
    DateTime startDate = new DateTime();
    startDate = startDate.minusHours(2);
    TaskRun mostRecentTaskRun = new TaskRun(startDate.toDate(), startDate.plusHours(8).toDate(), "", "", "", 1);
    String printLastRun = formatter.printLastRun(mostRecentTaskRun);
    assertEquals("2 hours ago", printLastRun);

    startDate = new DateTime();
    startDate = startDate.minusMinutes(1);
    mostRecentTaskRun = new TaskRun(startDate.toDate(), startDate.plusSeconds(8).toDate(), "", "", "", 1);
    printLastRun = formatter.printLastRun(mostRecentTaskRun);
    assertEquals("1 minute ago", printLastRun);
  }

  @Test
  public void testFormatterNullTask() {
    String printLastRun = formatter.printLastRun(null);
    assertEquals("Never", printLastRun);
  }

  @Test
  public void testTimeWithinOneMinute() {
    DateTime startDate = new DateTime();
    TaskRun mostRecentTaskRun = new TaskRun(startDate.minusSeconds(5).toDate(), startDate.toDate(), "", "", "", 1);
    String printLastRun = formatter.printLastRun(mostRecentTaskRun);
    assertEquals("0 minutes ago", printLastRun);
  }

}
