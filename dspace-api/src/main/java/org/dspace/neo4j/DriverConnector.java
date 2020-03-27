package org.dspace.neo4j;

import org.neo4j.driver.Driver;

public interface DriverConnector {

    public Driver getDriver();

    public void close();

}
