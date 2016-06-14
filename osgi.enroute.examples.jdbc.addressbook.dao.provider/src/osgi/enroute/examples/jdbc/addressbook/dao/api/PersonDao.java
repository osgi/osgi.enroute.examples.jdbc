package osgi.enroute.examples.jdbc.addressbook.dao.api;

import java.util.List;

import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;


public interface PersonDao {
    
    String TABLE_NAME = "PERSONS";
    
    String SQL_SELECT_ALL_PERSONS = "SELECT * FROM "+TABLE_NAME;

    String SQL_DELETE_PERSON_BY_PK = "DELETE FROM "+TABLE_NAME+" where PERSON_ID=?";

    String SQL_SELECT_PERSON_BY_PK = "SELECT * FROM "+TABLE_NAME+" where PERSON_ID=?";

    String SQL_INSERT_PERSON = "INSERT INTO "+TABLE_NAME+"(FIRST_NAME,LAST_NAME) VALUES(?,?)";

    String SQL_UPDATE_PERSON_BY_PK = "UPDATE "+TABLE_NAME+" SET FIRST_NAME=?, LAST_NAME=? WHERE PERSON_ID=?";
    
    public List<PersonDTO> select();

    public PersonDTO findByPK(Long pk) ;

    public Long save(PersonDTO data);

    public void update(PersonDTO data);

    public void delete(PersonDTO data) ;
}
