package osgi.enroute.examples.jdbc.addressbook.application;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.examples.jdbc.addressbook.dao.api.PersonDao;
import osgi.enroute.examples.jdbc.addressbook.dao.datatypes.PersonDTO;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource = { "angular.js", "angular-resource.js", "angular-route.js" }, priority = 1000)
@RequireBootstrapWebResource(resource = "css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "osgi.enroute.examples.jdbc.addressbook")
public class AddressbookApplication implements REST {

	@Reference
	private PersonDao personDao;

	public PersonDTO getPerson(Long personId) {
		return personDao.findByPK(personId);
	}

	public List<PersonDTO> getPerson() {
		return personDao.select();
	}

	public boolean deletePerson(long personId) {
		personDao.delete(personId);
		return true;
	}

	public PersonDTO postPerson(PersonDTO person) {
		if (person.personId > 0) {
			personDao.update(person);
			return person;
		}
		else {
			long id = personDao.save(person);
			person.personId = id;
			return person;
		}
	}
}
