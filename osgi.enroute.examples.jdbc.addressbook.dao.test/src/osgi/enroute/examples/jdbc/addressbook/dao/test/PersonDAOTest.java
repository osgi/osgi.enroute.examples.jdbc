package osgi.enroute.examples.jdbc.addressbook.dao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.examples.jdbc.addressbook.dao.api.CrudDAO;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;

public class PersonDAOTest extends JDBCExampleTest {

    final Logger LOGGER = LoggerFactory.getLogger(PersonDAOTest.class);

    public PersonDAOTest() throws Exception{
        super();
    }

    @Before
    public void setUp(){
        teardown();
        try {
            TransactionControl txControl = (TransactionControl) 
                    getService(TransactionControl.class, "(osgi.local.enabled=true)");       

            assertNotNull(txControl);

            JDBCConnectionProvider connectionProvider = getService(JDBCConnectionProvider.class,
                    "(dataSourceName="+txServiceProps.getProperty(DataSourceFactory.JDBC_DATASOURCE_NAME)+")");
            assertNotNull(connectionProvider);

            //FIX ME Move this code to SQL file
            txControl.required( () -> {
                Connection con = connectionProvider.getResource(txControl);
                Statement st = con.createStatement();

                st.execute("CREATE TABLE IF NOT EXISTS PERSONS("
                        + "PERSON_ID INT PRIMARY KEY AUTO_INCREMENT, "
                        + "FIRST_NAME VARCHAR(30),"
                        + "LAST_NAME VARCHAR(30)"
                        + ")");

                //TODO ideal candidate for Coordinator service, need to add example
                st.execute("INSERT INTO PERSONS VALUES (1001,'Tom','Cat');"
                        + "INSERT INTO PERSONS VALUES (1002,'Jerry','Mouse');"
                        + "INSERT INTO PERSONS VALUES (1003,'Mickey','Mouse');"
                        + "INSERT INTO PERSONS VALUES (1004,'Donald','Duck');");

                st.execute("CREATE TABLE IF NOT EXISTS PERSON_ADDRESSES("
                        + "EMAIL VARCHAR(100) PRIMARY KEY,"
                        + "PERSON_ID INT NOT NULL,"
                        + "CITY VARCHAR(100),"
                        + "COUNTRY VARCHAR(100)"
                        + ")");

                //TODO DAO for Address pending

                return null;
            });
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error During Setup");
        }

    }

    @SuppressWarnings({"unchecked" })   
    @Test
    public void testSelectAll() {


        try {
            CrudDAO<PersonDTO, Long> personDAO = getService(CrudDAO.class,"(entity=Person)");

            assertNotNull(personDAO);

            List<PersonDTO> persons =  personDAO.select();
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

    @SuppressWarnings("unchecked")
    @Test
    public void testSave() {
        try {

            CrudDAO<PersonDTO, Long> personDAO = getService(CrudDAO.class,"(entity=Person)");
            assertNotNull(personDAO);

            PersonDTO person = new PersonDTO();
            person.firstName="Pluto";
            person.lastName="Dog";
            person.personId=1005;

            personDAO.save(person);

            PersonDTO expected =  personDAO.findByPK(1005l);
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

    @SuppressWarnings("unchecked")
    public void testDelete() {
        try {

            CrudDAO<PersonDTO, Long> personDAO = getService(CrudDAO.class,"(entity=Person)");
            assertNotNull(personDAO);
            personDAO.delete(1001l);
            List<PersonDTO> persons =  personDAO.select();
            assertFalse(persons.isEmpty());
            assertTrue(3 == persons.size());

        } catch (ScopedWorkException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Deleting");
        }
        catch (InvalidSyntaxException e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error Deleting");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdate() {
        try {

            CrudDAO<PersonDTO, Long> personDAO = getService(CrudDAO.class,"(entity=Person)");
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

    @After
    public void tearDown(){
        try {
            TransactionControl txControl = (TransactionControl) 
                    getService(TransactionControl.class, "(osgi.local.enabled=true)");       

            assertNotNull(txControl);

            JDBCConnectionProvider connectionProvider = getService(JDBCConnectionProvider.class,
                    "(dataSourceName="+txServiceProps.getProperty(DataSourceFactory.JDBC_DATASOURCE_NAME)+")");
            assertNotNull(connectionProvider);

            //FIX ME Move this code to SQL file
            txControl.required( () -> {
                Connection con = connectionProvider.getResource(txControl);
                Statement st = con.createStatement();
                st.execute("DROP TABLE IF EXISTS PERSONS");
                st.execute("DROP TABLE IF EXISTS PERSON_ADDRESS");
                return null;
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            fail("Error During Tear Down");
        }
    }
}
