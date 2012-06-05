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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Class that represents a notification.
 */
@Embeddable
public class Notification {

  private static final boolean DEFAULT_NOTIFY_ON_SUCCESS = true;
  private static final boolean DEFAULT_NOTIFY_ON_FAILURE = true;

  private String recipients;

  private boolean notifyOnSuccess = DEFAULT_NOTIFY_ON_SUCCESS;
  private boolean notifyOnFailure = DEFAULT_NOTIFY_ON_FAILURE;

  public Notification() {
  }

  public Notification(String recipients, boolean notifyOnSuccess, boolean notifyOnFailure) {
    this.recipients = recipients;
    this.notifyOnSuccess = notifyOnSuccess;
    this.notifyOnFailure = notifyOnFailure;
  }

  public Notification(String recipients) {
    this(recipients, DEFAULT_NOTIFY_ON_SUCCESS, DEFAULT_NOTIFY_ON_FAILURE);
  }

  @Basic
  @Column(length = 4000)
  public String getRecipients() {
    return recipients;
  }

  public void setRecipients(String notificationRecipients) {
    this.recipients = notificationRecipients;
  }

  /**
   * @return the notifyOnSuccess
   */
  @Basic
  @Column(nullable = true)
  public boolean isNotifyOnSuccess() {
    return notifyOnSuccess;
  }

  /**
   * @param notifyOnSuccess the notifyOnSuccess to set
   */
  public void setNotifyOnSuccess(boolean notifyOnSuccess) {
    this.notifyOnSuccess = notifyOnSuccess;
  }

  /**
   * @return the notifyOnFailure
   */
  @Basic
  @Column(nullable = true)
  public boolean isNotifyOnFailure() {
    return notifyOnFailure;
  }

  /**
   * @param notifyOnFailure the notifyOnFailure to set
   */
  public void setNotifyOnFailure(boolean notifyOnFailure) {
    this.notifyOnFailure = notifyOnFailure;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (notifyOnFailure ? 1231 : 1237);
    result = prime * result + (notifyOnSuccess ? 1231 : 1237);
    result = prime * result + ((recipients == null) ? 0 : recipients.hashCode());
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
    if (!(obj instanceof Notification)) {
      return false;
    }
    Notification other = (Notification) obj;
    if (notifyOnFailure != other.notifyOnFailure) {
      return false;
    }
    if (notifyOnSuccess != other.notifyOnSuccess) {
      return false;
    }
    if (recipients == null) {
      if (other.recipients != null) {
        return false;
      }
    } else if (!recipients.equals(other.recipients)) {
      return false;
    }
    return true;
  }
}
