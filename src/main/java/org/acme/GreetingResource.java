package org.acme;

import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/hello")
public class GreetingResource {

    private static final AtomicInteger tablePostfix = new AtomicInteger();

    @Inject
    @Named("xa-ds-1")
    DataSource dataSource;

    @Inject
    @Named("xa-ds-2")
    DataSource dataSource2;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        QuarkusTransaction.begin();
        createTable(dataSource);
        createTable(dataSource2);
        QuarkusTransaction.commit();
        return "Hello from RESTEasy Reactive";
    }

    private static void createTable(DataSource dataSource) {
        try (var con = dataSource.getConnection()) {
            try (var st = con.createStatement()) {
                st.executeUpdate("CREATE TABLE abcd" + tablePostfix.incrementAndGet() + "(name CHAR)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
