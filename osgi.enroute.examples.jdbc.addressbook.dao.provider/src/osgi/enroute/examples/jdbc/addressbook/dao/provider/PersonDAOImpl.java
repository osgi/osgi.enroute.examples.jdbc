package osgi.enroute.examples.jdbc.addressbook.dao.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.examples.jdbc.addressbook.dao.api.AddressDao;
import osgi.enroute.examples.jdbc.addressbook.dao.api.PersonDao;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;

/**
 * 
 */
@Component(name = "osgi.enroute.examples.jdbc.addressbook.dao",
           service=PersonDao.class,
           configurationPid = "osgi.enroute.examples.jdbc.addressbook.dao.person")
public class PersonDAOImpl implements PersonDao {

    private final Logger LOGGER = LoggerFactory.getLogger(PersonDAOImpl.class);

    @Reference
    TransactionControl transactionControl;
    
    @Reference(unbind="-",name="provider")
    JDBCConnectionProvider jdbcConnectionProvider;
    
    Connection connection;
    
    @Activate
    void activate(Map<String, Object> props) {
        connection = jdbcConnectionProvider.getResource(transactionControl);
    }

    @Override
    public List<PersonDTO> select(){
       
        List<PersonDTO> persons =  transactionControl.notSupported(() -> {

            List<PersonDTO> dbResults =  new ArrayList<>();

            ResultSet rs =   connection.createStatement().executeQuery("SELECT * FROM PERSONS ");

            while(rs.next()){
                PersonDTO personDTO = new PersonDTO();
                personDTO.personId  = rs.getLong("person_id");
                personDTO.firstName  = rs.getString("first_name");
                personDTO.lastName  = rs.getString("last_name");
                dbResults.add(personDTO);
            }

            return dbResults;
        });

        return persons;
    }


    @Override
    public void delete(Long primaryKey){

        transactionControl.required(() -> {

            PreparedStatement pst = connection.prepareStatement("DELETE FROM PERSONS where PERSON_ID=?");
            pst.setLong(1, primaryKey);
            pst.executeQuery();
            LOGGER.info("Deleted person with ID : {}",primaryKey);
            return null;
        });
    }

    @Override
    public PersonDTO findByPK(Long pk) {
        final Connection connection = jdbcConnectionProvider.getResource(transactionControl);
        PersonDTO personDTO = transactionControl.supports(() -> {

            PersonDTO person = null;
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM PERSONS where PERSON_ID=?");
            pst.setLong(1, pk);

            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                person = new PersonDTO();
                person.personId  = rs.getLong("person_id");
                person.firstName  = rs.getString("first_name");
                person.lastName  = rs.getString("last_name");
            }

            return person;
        });

        return personDTO;
    }

    @Override
    public void save(PersonDTO data){

        final Connection connection = jdbcConnectionProvider.getResource(transactionControl);

        transactionControl.required( () -> {
            PreparedStatement pst = connection.prepareStatement("INSERT INTO PERSONS(FIRST_NAME,LAST_NAME) VALUES(?,?)");
            pst.setString(1, data.firstName);
            pst.setString(2, data.lastName);
            LOGGER.info("Saved person : {}",data);
            pst.executeUpdate();      
            return null;
        });           

    }

    @Override
    public void update(PersonDTO data){
        final Connection connection = jdbcConnectionProvider.getResource(transactionControl);
        transactionControl.required( () -> {
            PreparedStatement pst = connection.prepareStatement("UPDATE PERSONS SET FIRST_NAME=?, LAST_NAME=? WHERE PERSON_ID=?");
            pst.setString(1, data.firstName);
            pst.setString(2, data.lastName);
            pst.setLong(3, data.personId);
            pst.executeUpdate();
            LOGGER.info("Updated person : {}",data);
            return null;
        });      
    }
    
    @Deactivate
    void deactivate(){
        if(connection != null ){
            try {
                connection.close();
            }
            catch (SQLException e) {
                LOGGER.error("Error closing connection",e);
            }
            connection = null;
        }
    }

}
