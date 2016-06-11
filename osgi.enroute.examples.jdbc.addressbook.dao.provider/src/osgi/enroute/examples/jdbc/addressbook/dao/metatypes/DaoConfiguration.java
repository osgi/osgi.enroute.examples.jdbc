package osgi.enroute.examples.jdbc.addressbook.dao.metatypes;

import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(description="The Configuration for the Person DAO for Address Book",
                       name="Address Book Person DAO",
                       pid="osgi.enroute.examples.jdbc.addressbook.dao.person")
public interface DaoConfiguration {

    @AttributeDefinition(
            name = "Transaction Resource Provider",
            description = "The Resource Provider target for the transaction, typically the DataSource",
            defaultValue ="("+DataSourceFactory.JDBC_DATASOURCE_NAME+"=addressBookDS)", required = true)
    String provider_target();
}
