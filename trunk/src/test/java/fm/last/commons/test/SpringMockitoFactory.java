package fm.last.commons.test;

import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;

/**
 * Spring factory bean that creates Mockito mocks. Use in your Spring configuration like so:
 * 
 * <pre>
 *   &lt;bean id="mockDaoFactory" name="dao" class="fm.last.testing.SpringMockitoFactory"&gt;
 *       &lt;property name="type" value="fm.last.project.Dao" /&gt;
 *   &lt;/bean&gt;
 * </pre>
 * 
 * This will create a mock of type <code>fm.last.project.Dao</code> and register it in the app context with the bean
 * name 'dao'.
 */
public class SpringMockitoFactory implements FactoryBean<Object> {

  private Class<?> type;

  private Object spyTarget;

  /**
   * @param type the created object type
   */
  public void setType(final Class<?> type) {
    this.type = type;
  }

  public void setSpyOn(Object spyTarget) {
    this.spyTarget = spyTarget;
    type = spyTarget.getClass();
  }

  /**
   * Creates a mock of the type specified on the factory.
   */
  @Override
  public Object getObject() throws Exception {
    if (spyTarget != null) {
      return Mockito.spy(spyTarget);
    } else {
      return Mockito.mock(type);
    }
  }

  @Override
  public Class<?> getObjectType() {
    return type;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

}
