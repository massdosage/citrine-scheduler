package fm.last.citrine.jobs;

import static fm.last.citrine.scheduler.SchedulerConstants.TASK_COMMAND;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Very simple job implementation fails if the passed command string represents a boolean "true", otherwise it executes
 * successfully.
 */
public class FailJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    String command = jobDataMap.getString(TASK_COMMAND);
    boolean fail = Boolean.parseBoolean(command);
    if (fail) {
      throw new RuntimeException("Job failed");
    }
  }

}
