package fm.last.citrine.model;

/**
 * Enumeration representing the various Status values that a TaskRun can have.
 */
public enum Status {
  UNKNOWN(0), INITIALISING(5), /* not currently used */
  RUNNING(10), /* job is running */
  CANCELLING(15), /* received request to cancel by user */
  CANCELLED(20), /* cancelled by user */
  INTERRUPTED(21), /* interrupted (e.g. during shutdown) */
  ABORTED(21), /* some condition caused it to abort running (e.g. was already running when triggered) */
  FAILED(30), /* finished with an error */
  SUCCESS(40); /* finished with no errors */

  private int value;

  Status(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
