package osgi.enroute.examples.jdbc.addressbook.dao.provider;

import static osgi.enroute.examples.jdbc.addressbook.dao.provider.PersonAddressTable.SQL_ADD_ADDRESS;
import static osgi.enroute.examples.jdbc.addressbook.dao.provider.PersonAddressTable.SQL_DELETE_ALL_ADDRESS_BY_PERSON_ID;
import static osgi.enroute.examples.jdbc.addressbook.dao.provider.PersonAddressTable.SQL_SELECT_ADDRESS_BY_PERSON;
import static osgi.enroute.examples.jdbc.addressbook.dao.provider.PersonAddressTable.SQL_SELECT_ADDRESS_BY_PK;
import static osgi.enroute.examples.jdbc.addressbook.dao.provider.PersonAddressTable.SQL_UPDATE_ADDRESS_BY_PK_AND_PERSON_ID;

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
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.coordinator.Participant;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.examples.jdbc.addressbook.dao.api.AddressDao;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;
import osgi.enroute.examples.jdbc.addressbook.dao.participants.AddressSaveParticipant;
import osgi.enroute.examples.jdbc.addressbook.dao.participants.AddressUpdateParticipant;

@Component(name = "osgi.enroute.examples.jdbc.addressbook.personaddress.dao",
            service=AddressDao.class,
           configurationPid = "osgi.enroute.examples.jdbc.addressbook.dao")
public class AddressDaoImpl implements AddressDao {

    private Logger logger = LoggerFactory.getLogger(AddressDaoImpl.class);
    
    
    
    @Reference
    TransactionControl transactionControl;
    
    @Reference
    Coordinator coordinator;
    
    @Reference(unbind="-",name="provider")
    JDBCConnectionProvider jdbcConnectionProvider;
        
    @Reference(target="(name=addressSave)")
    Participant addressSaveParticipant;
    
    @Reference(target="(name=addressUpdate)")
    Participant addressUpdateParticipant;
    
    Connection connection;
    
    @Activate
    void activate(Map<String, Object> props) {
        connection = jdbcConnectionProvider.getResource(transactionControl);
    }
           
    @Deactivate
    void deactivate(){
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

       
    @Override
    public List<AddressDTO> select(Long personId) {

        List<AddressDTO> personsAddresses =  transactionControl.notSupported(() -> {

            List<AddressDTO> dbResults =  new ArrayList<>();
            
            PreparedStatement pst = connection
                    .prepareStatement(SQL_SELECT_ADDRESS_BY_PERSON);
            pst.setLong(1, personId);
            
            ResultSet rs =   pst.executeQuery();

            while(rs.next()){
                AddressDTO addressDTO = mapRecordToAddress(rs);
                dbResults.add(addressDTO);
            }

            return dbResults;
        });

        return personsAddresses;
    }

    @Override
    public AddressDTO findByPK(String pk) {
      
        AddressDTO personAddress = transactionControl.supports(() -> {

            AddressDTO addressDTO = null;
         
            PreparedStatement pst = connection.prepareStatement(SQL_SELECT_ADDRESS_BY_PK);
            pst.setString(1, pk);

            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                addressDTO = mapRecordToAddress(rs);
            }

            return addressDTO;
        });

        return personAddress;
    }

    @Override
    public void save(Long personId, AddressDTO data) {
      
        boolean isThereACoordination = coordinator.addParticipant
                (addressSaveParticipant);
        
        if(isThereACoordination){
            addAddressesToCoordinator(AddressSaveParticipant.class,data);            
        }else {
            transactionControl.required( () -> {
                PreparedStatement pst = connection.prepareStatement(SQL_ADD_ADDRESS);
                pst.setString(1, data.emailAddress);
                pst.setLong(2, data.personId);
                pst.setString(3, data.city);
                pst.setString(4, data.country);
                logger.info("Saved Person with id {}  and Address : {}",personId,data);
                pst.executeUpdate();      
                return null;
            });
        }
        
    }

    @Override
    public void update(Long personId, AddressDTO data) {
       
        boolean isThereACoordination = coordinator.addParticipant
                (addressUpdateParticipant);
        
        if(isThereACoordination){
            addAddressesToCoordinator(AddressUpdateParticipant.class,data);            
        }else {
            transactionControl.required( () -> {
                PreparedStatement pst = connection.prepareStatement(SQL_UPDATE_ADDRESS_BY_PK_AND_PERSON_ID);
                pst.setString(1, data.city);
                pst.setString(2, data.country);
                pst.setString(3, data.emailAddress);
                pst.setLong(4, data.personId);
                logger.info("Updated Person Address : {}",data);
                pst.executeUpdate();  

                return null;

            });    
        }
    }

    @Override
    public void delete(Long personId) {

        transactionControl.required( () -> {
            PreparedStatement pst = connection.prepareStatement(SQL_DELETE_ALL_ADDRESS_BY_PERSON_ID);
            pst.setLong(1, personId);
            logger.info("Deleted Person {} Addresses",personId);
            pst.executeUpdate();  

            return null;

        });    
    }

    @SuppressWarnings("unchecked")
    protected void addAddressesToCoordinator(Class<?> keyClass, AddressDTO addressDTO) {
        Coordination coordination = coordinator.peek();
        Map<Class<?>,Object> cordVars = coordination.getVariables();
        List<AddressDTO> addressDtos =  (List<AddressDTO>) cordVars.
                computeIfAbsent(keyClass, k -> new ArrayList<AddressDTO>());
        addressDtos.add(addressDTO);
    }
    
    protected AddressDTO mapRecordToAddress(ResultSet rs) throws SQLException{
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.personId  = rs.getLong(PersonAddressTable.PERSON_ID);
        addressDTO.emailAddress  = rs.getString(PersonAddressTable.EMAIL_ADDRESS);
        addressDTO.city  = rs.getString(PersonAddressTable.CITY);
        addressDTO.country  = rs.getString(PersonAddressTable.COUNTRY);
        return addressDTO;
    }

}
