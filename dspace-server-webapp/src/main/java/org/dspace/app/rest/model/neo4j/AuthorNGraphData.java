/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model.neo4j;

import org.dspace.neo4j.DSpaceRelation;

public class AuthorNGraphData {
    private String relation;

    public AuthorNGraphData() {
    }
    
    public AuthorNGraphData(DSpaceRelation rel) {
        // read metadata...
    }
    
    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
