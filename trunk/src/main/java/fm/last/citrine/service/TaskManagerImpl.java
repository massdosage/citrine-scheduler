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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import fm.last.citrine.dao.TaskDAO;
import fm.last.citrine.dao.TaskRunDAO;
import fm.last.citrine.model.Task;
import fm.last.citrine.scheduler.SchedulerManager;

/**
 * TaskManager implementation.
 */
public class TaskManagerImpl implements TaskManager, BeanFactoryAware {

  private final SchedulerManager schedulerManager;
  private final TaskDAO taskDAO;
  private TaskRunDAO taskRunDAO;

  public TaskManagerImpl(TaskDAO taskDAO, SchedulerManager schedulerManager) throws SchedulerException, ParseException {
    this.schedulerManager = schedulerManager;
    this.taskDAO = taskDAO;
  }

  @Override
  public void scheduleTasks() {
    List<Task> tasks = getTasks();
    for (Task task : tasks) {
      if (task.isEnabled()) {
        schedulerManager.scheduleTask(task, false);
      }
    }
  }

  /**
   * Retrieves a Task by its primary key.
   * 
   * @param id Task id.
   * @return Task identified by the passed id.
   */
  @Override
  public Task get(long id) {
    return taskDAO.get(id);
  }

  /**
   * Creates a or updates a Task object in storage.
   * 
   * @param task Task to save.
   */
  @Override
  public void save(Task task) {
    taskDAO.save(task);
    schedulerManager.scheduleTask(task, true);
  }

  /**
   * Retrieves all Tasks in storage.
   * 
   * @return List of all Tasks in storage.
   */
  @Override
  public List<Task> getTasks() {
    return taskDAO.getTasks();
  }

  @Override
  public List<Task> findByGroup(String group) {
    return taskDAO.findByGroup(group);
  }

  @Override
  public List<Task> findTasksInSameGroup(Task task) {
    // would be more efficient to add this method to DAO and do an exclude the passed task in select statement
    List<Task> tasks = taskDAO.findByGroup(task.getGroupName());
    tasks.remove(task);
    return tasks;
  }

  @Override
  public Set<Task> findCandidateChildren(Task task) {
    return taskDAO.findCandidateChildren(task);
  }

  @Override
  public List<Task> getCurrentlyRunningTasks() {
    List<Long> taskIds = schedulerManager.getCurrentlyRunningTaskIds();
    List<Task> runningTasks = new ArrayList<Task>(taskIds.size());
    for (Long taskId : taskIds) {
      runningTasks.add(get(taskId));
    }
    return runningTasks;
  }

  @Override
  public Set<String> getGroupNames() {
    return taskDAO.getGroupNames();
  }

  /**
   * Deletes a Task.
   * 
   * @param task Task to delete;
   */
  @Override
  public void delete(Task task) {
    schedulerManager.unscheduleTask(task);
    taskRunDAO.deleteByTaskId(task.getId()); // manually manage this association
    taskDAO.delete(task);
  }

  public TaskRunDAO getTaskRunDAO() {
    return taskRunDAO;
  }

  public void setTaskRunDAO(TaskRunDAO taskRunDAO) {
    this.taskRunDAO = taskRunDAO;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    // for some bizarre reason setBeanFactory gets called on this class *before* it gets called on
    // schedulerManager, and we need it set on schedulerManager during init-method, set set it now
    schedulerManager.setBeanFactory(beanFactory);
  }

}
