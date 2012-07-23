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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Useful utility methods for dealing with command line strings etc.
 */
public final class SysCommandUtils {

  private SysCommandUtils() {

  }

  /**
   * Converts a command line string to a List of Strings that can be passed to a ProcessBuilder. Splits string by space
   * characters but allows whitespace if contained within single or double quotes or for whitespace to be escaped by
   * "\".
   * 
   * @param commandString Command line string.
   * @return The commandString converted to a List of Strings.
   */
  public static List<String> convertCommand(String commandString) {
    List<String> command = new ArrayList<String>();
    StringBuilder currentCommand = new StringBuilder();
    boolean insideSingleQuotes = false;
    boolean insideDoubleQuotes = false;
    for (int i = 0; i < commandString.length(); i++) {
      Character previousChar = null;
      if (i > 0) {
        previousChar = commandString.charAt(i - 1);
      }
      char currentChar = commandString.charAt(i);
      if (currentChar == ' ' && !insideSingleQuotes && !insideDoubleQuotes
          && (previousChar != null && previousChar != '\\')) {
        if (!StringUtils.isEmpty(currentCommand.toString())) {
          command.add(currentCommand.toString().trim());
          currentCommand = new StringBuilder();
          continue;
        }
      }
      if (currentChar == '\'') {
        insideSingleQuotes = !insideSingleQuotes;
      } else if (currentChar == '\"') {
        insideDoubleQuotes = !insideDoubleQuotes;
      }
      currentCommand.append(currentChar);
    }

    if (!StringUtils.isEmpty(currentCommand.toString())) {
      command.add(currentCommand.toString().trim());
    }

    return command;
  }

}
