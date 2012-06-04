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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for doing string manipulation.
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

  /**
   * Escapes the unicode in the given string.
   * 
   * @param input The input string.
   * @param escapeAscii Whether to escape ASCII or not.
   * @return The escaped string.
   */
  public static String escapeUnicodeString(final String input, final boolean escapeAscii) {
    StringBuffer returnValue = new StringBuffer("");
    for (int i = 0; i < input.length(); i++) {
      char ch = input.charAt(i);
      if (!escapeAscii && ((ch >= 0x0020) && (ch <= 0x007e))) {
        returnValue.append(ch);
      } else {
        returnValue.append("\\u");

        String hex = Integer.toHexString(input.charAt(i) & 0xFFFF);
        if (hex.length() == 2) {
          returnValue.append("00");
        }
        returnValue.append(hex.toUpperCase());
      }
    }
    return returnValue.toString();
  }

  /**
   * Truncates the passed String to the passed maximum length, if it exceeds the maximum length.
   * 
   * @param input String to truncate.
   * @param maxLength Maximum length for String.
   * @return Input string truncated to the max length.
   */
  public static String truncate(String input, int maxLength) {
    if (input != null && input.length() > maxLength) {
      return input.substring(0, maxLength);
    }
    return input;
  }

  /**
   * Extracts the stack trace from the passed exception into a string.
   * 
   * @param t Throwable to extract stack trace from.
   * @return String representation of the stack trace.
   */
  public static String extractStackTraceString(Throwable t) {
    StringWriter sw = new StringWriter();
    t.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  /**
   * Returns any part of the two passed strings that matches from the beginning up until the point they share no
   * characters in common or the length of one the passeds string is exceeded.
   * 
   * @param string1 The first string.
   * @param string2 The second string.
   * @return The "common" beginning part of the two strings.
   */
  public static String matchingStartSubstring(String string1, String string2) {
    StringBuilder match = new StringBuilder();
    for (int i = 0; i < string1.length(); i++) {
      if (string1.length() > i && string2.length() > i) {
        if (string1.charAt(i) == string2.charAt(i)) {
          match.append(string1.charAt(i));
        } else {
          break;
        }
      }
    }
    return match.toString();
  }

  /**
   * Strips "non-printable" whitespace (carriage returns, tabs, linefeeds etc.) from the passed string.
   * 
   * @param input Input String.
   * @return The string with non-printable whitespace removed.
   */
  public static String removeNonPrintableWhitespace(String input) {
    if (input != null) {
      input = input.replaceAll("[\\f\\t\\r\\n]+", "");
    }
    return input;
  }

}
