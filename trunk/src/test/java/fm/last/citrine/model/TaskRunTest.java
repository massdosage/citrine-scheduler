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

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;

/**
 * Unit Test for TaskRun.
 */
public class TaskRunTest {

  @Test
  public void testEquals_StartDate_Date() {
    Date startDate = new Date();
    TaskRun run1 = new TaskRun();
    TaskRun run2 = new TaskRun();
    run1.setStartDate(startDate);
    run2.setStartDate(startDate);
    assertEquals(run1, run2);
  }

  @Test
  public void testEquals_StartDate_Timestamp() {
    Date startDate = new Date();
    TaskRun run1 = new TaskRun();
    TaskRun run2 = new TaskRun();
    run1.setStartDate(startDate);
    run2.setStartDate(new Timestamp(startDate.getTime()));
    assertEquals(run1, run2);
  }
  
  @Test
  public void testEquals_StartDate_Rounding() {
    Date startDate = new Date(1229446180131L);
    Date startDate2 = new Date(1229446180000L);
    TaskRun run1 = new TaskRun();
    TaskRun run2 = new TaskRun();
    run1.setStartDate(startDate);
    run2.setStartDate(startDate2);
    assertEquals(run1, run2);
  }
  
  @Test
  public void testEquals_EndDate_Rounding() {
    Date endDate = new Date(1229446180131L);
    Date endDate2 = new Date(1229446180000L);
    TaskRun run1 = new TaskRun();
    TaskRun run2 = new TaskRun();
    run1.setEndDate(endDate);
    run2.setEndDate(endDate2);
    assertEquals(run1, run2);
  }

}
