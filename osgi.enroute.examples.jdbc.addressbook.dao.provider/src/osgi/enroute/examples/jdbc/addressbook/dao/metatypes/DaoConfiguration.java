package osgi.enroute.examples.jdbc.addressbook.dao.metatypes;

import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(description="The Common Configuration for the DAO for Address Book",
                       name="Address Book DAO",
                       pid="osgi.enroute.examples.jdbc.addressbook.dao")
public interface DaoConfiguration {

    @AttributeDefinition(
            name = "Transaction Resource Provider",
            description = "The Resource Provider target for the transaction, typically the DataSource",
            defaultValue ="("+DataSourceFactory.JDBC_DATASOURCE_NAME+"=addressBookDS)", required = true)
    String provider_target();
}
