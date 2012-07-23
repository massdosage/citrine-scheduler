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
package fm.last.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test case for the StringUtils class.
 */
public class StringUtilsTest {

  @Test
  public void testTruncate() {
    String input = "abcdefghij";
    assertEquals("abc", StringUtils.truncate(input, 3));
    assertEquals(input, StringUtils.truncate(input, input.length()));
    assertEquals(input, StringUtils.truncate(input, input.length() + 10));
  }

  @Test
  public void testTruncate_Null() {
    assertEquals(null, StringUtils.truncate(null, 3));
  }

  @Test
  public void testMatchingStartSubstring() {
    assertEquals("abc", StringUtils.matchingStartSubstring("abc", "abc"));
    assertEquals("abc", StringUtils.matchingStartSubstring("abc", "abcdef"));
    assertEquals("abc", StringUtils.matchingStartSubstring("abcdef", "abc"));
    assertEquals("", StringUtils.matchingStartSubstring("abcdef", "xyz"));
    assertEquals("", StringUtils.matchingStartSubstring("abcdef", "xbcdef"));
    assertEquals("", StringUtils.matchingStartSubstring("abcdef", "xabc"));
    assertEquals("", StringUtils.matchingStartSubstring("", ""));
    assertEquals("", StringUtils.matchingStartSubstring("a", ""));
    assertEquals("", StringUtils.matchingStartSubstring("", "a"));
    // and what we really wanted this method for
    assertEquals(
        "/stats/label_reporting/xml/incoming/umg/",
        StringUtils
            .matchingStartSubstring(
                "/stats/label_reporting/xml/incoming/umg/",
                "/stats/label_reporting/xml/incoming/umg/00602517655362_1000000178594/UMG_metdat_LastFM_US_New_00602517655362_2008-04-23_15-36-01.xml"));
  }

  @Test
  public void testExtractStackTraceString_Throwable() {
    Throwable t = new Throwable("test");
    String stack = StringUtils.extractStackTraceString(t);
    // don't check entire string, just check a bit of it
    assertTrue(stack.startsWith("java.lang.Throwable: test"));
    assertTrue(stack.contains("at fm.last.util.StringUtilsTest.testExtractStackTraceString"));
  }

  @Test
  public void testExtractStackTraceString_Exception() {
    Exception e = new Exception("test");
    String stack = StringUtils.extractStackTraceString(e);
    // don't check entire string, just check a bit of it
    assertTrue(stack.startsWith("java.lang.Exception: test"));
    assertTrue(stack.contains("at fm.last.util.StringUtilsTest.testExtractStackTraceString"));
  }

  @Test
  public void testRemoveNonPrintableWhitespace() {
    assertEquals(null, StringUtils.removeNonPrintableWhitespace(null));
    assertEquals("Herbie Hancock", StringUtils.removeNonPrintableWhitespace("Herbie Hancock"));
    assertEquals("Herbie Hancock", StringUtils.removeNonPrintableWhitespace("Herbie\t Hancock"));
    assertEquals("Herbie Hancock", StringUtils.removeNonPrintableWhitespace("Herbie\t Hancock\r\f"));
    assertEquals("Herbie Hancock", StringUtils.removeNonPrintableWhitespace("Herbie\t Hancock\r\n"));
    assertEquals("Herbie Hancock", StringUtils.removeNonPrintableWhitespace("Her\tbie\t\t Hancock\n\n"));
  }

}
