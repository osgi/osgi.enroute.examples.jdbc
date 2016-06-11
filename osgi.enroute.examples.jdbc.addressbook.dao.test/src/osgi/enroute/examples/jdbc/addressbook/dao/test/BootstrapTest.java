package osgi.enroute.examples.jdbc.addressbook.dao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;

public class BootstrapTest extends JDBCExampleTest {
    
    public BootstrapTest() throws Exception {
        super();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testDataSourceFactoryAvailable(){

        try {
            assertNotNull(configAdmin);

            Filter dsfFilter = context.createFilter(txServiceProps.getProperty("aries.dsf.target.filter"));

            ServiceReference[] dsfServiceRefs = context.getServiceReferences(
                    DataSourceFactory.class.getName(), dsfFilter.toString());

            assertNotNull(dsfServiceRefs); 
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testJDBCConnectionProviderRegistered() throws Exception{
        ServiceReference[] conProviders = context.getServiceReferences(JDBCConnectionProvider.class.getName(), 
                "(dataSourceName=addressBookDS)");
        
        assertNotNull(conProviders);

        //TODO why three providers is started by default?
        //printServiceProperties(conProviders);
       // assertTrue(conProviders.length == 3);
        
        //Assuming only one ref for this test
        ServiceReference addressDBProviderRef = conProviders[0];
        assertNotNull(addressDBProviderRef);
        assertEquals("addressBookDS",addressDBProviderRef.getProperty("dataSourceName"));
    }    

}
