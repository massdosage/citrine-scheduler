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
package fm.last.citrine.service;

import java.util.List;
import java.util.Set;

import fm.last.citrine.model.Task;

/**
 * Manages Tasks.
 */
public interface TaskManager {

  /**
   * Retrieves a Task by its primary key.
   * 
   * @param id Task id.
   * @return Task identified by the passed id.
   */
  public Task get(long id);

  /**
   * Creates a or updates a Task object in storage.
   * 
   * @param task Task to save.
   */
  public void save(Task task);

  /**
   * Retrieves all Task in storage.
   * 
   * @return List of all Tasks in storage.
   */
  public List<Task> getTasks();

  /**
   * Retrieves all Task belonging to the same group.
   * 
   * @param group Name of group.
   * @return Tasks which are in the passed group.
   */
  public List<Task> findByGroup(String group);

  /**
   * Retrieves Tasks which are in the same group as the passed Tasks (and excluding the passed Task itself).
   * 
   * @param task Task to match group.
   * @return List of Tasks in the same group.
   */
  public List<Task> findTasksInSameGroup(Task task);

  /**
   * Finds Tasks who are valid candidates to be added as children to the passed Task. This finds Tasks in the same
   * groups and who are not already children or parents of the passed Task.
   * 
   * @param task Task to find candidate children for.
   * @return Set of candidate child Tasks.
   */
  public Set<Task> findCandidateChildren(Task task);

  /**
   * Deletes a Task.
   * 
   * @param task Task to delete;
   */
  public void delete(Task task);

  /**
   * Schedules all tasks.
   */
  public void scheduleTasks();

  /**
   * Retrieves a List of Tasks which are currently running.
   * 
   * @return A list of currently running tasks;
   */
  public List<Task> getCurrentlyRunningTasks();

  /**
   * Gets all group names.
   * 
   * @return All group names.
   */
  public Set<String> getGroupNames();

}
