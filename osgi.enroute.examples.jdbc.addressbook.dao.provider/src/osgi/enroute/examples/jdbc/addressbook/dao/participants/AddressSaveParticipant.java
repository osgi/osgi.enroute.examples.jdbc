/**
 * 
 */
package osgi.enroute.examples.jdbc.addressbook.dao.participants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Participant;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;
import osgi.enroute.examples.jdbc.addressbook.dao.provider.PersonAddressTable;

/**
 * @author kameshs
 *
 */
@Component(name="osgi.enroute.examples.jdbc.address.save.participant",
           property="name=addressSave",service=Participant.class,
           configurationPid="osgi.enroute.examples.jdbc.addressbook.dao")
public class AddressSaveParticipant implements Participant {

    
    private Logger logger = LoggerFactory.getLogger(AddressSaveParticipant.class);
    
    @Reference
    TransactionControl transactionControl;
        
    @Reference(unbind="-",name="provider")
    JDBCConnectionProvider jdbcConnectionProvider;
    
    Connection connection;
    
    @Activate
    void activate(Map<String, Object> props) {
        connection = jdbcConnectionProvider.getResource(transactionControl);
    }
        
    @SuppressWarnings("unchecked")
    @Override
    public void ended(Coordination coordination) throws Exception {
       
        logger.info("Batching Address save for Person");
       
        transactionControl.required( () -> {
            
            PreparedStatement pst = connection.prepareStatement(PersonAddressTable.SQL_ADD_ADDRESS);

            List<AddressDTO> addresses = (List<AddressDTO> ) coordination.getVariables().get(getClass());

            addresses.stream().forEach( data -> {

                long personId = data.personId;

                try {
                    pst.setString(1, data.emailAddress);
                    pst.setLong(2, data.personId);
                    pst.setString(3, data.city);
                    pst.setString(4, data.country);
                    logger.info("Add Person with id {}  and Address : {}",personId,data);
                    pst.addBatch();
                }
                catch (SQLException e) {
                    logger.info("Error Adding address to Person with id {}",personId,data);
                }      
            });
            
            return pst.executeBatch();
            
        });

    }

    @Override
    public void failed(Coordination coordination) throws Exception {
        // TODO Auto-generated method stub

    }

}
