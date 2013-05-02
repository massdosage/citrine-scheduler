package fm.last.citrine.service;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.citrine.dao.TaskDAO;
import fm.last.citrine.model.Task;
import fm.last.citrine.scheduler.SchedulerManager;

@RunWith(MockitoJUnitRunner.class)
public class TaskManagerImplTest {

  @Mock
  private TaskDAO taskDAO;
  @Mock
  private SchedulerManager schedulerManager;
  
  private TaskManager taskManager;
  
  @Before
  public void setUp() throws Exception {
    taskManager = new TaskManagerImpl(taskDAO, schedulerManager);
  }

  @Test
  public void testSaveNewTask() {
    Task task = new Task("taskName", "groupName", "beanName");
    taskManager.save(task);
    verify(schedulerManager).scheduleTask(task, true);
    verify(taskDAO).save(task);
  }
}
