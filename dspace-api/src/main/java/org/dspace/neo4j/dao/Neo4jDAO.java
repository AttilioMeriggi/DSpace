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

    /* Method for creating or updating nodes with their relationships */
    public void createUpdateNode(Context context, DSpaceNode dsnode);

    /* Method for deleating nodes with their relationships */
    public void deleteNodeWithRelationships(Context context, String IDDB);

    /* Method for delete all graph */
    public void deleteGraph(Context context);

    /* Method for reading the nodes of a specific label */
    public Map<String, DSpaceNode> readNodesByType(Context context, String entityType);

    /* Method for reading node of a specific IDDB */
    public DSpaceNode readNodeById(Context context, String IDDB);

    /* Method for read relationship properties between two nodes */
    public DSpaceRelation readPropertiesRel(Context context, String IDDB1, String IDDB2);

    /* Method for reading all nodes at a certain depth from that specified */
    public Map<String, DSpaceNode> readNodesByDepth(Context context, String IDDB, int depth);

    public void setAuthDriver(AuthenticationDriver authDriver);

}
