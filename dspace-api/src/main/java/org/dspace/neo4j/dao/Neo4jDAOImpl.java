/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j.dao;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;
import org.dspace.neo4j.DSpaceRelation;
import org.dspace.services.ConfigurationService;
import org.dspace.utils.DSpace;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class Neo4jDAOImpl implements Neo4jDAO {

    private AuthenticationDriver authDriver;
    private final Logger log = Logger.getLogger(Neo4jDAOImpl.class);

    public Neo4jDAOImpl() {
    }

    public void createUpdateNode(DSpaceNode dsnode) {
        AuthenticationDriver auth_driver = getAuthDriver();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            String entity_type = dsnode.getEntityType();

            /* Node creation without relationships */
            if (dsnode.getRelations().size() == 0) {
                StringBuilder query = new StringBuilder();
                query.append("MERGE (node1:");
                query.append(entity_type);
                query.append("{IDDB:$x");
                String final_query = query.toString();
                session.writeTransaction(tx -> tx.run(final_query, Values.parameters("x", dsnode.getIDDB())));
            }

            /* Create node with relationships */
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

            /* Insert metadata in relationships */
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
                    query.append("IDDB:\"");
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        } finally {
        }
    }

    public void deleteNodeWithRelationships(DSpaceNode dsnode) {
        AuthenticationDriver auth_driver = getAuthDriver();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH nodo:");
            query.append(dsnode.getEntityType());
            query.append("{IDDB:$x})");
            query.append("DETACH DELETE nodo");

            String final_query = query.toString();
            session.writeTransaction(tx -> tx.run(final_query, Values.parameters("x", dsnode.getIDDB())));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        }
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