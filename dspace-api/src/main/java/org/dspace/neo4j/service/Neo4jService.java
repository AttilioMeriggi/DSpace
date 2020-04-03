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

import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;

public interface Neo4jService {
    public void createUpdateNode(DSpaceNode dsnode);

    public void deleteNodeWithRelationships(DSpaceNode dsnode);

    public List<Map<String, Object>> read_nodes_type(DSpaceNode dsnode);

    public Map<String, Object> read_node_by_id(DSpaceNode dsnode);

    public List<Map<String, Object>> read_nodes_by_depth(DSpaceNode dsnode, int depth);

    public void setAuthDriver(AuthenticationDriver authDriver);
}