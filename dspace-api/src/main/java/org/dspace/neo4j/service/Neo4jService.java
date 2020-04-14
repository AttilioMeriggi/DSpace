/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.service;

import java.util.Map;
import java.util.UUID;

import org.dspace.core.Context;
import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;
import org.dspace.neo4j.DSpaceRelation;

public interface Neo4jService {

    public DSpaceNode buildEmptyItem(Context context, UUID id);

    public void insertUpdateItem(Context context, UUID id);

    public void deleteItem(Context context, UUID id);

    /* Service Neo4j for creating or updating nodes with their relationships */
    public void createUpdateNode(DSpaceNode dsnode);

    /* Service Neo4j for deleating nodes with their relationships */
    public void deleteNodeWithRelationships(String IDDB);

    /* Service Neo4j for delete all graph */
    public void deleteGraph();

    /* Service Neo4j for reading the nodes of a specific label */
    public Map<String, DSpaceNode> readNodesByType(String entityType);

    /* Service Neo4j for reading node of a specific IDDB */
    public DSpaceNode readNodeById(String IDDB);

    /* Service to read relationship properties between two nodes */
    public DSpaceRelation readPropertiesRel(String IDDB1, String IDDB2);

    /* Service Neo4j for reading all nodes at a certain depth from that specified */
    public Map<String, DSpaceNode> readNodesByDepth(String IDDB, int depth);

    public void setAuthDriver(AuthenticationDriver authDriver);
}