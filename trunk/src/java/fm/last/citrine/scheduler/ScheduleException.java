package fm.last.citrine.scheduler;

import org.springframework.core.NestedRuntimeException;

/**
 * Exception that is thrown if an error occurs scheduling a job.
 */
public class ScheduleException extends NestedRuntimeException {

  private static final long serialVersionUID = -6462803151009731543L;

  public ScheduleException(String msg) {
    super(msg);
  }

  public ScheduleException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
