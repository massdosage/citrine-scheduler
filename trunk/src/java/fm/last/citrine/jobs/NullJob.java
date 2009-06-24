package fm.last.citrine.jobs;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

/**
 * A job implementation that does nothing. Can be used as a placeholder in parent/child relationships etc.
 */
public class NullJob implements InterruptableJob {

  private static Logger log = Logger.getLogger(NullJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.debug("Null Job called");
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    log.debug("Interrupt called");
  }

}
