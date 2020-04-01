/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.factory;

import org.dspace.neo4j.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;

public class Neo4jFactoryImpl implements Neo4jFactory {

    @Autowired(required = true)
    private Neo4jService neo4jService;

    @Override
    public Neo4jService getNeo4jService() {
        return neo4jService;
    }
}
