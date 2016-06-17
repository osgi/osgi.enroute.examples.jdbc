package osgi.enroute.examples.jdbc.addressbook.dao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.examples.jdbc.addressbook.dao.JDBCExampleBase;
import osgi.enroute.examples.jdbc.addressbook.dao.api.AddressDao;
import osgi.enroute.examples.jdbc.addressbook.dao.api.PersonDao;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;

public class PersonDAOTest extends JDBCExampleBase {

    final Logger LOGGER = LoggerFactory.getLogger(PersonDAOTest.class);

    ServiceTracker<DataSourceFactory,DataSourceFactory> dsfTracker;

    ServiceTracker<PersonDao,PersonDao> personDaoTracker;

    DataSourceFactory dataSourceFactory;

    public PersonDAOTest() throws Exception{
        super();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setUp() throws Exception{

        configAdmin  = (ConfigurationAdmin) cmTracker.waitForService(5000);

        assertNotNull(configAdmin);

        localJDBCProviderConfig = configAdmin.createFactoryConfiguration(FACTORY_PID_ARIES_TX_CONTROL_JDBC_LOCAL,null);
        localJDBCProviderConfig.update((Hashtable)txServiceProps);

        Filter dsfFilter = FrameworkUtil.createFilter("(&("+
                Constants.OBJECTCLASS + "="+DataSourceFactory.class.getName()+")"
                +txServiceProps.getProperty("aries.dsf.target.filter")+")");

        dsfTracker = new ServiceTracker<>(context, dsfFilter, null);

        dsfTracker.open();

        dataSourceFactory = dsfTracker.waitForService(3000);    

        Properties dsProps = new Properties();
        dsProps.load(this.getClass().getResourceAsStream("/ds.properties"));

        Connection con = dataSourceFactory.createDataSource(dsProps).getConnection();

        createTestTables(con);

        personDaoTracker = new ServiceTracker<>(context, PersonDao.class, null);
        personDaoTracker.open();

    }

    protected void createTestTables(Connection connection) throws SQLException {

        assertNotNull(connection);

        Statement st = connection.createStatement();

        st.execute("CREATE TABLE IF NOT EXISTS PERSONS("
                + "PERSON_ID INT PRIMARY KEY AUTO_INCREMENT, "
                + "FIRST_NAME VARCHAR(30),"
                + "LAST_NAME VARCHAR(30)"
                + ")");

        st.execute("INSERT INTO PERSONS VALUES (1001,'Tom','Cat');"
                + "INSERT INTO PERSONS VALUES (1002,'Jerry','Mouse');"
                + "INSERT INTO PERSONS VALUES (1003,'Mickey','Mouse');"
                + "INSERT INTO PERSONS VALUES (1004,'Donald','Duck');");

        st.execute("CREATE TABLE IF NOT EXISTS PERSON_ADDRESSES("
                + "EMAIL_ADDRESS VARCHAR(100) PRIMARY KEY,"
                + "PERSON_ID INT NOT NULL,"
                + "CITY VARCHAR(100),"
                + "COUNTRY VARCHAR(3)"
                + ")");

        st.execute("INSERT INTO PERSON_ADDRESSES VALUES ('tom.cat@example.com',1001,'Palo Alto','US');"
                + "INSERT INTO PERSON_ADDRESSES VALUES ('jerry@example.com',1002,'Palo Alto','US');"
                + "INSERT INTO PERSON_ADDRESSES VALUES ('jerry.mouse@example.com',1002,'Palo Alto','US');");
    }

    @Test
    public void testSelectAll() throws Exception {

        PersonDao personDao =  personDaoTracker.waitForService(3000);
        assertNotNull(personDao);

        List<PersonDTO> persons =  personDao.select();
        assertFalse(persons.isEmpty());
        assertTrue(4 == persons.size());
    }

    @Test
    public void testSave() throws Exception{

        PersonDao personDao =  personDaoTracker.waitForService(3000);

        assertNotNull(personDao);

        PersonDTO person = new PersonDTO();
        person.firstName="Pluto";
        person.lastName="Dog";
        person.personId=1005;

        personDao.save(person);

        PersonDTO expected =  personDao.findByPK(1005l);
        assertEquals(1005,expected.personId);
        assertEquals("Pluto",expected.firstName);
        assertEquals("Dog",expected.lastName);
    }

    @Test
    public void testSaveWithAddress() throws Exception {

        PersonDao personDao =  personDaoTracker.waitForService(3000);

        ArrayList<AddressDTO> addresses = new ArrayList<>();

        assertNotNull(personDao);

        PersonDTO person = new PersonDTO();
        person.firstName="Pluto";
        person.lastName="Dog";
        person.personId=1005;

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.personId=1005;
        addressDTO.emailAddress="pluto@example.com";
        addressDTO.city="Orlando";
        addressDTO.country="US";
        addresses.add(addressDTO);

        person.addresses=addresses;

        personDao.save(person);

        PersonDTO expected =  personDao.findByPK(1005l);
        assertEquals(1005,expected.personId);
        assertEquals("Pluto",expected.firstName);
        assertEquals("Dog",expected.lastName);
        assertEquals(1,expected.addresses.size());
    }

    @Test
    public void testSaveWithAddresses() throws Exception{

        PersonDao personDao =  personDaoTracker.waitForService(3000);

        ArrayList<AddressDTO> addresses = new ArrayList<>();

        assertNotNull(personDao);
        PersonDTO person = new PersonDTO();
        person.firstName="Pluto";
        person.lastName="Dog";
        person.personId=1005;

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.personId=1005;
        addressDTO.emailAddress="pluto@example.com";
        addressDTO.city="Orlando";
        addressDTO.country="US";
        addresses.add(addressDTO);

        addressDTO = new AddressDTO();
        addressDTO.personId=1005;
        addressDTO.emailAddress="pluto2@example.com";
        addressDTO.city="Orlando";
        addressDTO.country="US";
        addresses.add(addressDTO);

        person.addresses=addresses;


        personDao.save(person);

        PersonDTO expected =  personDao.findByPK(1005l);
        assertEquals(1005,expected.personId);
        assertEquals("Pluto",expected.firstName);
        assertEquals("Dog",expected.lastName);
        assertEquals(2,expected.addresses.size());
    }

    public void testDelete() throws Exception {

        PersonDao personDao =  personDaoTracker.waitForService(3000);

        assertNotNull(personDao);
        PersonDTO person = new PersonDTO();
        person.personId = 1001;
        personDao.delete(person);
        List<PersonDTO> persons =  personDao.select();
        assertFalse(persons.isEmpty());
        assertTrue(3 == persons.size());

        //Check Address tables for person id 1001 is cleared as well
        AddressDao addressDao = getService(AddressDao.class,null);
        assertNotNull(addressDao);
        List<AddressDTO> addresses =  addressDao.select(1001l);
        assertTrue(addresses.isEmpty());
    }

    @Test
    public void testUpdate() throws Exception {

        PersonDao personDao =  personDaoTracker.waitForService(3000);

        assertNotNull(personDao);

        PersonDTO person = new PersonDTO();
        person.firstName="Thomas";
        person.lastName="Cat";
        person.personId=1001;

        personDao.update(person);

        PersonDTO expected =  personDao.findByPK(1001l);
        assertEquals(1001,expected.personId);
        assertEquals("Thomas",expected.firstName);
        assertEquals("Cat",expected.lastName);
    }

    @Test
    public void testUpdateWithAddress() throws Exception{

        PersonDao personDao =  personDaoTracker.waitForService(3000);

        assertNotNull(personDao);

        PersonDTO person = new PersonDTO();
        person.firstName="Jekyll";
        person.lastName="Mouse";
        person.personId=1002;

        ArrayList<AddressDTO> addresses = new ArrayList<>();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.personId=1002;
        addressDTO.emailAddress="jerry01@example.com";
        addressDTO.city="Orlando";
        addressDTO.country="US";
        addresses.add(addressDTO);

        addressDTO = new AddressDTO();
        addressDTO.personId=1002;
        addressDTO.emailAddress="jerry02@example.com";
        addressDTO.city="Orlando";
        addressDTO.country="US";
        addresses.add(addressDTO);

        person.addresses = addresses;

        personDao.update(person);

        PersonDTO expected =  personDao.findByPK(1002l);
        assertEquals(1002,expected.personId);
        assertEquals("Jekyll",expected.firstName);
        assertEquals("Mouse",expected.lastName);

        assertEquals(2,expected.addresses.size());
    }

    @Test(expected=ScopedWorkException.class)
    public void testTransactionRollback() throws Exception {

        try {

            PersonDao personDao =  personDaoTracker.waitForService(3000);

            assertNotNull(personDao);

            PersonDTO person = new PersonDTO();
            person.firstName="Rollback";
            person.lastName="Test";

            ArrayList<AddressDTO> addresses = new ArrayList<>();
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.emailAddress="rollback.test@example.com";
            addressDTO.city="Orlando";
            addressDTO.country="DUMMY";
            addresses.add(addressDTO);

            person.addresses = addresses;

            personDao.save(person);

        } 
        /*
         * 
         * Incorrect value inserted to Address Table should trigger an exception
         * and rollback as they are within transaction control, which eventually
         * means the insert of person record should also be rolled back
         */
        catch (ScopedWorkException e) {
            try {
                //Check the record does not exist
                DataSourceFactory dataSourceFactory = (DataSourceFactory) 
                        getService(DataSourceFactory.class, "(osgi.jdbc.driver.name=h2)");       

                assertNotNull(dataSourceFactory);

                Properties dsProps = new Properties();
                dsProps.load(this.getClass().getResourceAsStream("/ds.properties"));

                Connection con = dataSourceFactory.createDataSource(dsProps).getConnection();

                assertNotNull(con);

                ResultSet rs =  con.createStatement().
                        executeQuery("SELECT * FROM PERSONS WHERE FIRST_NAME='Rollback' and LAST_NAME='Test'");

                assertNotNull(rs);
                assertFalse(rs.next());
            }
            catch (InvalidSyntaxException | IOException | SQLException e1) {
                fail("Error During Rollback Test Check");
            }
            
            throw e;
        } 

    }

    @After
    public void tearDown() throws Exception{

        assertNotNull(dataSourceFactory);

        Properties dsProps = new Properties();
        dsProps.load(this.getClass().getResourceAsStream("/ds.properties"));

        Connection con = dataSourceFactory.createDataSource(dsProps).getConnection();

        assertNotNull(con);
        Statement st = con.createStatement();
        st.execute("DROP TABLE IF EXISTS PERSONS");
        st.execute("DROP TABLE IF EXISTS PERSON_ADDRESSES");

        //Clean up 
        dataSourceFactory = null;
        configAdmin  = null;

        if(localJDBCProviderConfig!= null){
            localJDBCProviderConfig.delete();
            localJDBCProviderConfig = null;
        }

        if(cmTracker != null){
            cmTracker.close();
        }

        if(dsfTracker != null){
            dsfTracker.close();
        }

        if(personDaoTracker != null){
            personDaoTracker.close();
        }

    }
}
