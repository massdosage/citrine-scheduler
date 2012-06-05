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
package fm.last.citrine.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.log4j.Logger;
import org.hibernate.annotations.AccessType;

/**
 * Class that represents a Task. It contains values that are used to trigger Quartz Jobs at a certain time as well as
 * values that are needed by Tasks to run.
 */
@Entity
@Table(name = TableConstants.TABLE_TASKS)
public class Task {

  private static Logger log = Logger.getLogger(Task.class);

  private static final boolean DEFAULT_STOP_ON_ERROR = false;
  private static final boolean DEFAULT_ERROR_IF_RUNNING = true;
  private static final boolean DEFAULT_ENABLED = true;

  private long id;
  private int version;
  private String name;
  private String description;
  private Integer priority; // not used yet
  private String timerSchedule;
  private String groupName;
  private String command;
  private String beanName;

  private boolean enabled = DEFAULT_ENABLED;
  private boolean stopOnError = DEFAULT_STOP_ON_ERROR;
  private boolean errorIfRunning = DEFAULT_ERROR_IF_RUNNING;

  private Set<Task> parentTasks = new HashSet<Task>();
  private Set<Task> childTasks = new HashSet<Task>();

  private Notification notification;

  public Task(String name, String groupName, String beanName, boolean enabled, boolean stopOnError, String command,
      String timerSchedule) {
    this.name = name;
    this.timerSchedule = timerSchedule;
    this.enabled = enabled;
    this.stopOnError = stopOnError;
    this.groupName = groupName;
    this.command = command;
    this.beanName = beanName;
    this.notification = new Notification();
  }

  public Task(String name, String groupName, String beanName) {
    this(name, groupName, beanName, DEFAULT_ENABLED, DEFAULT_STOP_ON_ERROR, null, null);
  }

  public Task(String name) {
    this(name, null, null);
  }

  public Task() {
    this(null);
  }

  @Transient
  public Set<Long> getParentTaskIds(boolean recursive) {
    Set<Long> parentIds = new HashSet<Long>();
    for (Task parent : this.getParentTasks()) {
      parentIds.add(parent.getId());
      if (recursive) {
        parentIds.addAll(parent.getParentTaskIds(recursive));
      }
    }
    return parentIds;
  }

  @Transient
  public Set<Long> getChildTaskIds(boolean recursive) {
    Set<Long> childIds = new HashSet<Long>();
    for (Task child : this.getChildTasks()) {
      childIds.add(child.getId());
      if (recursive) {
        childIds.addAll(child.getChildTaskIds(recursive));
      }
    }
    return childIds;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Version
  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Basic
  @Column(nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(length = 4000)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Basic
  @Column(nullable = true)
  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  @Basic
  public String getTimerSchedule() {
    return timerSchedule;
  }

  public void setTimerSchedule(String timerSchedule) {
    this.timerSchedule = timerSchedule;
  }

  @Basic
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Indicates whether child tasks should run if an error occurs while running this tasks. Currently this is implemented
   * in an "AND" fashion - if a child has two parents with this flag set, and only one of them fails, the child will
   * still run (i.e. BOTH parents must fail to stop the child running).
   * 
   * @return Whether child tasks should be run if an error occurs in this task.
   */
  @Basic
  @Column(nullable = true)
  public boolean isStopOnError() {
    // to implement "OR" behaviour as opposed to the description above, when the first parent finishes, it would
    // have to set some sort of flag (a failed batchrun for the child?) to prevent the child running when the
    // second parent finishes, this is considerably trickier than AND
    return stopOnError;
  }

  public void setStopOnError(boolean stopOnError) {
    this.stopOnError = stopOnError;
  }

  /**
   * @return Whether this Task should throw an error if an attempt is made to start it while it is already running.
   */
  @Basic
  public boolean isErrorIfRunning() {
    return errorIfRunning;
  }

  /**
   * @param errorIfRunning Whether this Task should throw an error if an attempt is made to start it while it is already
   *          running.
   */
  public void setErrorIfRunning(boolean errorIfRunning) {
    this.errorIfRunning = errorIfRunning;
  }

  @Basic
  @Column(nullable = false)
  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  @Basic
  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  @Basic
  @Column(nullable = false)
  public String getBeanName() {
    return beanName;
  }

  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }

  @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "childTasks", targetEntity = fm.last.citrine.model.Task.class)
  public Set<Task> getParentTasks() {
    return parentTasks;
  }

  public void setParentTasks(Set<Task> dependingTasks) {
    this.parentTasks = dependingTasks;
  }

  @AccessType("field")
  @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = fm.last.citrine.model.Task.class)
  @JoinTable(name = TableConstants.TABLE_TASK_CHILD_TASK, joinColumns = { @JoinColumn(name = "task_id") }, inverseJoinColumns = { @JoinColumn(name = "child_task_id") })
  public Set<Task> getChildTasks() {
    return childTasks;
  }

  public void setChildTasks(Set<Task> children) {
    childTasks.clear();
    if (children != null) {
      for (Task child : children) {
        addChildTask(child);
      }
    }
  }

  public boolean addChildTask(Task childTask) {
    childTask.addParentTask(this);
    return this.childTasks.add(childTask);
  }

  // do not allow parent Tasks to be added directly, force task graph to be built from adding child
  // tasks to ensure links between two sets are always correct
  private boolean addParentTask(Task parentTask) {
    return this.parentTasks.add(parentTask);
  }

  public boolean removeChildTask(Task childTask) {
    return this.childTasks.remove(childTask);
  }

  public boolean removeParentTask(Task parentTask) {
    return this.parentTasks.remove(parentTask);
  }

  /**
   * @return Whether this Task has at least one child Task or not.
   */
  public boolean hasChild() {
    return this.childTasks.size() > 0;
  }

  /**
   * @return Whether this Task has at least one parent Task or not.
   */
  public boolean hasParent() {
    return this.parentTasks.size() > 0;
  }

  /**
   * @return the notification
   */
  public Notification getNotification() {
    return notification;
  }

  /**
   * @param notification the notification to set
   */
  public void setNotification(Notification notification) {
    this.notification = notification;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
    result = prime * result + ((beanName == null) ? 0 : beanName.hashCode());
    result = prime * result + ((command == null) ? 0 : command.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (enabled ? 1231 : 1237);
    result = prime * result + (stopOnError ? 1231 : 1237);
    result = prime * result + (errorIfRunning ? 1231 : 1237);
    result = prime * result + ((priority == null) ? 0 : priority.hashCode());
    result = prime * result + ((timerSchedule == null) ? 0 : timerSchedule.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Task)) {
      return false;
    }
    final Task other = (Task) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (groupName == null) {
      if (other.groupName != null) {
        return false;
      }
    } else if (!groupName.equals(other.groupName)) {
      return false;
    }
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (timerSchedule == null) {
      if (other.timerSchedule != null) {
        return false;
      }
    } else if (!timerSchedule.equals(other.timerSchedule)) {
      return false;
    }
    if (beanName == null) {
      if (other.beanName != null) {
        return false;
      }
    } else if (!beanName.equals(other.beanName)) {
      return false;
    }
    if (command == null) {
      if (other.command != null) {
        return false;
      }
    } else if (!command.equals(other.command)) {
      return false;
    }
    if (enabled != other.enabled) {
      return false;
    }
    if (stopOnError != other.stopOnError) {
      return false;
    }
    if (errorIfRunning != other.errorIfRunning) {
      return false;
    }
    if (priority == null) {
      if (other.priority != null) {
        return false;
      }
    } else if (!priority.equals(other.priority)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "id=" + id + ",name=" + name;
  }

}
