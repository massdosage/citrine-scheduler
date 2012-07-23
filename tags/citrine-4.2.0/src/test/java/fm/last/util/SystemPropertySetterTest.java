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
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test case for the SystemPropertySetter.
 */
public class SystemPropertySetterTest {

  @Test
  public void test() {
    String key = "fm.last.test.key";
    String value = "foo";
    Map<String, String> propertyMap = new HashMap<String, String>();
    propertyMap.put(key, value);
    assertNull(System.getProperty(key));
    new SystemPropertySetter(propertyMap);
    assertEquals(value, System.getProperty(key));
  }

}
