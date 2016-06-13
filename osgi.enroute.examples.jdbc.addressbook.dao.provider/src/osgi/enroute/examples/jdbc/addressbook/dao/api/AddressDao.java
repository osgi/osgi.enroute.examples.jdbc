package osgi.enroute.examples.jdbc.addressbook.dao.api;

import java.util.List;

import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;


public interface AddressDao {
    
    String SQL_SELECT_ADDRESS_BY_PERSON = "SELECT * FROM PERSONS_ADDRESSES WHERE PERSON_ID = ? ";

    String SQL_DELETE_ADDRESS = "DELETE FROM PERSONS_ADDRESSES WHERE EMAIL_ADDRESS = ? AND  PERSON_ID=?";
    
    String SQL_DELETE_ALL_ADDRESS_BY_PERSON_ID = "DELETE FROM PERSONS_ADDRESSES WHERE PERSON_ID=?";

    String SQL_SELECT_ADDRESS_BY_PK = "SELECT * FROM PERSONS_ADDRESSES where EMAIL_ADDRESS=?";

    String SQL_ADD_ADDRESS = "INSERT INTO PERSONS_ADDRESSES(EMAIL_ADDRESS,PERSON_ID,CITY,COUNTRY) VALUES(?,??,?)";

    String SQL_UPDATE_ADDRESS_BY_PK_AND_PERSON_ID = "UPDATE PERSONS_ADDRESSES SET CITY=?, COUNTRY=? WHERE EMAIL_ADDRESS = ? AND  PERSON_ID=?";
    
    public List<AddressDTO> select(Long personId);

    public AddressDTO findByPK(String emailAddress);

    public void save(AddressDTO data);

    public void update(AddressDTO data);

    public void delete(AddressDTO data) ;

    public void addAddresses(List<AddressDTO> addresses);
    
    public void deleteAddresses(Long personId);
}
