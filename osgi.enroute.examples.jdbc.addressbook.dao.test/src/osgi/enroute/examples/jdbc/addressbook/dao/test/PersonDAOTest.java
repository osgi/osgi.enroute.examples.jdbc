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
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.examples.jdbc.addressbook.dao.JDBCExampleBase;
import osgi.enroute.examples.jdbc.addressbook.dao.api.AddressDao;
import osgi.enroute.examples.jdbc.addressbook.dao.api.PersonDao;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;

public class PersonDAOTest extends JDBCExampleBase {

    final Logger LOGGER = LoggerFactory.getLogger(PersonDAOTest.class);

    public PersonDAOTest() throws Exception{
        super();
    }

    @Before
    public void setUp(){
        teardown();
        try {
            DataSourceFactory dataSourceFactory = (DataSourceFactory) 
                    getService(DataSourceFactory.class, "(osgi.jdbc.driver.name=h2)");       

            assertNotNull(dataSourceFactory);

            Properties dsProps = new Properties();
            dsProps.load(this.getClass().getResourceAsStream("/ds.properties"));

            Connection con = dataSourceFactory.createDataSource(dsProps).getConnection();

            assertNotNull(con);
            Statement st = con.createStatement();

            st.execute("CREATE TABLE IF NOT EXISTS "+PersonDao.TABLE_NAME+"("
                    + "PERSON_ID INT PRIMARY KEY AUTO_INCREMENT, "
                    + "FIRST_NAME VARCHAR(30),"
                    + "LAST_NAME VARCHAR(30)"
                    + ")");

            st.execute("INSERT INTO "+PersonDao.TABLE_NAME+" VALUES (1001,'Tom','Cat');"
                    + "INSERT INTO "+PersonDao.TABLE_NAME+" VALUES (1002,'Jerry','Mouse');"
                    + "INSERT INTO "+PersonDao.TABLE_NAME+" VALUES (1003,'Mickey','Mouse');"
                    + "INSERT INTO "+PersonDao.TABLE_NAME+" VALUES (1004,'Donald','Duck');");

            st.execute("CREATE TABLE IF NOT EXISTS "+AddressDao.TABLE_NAME+"("
                    + "EMAIL_ADDRESS VARCHAR(100) PRIMARY KEY,"
                    + "PERSON_ID INT NOT NULL,"
                    + "CITY VARCHAR(100),"
                    + "COUNTRY VARCHAR(3)"
                    + ")");

            st.execute("INSERT INTO "+AddressDao.TABLE_NAME+" VALUES ('tom.cat@example.com',1001,'Palo Alto','US');"
                    + "INSERT INTO "+AddressDao.TABLE_NAME+" VALUES ('jerry@example.com',1002,'Palo Alto','US');"
                    + "INSERT INTO "+AddressDao.TABLE_NAME+" VALUES ('jerry.mouse@example.com',1002,'Palo Alto','US');");

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error During Setup");
        }

    }

    @Test
    public void testSelectAll() {
        try {

            PersonDao personDao = getService(PersonDao.class,null);
            assertNotNull(personDao);

            List<PersonDTO> persons =  personDao.select();
            assertFalse(persons.isEmpty());
            assertTrue(4 == persons.size());
            
        } catch (ScopedWorkException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Selecting");
        }
        catch (InvalidSyntaxException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Selecting");
        }
    }

    @Test
    public void testSave() {
        try {

            PersonDao personDao = getService(PersonDao.class,null);
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

        } catch (ScopedWorkException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Saving");
        }
        catch (InvalidSyntaxException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Saving");
        }
    }

    @Test
    public void testSaveWithAddress() {
        try {

            PersonDao personDao = getService(PersonDao.class,null);
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

        } catch (ScopedWorkException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Saving");
        }
        catch (InvalidSyntaxException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Saving");
        }
    }

    @Test
    public void testSaveWithAddresses() {
        try {

            PersonDao personDao = getService(PersonDao.class,null);
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

        } catch (ScopedWorkException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Saving");
        }
        catch (InvalidSyntaxException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Saving");
        }
    }

    public void testDelete() {
        try {

            PersonDao personDao = getService(PersonDao.class,null);

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

        } catch (ScopedWorkException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Deleting");
        }
        catch (InvalidSyntaxException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Deleting");
        }
    }

    @Test
    public void testUpdate() {
        try {

            PersonDao personDAO = getService(PersonDao.class,null);

            assertNotNull(personDAO);

            PersonDTO person = new PersonDTO();
            person.firstName="Thomas";
            person.lastName="Cat";
            person.personId=1001;

            personDAO.update(person);

            PersonDTO expected =  personDAO.findByPK(1001l);
            assertEquals(1001,expected.personId);
            assertEquals("Thomas",expected.firstName);
            assertEquals("Cat",expected.lastName);

        } catch (ScopedWorkException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Updating");
        }
        catch (InvalidSyntaxException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Updating");
        }
    }

    @Test
    public void testUpdateWithAddress() {
        try {

            PersonDao personDAO = getService(PersonDao.class,null);

            assertNotNull(personDAO);

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

            personDAO.update(person);

            PersonDTO expected =  personDAO.findByPK(1002l);
            assertEquals(1002,expected.personId);
            assertEquals("Jekyll",expected.firstName);
            assertEquals("Mouse",expected.lastName);

            assertEquals(2,expected.addresses.size());


        } catch (ScopedWorkException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Updating");
        }
        catch (InvalidSyntaxException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Updating");
        }
    }

    @Test
    public void testTransactionRollback() {

        try {

            PersonDao personDAO = getService(PersonDao.class,null);

            assertNotNull(personDAO);

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

            personDAO.save(person);

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
                        executeQuery("SELECT * FROM "+PersonDao.TABLE_NAME+" WHERE FIRST_NAME='Rollback' and LAST_NAME='Test'");

                assertNotNull(rs);
                assertFalse(rs.next());
            }
            catch (InvalidSyntaxException | IOException | SQLException e1) {
                fail("Error During Rollback Test Check");
            }
        } catch (InvalidSyntaxException e) {
           fail("Error During Rollback Test");
        }
    }

    @After
    public void tearDown(){

        try {
            DataSourceFactory dataSourceFactory = (DataSourceFactory) 
                    getService(DataSourceFactory.class, "(osgi.jdbc.driver.name=h2)");       

            assertNotNull(dataSourceFactory);

            Properties dsProps = new Properties();
            dsProps.load(this.getClass().getResourceAsStream("/ds.properties"));

            Connection con = dataSourceFactory.createDataSource(dsProps).getConnection();

            assertNotNull(con);
            Statement st = con.createStatement();
            st.execute("DROP TABLE IF EXISTS "+PersonDao.TABLE_NAME);
            st.execute("DROP TABLE IF EXISTS "+AddressDao.TABLE_NAME);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error During Tear Down");
        }
    }
}
