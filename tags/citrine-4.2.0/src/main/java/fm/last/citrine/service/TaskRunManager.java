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

import org.joda.time.DateTime;
import org.quartz.JobListener;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.TaskRun;

/**
 * Manages TaskRuns.
 */
public interface TaskRunManager extends JobListener {

  /**
   * Creates a or updates a TaskRun object in storage.
   * 
   * @param taskRun TaskRun to save.
   */
  public void save(TaskRun taskRun);

  /**
   * Retrieves a TaskRun by its primary key.
   * 
   * @param id TaskRun id.
   * @return TaskRun identified by the passed id.
   */
  public TaskRun get(long id);

  /**
   * Finds TaskRun that belong to a certain Task.
   * 
   * @param jobId The Task ID.
   * @return List of matching TaskRuns.
   */
  public List<TaskRun> findByTaskId(long jobId);

  /**
   * Finds TaskRuns that belong to a certain Task. Returned List is ordered by TaskRun creation (most recent TaskRun
   * first).
   * 
   * @param taskId The Task ID.
   * @param firstResult The position of the first result.
   * @param maxResults The maximum number of results.
   * @return List of matching TaskRuns.
   */
  public List<TaskRun> findByTaskId(final long taskId, final int firstResult, final int maxResults);

  /**
   * Deletes the TaskRun identified by the passed ID.
   * 
   * @param taskRunId ID of the TaskRun to delete.
   */
  public void delete(long taskRunId);
  

  /**
   * Deletes all TaskRuns which have an end date on or before the passed date.
   * 
   * @param before DateTime to delete TaskRuns before (inclusive).
   */
  public void deleteBefore(DateTime before);

  /**
   * Determines whether there is an actively running TaskRun for the passed task.
   * 
   * @param taskId ID of task.
   * @return Whether there is a runnning TaskRun or not.
   */
  public boolean isRunning(long taskId);

  /**
   * Inform the task run manager that it needs to shut itself down, release all resources etc.
   */
  public void shutdown();

  /**
   * Sets the Status of the passed JobRun.
   * 
   * @param taskRun TaskRun to change status.
   * @param status New status.
   */
  public void setStatus(TaskRun taskRun, Status status);

  /**
   * Gets the most recent TaskRun (i.e. with the latest StartDate) for the passed task.
   * 
   * @param taskId Id of the Task.
   * @return The most recent TaskRun, or null if none could be found.
   */
  public TaskRun getMostRecent(long taskId);

  /**
   * Attempts to stop a running TaskRun.
   * 
   * @param taskRunId The id of the TaskRun to stop.
   * @return True if the TaskRun was stopped, false if the TaskRun could not be stopped.
   */
  public boolean stop(long taskRunId);

}
