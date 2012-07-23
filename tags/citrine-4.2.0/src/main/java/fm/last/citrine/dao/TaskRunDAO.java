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
package fm.last.citrine.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fm.last.citrine.model.Status;
import fm.last.citrine.model.TaskRun;

/**
 * Data Access Object for managing storage of TaskRuns.
 */
public class TaskRunDAO extends HibernateDaoSupport {

  private static Logger log = Logger.getLogger(TaskRunDAO.class);

  /**
   * Maximum number of TaskRuns that will be returned when finding JobRuns for a Job.
   */
  private int maxTaskRunResults = 0;

  /**
   * Creates a or updates a TaskRun object in storage.
   * 
   * @param taskRun TaskRun to save.
   */
  public void save(TaskRun taskRun) {
    getHibernateTemplate().saveOrUpdate(taskRun);
  }

  /**
   * Retrieves a TaskRun by its primary key.
   * 
   * @param id TaskRun id.
   * @return TaskRun identified by the passed id.
   */
  public TaskRun get(long id) {
    return (TaskRun) getHibernateTemplate().get(TaskRun.class, id);
  }

  /**
   * Finds TaskRuns that belong to a certain Task. Returned List is ordered by TaskRun creation (most recent TaskRun
   * first).
   * 
   * @param taskId The Task ID.
   * @param firstResult The position of the first result.
   * @param maxResults The maximum number of results.
   * @return List of matching TaskRuns.
   */
  public List<TaskRun> findByTaskId(final long taskId, final int firstResult, final int maxResults) {
    return getHibernateTemplate().executeFind(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        session.clear(); // need this as sometimes this retrieves older data than what is in DB...
        Query query = session.createQuery("from TaskRun where taskId = :taskId order by id DESC");
        query.setLong("taskId", taskId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        List list = query.list();
        return list;
      }
    });
  }

  /**
   * Finds TaskRuns that belong to a certain Task. Returned List is ordered by TaskRun creation (most recent TaskRun
   * first).
   * 
   * @param taskId The Task ID.
   * @return List of matching TaskRuns.
   */
  public List<TaskRun> findByTaskId(final long taskId) {
    return this.findByTaskId(taskId, -1, maxTaskRunResults);
  }

  /**
   * Deletes the TaskRun identified by the passed ID.
   * 
   * @param taskRunId ID of the TaskRun to delete.
   */
  public void delete(long taskRunId) {
    getHibernateTemplate().bulkUpdate("delete from TaskRun where id=?", taskRunId);
  }

  /**
   * Deletes all TaskRuns with the passed JobId.
   * 
   * @param taskId Task ID.
   */
  public void deleteByTaskId(long taskId) {
    getHibernateTemplate().bulkUpdate("delete from TaskRun where taskId=?", taskId);
  }

  /**
   * Deletes all TaskRuns which have an end date on or before the passed date.
   * 
   * @param before DateTime to delete TaskRuns before (inclusive).
   */
  public void deleteBefore(DateTime before) {
    getHibernateTemplate().bulkUpdate("delete from TaskRun where endDate<=?", before.toDate());
  }

  /**
   * Determines whether there are any open (running or initialising) TaskRuns for the Task identified by the passed ID.
   * 
   * @param taskId The task ID.
   * @return True if there are running tasks, false otherwise.
   */
  public boolean isRunning(long taskId) {
    int count = DataAccessUtils.intResult(getHibernateTemplate().findByNamedParam(
        "select count(*) from TaskRun where taskId = :taskId AND (status = 'RUNNING' OR status = 'INITIALISING')",
        "taskId", taskId));
    if (count > 0) {
      return true;
    }
    return false;
  }

  /**
   * Gets the most recent TaskRun (i.e. with the latest StartDate) for the passed task.
   * 
   * @param taskId Id of the task.
   * @return The most recent TaskRun, or null if none could be found.
   */
  public TaskRun getMostRecentTaskRun(final long taskId) {
    List<TaskRun> taskRuns = getHibernateTemplate().executeFind(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Query query = session.createQuery("from TaskRun where taskId = :taskId order by startDate DESC");
        query.setLong("taskId", taskId);
        query.setMaxResults(1);
        List list = query.list();
        return list;
      }
    });

    if (taskRuns.size() != 1) {
      return null;
    } else {
      return taskRuns.get(0);
    }
  }

  /**
   * Updates the status of any tasks not considered complete as interrupted. This is typically used to mark tasks which
   * never finished when the server restarts.
   */
  public void setInterruptedStatus() {
    getHibernateTemplate().bulkUpdate("update TaskRun set status=? where (status=? or status=? or status=?)",
        new Object[] { Status.INTERRUPTED, Status.RUNNING, Status.INITIALISING, Status.CANCELLING });
    getHibernateTemplate().clear(); // remove everything from session cache
  }

  public int getMaxTaskRunResults() {
    return maxTaskRunResults;
  }

  public void setMaxTaskRunResults(int maxJobRunResults) {
    this.maxTaskRunResults = maxJobRunResults;
  }

}
