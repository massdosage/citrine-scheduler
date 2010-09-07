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
package fm.last.citrine.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Request related utility functions.
 */
public final class RequestUtils {

  /**
   * Private constructor to prevent direct instantiation.
   */
  private RequestUtils() {
  }

  /**
   * Retrieves a long value from the passed request.
   * 
   * @param request Request.
   * @param paramName The name of the parameter.
   * @return The value of the parameter as a Long.
   * @throws ServletException If the parameter was not set on the request.
   */
  public static Long getLongValue(HttpServletRequest request, String paramName) throws ServletException {
    return getLongValue(request, paramName, true);
  }

  /**
   * Retrieves a long value from the passed request.
   * 
   * @param request Request.
   * @param paramName The name of the parameter.
   * @param required Whether the parameter is required or not.
   * @return The value of the parameter as a Long, or null if the parameter was not set and required is false.
   * @throws ServletException If the parameter was not set on the request and required is true.
   */
  public static Long getLongValue(HttpServletRequest request, String paramName, boolean required)
    throws ServletException {
    String idString = request.getParameter(paramName);
    if (idString != null) {
      return Long.valueOf(idString);
    } else {
      if (required) {
        throw new ServletException(paramName + " required");
      }
    }
    return null;
  }

}
