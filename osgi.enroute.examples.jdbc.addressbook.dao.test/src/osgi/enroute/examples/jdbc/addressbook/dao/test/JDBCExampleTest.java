package osgi.enroute.examples.jdbc.addressbook.dao.test;

import static org.junit.Assert.assertNotNull;

import java.util.Hashtable;
import java.util.Properties;

import org.junit.AfterClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCExampleTest {

    private static final String FACTORY_PID_ARIES_TX_CONTROL_JDBC_LOCAL = "org.apache.aries.tx.control.jdbc.local";
    
    final Logger LOGGER = LoggerFactory.getLogger(JDBCExampleTest.class);
    
    protected final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

    protected  Properties txServiceProps = new Properties();

    @SuppressWarnings("rawtypes")
    protected  static ServiceTracker cmTracker;

    protected  ConfigurationAdmin configAdmin;

    protected  Configuration localJDBCProviderConfig;
        
    @SuppressWarnings({"unchecked","rawtypes"})
    public JDBCExampleTest() throws Exception {
        assertNotNull(context);
        
        txServiceProps.load(this.getClass().getResourceAsStream("/localProvider.properties"));

        cmTracker = new ServiceTracker<>(context, ConfigurationAdmin.class, null);

        cmTracker.open();

        configAdmin  = (ConfigurationAdmin) cmTracker.waitForService(5000);

        localJDBCProviderConfig = configAdmin.createFactoryConfiguration(FACTORY_PID_ARIES_TX_CONTROL_JDBC_LOCAL,null);
        localJDBCProviderConfig.update((Hashtable)txServiceProps);
        //Giving some time for the service discovery and registration to happen
        Thread.sleep(1000);
    }
    
    @AfterClass
    public static void teardown(){
        cmTracker.close();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> T getService(Class<T> clazz,String filter) throws InvalidSyntaxException {
        
        ServiceReference[]  personDAOServiceRefs = 
                context.getAllServiceReferences(clazz.getName(),filter);

        assertNotNull(personDAOServiceRefs);

        T service = (T) context.getService(personDAOServiceRefs[0]);
        
        return service;
    }    
    
    @SuppressWarnings({ "rawtypes"})
    protected void printServiceProperties(ServiceReference... serviceRefs) {
        for (ServiceReference serviceReference : serviceRefs) {
            if(serviceReference  != null){
                String[] serviceRefKeys = serviceReference.getPropertyKeys();
                for (String key : serviceRefKeys) {
                    LOGGER.info("{}:{}",key,serviceReference.getProperty(key));
                }
            }
        }
    }
}
