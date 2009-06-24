package fm.last.citrine.jobs;

import static fm.last.citrine.scheduler.SchedulerConstants.TASK_COMMAND;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Very simple job implemetation which waits for the amount of time in the passed command string (this must 
 * be convertible to a long).
 */
public class WaitJob implements Job {

  private static Logger log = Logger.getLogger(WaitJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    String command = jobDataMap.getString(TASK_COMMAND);
    long waitTime = Long.parseLong(command);
    log.debug("Waiting for " + waitTime);
    try {
      Thread.sleep(waitTime);
    } catch (InterruptedException e) {
      log.error(e);
    }
  }

}
