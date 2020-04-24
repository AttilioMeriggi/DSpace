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

    /**
     * Service to create a simple item in neo4j with type and IDDB
     * 
     * @param context
     * @param id
     * @return DSpaceNode created
     */
    public DSpaceNode buildEmptyItem(Context context, UUID id);

    /**
     * Service to create relationships and insert metadata
     * 
     * @param context
     * @param id
     */
    public void insertUpdateItem(Context context, UUID id);

    /**
     * Service to delete item in neo4j with all relationships
     * 
     * @param context
     * @param id
     */
    public void deleteItem(Context context, UUID id);

    /**
     * Service Neo4j for creating or updating nodes with their relationships
     * 
     * @param context
     * @param dsnode
     */
    public void createUpdateNode(Context context, DSpaceNode dsnode);

    /**
     * Service Neo4j for deleting nodes with their relationships
     * 
     * @param context
     * @param IDDB
     */
    public void deleteNodeWithRelationships(Context context, String IDDB);

    /**
     * Service Neo4j for delete all graph
     * 
     * @param context
     */
    public void deleteGraph(Context context);

    /**
     * Service Neo4j for reading the nodes of a specific label
     * 
     * @param context
     * @param entityType
     * @return map containing matched nodes
     */
    public Map<String, DSpaceNode> readNodesByType(Context context, String entityType);

    /**
     * Service Neo4j for reading node of a specific IDDB
     * 
     * @param context
     * @param IDDB
     * @return DSpaceNode found (depth=1)
     */
    public DSpaceNode readNodeById(Context context, String IDDB);

    /**
     * Service for reading node of a specific IDDB
     * 
     * @param context
     * @param IDDB
     * @param depth
     * @return DSpaceNode found
     */
    public DSpaceNode readNodeById(Context context, String IDDB, int depth);

    /**
     * Service to read relationship properties between two nodes
     * 
     * @param context
     * @param IDDB1
     * @param IDDB2
     * @return DSpaceRelation if two nodes are related
     */
    public DSpaceRelation readPropertiesRel(Context context, String IDDB1, String IDDB2);

    /**
     * Service Neo4j for reading all nodes at a certain depth from that specified
     * 
     * @param context
     * @param IDDB
     * @param depth
     * @return map containing nodes at the specified depth
     */
    public Map<String, DSpaceNode> readNodesByDepth(Context context, String IDDB, int depth);

    /**
     * Set driver instance
     * 
     * @param authDriver
     */
    public void setAuthDriver(AuthenticationDriver authDriver);
}