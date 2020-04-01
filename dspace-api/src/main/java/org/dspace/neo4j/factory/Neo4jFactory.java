/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.factory;

import org.dspace.neo4j.service.Neo4jService;
import org.dspace.services.factory.DSpaceServicesFactory;

/***
 * Abstract factory to get services for the DMS Import Framework package, use
 *
 * Neo4jFactory.getInstance() to retrieve an implementation
 *
 */
public interface Neo4jFactory {

    public abstract Neo4jService getNeo4jService();

    public static Neo4jFactory getInstance() {
        return DSpaceServicesFactory.getInstance().getServiceManager().getServiceByName("neo4jFactory",
                Neo4jFactory.class);
    }
}
