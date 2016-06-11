package osgi.enroute.examples.jdbc.addressbook.application;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
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
    
    @Activate
    void activate(Map<String,Object> config){
        
    }
    
	public String getUpper(String string) {
		return string.toUpperCase();
	}
	
	@ObjectClassDefinition
	interface Config {
	    
	}

}
