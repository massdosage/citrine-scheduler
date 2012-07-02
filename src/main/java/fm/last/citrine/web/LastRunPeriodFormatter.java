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

import java.util.concurrent.TimeUnit;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import fm.last.citrine.model.TaskRun;

public class LastRunPeriodFormatter {

  private static final String FINAL_SEPARATOR = " and ";
  private static final String SEPARATOR = ", ";
  private final PeriodFormatter periodFormatter = new PeriodFormatterBuilder().appendYears()
      .appendSuffix(" year", " years").appendSeparator(SEPARATOR, FINAL_SEPARATOR).appendMonths()
      .appendSuffix(" month", " months").appendSeparator(SEPARATOR, FINAL_SEPARATOR).appendWeeks()
      .appendSuffix(" week", " weeks").appendSeparator(SEPARATOR, FINAL_SEPARATOR).appendDays()
      .appendSuffix(" day", " days").appendSeparator(SEPARATOR, FINAL_SEPARATOR).appendHours()
      .appendSuffix(" hour", " hours").appendSeparator(SEPARATOR, FINAL_SEPARATOR).appendMinutes()
      .appendSuffix(" minute", " minutes").toFormatter();

  public String printLastRun(TaskRun mostRecentTaskRun) {
    String result = "Never";
    if (mostRecentTaskRun != null) {
      // Just started jobs give a time-stamp of less then a minute need to set it to zero otherwise nothing gets printed
      long now = System.currentTimeMillis();
      long recentTaskTime = mostRecentTaskRun.getStartDate().getTime();
      long duration = now - recentTaskTime;
      Period period;
      if (duration < TimeUnit.MINUTES.toMillis(1)) {
        period = new Period(0);
      } else {
        period = new Period(recentTaskTime, now);
      }
      result = periodFormatter.print(period) + " ago";
    }
    return result;
  }

}
