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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

/**
 * Class representing a run of a particular Task.
 */
@Entity
@Table(name = TableConstants.TABLE_TASK_RUNS)
@org.hibernate.annotations.Table(appliesTo = TableConstants.TABLE_TASK_RUNS, indexes = { @Index(name = "i_task_runs_taskId_startDate", columnNames = {
    "taskId", "startDate" }) })
public class TaskRun {

  private long id;
  private int version;
  private Status status;
  private Date startDate;
  private Date endDate;
  private String sysOut;
  private String sysErr;
  private String stackTrace;
  private long taskId; // for now we are handling this manually rather than via a many-to-one relationship

  public TaskRun() {
  }

  public TaskRun(Status status, Date startDate, Date endDate, String sysOut, String sysErr, String stackTrace,
      long taskId) {
    this.status = status;
    this.startDate = startDate;
    this.endDate = endDate;
    this.sysOut = sysOut;
    this.sysErr = sysErr;
    this.stackTrace = stackTrace;
    this.taskId = taskId;
  }

  public TaskRun(Date startDate, Date endDate, String sysOut, String sysErr, String stackTrace, long taskId) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.sysOut = sysOut;
    this.sysErr = sysErr;
    this.stackTrace = stackTrace;
    this.taskId = taskId;
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

  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Basic
  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  @Basic
  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  @Basic
  @Column(length = 4000)
  public String getSysOut() {
    return sysOut;
  }

  public void setSysOut(String sysOut) {
    this.sysOut = sysOut;
  }

  @Basic
  @Column(length = 4000)
  public String getSysErr() {
    return sysErr;
  }

  public void setSysErr(String sysErr) {
    this.sysErr = sysErr;
  }

  @Basic
  @Column(nullable = false)
  public long getTaskId() {
    return taskId;
  }

  public void setTaskId(long jobId) {
    this.taskId = jobId;
  }

  @Basic
  @Column(length = 4000)
  public String getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
  }

  @Override
  public String toString() {
    return "Id=" + this.id + ", taskId=" + this.taskId + ", status=" + this.status + ", startdate=" + this.startDate
        + ", enddate=" + this.endDate;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + ((stackTrace == null) ? 0 : stackTrace.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((sysErr == null) ? 0 : sysErr.hashCode());
    result = prime * result + ((sysOut == null) ? 0 : sysOut.hashCode());
    result = prime * result + (int) (taskId ^ (taskId >>> 32));
    result = prime * result + version;
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
    if (!(obj instanceof TaskRun)) {
      return false;
    }
    TaskRun other = (TaskRun) obj;
    if (endDate == null) {
      if (other.endDate != null) {
        return false;
      }
    } else if (!(endDate.getTime() / 1000 == other.endDate.getTime() / 1000)) {
      // only check dates to a granularity of a second as saving to db often nukes this
      return false;
    }
    if (stackTrace == null) {
      if (other.stackTrace != null) {
        return false;
      }
    } else if (!stackTrace.equals(other.stackTrace)) {
      return false;
    }
    if (startDate == null) {
      if (other.startDate != null) {
        return false;
      }
    } else if (!(startDate.getTime() / 1000 == other.startDate.getTime() / 1000)) {
      // only check dates to a granularity of a second as saving to db often nukes this
      return false;
    }
    if (status == null) {
      if (other.status != null) {
        return false;
      }
    } else if (!status.equals(other.status)) {
      return false;
    }
    if (sysErr == null) {
      if (other.sysErr != null) {
        return false;
      }
    } else if (!sysErr.equals(other.sysErr)) {
      return false;
    }
    if (sysOut == null) {
      if (other.sysOut != null) {
        return false;
      }
    } else if (!sysOut.equals(other.sysOut)) {
      return false;
    }
    if (taskId != other.taskId) {
      return false;
    }
    if (version != other.version) {
      return false;
    }
    return true;
  }

}
