/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dspace.core.Context;
import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;

public interface Neo4jService {

    public DSpaceNode buildEmptyItem(Context context, UUID id);

    public void insertUpdateItem(Context context, UUID id);

    /* Service Neo4j for creating or updating nodes with their relationships */
    public void createUpdateNode(DSpaceNode dsnode);

    /* Service Neo4j for deleating nodes with their relationships */
    public void deleteNodeWithRelationships(DSpaceNode dsnode);

    /* Service Neo4j for delete all graph */
    public void deleteGraph();

    /* Service Neo4j for reading the nodes of a specific label */
    public List<Map<String, Object>> readNodesByType(DSpaceNode dsnode);

    /* Service Neo4j for reading node of a specific IDDB */
    public Map<String, Object> readNodeById(DSpaceNode dsnode);

    /* Service to read relationship properties between two nodes */
    public Map<String, Object> readPropertiesRel(DSpaceNode dsnode1, DSpaceNode dsnode2);

    /* Service Neo4j for reading all nodes at a certain depth from that specified */
    public Map<String, DSpaceNode> readNodesByDepth(String IDDB, int depth);

    public void setAuthDriver(AuthenticationDriver authDriver);
}