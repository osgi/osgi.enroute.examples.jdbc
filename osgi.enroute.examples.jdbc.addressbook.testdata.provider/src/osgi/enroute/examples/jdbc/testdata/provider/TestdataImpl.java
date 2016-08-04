package osgi.enroute.examples.jdbc.testdata.provider;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.dto.api.TypeReference;
import osgi.enroute.examples.jdbc.addressbook.dao.api.PersonDao;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.AddressDTO;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;
import osgi.enroute.rest.api.REST;

/**
 * 
 */
@Component(name = "osgi.enroute.examples.jdbc.testdata")
public class TestdataImpl implements REST {
	final static TypeReference<List<TestdataDTO>> TESTDATAS= new TypeReference<List<TestdataDTO>>() {};
	
	@Reference
	DTOs dtos;
	
    @Reference
    private PersonDao personDao;

    public void postTestdata(Map<String,String> map) throws Exception {
    	System.out.println("Testdata " + map);
    	
    	List<TestdataDTO> list = dtos.decoder(TESTDATAS).get( getClass().getResourceAsStream("testdata.json"));
    	
    	for ( TestdataDTO td : list) {
    		PersonDTO data = new PersonDTO();
    		data.firstName = td.first_name;
    		data.lastName = td.last_name;
    		AddressDTO address = new AddressDTO();
    		address.city = td.city;
    		address.country = td.state;
    		address.emailAddress = td.email;
    		data.addresses.add(address);
    		System.out.println("Saving " + data);
    		personDao.save(data);
    	}
    	
    }

}
