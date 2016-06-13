package osgi.enroute.examples.jdbc.addressbook.dao.api;

import java.util.List;

import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;


public interface PersonDao {
    
    String SQL_SELECT_ALL_PERSONS = "SELECT * FROM PERSONS ";

    String SQL_DELETE_PERSON_BY_PK = "DELETE FROM PERSONS where PERSON_ID=?";

    String SQL_SELECT_PERSON_BY_PK = "SELECT * FROM PERSONS where PERSON_ID=?";

    String SQL_INSERT_PERSON = "INSERT INTO PERSONS(FIRST_NAME,LAST_NAME) VALUES(?,?)";

    String SQL_UPDATE_PERSON_BY_PK = "UPDATE PERSONS SET FIRST_NAME=?, LAST_NAME=? WHERE PERSON_ID=?";
    
    public List<PersonDTO> select();

    public PersonDTO findByPK(Long pk) ;

    public void save(PersonDTO data);

    public void update(PersonDTO data);

    public void delete(Long pk) ;
}
