/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;
import org.dspace.neo4j.DSpaceRelation;
import org.dspace.services.ConfigurationService;
import org.dspace.utils.DSpace;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class Neo4jDAOImpl implements Neo4jDAO {

    private AuthenticationDriver authDriver;
    private final Logger log = Logger.getLogger(Neo4jDAOImpl.class);

    public Neo4jDAOImpl() {
    }

    @Override
    public void createUpdateNode(Context context, DSpaceNode dsnode) {
        AuthenticationDriver auth_driver = getAuthDriver();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            String entity_type = dsnode.getEntityType();

            /* remove all metadata if the node exist */
            {
                StringBuilder query = new StringBuilder();
                query.append("MATCH (nodo:");
                query.append(entity_type);
                query.append("{IDDB:$x}) ");
                query.append("SET nodo = {} ");
                query.append("SET nodo = {IDDB:$y}");
                String final_query = query.toString();
                session.writeTransaction(
                        tx -> tx.run(final_query, Values.parameters("x", dsnode.getIDDB(), "y", dsnode.getIDDB())));
            }

            /* Node creation without relationships */
            {
                StringBuilder query = new StringBuilder();
                query.append("MERGE (node1:");
                query.append(entity_type);
                query.append("{IDDB:$x})");
                String final_query = query.toString();
                session.writeTransaction(tx -> tx.run(final_query, Values.parameters("x", dsnode.getIDDB())));
            }

            /* Create node with relationships */
            if (dsnode.getRelations() != null) {
                for (DSpaceRelation rels : dsnode.getRelations()) {
                    String entity_target = rels.getTarget().getEntityType();

                    StringBuilder query = new StringBuilder();
                    query.append("MERGE (node1:");
                    query.append(entity_type);
                    query.append(" {IDDB:$x}) MERGE (node2:");
                    query.append(entity_target);
                    query.append(" {IDDB:$w}) SET node1 = {IDDB:$x}, node2 = {IDDB:$w} MERGE(node1)-[:");
                    query.append(rels.getType());
                    query.append("]-(node2)");

                    String final_query = query.toString();
                    session.writeTransaction(tx -> tx.run(final_query,
                            Values.parameters("x", dsnode.getIDDB(), "w", rels.getTarget().getIDDB())));
                }
            }

            /* Insert metadata in start node */
            Map<String, List<String>> metadata_start = dsnode.getMetadata();
            if (metadata_start != null) {
                for (String key : metadata_start.keySet()) {
                    List<String> metadata_curr = metadata_start.get(key);
                    String s = "[";
                    int i = 0;
                    for (String value : metadata_curr) {
                        if (i != 0) {
                            s += ",";
                        }
                        s += "\"" + value + "\"";
                        i++;
                    }
                    s += "]";
                    StringBuilder query = new StringBuilder();
                    query.append("MATCH (node1:");
                    query.append(entity_type);
                    query.append("{IDDB:$x}) SET node1.");
                    query.append(key.replaceAll("\\.|:", "_"));
                    query.append(" = " + s);

                    String final_query = query.toString();
                    session.writeTransaction(tx -> tx.run(final_query, Values.parameters("x", dsnode.getIDDB())));
                }
            }

            /* Insert metadata in end nodes */
            if (dsnode.getRelations() != null) {
                for (DSpaceRelation rels : dsnode.getRelations()) {
                    DSpaceNode curr = rels.getTarget();
                    Map<String, List<String>> curr_metadata = curr.getMetadata();
                    if (curr_metadata != null) {
                        for (String key : curr_metadata.keySet()) {
                            List<String> metadata_curr = curr_metadata.get(key);
                            String s = "[";
                            int i = 0;
                            for (String value : metadata_curr) {
                                if (i != 0) {
                                    s += ",";
                                }
                                s += "\"" + value + "\"";
                                i++;
                            }
                            s += "]";
                            StringBuilder query = new StringBuilder();
                            query.append("MATCH (node:");
                            query.append(curr.getEntityType());
                            query.append("{IDDB:$x}) SET node.");
                            query.append(key.replaceAll("\\.|:", "_"));
                            query.append(" = " + s);

                            String final_query = query.toString();
                            session.writeTransaction(tx -> tx.run(final_query, Values.parameters("x", curr.getIDDB())));
                        }
                    }
                }
            }

            /* Insert metadata in relationships */
            if (dsnode.getRelations() != null) {
                for (DSpaceRelation rels : dsnode.getRelations()) {
                    Map<String, List<String>> metadata_rels = rels.getMetadata();
                    if (metadata_rels != null) {
                        for (String key : metadata_rels.keySet()) {
                            List<String> metadata_curr = metadata_rels.get(key);
                            String s = "[";
                            int i = 0;
                            for (String value : metadata_curr) {
                                if (i != 0) {
                                    s += ",";
                                }
                                s += "\"" + value + "\"";
                                i++;
                            }
                            s += "]";
                            StringBuilder query = new StringBuilder();
                            query.append("MATCH (node1:");
                            query.append(entity_type);
                            query.append("{IDDB:\"");
                            query.append(dsnode.getIDDB());
                            query.append("\"})");
                            query.append("-[rels:");
                            query.append(rels.getType());
                            query.append("]-");
                            query.append("(node2:");
                            query.append(rels.getTarget().getEntityType());
                            query.append("{IDDB:\"");
                            query.append(rels.getTarget().getIDDB());
                            query.append("\"}) ");
                            query.append("SET rels.");
                            query.append(key.replaceAll("\\.|:", "_"));
                            query.append(" = " + s);

                            String final_query = query.toString();
                            session.writeTransaction(tx -> tx.run(final_query));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        } finally {
        }
    }

    @Override
    public void deleteNodeWithRelationships(Context context, String IDDB) {
        AuthenticationDriver auth_driver = getAuthDriver();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (nodo");
            query.append("{IDDB:$x}) ");
            query.append("DETACH DELETE nodo");

            String final_query = query.toString();
            session.writeTransaction(tx -> tx.run(final_query, Values.parameters("x", IDDB)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
    }

    @Override
    public void deleteGraph(Context context) {
        AuthenticationDriver auth_driver = getAuthDriver();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (nodo) ");
            query.append("DETACH DELETE nodo");
            String final_query = query.toString();
            session.writeTransaction(tx -> tx.run(final_query));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
    }

    @Override
    public Map<String, DSpaceNode> readNodesByType(Context context, String entityType) {
        AuthenticationDriver auth_driver = getAuthDriver();
        Map<String, DSpaceNode> final_map = new HashMap<String, DSpaceNode>();
        Map<String, Object> record_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {

            StringBuilder query = new StringBuilder();
            query.append("MATCH (node1:");
            query.append(entityType);
            query.append(") ");
            query.append("RETURN properties(node1)");

            String final_query = query.toString();
            Result result = session.run(final_query);

            for (Record record : result.list()) {
                String ID = "";
                Map<String, List<String>> currMetadata = new HashMap<String, List<String>>();
                DSpaceNode currNode = new DSpaceNode(ID, currMetadata);
                record_map = record.asMap();
                for (String s : record_map.keySet()) {
                    Map<String, Object> o = (Map<String, Object>) record_map.get(s);
                    for (String s2 : o.keySet()) {
                        switch (o.get(s2).getClass().toString()) {
                            case "class java.lang.String":
                                ID = (String) o.get(s2);
                                break;
                            case "class java.util.Collections$UnmodifiableRandomAccessList":
                            case "class java.util.Collections$SingletonList":
                                List<String> properties = (List<String>) o.get(s2);
                                currMetadata.put(s2, properties);
                                break;
                            default:
                        }
                    }
                    currNode.setEntityType(entityType);
                    currNode.setIDDB(ID);
                    currNode.setMetadata(currMetadata);

                }
                final_map.put(currNode.getIDDB(), currNode);
            }

            if (final_map.isEmpty()) {
                return Collections.emptyMap();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }

        return final_map;
    }

    @Override
    public DSpaceNode readNodeById(Context context, String IDDB) {
        return readNodeById(context, IDDB, 1, null);
    }

    @Override
    public DSpaceNode readNodeById(Context context, String IDDB, int depth) {
        return readNodeById(context, IDDB, depth, null);
    }

    private DSpaceNode readNodeById(Context context, String IDDB, int depth, List<String> IDDBFather) {
        if (depth <= 0)
            return null;

        AuthenticationDriver auth_driver = getAuthDriver();
        DSpaceNode readNode = null;
        Map<String, Object> record_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {

            StringBuilder query = new StringBuilder();
            query.append("MATCH (node");
            query.append("{IDDB:$x}) ");
            query.append("RETURN properties(node)");
            String final_query = query.toString();

            Result result = session.run(final_query, Values.parameters("x", IDDB));
            for (Record record : result.list()) {
                String ID = "";
                Map<String, List<String>> currMetadata = new HashMap<String, List<String>>();
                readNode = new DSpaceNode(ID, currMetadata);
                record_map = record.asMap();
                for (String s : record_map.keySet()) {
                    Map<String, Object> o = (Map<String, Object>) record_map.get(s);
                    for (String s2 : o.keySet()) {
                        switch (o.get(s2).getClass().toString()) {
                            case "class java.lang.String":
                                ID = (String) o.get(s2);
                                readNode.setIDDB(ID);
                                break;
                            case "class java.util.Collections$UnmodifiableRandomAccessList":
                            case "class java.util.Collections$SingletonList":
                                List<String> properties = (List<String>) o.get(s2);
                                currMetadata.put(s2, properties);
                                break;
                            default:
                        }
                    }
                    readNode.setEntityType(readNodeLabel(context, ID));
                    readNode.setIDDB(ID);
                    readNode.setMetadata(currMetadata);
                    readNode.setRelations(readNodeRelationships(context, ID, IDDBFather));
                    if (readNode.getRelations() != null && readNode.getRelations().size() > 0) {
                        for (DSpaceRelation rel : readNode.getRelations()) {
                            List<String> IDDBList = new ArrayList<String>();
                            if (IDDBFather != null) {
                                IDDBList.addAll(IDDBFather);
                            }
                            IDDBList.add(IDDB);
                            DSpaceNode node = readNodeById(context, rel.getTarget().getIDDB(), depth - 1, IDDBList);
                            if (node != null) {
                                rel.setTarget(node);
                            }

                        }
                    }

                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
        return readNode;
    }

    private List<DSpaceRelation> readNodeRelationships(Context context, String IDDB, List<String> skipIDDB) {
        Map<String, DSpaceNode> relatedNodes = readNodesByDepth(context, IDDB, 1);
        List<DSpaceRelation> relations = new ArrayList<DSpaceRelation>();
        for (String s : relatedNodes.keySet()) {
            DSpaceRelation currRelation = new DSpaceRelation();
            currRelation.setType(readTypeRelationships(context, IDDB, relatedNodes.get(s).getIDDB()));
            currRelation.setTarget(relatedNodes.get(s));
            relatedNodes.get(s).setEntityType(readNodeLabel(context, relatedNodes.get(s).getIDDB()));
            currRelation.setMetadata(readPropertiesRel(context, IDDB, relatedNodes.get(s).getIDDB()).getMetadata());

            if (skipIDDB == null || !skipIDDB.contains(s)) {
                relations.add(currRelation);
            }
        }
        return relations;
    }

    private String readTypeRelationships(Context context, String IDDB1, String IDDB2) {
        AuthenticationDriver auth_driver = getAuthDriver();
        String relationType = null;
        Map<String, Object> record_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (node1{IDDB:$x})-[rel]-(node2{IDDB:$y}) RETURN type(rel)");
            String final_query = query.toString();
            Result result = session.run(final_query, Values.parameters("x", IDDB1, "y", IDDB2));
            for (Record record : result.list()) {
                record_map = record.asMap();
                for (String s : record_map.keySet()) {
                    relationType = (String) record_map.get(s);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
        return relationType;
    }

    private String readNodeLabel(Context context, String IDDB) {
        AuthenticationDriver auth_driver = getAuthDriver();
        String label = null;
        Map<String, Object> record_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (node{IDDB:$x}) RETURN labels(node)");
            String final_query = query.toString();
            Result result = session.run(final_query, Values.parameters("x", IDDB));
            for (Record record : result.list()) {
                record_map = record.asMap();
                for (String s : record_map.keySet()) {
                    List<Object> labels_list = (List<Object>) record_map.get(s);
                    switch (record_map.get(s).getClass().toString()) {
                        case "class java.util.Collections$UnmodifiableRandomAccessList":
                        case "class java.util.Collections$SingletonList":
                            label = (String) labels_list.get(0);
                            break;
                        default:
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
        return label;
    }

    @Override
    public DSpaceRelation readPropertiesRel(Context context, String IDDB1, String IDDB2) {
        AuthenticationDriver auth_driver = getAuthDriver();
        DSpaceRelation final_rel = null;
        Map<String, Object> record_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (node1");
            query.append("{IDDB:$x})-[rel]-(node2{IDDB:$y}) ");
            query.append("RETURN properties(rel)");
            String final_query = query.toString();
            Result result = session.run(final_query, Values.parameters("x", IDDB1, "y", IDDB2));
            for (Record record : result.list()) {
                Map<String, List<String>> currMetadata = new HashMap<String, List<String>>();
                final_rel = new DSpaceRelation(currMetadata);
                record_map = record.asMap();
                for (String s : record_map.keySet()) {
                    Map<String, Object> o = (Map<String, Object>) record_map.get(s);
                    for (String s2 : o.keySet()) {
                        switch (o.get(s2).getClass().toString()) {
                            case "class java.lang.String":
                                break;
                            case "class java.util.Collections$UnmodifiableRandomAccessList":
                            case "class java.util.Collections$SingletonList":
                                List<String> properties = (List<String>) o.get(s2);
                                currMetadata.put(s2, properties);
                                break;
                            default:
                        }
                    }
                    final_rel.setMetadata(currMetadata);

                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
        return final_rel;
    }

    @Override
    public Map<String, DSpaceNode> readNodesByDepth(Context context, String IDDB, int depth) {
        AuthenticationDriver auth_driver = getAuthDriver();
        Map<String, DSpaceNode> final_map = new HashMap<String, DSpaceNode>();
        Map<String, Object> record_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (start");
            query.append("{IDDB:$x})-[*1..");
            query.append(depth);
            query.append("]-(ends) ");
            query.append("WITH DISTINCT ends ");
            query.append("RETURN properties(ends)");

            String final_query = query.toString();
            Result result = session.run(final_query, Values.parameters("x", IDDB));
            for (Record record : result.list()) {
                String ID = "";
                Map<String, List<String>> currMetadata = new HashMap<String, List<String>>();
                DSpaceNode currNode = new DSpaceNode(ID, currMetadata);
                record_map = record.asMap();
                for (String s : record_map.keySet()) {
                    Map<String, Object> o = (Map<String, Object>) record_map.get(s);
                    for (String s2 : o.keySet()) {
                        switch (o.get(s2).getClass().toString()) {
                            case "class java.lang.String":
                                ID = (String) o.get(s2);
                                break;
                            case "class java.util.Collections$UnmodifiableRandomAccessList":
                            case "class java.util.Collections$SingletonList":
                                List<String> properties = (List<String>) o.get(s2);
                                currMetadata.put(s2, properties);
                                break;
                            default:
                        }
                    }
                    currNode.setIDDB(ID);
                    currNode.setMetadata(currMetadata);

                }
                if (!currNode.getIDDB().equals(IDDB)) {
                    final_map.put(currNode.getIDDB(), currNode);
                }
            }

            if (final_map.isEmpty()) {
                return Collections.emptyMap();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
        return final_map;
    }

    public void setAuthDriver(AuthenticationDriver authDriver) {
        this.authDriver = authDriver;
    }

    public AuthenticationDriver getAuthDriver() {
        if (this.authDriver == null) {
            ConfigurationService configurationService = new DSpace().getConfigurationService();
            this.authDriver = new AuthenticationDriver(configurationService.getProperty("neo4j.config.server"),
                    configurationService.getProperty("neo4j.config.username"),
                    configurationService.getProperty("neo4j.config.password"));
        }
        return this.authDriver;
    }

}
