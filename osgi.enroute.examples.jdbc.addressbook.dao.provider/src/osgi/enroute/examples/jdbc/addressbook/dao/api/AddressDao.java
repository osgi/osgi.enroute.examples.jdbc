package osgi.enroute.examples.jdbc.addressbook.dao.api;

import java.util.List;

import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;


public interface AddressDao {
    
    
    
    public List<AddressDTO> select(Long personId);

    public AddressDTO findByPK(String emailAddress);

    public void save(Long personId,AddressDTO data);

    public void update(Long personId,AddressDTO data);

    public void delete(Long personId) ;

}
