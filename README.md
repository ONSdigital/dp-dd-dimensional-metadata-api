# Data Discovery Dimensional Metadata API

Provides a REST API for querying the dimensional datasets that are available and
associated metadata, such as defined dimensions, geographical areas, time periods, and other options.

## Building

```bash
mvn clean install
```

## Running

```bash
java -jar target/dd-dimensional-metadata-api-1.0.0-SNAPSHOT.jar
```

This will start the server running on port 8080 talking to a local PostgreSQL database. Use the
following environment variables to configure the system:

 * `BASE_URL`: Base URL to use when constructing links to resources. Defaults to `http://localhost:8080`.
 * `DB_USER`: The database username. Defaults to `data_discovery`.
 * `DB_PASSWORD`: The database password. Defaults to `password`.
 * `DB_URL`: The database JDBC URL. Defaults to `jdbc:postgresql://localhost:5432/data_discovery`.
 * `DB_DRIVER`: The JDBC driver to load. Defaults to `org.postgresql.Driver`.

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md) for details.

## License

Copyright © 2016, Office for National Statistics (https://www.ons.gov.uk)

Released under the MIT license, see [LICENSE](LICENSE.md) for details.
