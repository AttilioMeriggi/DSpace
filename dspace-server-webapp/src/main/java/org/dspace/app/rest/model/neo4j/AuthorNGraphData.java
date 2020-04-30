/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model.neo4j;

import java.util.List;
import java.util.Map;

import org.dspace.neo4j.DSpaceRelation;

public class AuthorNGraphData {
    private String relation = "";

    public AuthorNGraphData() {
    }
    
    public AuthorNGraphData(DSpaceRelation rel, List<String> metadata) {
        if (rel.getMetadata() != null && rel.getMetadata().size() > 0) {
            for (Map.Entry<String, List<String>> ml : rel.getMetadata().entrySet()) {
                if (metadata.contains(ml.getKey()) && ml.getValue() != null && ml.getValue().size() > 0) {
                    if (relation != null && relation.length() > 0) {
                        relation += ", ";
                    }
                    relation += ml.getKey() + ":" + ml.getValue().toString();
                }
            }
        }
    }
    
    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
    
    public void addRelation(AuthorNGraphData d) {
        //if (d.getRelation() != null && d.getRelation().length() > 0) {
            relation += "; " + d.getRelation();
        //}
    }
}
