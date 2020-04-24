/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.dao;

import java.util.Map;

import org.dspace.core.Context;
import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;
import org.dspace.neo4j.DSpaceRelation;

public interface Neo4jDAO {

    /***
     * Method for creating or updating nodes with their relationships
     * 
     * @param context
     * @param dsnode
     */
    public void createUpdateNode(Context context, DSpaceNode dsnode);

    /**
     * Method for deleting nodes with their relationships
     * 
     * @param context
     * @param IDDB
     */
    public void deleteNodeWithRelationships(Context context, String IDDB);

    /**
     * Method for delete all graph
     * 
     * @param context
     */
    public void deleteGraph(Context context);

    /**
     * Method for reading the nodes of a specific label
     * 
     * @param context
     * @param entityType
     * @return map containing matched nodes
     */
    public Map<String, DSpaceNode> readNodesByType(Context context, String entityType);

    /**
     * Method for reading node of a specific IDDB
     * 
     * @param context
     * @param IDDB
     * @return DSpaceNode found (depth=1)
     */
    public DSpaceNode readNodeById(Context context, String IDDB);

    /**
     * Method for reading node of a specific IDDB
     * 
     * @param context
     * @param IDDB
     * @param depth
     * @return DSpaceNode found
     */
    public DSpaceNode readNodeById(Context context, String IDDB, int depth);

    /**
     * Method for read relationship properties between two nodes
     * 
     * @param context
     * @param IDDB1
     * @param IDDB2
     * @return DSpaceRelation if two nodes are related
     */
    public DSpaceRelation readPropertiesRel(Context context, String IDDB1, String IDDB2);

    /**
     * Method for reading all nodes at a certain depth from that specified
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
