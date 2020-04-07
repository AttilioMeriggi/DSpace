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
    public DSpaceNode convertItem() {
        //TODO
        return null;
    }

    @Override
    public void createUpdateNode(DSpaceNode dsnode) {
        AuthenticationDriver auth_driver = getAuthDriver();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            String entity_type = dsnode.getEntityType();

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

            /* Insert metadata in end nodes */
            if (dsnode.getRelations() != null) {
                for (DSpaceRelation rels : dsnode.getRelations()) {
                    DSpaceNode curr = rels.getTarget();
                    Map<String, List<String>> curr_metadata = curr.getMetadata();
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

            /* Insert metadata in relationships */
            if (dsnode.getRelations() != null) {
                for (DSpaceRelation rels : dsnode.getRelations()) {
                    Map<String, List<String>> metadata_rels = rels.getMetadata();
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        } finally {
        }
    }

    @Override
    public void deleteNodeWithRelationships(DSpaceNode dsnode) {
        AuthenticationDriver auth_driver = getAuthDriver();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (nodo:");
            query.append(dsnode.getEntityType());
            query.append("{IDDB:$x}) ");
            query.append("DETACH DELETE nodo");

            String final_query = query.toString();
            session.writeTransaction(tx -> tx.run(final_query, Values.parameters("x", dsnode.getIDDB())));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
    }

    @Override
    public void deleteGraph() {
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

    /**
     * Read nodes of a type with all properties
     * 
     * @param dsnode
     * @return list maps properties nodes or EmptyList
     */
    @Override
    public List<Map<String, Object>> read_nodes_type(DSpaceNode dsnode) {
        AuthenticationDriver auth_driver = getAuthDriver();
        String entity_type = dsnode.getEntityType();
        List<Map<String, Object>> list_results_maps = new ArrayList<Map<String, Object>>();
        Map<String, Object> results = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {

            StringBuilder query = new StringBuilder();
            query.append("MATCH (node1:");
            query.append(entity_type);
            query.append(") ");
            query.append("RETURN properties(node1)");

            String final_query = query.toString();
            Result result = session.run(final_query);

            for (Record record : result.list()) {
                results = record.asMap();
                list_results_maps.add(results);
            }

            if (list_results_maps.size() == 0) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }

        return list_results_maps;
    }

    /**
     * Read node by IDDB
     * 
     * @param dsnode
     * @return node map with all properties or EmptyMap
     */
    @Override
    public Map<String, Object> read_node_by_id(DSpaceNode dsnode) {
        AuthenticationDriver auth_driver = getAuthDriver();
        String entity_type = dsnode.getEntityType();
        Map<String, Object> properties_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {

            StringBuilder query = new StringBuilder();
            query.append("MATCH (node:");
            query.append(entity_type);
            query.append("{IDDB:$x}) ");
            query.append("RETURN properties(node)");
            String final_query = query.toString();

            Result result = session.run(final_query, Values.parameters("x", dsnode.getIDDB()));
            for (Record record : result.list()) {
                properties_map = record.asMap();
            }

            if (properties_map.isEmpty()) {
                return Collections.emptyMap();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }

        return properties_map;
    }

    /**
     * Read properties relationship between two nodes
     * 
     */
    @Override
    public Map<String, Object> read_properties_rel(DSpaceNode dsnode1, DSpaceNode dsnode2) {
        AuthenticationDriver auth_driver = getAuthDriver();
        Map<String, Object> properties_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (node1:");
            query.append(dsnode1.getEntityType());
            query.append("{IDDB:$x})-[rel]-(node2:");
            query.append(dsnode2.getEntityType());
            query.append("{IDDB:$y}) ");
            query.append("RETURN properties(rel)");
            String final_query = query.toString();
            Result result = session.run(final_query, Values.parameters("x", dsnode1.getIDDB(), "y", dsnode2.getIDDB()));
            for (Record record : result.list()) {
                properties_map = record.asMap();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
        return properties_map;
    }

    /**
     * Read nodes by depth with all properties (including start node)
     * 
     * @param dsnode
     * @param depth
     * @return list maps properties node or EmptyList
     */
    @Override
    public List<Map<String, Object>> read_nodes_by_depth(DSpaceNode dsnode, int depth) {
        AuthenticationDriver auth_driver = getAuthDriver();
        String entity_type = dsnode.getEntityType();
        List<Map<String, Object>> list_properties_map = new ArrayList<Map<String, Object>>();
        Map<String, Object> properties_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (start:");
            query.append(entity_type);
            query.append("{IDDB:$x})-[*1..");
            query.append(depth);
            query.append("]-(ends) ");
            query.append("WITH DISTINCT ends ");
            query.append("RETURN properties(ends)");

            String final_query = query.toString();
            Result result = session.run(final_query, Values.parameters("x", dsnode.getIDDB()));
            for (Record record : result.list()) {
                properties_map = record.asMap();
                list_properties_map.add(properties_map);
            }

            if (list_properties_map.size() == 0) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
        return list_properties_map;
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