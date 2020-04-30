/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.neo4j.repository;

import java.util.Arrays;

import org.dspace.app.rest.model.Neo4jRest;
import org.dspace.app.rest.model.neo4j.AuthorNGraph;
import org.dspace.core.Context;
import org.dspace.neo4j.DSpaceNode;
import org.dspace.neo4j.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component(Neo4jRest.CATEGORY + "." + Neo4jRest.NAME + "." + Neo4jRest.REST)
public class Neo4jRepository {

    @Autowired
    Neo4jService neo4jService;

    /***
     * Insert a DSpaceNode.
     * 
     * @param context    The context
     * @param dspaceNode The dspaceNode
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void createUpdateNode(Context context, DSpaceNode dspaceNode) {
        neo4jService.createUpdateNode(context, dspaceNode);
    }

    /***
     * Read a DSpaceNode
     * 
     * @param context The context
     * @param iddb    The key
     * @return The dspaceNode
     */
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public DSpaceNode readNodeById(Context context, String iddb) {
        DSpaceNode dspaceNode = neo4jService.readNodeById(context, iddb);

        return dspaceNode;
    }

    /***
     * Return an AuthorNGraph, starting from a UDDI of an item node (or an IDDB of a
     * neo4j node).
     * 
     * @param context          The context
     * @param iddb             The key
     * @param depth            The depth of the graph
     * @param metadata         The Metadata used to fill the name
     * @param relationMetadata The metadata used to fill the relation
     * @return
     */
    public AuthorNGraph authorNGraph(Context context, String iddb, int depth, String metadata,
            String relationMetadata) {
        DSpaceNode readFromRes1ById = neo4jService.readNodeById(context, iddb, depth);

        AuthorNGraph authorNGraph = AuthorNGraph.build(readFromRes1ById, metadata,
                Arrays.asList(relationMetadata.split(",")));

        return authorNGraph;
    }

    /***
     * Return the graph, starting from a UDDI of an item node (or an IDDB of a neo4j
     * node).
     * 
     * @param context The context
     * @param iddb    The key
     * @param depth   The depth of the graph
     * @return
     */
    public DSpaceNode graph(Context context, String iddb, int depth) {
        DSpaceNode readFromRes1ById = neo4jService.readNodeById(context, iddb, depth);

        return readFromRes1ById;
    }
}
