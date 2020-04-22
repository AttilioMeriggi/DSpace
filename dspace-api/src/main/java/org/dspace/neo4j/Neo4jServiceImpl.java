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
    private static final String DEFAULT_ENTITY_TYPE = "person";

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
            if (values == null || values.size() <= 0) {
                entityType = DEFAULT_ENTITY_TYPE;
            } else {
                for (MetadataValue value : values) {
                    entityType = value.getValue();
                }
            }
            dsnode = new DSpaceNode(entityType, item.getID().toString());
            this.createUpdateNode(context, dsnode);
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
                try {
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
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            List<MetadataValue> values = itemService.getMetadata(item, "relationship", "type", null, null);

            String entityType = "";
            if (values == null || values.size() <= 0) {
                entityType = DEFAULT_ENTITY_TYPE;
            } else {
                for (MetadataValue value : values) {
                    entityType = value.getValue();
                }
            }
            Map<String, List<String>> metadataNode = new HashMap<String, List<String>>();
            for (MetadataValue m : metadataItem) {
                String key = m.getMetadataField().toString();
                if (!metadataNode.containsKey(key)) {
                    metadataNode.put(key, new ArrayList<String>());
                }
                metadataNode.get(key).add(m.getValue());
            }
            this.createUpdateNode(context, new DSpaceNode(entityType, id.toString(), metadataNode, relations));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteItem(Context context, UUID id) {
        try {
            this.deleteNodeWithRelationships(context, id.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void createUpdateNode(Context context, DSpaceNode dsnode) {
        neo4jDAO.createUpdateNode(context, dsnode);
    }

    @Override
    public void deleteNodeWithRelationships(Context context, String IDDB) {
        neo4jDAO.deleteNodeWithRelationships(context, IDDB);
    }

    @Override
    public void deleteGraph(Context context) {
        neo4jDAO.deleteGraph(context);
    }

    @Override
    public Map<String, DSpaceNode> readNodesByType(Context context, String entityType) {
        return neo4jDAO.readNodesByType(context, entityType);
    }

    @Override
    public DSpaceNode readNodeById(Context context, String IDDB) {
        return neo4jDAO.readNodeById(context, IDDB);
    }
    
    @Override
    public DSpaceNode readNodeById(Context context, String IDDB, int depth) {
        return neo4jDAO.readNodeById(context, IDDB, depth);
    }

    @Override
    public DSpaceRelation readPropertiesRel(Context context, String IDDB1, String IDDB2) {
        return neo4jDAO.readPropertiesRel(context, IDDB1, IDDB2);
    }

    @Override
    public Map<String, DSpaceNode> readNodesByDepth(Context context, String IDDB, int depth) {
        return neo4jDAO.readNodesByDepth(context, IDDB, depth);
    }

    @Override
    public void setAuthDriver(AuthenticationDriver authDriver) {
        neo4jDAO.setAuthDriver(authDriver);
    }
}
