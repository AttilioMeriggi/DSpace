/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.service;

import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;

public interface Neo4jService {
    public void createUpdateNode(DSpaceNode dsnode);

    public void deleteNodeWithRelationships(DSpaceNode dsnode);
    
    public void setAuthDriver(AuthenticationDriver authDriver);
}