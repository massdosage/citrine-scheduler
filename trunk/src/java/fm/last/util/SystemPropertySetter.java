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

import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class for setting system properties.
 */
public class SystemPropertySetter {

  /**
   * Sets the contents of the passed map as system properties. The key is the system property name and the value is the
   * property value.
   * 
   * @param propertyValues A map of system property keys and values to be set.
   */
  public SystemPropertySetter(Map<String, String> propertyValues) {
    for (Entry<String, String> entry : propertyValues.entrySet()) {
      System.setProperty(entry.getKey(), entry.getValue());
    }
  }

}
