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

import fm.last.citrine.model.Task;

/**
 * Data transfer object for the Task edit view.
 */
public class TaskDTO {

  private Task task = new Task();
  private String selectedGroupName;

  public TaskDTO() {
  }

  public TaskDTO(Task task) {
    setTask(task);
  }

  /**
   * @return the task
   */
  public Task getTask() {
    return task;
  }

  /**
   * @param task the task to set
   */
  public void setTask(Task task) {
    this.task = task;
  }

  /**
   * @return the selectedGroupName
   */
  public String getSelectedGroupName() {
    return selectedGroupName;
  }

  /**
   * @param selectedGroupName the selectedGroupName to set
   */
  public void setSelectedGroupName(String selectedGroupName) {
    this.selectedGroupName = selectedGroupName;
  }

}
