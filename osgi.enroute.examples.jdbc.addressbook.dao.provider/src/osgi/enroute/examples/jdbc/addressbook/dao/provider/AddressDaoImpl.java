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
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonAddressColumns;

@Component(name = "osgi.enroute.examples.jdbc.addressbook.personaddress.dao",
            service=AddressDao.class,
           configurationPid = "osgi.enroute.examples.jdbc.addressbook.dao")
public class AddressDaoImpl implements AddressDao {

    private final Logger LOGGER = LoggerFactory.getLogger(AddressDaoImpl.class);

    @Reference
    TransactionControl transactionControl;
    
    @Reference(unbind="-",name="provider")
    JDBCConnectionProvider jdbcConnectionProvider;
    
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
                LOGGER.error("Error closing connection",e);
            }
            connection = null;
        }
    }

    
    @Override
    public void addAddresses(List<AddressDTO> addresses) {
        // TODO Auto-generated method stub - hook it with Coordinator
        
    }
    
    

    @Override
    public void deleteAddresses(Long personId) {
        // TODO Auto-generated method stub  - hook it with Coordinator
        
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
    public void save(AddressDTO data) {
        transactionControl.required( () -> {
            PreparedStatement pst = connection.prepareStatement(SQL_ADD_ADDRESS);
            pst.setString(1, data.emailAddress);
            pst.setLong(2, data.personId);
            pst.setString(3, data.city);
            pst.setString(4, data.country);
            LOGGER.info("Saved Person Address : {}",data);
            pst.executeUpdate();      
            return null;
        });        
        
    }

    @Override
    public void update(AddressDTO data) {
        transactionControl.required( () -> {
            PreparedStatement pst = connection.prepareStatement(SQL_UPDATE_ADDRESS_BY_PK_AND_PERSON_ID);
            pst.setString(1, data.city);
            pst.setString(2, data.country);
            pst.setString(3, data.emailAddress);
            pst.setLong(4, data.personId);
            LOGGER.info("Updated Person Address : {}",data);
            pst.executeUpdate();      
            return null;
        });     
        
    }

    @Override
    public void delete(AddressDTO data) {
        transactionControl.required(() -> {
            PreparedStatement pst = connection.prepareStatement(SQL_DELETE_ADDRESS);
            pst.setString(1, data.emailAddress);
            pst.setLong(2, data.personId);
            pst.executeQuery();
            LOGGER.info("Deleted Person Address with ID : {} and Email {}",data.personId,data.emailAddress);
            return null;
        });
        
    }
    
    protected AddressDTO mapRecordToAddress(ResultSet rs) throws SQLException{
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.personId  = rs.getLong(PersonAddressColumns.PERSON_ID.columnName());
        addressDTO.emailAddress  = rs.getString(PersonAddressColumns.EMAIL_ADDRESS.columnName());
        addressDTO.city  = rs.getString(PersonAddressColumns.CITY.columnName());
        addressDTO.country  = rs.getString(PersonAddressColumns.COUNTRY.columnName());
        return addressDTO;
    }

}
