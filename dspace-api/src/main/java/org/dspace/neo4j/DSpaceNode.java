package org.dspace.neo4j;

import java.util.List;
import java.util.Map;

public class DSpaceNode {
    String entityType;
    String IDDB;
    Map<String, List<String>> metadata;
    List<DSpaceRelation> relations;

    public DSpaceNode(String entityType, String IDDB, Map<String,
        List<String>> metadata, List<DSpaceRelation> relations) {

        this.entityType = entityType;
        this.IDDB = IDDB;
        this.metadata = metadata;
        this.relations = relations;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getIDDB() {
        return IDDB;
    }

    public void setIDDB(String iDDB) {
        IDDB = iDDB;
    }

    public Map<String, List<String>> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, List<String>> metadata) {
        this.metadata = metadata;
    }

    public List<DSpaceRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<DSpaceRelation> relations) {
        this.relations = relations;
    }
}