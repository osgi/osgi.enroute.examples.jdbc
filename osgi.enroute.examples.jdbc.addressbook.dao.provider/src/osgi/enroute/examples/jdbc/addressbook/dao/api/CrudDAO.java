package osgi.enroute.examples.jdbc.addressbook.dao.api;

import java.io.Serializable;
import java.util.List;

import org.osgi.dto.DTO;

public interface CrudDAO<T extends DTO, PK extends Serializable> {

    public List<T> select();
    
    public T findByPK(PK data);
    
    public void save(T data);
    
    public void update(T data);
    
    public void delete(T data);
}
