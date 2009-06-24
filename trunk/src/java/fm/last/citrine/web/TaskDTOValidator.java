/*
 * Copyright 2009 Last.fm
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

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import fm.last.citrine.model.Task;

/**
 * Validator for Task DTO.
 */
public class TaskDTOValidator implements Validator {

  @Override
  public boolean supports(Class someClass) {
    return TaskDTO.class.equals(someClass);
  }

  @Override
  public void validate(Object object, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "task.groupName", "group.empty");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "task.name", "name.empty");

    Task task = ((TaskDTO) object).getTask();
    if (!StringUtils.isEmpty(task.getTimerSchedule())) {
      // if there is a timer schedule, check it is valid for quartz cron trigger
      try {
        new CronExpression(task.getTimerSchedule());
      } catch (ParseException e) {
        errors.rejectValue("task.timerSchedule", "timer.schedule.invalid", e.getMessage());
      }
    }
    if (task.getGroupName() != null && Constants.GROUP_NAME_ALL.equals(task.getGroupName().trim())) {
      errors.rejectValue("task.groupName", "group.illegal", Constants.GROUP_NAME_ALL + " not allowed as group name");
    }
  }

}
