/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j;

import java.util.List;
import java.util.Map;

import org.hibernate.proxy.HibernateProxyHelper;

public class DSpaceNode {
    String entityType;
    String IDDB;
    Map<String, List<String>> metadata;
    List<DSpaceRelation> relations;

    public DSpaceNode(String entityType, String IDDB) {
        this.entityType = entityType;
        this.IDDB = IDDB;
    }

    public DSpaceNode(String entityType, String IDDB, Map<String, List<String>> metadata,
            List<DSpaceRelation> relations) {

        this(entityType, IDDB);
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

    /**
     * Return <code>true</code> if <code>other</code> is the same DSpaceNode as
     * this object, <code>false</code> otherwise
     *
     * @param obj object to compare to
     * @return <code>true</code> if object passed in represents the same
     *         DSpaceNode as this object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Class<?> objClass = HibernateProxyHelper.getClassWithoutInitializingProxy(obj);
        if (getClass() != objClass) {
            return false;
        }
        final DSpaceNode other = (DSpaceNode) obj;
        if (this.IDDB.equals(other.IDDB)) {
            return false;
        }
        if (!this.getIDDB().equals(other.getIDDB())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.IDDB.hashCode();
        hash = 47 * hash + this.getIDDB().hashCode();
        return hash;
    }
}
