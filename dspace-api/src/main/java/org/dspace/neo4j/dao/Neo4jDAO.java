/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.dao;

import java.util.List;
import java.util.Map;

import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;

public interface Neo4jDAO {

    /* */
    public DSpaceNode convertItem();

    /* Method for creating or updating nodes with their relationships */
    public void createUpdateNode(DSpaceNode dsnode);

    /* Method for deleating nodes with their relationships */
    public void deleteNodeWithRelationships(DSpaceNode dsnode);

    /* Method for delete all graph */
    public void deleteGraph();

    /* Method for reading the nodes of a specific label */
    public List<Map<String, Object>> read_nodes_type(DSpaceNode dsnode);

    /* Method for reading node of a specific IDDB */
    public Map<String, Object> read_node_by_id(DSpaceNode dsnode);

    /* Method for read relationship properties between two nodes */
    public Map<String, Object> read_properties_rel(DSpaceNode dsnode1, DSpaceNode dsnode2);

    /* Method for reading all nodes at a certain depth from that specified */
    public List<Map<String, Object>> read_nodes_by_depth(DSpaceNode dsnode, int depth);

    public void setAuthDriver(AuthenticationDriver authDriver);

}
