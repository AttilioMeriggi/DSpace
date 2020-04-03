/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j;

import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class BoltDriverConnector implements DriverConnector {

    private final Driver driver;

    public BoltDriverConnector(String url) {
        this(url, null, null);
    }

    public BoltDriverConnector(String url, String username, String password) {
        boolean hasPassword = password != null && !password.isEmpty();
        AuthToken token = hasPassword ? AuthTokens.basic(username, password) : AuthTokens.none();
        driver = GraphDatabase.driver(url, token, Config.builder().withoutEncryption().build());
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    @Override
    public void close() {
        driver.close();
    }
}
