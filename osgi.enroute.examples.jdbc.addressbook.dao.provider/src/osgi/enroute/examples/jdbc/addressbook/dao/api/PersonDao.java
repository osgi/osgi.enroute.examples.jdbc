package osgi.enroute.examples.jdbc.addressbook.dao.api;

import java.util.List;

import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;


public interface PersonDao {
    
    public List<PersonDTO> select();

    public PersonDTO findByPK(Long pk) ;

    public Long save(PersonDTO data);

    public void update(PersonDTO data);

    public void delete(PersonDTO data) ;
}
