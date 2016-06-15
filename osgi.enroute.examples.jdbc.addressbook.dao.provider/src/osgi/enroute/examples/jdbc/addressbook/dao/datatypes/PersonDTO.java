package osgi.enroute.examples.jdbc.addressbook.dao.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.osgi.dto.DTO;

public class PersonDTO extends DTO {

    public long personId;
    public String firstName;
    public String lastName;
    
    public List<AddressDTO> addresses = new ArrayList<>();
}
