/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.neo4j.repository;

import org.dspace.app.rest.model.Neo4jRest;
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
}
