package org.dspace.neo4j.dao;

import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;

public interface Neo4jDAO {

    public void createUpdateNode(DSpaceNode dsnode);
    
    public void deleteNodeWithRelationships(DSpaceNode dsnode);
    
    public void setAuthDriver(AuthenticationDriver authDriver);
}
