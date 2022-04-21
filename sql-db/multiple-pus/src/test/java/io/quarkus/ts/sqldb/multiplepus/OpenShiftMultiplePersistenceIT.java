package io.quarkus.ts.sqldb.multiplepus;

import io.quarkus.test.bootstrap.MariaDbService;
import io.quarkus.test.bootstrap.PostgresqlService;
import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.OpenShiftScenario;
import io.quarkus.test.services.Container;
import io.quarkus.test.services.QuarkusApplication;

@OpenShiftScenario
public class OpenShiftMultiplePersistenceIT extends AbstractMultiplePersistenceIT {

    static final int MYSQL_PORT = 3306;

    static final int POSTGRESQL_PORT = 5432;

    @Container(image = "${mariadb.103.image}", port = MYSQL_PORT, expectedLog = "Only MySQL server logs after this point")
    static MariaDbService mariadb = new MariaDbService();

    @Container(image = "${postgresql.13.image}", port = POSTGRESQL_PORT, expectedLog = "listening on IPv4 address")
    static PostgresqlService postgresql = new PostgresqlService()
            //fixme https://github.com/quarkus-qe/quarkus-test-framework/issues/455
            .withProperty("POSTGRES_USER", "user")
            .withProperty("POSTGRES_PASSWORD", "user")
            .withProperty("POSTGRES_DB", "mydb");

    @QuarkusApplication
    static RestService app = new RestService()
            .withProperty("MARIA_DB_USERNAME", mariadb.getUser())
            .withProperty("MARIA_DB_PASSWORD", mariadb.getPassword())
            .withProperty("MARIA_DB_JDBC_URL", mariadb::getJdbcUrl)
            .withProperty("POSTGRESQL_USERNAME", postgresql.getUser())
            .withProperty("POSTGRESQL_PASSWORD", postgresql.getPassword())
            .withProperty("POSTGRESQL_JDBC_URL", postgresql::getJdbcUrl);

    @Override
    RestService getApp() {
        return app;
    }
}
