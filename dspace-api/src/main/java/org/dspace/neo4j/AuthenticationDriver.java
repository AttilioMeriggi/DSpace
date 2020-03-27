package org.dspace.neo4j;

public class AuthenticationDriver {
    private final DriverConnector bolt;

    public AuthenticationDriver(String uri, String username, String password) {
        bolt = createBoltConnector(uri, username, password);
    }

    private DriverConnector createBoltConnector(String uri, String username, String password) {
        try {
            return new BoltDriverConnector(uri, username, password);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Neo4j-ServerURL " + uri);
        }
    }

    public DriverConnector getBoltDriver() {
        return bolt;
    }

}
