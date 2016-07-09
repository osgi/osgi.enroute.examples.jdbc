<h1><img src="http://enroute.osgi.org/img/enroute-logo-64.png" witdh=40px style="float:left;margin: 0 1em 1em 0;width:40px">
OSGi enRoute JDBC Example</h1>

This repository represents a JDBC example workspace for bndtools, it demonstrates on how to use JDBC with [Aries Tx-Control][1].   The application uses the [PAX-JDBC][2] bundle for defining the datasources.

If you want to get started with enRoute, then follow the steps in the [quick-start guide][3].

## Example

This example builds simple address book application which stores a Person with one or more email addresses

### Database Entities 
The example uses two DB entities Person and Example as shown in the diagram below

![enRoute JDBC Example ERD](./enRoute_JDBC_ERD.png?dl=0 "enRoute JDBC Example ERD")

### Application
The application is available over the url http://localhost:8080/osgi.enroute.examples.jdbc/index.html 

The example application and datasource is configured using the [configuration.json](./osgi.enroute.examples.jdbc.addressbook.application/configuration/configuration.json)

[1]: http://aries.apache.org/modules/tx-control
[2]: https://ops4j1.jira.com/wiki/display/PAXJDBC
[3]: http://enroute.osgi.org/quick-start.html

