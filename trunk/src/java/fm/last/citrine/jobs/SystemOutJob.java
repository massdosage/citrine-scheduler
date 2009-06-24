package fm.last.citrine.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Simple Job implementation that prints to sysout when run (used mainly for testing).
 */
public class SystemOutJob implements Job {
  
  private static Logger log = Logger.getLogger(SystemOutJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    System.out.println("Job execute called");
    try {
      Thread.sleep(1000); //sleep for a second to fake actually doing some work
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
