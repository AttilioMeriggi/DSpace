/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.neo4j.dao.Neo4jDAO;
import org.dspace.neo4j.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;

public class Neo4jServiceImpl implements Neo4jService {

    private static final Logger log = LogManager.getLogger(Neo4jServiceImpl.class);

    @Autowired(required = true)
    private Neo4jDAO neo4jDAO;

    @Autowired(required = true)
    private ItemService itemService;

    @Override
    public DSpaceNode buildEmptyItem(Context context, UUID id) {
        DSpaceNode dsnode = null;
        try {
            Item item = itemService.find(context, id);
            List<MetadataValue> values = itemService.getMetadata(item, "relationship", "type", null, null);

            String entityType = "";
            for (MetadataValue value : values) {
                entityType = value.getValue();
            }
            dsnode = new DSpaceNode(entityType, item.getID().toString());
            this.createUpdateNode(dsnode);
        } catch (Exception e) {
            log.error("Error creating empty item", e);

            return null;
        }
        return dsnode;
    }

    @Override
    public void insertUpdateItem(Context context, UUID id) {
        try {
            Item item = itemService.find(context, id);

            List<DSpaceRelation> relations = new ArrayList<DSpaceRelation>();

            List<MetadataValue> metadataItem = item.getMetadata();
            for (MetadataValue metadataValue : metadataItem) {
                if (metadataValue.getAuthority() != null) {
                    String idAuthority = metadataValue.getAuthority();
                    DSpaceNode converted = buildEmptyItem(context, UUID.fromString(idAuthority));
                    Map<String, List<String>> metadataNode = new HashMap<String, List<String>>();
                    for (MetadataValue m : metadataItem) {
                        String key = m.getMetadataField().toString();
                        if (!metadataNode.containsKey(key)) {
                            metadataNode.put(key, new ArrayList<String>());
                        }
                        metadataNode.get(key).add(m.getValue());
                    }

                    relations.add(new DSpaceRelation(converted.getEntityType(), converted, metadataNode));
                }
            }
            List<MetadataValue> values = itemService.getMetadata(item, "relationship", "type", null, null);

            String entityType = "";
            for (MetadataValue value : values) {
                entityType = value.getValue();
            }
            Map<String, List<String>> metadataNode = new HashMap<String, List<String>>();
            for (MetadataValue m : metadataItem) {
                String key = m.getMetadataField().toString();
                if (!metadataNode.containsKey(key)) {
                    metadataNode.put(key, new ArrayList<String>());
                }
                metadataNode.get(key).add(m.getValue());
            }
            this.createUpdateNode(new DSpaceNode(entityType, id.toString(), metadataNode, relations));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteItem(Context context, UUID id) {
        try {
            neo4jDAO.deleteNodeWithRelationships(id.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void createUpdateNode(DSpaceNode dsnode) {
        neo4jDAO.createUpdateNode(dsnode);
    }

    @Override
    public void deleteNodeWithRelationships(String IDDB) {
        neo4jDAO.deleteNodeWithRelationships(IDDB);
    }

    @Override
    public void deleteGraph() {
        neo4jDAO.deleteGraph();
    }

    @Override
    public List<Map<String, Object>> readNodesByType(DSpaceNode dsnode) {
        return neo4jDAO.readNodesByType(dsnode);
    }

    @Override
    public DSpaceNode readNodeById(String IDDB) {
        return neo4jDAO.readNodeById(IDDB);
    }

    @Override
    public Map<String, Object> readPropertiesRel(DSpaceNode dsnode1, DSpaceNode dsnode2) {
        return neo4jDAO.readPropertiesRel(dsnode1, dsnode2);
    }

    @Override
    public Map<String, DSpaceNode> readNodesByDepth(String IDDB, int depth) {
        return neo4jDAO.readNodesByDepth(IDDB, depth);
    }

    @Override
    public void setAuthDriver(AuthenticationDriver authDriver) {
        neo4jDAO.setAuthDriver(authDriver);
    }
}
