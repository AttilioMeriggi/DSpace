/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j;

import org.dspace.neo4j.dao.Neo4jDAO;
import org.dspace.neo4j.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;

public class Neo4jServiceImpl implements Neo4jService {

    @Autowired(required = true)
    private Neo4jDAO neo4jDAO;
    
    @Override
    public void createUpdateNode(DSpaceNode dsnode) {
        neo4jDAO.createUpdateNode(dsnode);
    }

    @Override
    public void deleteNodeWithRelationships(DSpaceNode dsnode) {
        neo4jDAO.deleteNodeWithRelationships(dsnode);
    }

    @Override
    public void setAuthDriver(AuthenticationDriver authDriver) {
        neo4jDAO.setAuthDriver(authDriver);
    }
}
