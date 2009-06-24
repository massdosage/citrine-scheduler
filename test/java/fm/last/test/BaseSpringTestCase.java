package fm.last.test;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import fm.last.citrine.model.TableConstants;

/**
 * Base test case for unit test classes which rely on spring application context.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml", "/applicationContext-test.xml" })
public class BaseSpringTestCase {

  /**
   * The SimpleJdbcTemplate that this base class manages, available to subclasses.
   */
  protected SimpleJdbcTemplate simpleJdbcTemplate;

  @Resource
  private DataSource dataSource;

  @Before
  public void setUp() {
    this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
  }

  /**
   * Count the rows in the given table.
   * 
   * @param tableName table name to count rows in
   * @return the number of rows in the table
   */
  protected int countRowsInTable(String tableName) {
    return SimpleJdbcTestUtils.countRowsInTable(this.simpleJdbcTemplate, tableName);
  }

  /**
   * Convenience method for deleting all rows from the specified tables. Use with caution outside of a transaction!
   * 
   * @param names the names of the tables from which to delete
   * @return the total number of rows deleted from all specified tables
   */
  protected int deleteFromTables(String... names) {
    return SimpleJdbcTestUtils.deleteFromTables(this.simpleJdbcTemplate, names);
  }

  /**
   * Deletes all data from all the the task-related tables.
   */
  protected void cleanupTaskTables() {
    deleteFromTables(TableConstants.TABLE_TASK_CHILD_TASK, TableConstants.TABLE_TASK_RUNS, TableConstants.TABLE_TASKS);
  }

}
