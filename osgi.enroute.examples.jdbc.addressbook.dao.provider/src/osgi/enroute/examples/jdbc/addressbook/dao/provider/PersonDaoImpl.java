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
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonColumns;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;

/**
 * 
 */
@Component(name = "osgi.enroute.examples.jdbc.addressbook.person.dao",
           service=PersonDao.class,
           configurationPid = "osgi.enroute.examples.jdbc.addressbook.dao")
public class PersonDaoImpl implements PersonDao {

    private final Logger LOGGER = LoggerFactory.getLogger(PersonDaoImpl.class);

    @Reference
    TransactionControl transactionControl;
    
    @Reference(unbind="-",name="provider")
    JDBCConnectionProvider jdbcConnectionProvider;
    
    @Reference
    AddressDao addressDao;
    
    Connection connection;
    
    @Activate
    void activate(Map<String, Object> props) {
        connection = jdbcConnectionProvider.getResource(transactionControl);
    }

    @Override
    public List<PersonDTO> select(){
       
        List<PersonDTO> persons =  transactionControl.notSupported(() -> {

            List<PersonDTO> dbResults =  new ArrayList<>();

            ResultSet rs =   connection.createStatement().executeQuery(SQL_SELECT_ALL_PERSONS);

            while(rs.next()){
                PersonDTO personDTO = mapRecordToPerson(rs);
                personDTO.addresses =  addressDao.select(personDTO.personId);
                dbResults.add(personDTO);
            }

            return dbResults;
        });

        return persons;
    }


    @Override
    public void delete(Long primaryKey){

        transactionControl.required(() -> {

            PreparedStatement pst = connection.prepareStatement(SQL_DELETE_PERSON_BY_PK);
            pst.setLong(1, primaryKey);
            pst.executeQuery();
            addressDao.deleteAddresses(primaryKey);
            LOGGER.info("Deleted Person with ID : {}",primaryKey);
            return null;
        });
    }

    @Override
    public PersonDTO findByPK(Long pk) {
        
        PersonDTO person = transactionControl.supports(() -> {

            PersonDTO personDTO = null;
         
            PreparedStatement pst = connection.prepareStatement(SQL_SELECT_PERSON_BY_PK);
            pst.setLong(1, pk);

            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                personDTO = mapRecordToPerson(rs);
            }

            return personDTO;
        });

        return person;
    }

    @Override
    public void save(PersonDTO data){
        transactionControl.required( () -> {
            PreparedStatement pst = connection.prepareStatement(SQL_INSERT_PERSON);
            pst.setString(1, data.firstName);
            pst.setString(2, data.lastName);
            LOGGER.info("Saved person : {}",data);
            pst.executeUpdate();
            if(!data.addresses.isEmpty()){
                if(data.addresses.size() > 1){
                    addressDao.addAddresses(data.addresses);
                }else{
                    addressDao.save(data.addresses.get(0));
                }
            }
            return null;
        });           

    }

    @Override
    public void update(PersonDTO data){
        
        transactionControl.required( () -> {
            PreparedStatement pst = connection.prepareStatement(SQL_UPDATE_PERSON_BY_PK);
            pst.setString(1, data.firstName);
            pst.setString(2, data.lastName);
            pst.setLong(3, data.personId);
            pst.executeUpdate();
            LOGGER.info("Updated person : {}",data);
            return null;
        });      
    }
    

    protected PersonDTO mapRecordToPerson(ResultSet rs) throws SQLException {
        PersonDTO personDTO = new PersonDTO();
        personDTO.personId  = rs.getLong(PersonColumns.PERSON_ID.columnName());
        personDTO.firstName  = rs.getString(PersonColumns.FIRST_NAME.columnName());
        personDTO.lastName  = rs.getString(PersonColumns.LAST_NAME.columnName());
        return personDTO;
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
