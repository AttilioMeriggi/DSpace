/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model.neo4j;

import java.util.List;

import org.dspace.neo4j.DSpaceNode;
import org.dspace.neo4j.DSpaceRelation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthorNGraph {
    private String id;
    private String name;
    private AuthorNGraphData authorNGraphData;
    private List<AuthorNGraph> children;

    public AuthorNGraph() {
    }

    public AuthorNGraph(DSpaceNode node, DSpaceRelation relation) {
        id = node.getIDDB();
        name = // metadato dc.author
                "";

        if (relation != null) {
            authorNGraphData = new AuthorNGraphData(relation);
        }
    }

    public void addChildren(DSpaceRelation relation) {
        if (relation.getTarget() != null) {
            // children...
            DSpaceNode target = relation.getTarget();
            if (target.getRelations() != null && target.getRelations().size() > 0) {
                for (DSpaceRelation inner : target.getRelations()) {
                    children.add(AuthorNGraph.build(inner.getTarget(), relation));
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthorNGraphData getAuthorNGraphData() {
        return authorNGraphData;
    }

    public void setAuthorNGraphData(AuthorNGraphData authorNGraphData) {
        this.authorNGraphData = authorNGraphData;
    }

    public List<AuthorNGraph> getChildren() {
        return children;
    }

    public void setChildren(List<AuthorNGraph> children) {
        this.children = children;
    }

    /***
     * Create a AuthorNGraph from a jsonNode.
     * 
     * @param jsonNode The json serialized object.
     * @return The AuthorNGraph object
     * @throws JsonProcessingException
     */
    static public AuthorNGraph build(JsonNode jsonNode) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        AuthorNGraph authorNGraph = mapper.treeToValue(jsonNode, AuthorNGraph.class);
        return authorNGraph;
    }

    /***
     * Create a AuthorNGraph from a jsonString.
     * 
     * @param jsonString The json serialized object.
     * @return The AuthorNGraph object
     * @throws JsonProcessingException
     */
    static public AuthorNGraph build(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        AuthorNGraph authorNGraph = mapper.readValue(jsonString, AuthorNGraph.class);
        return authorNGraph;
    }

    /***
     * Create a AuthorNGraph from a DSpaceNode.
     * 
     * In inner recursion node is a target node and relation is the one that is used
     * to reach the target node.
     * 
     * The start the call give the dspaceNode and null as relation.
     * 
     * @param node The DSpaceNode object.
     * @return The AuthorNGraph object
     * @throws JsonProcessingException
     */
    static public AuthorNGraph build(DSpaceNode node, DSpaceRelation relation) {
        AuthorNGraph authorNGraph = null;

        if (node.getRelations() == null || node.getRelations().isEmpty()) {
            authorNGraph = new AuthorNGraph(node, relation);
        } else {
            authorNGraph = new AuthorNGraph(node, relation);

            for (DSpaceRelation r : node.getRelations()) {
                authorNGraph.addChildren(r);
            }
        }
        return authorNGraph;
    }
}
