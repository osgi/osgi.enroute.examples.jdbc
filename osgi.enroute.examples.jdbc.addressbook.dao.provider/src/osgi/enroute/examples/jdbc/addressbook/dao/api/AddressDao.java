package osgi.enroute.examples.jdbc.addressbook.dao.api;

import java.util.List;

import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;


public interface AddressDao {
    
    String TABLE_NAME = "PERSON_ADDRESSES";
    
    String SQL_SELECT_ADDRESS_BY_PERSON = "SELECT * FROM "+TABLE_NAME +" WHERE PERSON_ID = ? ";

    String SQL_DELETE_ADDRESS = "DELETE FROM "+ TABLE_NAME+" WHERE EMAIL_ADDRESS = ? AND  PERSON_ID=?";
    
    String SQL_DELETE_ALL_ADDRESS_BY_PERSON_ID = "DELETE FROM "+TABLE_NAME+" WHERE PERSON_ID=?";

    String SQL_SELECT_ADDRESS_BY_PK = "SELECT * FROM "+TABLE_NAME+" where EMAIL_ADDRESS=?";

    String SQL_ADD_ADDRESS = "INSERT INTO "+TABLE_NAME+"(EMAIL_ADDRESS,PERSON_ID,CITY,COUNTRY) VALUES(?,?,?,?)";

    String SQL_UPDATE_ADDRESS_BY_PK_AND_PERSON_ID = "UPDATE "+TABLE_NAME+" SET CITY=?, COUNTRY=? WHERE EMAIL_ADDRESS = ? AND  PERSON_ID=?";
    
    public List<AddressDTO> select(Long personId);

    public AddressDTO findByPK(String emailAddress);

    public void save(Long personId,AddressDTO data);

    public void update(Long personId,AddressDTO data);

    public void delete(Long personId) ;

}
