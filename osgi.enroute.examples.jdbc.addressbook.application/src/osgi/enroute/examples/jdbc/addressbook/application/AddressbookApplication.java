package osgi.enroute.examples.jdbc.addressbook.application;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.examples.jdbc.addressbook.dao.api.CrudDAO;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="osgi.enroute.examples.jdbc.addressbook")
public class AddressbookApplication implements REST {

    private static final Logger logger  = LoggerFactory.getLogger(AddressbookApplication.class);

    private CrudDAO<PersonDTO, Long> personDao;

    public String getUpper(String string) {
        return string.toUpperCase();
    }

    public PersonDTO getPerson(Long personId) {
        logger.info("Get Person Details for Id:{}",personId);
        PersonDTO personDTO = new PersonDTO();
        try {
            personDTO = personDao.findByPK(personId);
        }
        catch (ScopedWorkException e) {
            logger.error("Error retriving Person with Id: "+personId,e);
        }
        return personDTO;
    }

    public List<PersonDTO> getPerson() {
        logger.info("Get All Persons");
        List<PersonDTO> persons = new ArrayList<>();
        try {
            persons = personDao.select();
        }
        catch (ScopedWorkException e) {
            logger.error("Error retriving all person",e);
        }
        return persons;
    }
    
    public boolean deletePerson(Long personId) {
        logger.info("Delete Person Id:{}",personId);
        try {
            personDao.delete(personId);
        }
        catch (ScopedWorkException e) {
            logger.error("Error retriving Person with Id: "+personId,e);
        }
        
        return true;
    }
    
    public boolean postPerson(PersonDTO person) {
        logger.info("Save Person {}",person);
        try {
            personDao.save(person);
        }
        catch (ScopedWorkException e) {
            logger.error("Error saving Person  "+person,e);
        }
        return true;
    }

    
    public boolean putPerson(PersonDTO person) {
        logger.info("Update Person {}",person);
        try {
            personDao.update(person);
        }
        catch (ScopedWorkException e) {
            logger.error("Error updating Person  "+person,e);
        }
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Reference(target="(entity=Person)")
    public void setPersonDao(CrudDAO personDao){
        this.personDao = personDao;
    }

}
