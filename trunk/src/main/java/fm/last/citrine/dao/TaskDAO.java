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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fm.last.citrine.model.Task;

/**
 * Data Access Object for managing storage of Tasks.
 */
public class TaskDAO extends HibernateDaoSupport {

  private static Logger log = Logger.getLogger(TaskDAO.class);

  /**
   * Creates a or updates a Task object in storage.
   * 
   * @param task Task to save.
   */
  public void save(Task task) {
    getHibernateTemplate().saveOrUpdate(task);
    getHibernateTemplate().flush();
  }

  /**
   * Retrieves a Task by its primary key.
   * 
   * @param id Task id.
   * @return Task identified by the passed id.
   */
  public Task get(long id) {
    return (Task) getHibernateTemplate().get(Task.class, id);
  }

  /**
   * Retrieves all Tasks in storage.
   * 
   * @return List of all Tasks in storage.
   */
  public List<Task> getTasks() {
    return getHibernateTemplate().find("from Task order by name asc");
  }

  /**
   * Retrieves all Tasks belonging to the same group.
   * 
   * @param group Name of group.
   * @return Tasks which are in the passed group.
   */
  public List<Task> findByGroup(String group) {
    return getHibernateTemplate().findByNamedParam("from Task where groupName = :groupName", "groupName", group);
  }

  /**
   * Gets all group names.
   * 
   * @return All group names.
   */
  public Set<String> getGroupNames() {
    List<String> results = getHibernateTemplate().find("select distinct groupName from Task order by groupName asc");
    return new TreeSet<String>(results);
  }

  /**
   * Finds Tasks who are valid candidates to be added as children to the passed Task. This finds Tasks in the same
   * groups and who are not already children or parents of the passed Task.
   * 
   * @param task Task to find candidate children for.
   * @return Set of candidate child Tasks.
   */
  public Set<Task> findCandidateChildren(final Task task) {
    final Set<Long> parentTasklds = task.getParentTaskIds(true);
    final Set<Long> childTaskIds = task.getChildTaskIds(false);

    List<Task> taskList = (List<Task>) this.getHibernateTemplate().execute(new HibernateCallback() {

      public Object doInHibernate(Session session) {
        Criteria criteria = session.createCriteria(Task.class);
        criteria.add(Expression.eq("groupName", task.getGroupName()));
        criteria.add(Expression.not(Expression.eq("id", task.getId())));
        if (!parentTasklds.isEmpty()) {
          criteria.add(Expression.not(Expression.in("id", parentTasklds)));
        }
        if (!childTaskIds.isEmpty()) {
          criteria.add(Expression.not(Expression.in("id", childTaskIds)));
        }
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
      }

    });

    return new HashSet<Task>(taskList);
  }

  /**
   * Deletes a Task.
   * 
   * @param task Task to delete;
   */
  public void delete(Task task) {
    // need to remove this Task as parent from all children
    Set<Task> childTasks = task.getChildTasks();
    if (childTasks != null) {
      for (Task child : childTasks) {
        child.removeParentTask(task);
        save(child);
      }
    }

    // need to remove this Task as a child of its parent
    Set<Task> parentTasks = task.getParentTasks();
    if (parentTasks != null) {
      for (Task parent : parentTasks) {
        parent.removeChildTask(task);
        save(parent);
      }
    }

    getHibernateTemplate().delete(task);
  }

}
