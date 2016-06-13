package osgi.enroute.examples.jdbc.addressbook.dao.api;

import java.io.Serializable;
import java.util.List;

import org.osgi.dto.DTO;
import org.osgi.service.transaction.control.ScopedWorkException;

public interface CrudDAO<T extends DTO, PK extends Serializable> {

    public List<T> select() throws ScopedWorkException;
    
    public T findByPK(PK data) throws ScopedWorkException;
    
    public void save(T data) throws ScopedWorkException;
    
    public void update(T data) throws ScopedWorkException;
    
    public void delete(PK primaryKey) throws ScopedWorkException;
}
