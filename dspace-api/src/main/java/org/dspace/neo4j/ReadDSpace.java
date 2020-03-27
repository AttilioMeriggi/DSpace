package org.dspace.neo4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dspace.services.ConfigurationService;
import org.dspace.utils.DSpace;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class ReadDSpace {
    private AuthenticationDriver auth_driver;
    private final Logger log = Logger.getLogger(ReadDSpace.class);

    public ReadDSpace() {
        ConfigurationService configurationService = new DSpace().getConfigurationService();
        this.auth_driver = new AuthenticationDriver(configurationService.getProperty("neo4j.server"),
            configurationService.getProperty("neo4j.username"),
                        configurationService.getProperty("neo4j.password"));
    }

    /**
     * Read nodes of a type with all properties
     * @param dsnode
     * @return list maps properties nodes or EmptyList
     */
    public List<Map<String, Object>> read_nodes_type (DSpaceNode dsnode) {
        String entity_type = dsnode.getEntityType();
        List<Map<String, Object>> list_results_maps = new ArrayList<Map<String, Object>>();
        Map<String, Object> results = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {

            StringBuilder query = new StringBuilder();
            query.append("MATCH (node1:");
            query.append(entity_type);
            query.append(")");
            query.append(" RETURN properties(node1)");

            String final_query = query.toString();
            Result result = session.run(final_query, Values.parameters("x",dsnode.getIDDB()));

            for (Record record : result.list()) {
                results = record.asMap();
                list_results_maps.add(results);
            }

            if (list_results_maps.isEmpty()) {
                return Collections.emptyList();
            }

            //TODO: System.out.println (Maps) ?
        } catch (Exception e) {
            //e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally { }

        return list_results_maps;
    }


    /**
     * Read node by IDDB
     * @param dsnode
     * @return node map with all properties or EmptyMap
     */
    public Map<String, Object> read_node_by_id (DSpaceNode dsnode) {
        String entity_type = dsnode.getEntityType();
        Map<String, Object> properties_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {

            StringBuilder query = new StringBuilder();
            query.append("MATCH node: ");
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
            //e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally { }

        return properties_map;
    }

    //TODO: Printf result

    /**
     * Read nodes by depth with all properties (including start node)
     * @param dsnode
     * @param depth
     * @return list maps properties node or EmptyList
     */
    public List<Map<String, Object>> read_nodes_by_depth (DSpaceNode dsnode, int depth) {
        String entity_type = dsnode.getEntityType();
        List<Map<String, Object>> list_properties_map = new ArrayList<Map<String, Object>>();
        Map<String, Object> properties_map = new HashMap<String, Object>();
        try (Session session = auth_driver.getBoltDriver().getDriver().session()) {
            StringBuilder query = new StringBuilder();
            query.append("MATCH (start:");
            query.append(entity_type);
            query.append("{IDDB:$x})-[*0..");
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

            //TODO: PRINTF???

            if (list_properties_map.size() == 1) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally { }
        return list_properties_map;
    }
}

