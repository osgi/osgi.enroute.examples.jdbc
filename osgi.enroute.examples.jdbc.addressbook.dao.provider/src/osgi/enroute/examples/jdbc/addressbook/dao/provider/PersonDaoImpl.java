package osgi.enroute.examples.jdbc.addressbook.dao.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.examples.jdbc.addressbook.dao.api.AddressDao;
import osgi.enroute.examples.jdbc.addressbook.dao.api.PersonDao;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;

/**
 * 
 */
@Component(name = "osgi.enroute.examples.jdbc.addressbook.person.dao",
service=PersonDao.class,
configurationPid = "osgi.enroute.examples.jdbc.addressbook.dao")
public class PersonDaoImpl implements PersonDao,PersonTable {

    private Logger logger = LoggerFactory.getLogger(PersonDaoImpl.class);
    
    //Some cordinator names for better understanding
    String ADDRESS_DAO_SAVE_COORDINATOR = "osgi.enroute.examples.jdbc.addressbook.save";
    String ADDRESS_DAO_UPDATE_COORDINATOR = "osgi.enroute.examples.jdbc.addressbook.update";

    @Reference
    TransactionControl transactionControl;

    @Reference(unbind="-",name="provider")
    JDBCConnectionProvider jdbcConnectionProvider;


    @Reference
    Coordinator coordinator;

    @Reference
    AddressDao addressDao;

    Connection connection;

    @Activate
    void start(Map<String, Object> props) {
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
    public void delete(PersonDTO data){

        transactionControl.required(() -> {
            Long primaryKey = data.personId;
            PreparedStatement pst = connection.prepareStatement(SQL_DELETE_PERSON_BY_PK);
            pst.setLong(1,primaryKey);
            pst.executeUpdate();
            addressDao.delete(primaryKey);
            logger.info("Deleted Person with ID : {}",primaryKey);
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
                personDTO.addresses =  addressDao.select(pk);
            }

            return personDTO;
        });

        return person;
    }

    @Override
    public Long save(PersonDTO data){

        long personPK =  transactionControl.required( () -> {

            PreparedStatement pst = connection.prepareStatement(SQL_INSERT_PERSON,Statement.RETURN_GENERATED_KEYS);

            pst.setString(1, data.firstName);
            pst.setString(2, data.lastName);           

            pst.executeUpdate();
            
            AtomicLong genPersonId = new AtomicLong(data.personId);
            
            if(genPersonId.get() <= 0){
                ResultSet genKeys = pst.getGeneratedKeys();

                if(genKeys.next()){
                    genPersonId.set( genKeys.getLong(1));
                }
            }

            logger.info("Saved Person with ID : {}",genPersonId.get());

            if(genPersonId.get() > 0){
                List<AddressDTO> addresses = data.addresses;

                //START - COORDINATION
                coordinator.begin(ADDRESS_DAO_SAVE_COORDINATOR, TimeUnit.MINUTES.toMillis(5));

                addresses.stream().forEach( address ->{
                    address.personId = genPersonId.get();
                    addressDao.save(genPersonId.get(),address);
                });

                coordinator.peek().end();

                //END - COORDINATION
            }

            return genPersonId.get();
        });           

        return personPK;
    }

    @Override
    public void update(PersonDTO data){

        transactionControl.required( () -> {

            PreparedStatement pst = connection.prepareStatement(SQL_UPDATE_PERSON_BY_PK);
            pst.setString(1, data.firstName);
            pst.setString(2, data.lastName);
            pst.setLong(3, data.personId);
            pst.executeUpdate();

            logger.info("Updated person : {}",data);

            final long personId = data.personId;

            List<AddressDTO> addresses = data.addresses;

            //START - COORDINATION

            coordinator.begin(ADDRESS_DAO_UPDATE_COORDINATOR, TimeUnit.MINUTES.toMillis(5));

            addresses.stream().forEach( address -> addressDao.update(personId,address));

            coordinator.peek().end();

            //END - COORDINATION
            return null;
        });      
    }


    protected PersonDTO mapRecordToPerson(ResultSet rs) throws SQLException {
        PersonDTO personDTO = new PersonDTO();
        personDTO.personId  = rs.getLong(PERSON_ID);
        personDTO.firstName  = rs.getString(FIRST_NAME);
        personDTO.lastName  = rs.getString(LAST_NAME);
        return personDTO;
    }

    @Deactivate
    void stop(){
        if(connection != null ){
            try {
                connection.close();
            }
            catch (SQLException e) {
                logger.error("Error closing connection",e);
            }
            connection = null;
        }
    }

}
