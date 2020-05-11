/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.neo4j;

import javax.servlet.http.HttpServletRequest;

import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.Neo4jRest;
import org.dspace.app.rest.model.neo4j.AuthorNGraph;
import org.dspace.app.rest.neo4j.repository.Neo4jRepository;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.neo4j.DSpaceNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This RestController takes care of all operation on Neo4j objects This class
 * will typically handle objects from:
 * <ul>
 * <li>{@link org.dspace.neo4j.DSpaceNode}</li>
 * <li>{@link org.dspace.neo4j.DSpaceRelation}</li>
 * </ul>
 * 
 * @author fcadili
 */
@RestController
@RequestMapping("/api/" + Neo4jRest.CATEGORY + "/" + Neo4jRest.NAME)
public class Neo4jController {

    @Autowired
    private Neo4jRepository neo4jRepository;

    /***
     * Insert a DSpaceNode.
     * 
     * @param request  The request
     * @param jsonNode The dspaceNode
     * @throws AuthorizeException
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/dspacenode")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void createUpdateNode(HttpServletRequest request, @RequestBody(required = true) JsonNode jsonNode)
            throws AuthorizeException {

        try {
            Context context = ContextUtil.obtainContext(request);
            DSpaceNode dspaceNode = DSpaceNode.build(jsonNode);

            neo4jRepository.createUpdateNode(context, dspaceNode);
        } catch (JsonProcessingException jpe) {
            throw new UnprocessableEntityException("Error on json: " + jsonNode, jpe);
        }
    }

    /***
     * Read a DSpaceNode.
     * 
     * @param request The request
     * @param iddb    The key
     * @return The DSpaceNode
     * @throws AuthorizeException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/dspacenode/{iddb}")
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public DSpaceNode readNodeById(HttpServletRequest request, @PathVariable("iddb") String iddb)
            throws AuthorizeException {

        Context context = ContextUtil.obtainContext(request);

        return neo4jRepository.readNodeById(context, iddb);
    }

    /***
     * Return an AuthorNGraph, starting from a UDDI of an item node (or an IDDB of a
     * neo4j node).
     * 
     * @param request          The request
     * @param iddb             The key
     * @param depth            The depth of the graph
     * @param metadata         The Metadata used to fill the name
     * @param relationMetadata The metadata used to fill the relation
     * @return The AuthorNGraph
     * @throws AuthorizeException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/authorngraph/{iddb}")
    public AuthorNGraph authorNGraph(HttpServletRequest request, @PathVariable("iddb") String iddb,
            @RequestParam(value = "depth", required = true) int depth,
            @RequestParam(value = "metadata", required = true) String metadata,
            @RequestParam(value = "relationMetadata", required = true) String relationMetadata)
            throws AuthorizeException {

        Context context = ContextUtil.obtainContext(request);

        return neo4jRepository.authorNGraph(context, iddb, depth, metadata, relationMetadata);
    }

    /***
     * Return the graph, starting from a UDDI of an item node (or an IDDB of a neo4j
     * node).
     * 
     * @param request The request
     * @param iddb    The key
     * @param depth   The depth of the graph
     * @return The DSpaceNode graph
     * @throws AuthorizeException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/ngraph/{iddb}")
    public DSpaceNode graph(HttpServletRequest request, @PathVariable("iddb") String iddb,
            @RequestParam(value = "depth", required = true) int depth) throws AuthorizeException {

        Context context = ContextUtil.obtainContext(request);

        return neo4jRepository.graph(context, iddb, depth);
    }
}