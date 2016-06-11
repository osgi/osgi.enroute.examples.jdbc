package osgi.enroute.examples.jdbc.addressbook.dao.provider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.examples.jdbc.addressbook.dao.api.CrudDAO;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;

/**
 * 
 */
@Component(name = "osgi.enroute.examples.jdbc.addressbook.dao",property="entity=Person",service=CrudDAO.class)
public class PersonDAOImpl implements CrudDAO<PersonDTO,Long> {

    @SuppressWarnings("unused")
    private final Logger LOGGER = LoggerFactory.getLogger(PersonDAOImpl.class);

    @Reference
    DataSourceFactory dataSourceFactory;

    @Reference
    TransactionControl transactionControl;

    JDBCConnectionProvider jdbcConnectionProvider;

    @Reference(target="(dataSourceName=addressBookDS)")
    void setProvider(JDBCConnectionProvider jdbcConnectionProvider){
        this.jdbcConnectionProvider = jdbcConnectionProvider;
    }

    @Override
    public List<PersonDTO> select() {
        final Connection connection = jdbcConnectionProvider.getResource(transactionControl);

        //Usually we don't require Transactions for Select 
        List<PersonDTO> persons =  transactionControl.notSupported(() -> {

            List<PersonDTO> dbResults =  new ArrayList<>();

            ResultSet rs =   connection.createStatement().executeQuery("SELECT * FROM PERSONS ");

            while(rs.next()){
                PersonDTO personDTO = new PersonDTO();
                personDTO.personId  = rs.getString("person_id");
                personDTO.firstName  = rs.getString("first_name");
                personDTO.lastName  = rs.getString("last_name");
                dbResults.add(personDTO);
            }

            return dbResults;
        });

        return persons;
    }


    @Override
    public void delete(PersonDTO data) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public PersonDTO findByPK(Long pk) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void save(PersonDTO data) {

    }

    @Override
    public void update(PersonDTO data) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
