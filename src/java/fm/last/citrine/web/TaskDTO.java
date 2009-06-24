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
