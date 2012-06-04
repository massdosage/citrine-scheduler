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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import fm.last.citrine.service.LogFileManager;

public class DisplayLogsControllerTest {

  private DisplayLogsController displayLogsController = new DisplayLogsController();

  @Mock
  private LogFileManager mockLogFileManager;

  private MockHttpServletRequest mockRequest = new MockHttpServletRequest();

  private MockHttpServletResponse mockResponse = new MockHttpServletResponse();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    displayLogsController.setLogFileManager(mockLogFileManager);
  }

  @Test
  public void testNoLoList() throws Exception {
    List<String> logFiles = new ArrayList<String>();
    logFiles.add("log1.log");
    logFiles.add("log2.log");
    when(mockLogFileManager.findAllLogFiles()).thenReturn(logFiles);
    ModelAndView modelAndView = displayLogsController.list(mockRequest, mockResponse);
    assertEquals("logs_list", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(1, model.size());
    assertEquals(logFiles, model.get("logFiles"));
  }

  @Test
  public void testDisplay() throws Exception {
    String fileContent = "bla";
    String logFileName = "log000.log";
    when(mockLogFileManager.tail(logFileName, displayLogsController.getTailBytes())).thenReturn(fileContent);
    mockRequest.setParameter(DisplayLogsController.PARAM_LOG_FILE, logFileName);
    ModelAndView modelAndView = displayLogsController.display(mockRequest, mockResponse);
    assertEquals("log_display", modelAndView.getViewName());
    Map<String, Object> model = modelAndView.getModel();
    assertEquals(3, model.size());
    assertEquals(fileContent, model.get("contents"));
    assertEquals(null, model.get("taskId")); // didn't pass one in for this test
    assertEquals(null, model.get("selectedGroupName")); // didn't pass one in for this test
  }

  @Test
  public void setSetTailBytes() {
    displayLogsController.setTailBytes(500);
    assertEquals(500, displayLogsController.getTailBytes());
  }

}
