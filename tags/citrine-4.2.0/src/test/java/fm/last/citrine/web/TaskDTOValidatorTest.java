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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fm.last.citrine.model.Task;

/**
 * Unit test for the TaskDTOValidator.
 */
public class TaskDTOValidatorTest {

  private Validator validator = new TaskDTOValidator();
  // a valid task
  private Task task = new Task("taskName", "groupName", "beanName");
  private TaskDTO taskDTO = new TaskDTO(task);
  private Errors errors = new BindException(taskDTO, "");

  @Test
  public void testValid() {
    validator.validate(taskDTO, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  public void testMissingName() {
    task.setName(null);
    validator.validate(taskDTO, errors);
    assertTrue(errors.hasErrors());
    assertEquals(1, errors.getFieldErrors().size());
  }

  @Test
  public void testIllegalGroupName() {
    task.setGroupName(Constants.GROUP_NAME_ALL);
    validator.validate(taskDTO, errors);
    assertTrue(errors.hasErrors());
    assertEquals(1, errors.getFieldErrors().size());
  }

  @Test
  public void testIllegalGroupNameWithWhiteSpace() {
    task.setGroupName("  " + Constants.GROUP_NAME_ALL + "  ");
    validator.validate(taskDTO, errors);
    assertTrue(errors.hasErrors());
    assertEquals(1, errors.getFieldErrors().size());
  }

  @Test
  public void testMissingGroupName() {
    task.setGroupName(null);
    validator.validate(taskDTO, errors);
    assertTrue(errors.hasErrors());
    assertEquals(1, errors.getFieldErrors().size());
  }

  @Test
  public void testMissingNameAndGroupName() {
    task.setName(null);
    task.setGroupName(null);
    validator.validate(taskDTO, errors);
    assertTrue(errors.hasErrors());
    assertEquals(2, errors.getFieldErrors().size());
  }

  @Test
  public void testTimerSchedule_EmptyString() {
    task.setTimerSchedule("");
    validator.validate(taskDTO, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  public void testTimerSchedule_Valid() {
    task.setTimerSchedule("");
    validator.validate(taskDTO, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  public void testTimerSchedule_Invalid() {
    task.setTimerSchedule("bla");
    validator.validate(taskDTO, errors);
    assertTrue(errors.hasErrors());
  }

  @Test
  public void testTimerSchedule_Invalid2() {
    task.setTimerSchedule("0 15 * ? * ? *");
    validator.validate(taskDTO, errors);
    assertTrue(errors.hasErrors());
  }

}
