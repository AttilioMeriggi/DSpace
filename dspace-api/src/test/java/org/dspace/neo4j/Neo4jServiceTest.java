/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.AbstractNeo4jTest;
import org.dspace.neo4j.factory.Neo4jFactory;
import org.dspace.neo4j.service.Neo4jService;
import org.junit.Before;
import org.junit.Test;

public class Neo4jServiceTest extends AbstractNeo4jTest {
    private static final Logger log = LogManager.getLogger(Neo4jServiceTest.class);
    private Neo4jService neo4jService = Neo4jFactory.getInstance().getNeo4jService();
    private AuthenticationDriver authDriver = null;

    private DSpaceNode generic_researcher;
    private DSpaceNode generic_publication;

    private Map<String, List<String>> metadata_res1;
    private Map<String, List<String>> metadata_res2;
    private Map<String, List<String>> metadata_res3;

    private Map<String, List<String>> metadata_pub1;
    private Map<String, List<String>> metadata_pub2;

    private Map<String, List<String>> metadata_rel1;
    private Map<String, List<String>> metadata_rel2;
    private Map<String, List<String>> metadata_rel3;

    @Before
    @Override
    public void init() {
        try {
            super.init();

            authDriver = new AuthenticationDriver(neo4j.boltURI().toString(), null, null);
            neo4jService.setAuthDriver(authDriver);
        } catch (Exception ex) {
            log.error("Error during test initialization", ex);
        }
    }

    @Before
    public void setUp() throws Exception {

        generic_researcher = new DSpaceNode("Researcher", "1");
        generic_publication = new DSpaceNode("Publication", "101");

        /* Metadata researcher_1 IDDB = 1 */
        metadata_res1 = new HashMap<String, List<String>>();
        List<String> list1_res1 = new ArrayList<>();
        list1_res1.add("Steve");
        List<String> list2_res1 = new ArrayList<>();
        list2_res1.add("Smith");
        List<String> list3_res1 = new ArrayList<String>();
        list3_res1.add("Oxford University");
        list3_res1.add("Roma Tre");
        metadata_res1.put("dc.name", list1_res1);
        metadata_res1.put("dc.surname", list2_res1);
        metadata_res1.put("dc.department", list3_res1);

        /* Metadata researcher_2 IDDB = 2 */
        metadata_res2 = new HashMap<String, List<String>>();
        List<String> list1_res2 = new ArrayList<>();
        list1_res2.add("Claire");
        List<String> list2_res2 = new ArrayList<>();
        list2_res2.add("Williams");
        List<String> list3_res2 = new ArrayList<String>();
        list3_res2.add("Bocconi");
        list3_res2.add("Sapienza");
        metadata_res2.put("dc.name", list1_res2);
        metadata_res2.put("dc.surname", list2_res2);
        metadata_res2.put("dc.department", list3_res2);

        /* Metadata researcher_3 IDDB = 3 */
        metadata_res3 = new HashMap<String, List<String>>();
        List<String> list1_res3 = new ArrayList<>();
        list1_res3.add("Tom");
        List<String> list2_res3 = new ArrayList<>();
        list2_res3.add("Taylor");
        List<String> list3_res3 = new ArrayList<String>();
        list3_res3.add("Oxford University");
        metadata_res3.put("dc.name", list1_res3);
        metadata_res3.put("dc.surname", list2_res3);
        metadata_res3.put("dc.department", list3_res3);

        /* Metadata publication_1 IDDB = 101 */
        metadata_pub1 = new HashMap<String, List<String>>();
        List<String> list1_pub1 = new ArrayList<>();
        list1_pub1.add("Web Research");
        List<String> list2_pub1 = new ArrayList<>();
        list2_pub1.add("Magazine");
        metadata_pub1.put("dc.title", list1_pub1);
        metadata_pub1.put("dc.type", list2_pub1);

        /* Metadata publication_2 IDDB = 102 */
        metadata_pub2 = new HashMap<String, List<String>>();
        List<String> list1_pub2 = new ArrayList<>();
        list1_pub2.add("Software Research");
        List<String> list2_pub2 = new ArrayList<>();
        list2_pub2.add("Item");
        metadata_pub2.put("dc.title", list1_pub2);
        metadata_pub2.put("dc.type", list2_pub2);

        /* Metadata relationship_1 */
        metadata_rel1 = new HashMap<String, List<String>>();
        List<String> list1_rel1 = new ArrayList<>();
        list1_rel1.add("13/01/2020");
        List<String> list2_rel1 = new ArrayList<>();
        list2_rel1.add("Italy");
        list2_rel1.add("Usa");
        list2_rel1.add("Spain");
        metadata_rel1.put("rel.date", list1_rel1);
        metadata_rel1.put("rel.place", list2_rel1);

        /* Metadata relationship_2 */
        metadata_rel2 = new HashMap<String, List<String>>();
        List<String> list1_rel2 = new ArrayList<>();
        list1_rel2.add("20/01/2020");
        List<String> list2_rel2 = new ArrayList<>();
        list2_rel2.add("Italy");
        list2_rel2.add("Usa");
        list2_rel2.add("Japan");
        metadata_rel2.put("rel.date", list1_rel2);
        metadata_rel2.put("rel.place", list2_rel2);

        /* Metadata relationship_3 */
        metadata_rel3 = new HashMap<String, List<String>>();
        List<String> list1_rel3 = new ArrayList<>();
        list1_rel3.add("24/07/2020");
        List<String> list2_rel3 = new ArrayList<>();
        list2_rel3.add("Argentina");
        metadata_rel3.put("rel.date", list1_rel3);
        metadata_rel3.put("rel.place", list2_rel3);

    }

    /**
     * Test 1: insert node researcher without relationships (single node)
     */
    @Test
    public void insertSingleNodeTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        neo4jService.createUpdateNode(researcher_1);

        List<Map<String, Object>> result = neo4jService.read_nodes_type(generic_researcher);
        assertEquals(1, result.size());
    }

    /**
     * Test 2: Create a node researcher and a relationship with another
     * (publication) also to be created
     * 
     */
    @Test
    public void createNodeWithOneRelationshipTest() {
        neo4jService.deleteGraph();
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, null);
        DSpaceRelation rel1_res1 = new DSpaceRelation("coauthor", publication_1, metadata_rel1);
        List<DSpaceRelation> list_rel_res1 = new ArrayList<DSpaceRelation>();
        list_rel_res1.add(rel1_res1);
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, list_rel_res1);
        neo4jService.createUpdateNode(researcher_1);

        List<Map<String, Object>> result_type_researcher = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result_type_publication = neo4jService.read_nodes_type(generic_publication);
        assertEquals(1, result_type_researcher.size());
        assertEquals(1, result_type_publication.size());
    }

    /**
     * Test 3: create node publication with two relationships (researcher nodes
     * already exists)
     * 
     */
    @Test
    public void createNodeWithTwoRelationshipsTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        /* Insert the researcher nodes individually (without relationships) */
        neo4jService.createUpdateNode(researcher_1);
        neo4jService.createUpdateNode(researcher_2);

        List<Map<String, Object>> result1_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result1_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertEquals(2, result1_type_res.size());
        assertTrue(result1_type_pub.isEmpty());

        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel2);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(rel1_pub1);
        list_rel_pub1.add(rel2_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        neo4jService.createUpdateNode(publication_1);

        List<Map<String, Object>> result2_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result2_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertEquals(2, result2_type_res.size());
        assertEquals(1, result2_type_pub.size());
    }

    /**
     * Test 4: Create a new publication and researcher node and link the publication
     * to the researcher node created and two other existing
     * 
     */
    @Test
    public void createTwoNodeAndCreateThreeRelationshipsTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        /* Insert the researcher nodes individually (without relationships) */
        neo4jService.createUpdateNode(researcher_1);
        neo4jService.createUpdateNode(researcher_2);

        List<Map<String, Object>> result1_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result1_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertEquals(2, result1_type_res.size());
        assertTrue(result1_type_pub.isEmpty());

        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "3", metadata_res3, null);
        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel2);
        DSpaceRelation rel3_pub1 = new DSpaceRelation("collaboration", researcher_3, metadata_rel3);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(rel1_pub1);
        list_rel_pub1.add(rel2_pub1);
        list_rel_pub1.add(rel3_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        neo4jService.createUpdateNode(publication_1);

        /* Read by type */
        List<Map<String, Object>> result2_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result2_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertEquals(3, result2_type_res.size());
        assertEquals(1, result2_type_pub.size());

        /* Read by depth 1 */
        List<Map<String, Object>> result_depth_res = neo4jService.read_nodes_by_depth(generic_researcher, 1);
        List<Map<String, Object>> result_depth_pub = neo4jService.read_nodes_by_depth(generic_publication, 2);
        assertEquals(1, result_depth_res.size());
        assertEquals(3, result_depth_pub.size());
    }

    /**
     * Test 5: create a graph with three researcher nodes and one publication node
     * and then delete it
     * 
     */
    public void deleteGraphTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "3", metadata_res3, null);
        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel2);
        DSpaceRelation rel3_pub1 = new DSpaceRelation("collaboration", researcher_3, metadata_rel3);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(rel1_pub1);
        list_rel_pub1.add(rel2_pub1);
        list_rel_pub1.add(rel3_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        neo4jService.createUpdateNode(publication_1);

        /* Pre-delete */
        List<Map<String, Object>> result1_type_res1 = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result1_type_pub1 = neo4jService.read_nodes_type(generic_publication);
        assertEquals(3, result1_type_res1.size());
        assertEquals(1, result1_type_pub1.size());

        neo4jService.deleteGraph();

        /* Post-delete */
        List<Map<String, Object>> result2_type_res1 = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result2_type_pub1 = neo4jService.read_nodes_type(generic_publication);
        assertTrue(result2_type_res1.isEmpty());
        assertTrue(result2_type_pub1.isEmpty());

    }

    /**
     * Test 6: Delete one nodes with its relationship
     * 
     */
    @Test
    public void deleteNodeWithOneRelationshipTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "3", metadata_res3, null);
        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel2);
        DSpaceRelation rel3_pub1 = new DSpaceRelation("collaboration", researcher_3, metadata_rel3);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(rel1_pub1);
        list_rel_pub1.add(rel2_pub1);
        list_rel_pub1.add(rel3_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        neo4jService.createUpdateNode(publication_1);

        /* Pre-delete */
        List<Map<String, Object>> result1_type_res1 = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result1_type_pub1 = neo4jService.read_nodes_type(generic_publication);
        assertEquals(3, result1_type_res1.size());
        assertEquals(1, result1_type_pub1.size());
        List<Map<String, Object>> result1_depth_res1 = neo4jService.read_nodes_by_depth(generic_researcher, 1);
        List<Map<String, Object>> result1_depth_pub1 = neo4jService.read_nodes_by_depth(generic_publication, 1);
        assertEquals(1, result1_depth_res1.size());
        assertEquals(3, result1_depth_pub1.size());

        neo4jService.deleteNodeWithRelationships(generic_researcher);

        /* Post-delete */
        List<Map<String, Object>> result2_type_res1 = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result2_type_pub1 = neo4jService.read_nodes_type(generic_publication);
        assertEquals(2, result2_type_res1.size());
        assertEquals(1, result2_type_pub1.size());
        List<Map<String, Object>> result2_depth_res1 = neo4jService.read_nodes_by_depth(generic_researcher, 1);
        List<Map<String, Object>> result2_depth_pub1 = neo4jService.read_nodes_by_depth(generic_publication, 1);
        assertTrue(result2_depth_res1.isEmpty());
        assertEquals(2, result2_depth_pub1.size());
    }

    /**
     * Test 7: Delete one nodes with its relationships
     * 
     */
    @Test
    public void deleteNodeWithThreeRelationshipsTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "3", metadata_res3, null);
        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel2);
        DSpaceRelation rel3_pub1 = new DSpaceRelation("collaboration", researcher_3, metadata_rel3);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(rel1_pub1);
        list_rel_pub1.add(rel2_pub1);
        list_rel_pub1.add(rel3_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        neo4jService.createUpdateNode(publication_1);

        /* Pre-delete */
        List<Map<String, Object>> result1_type_res1 = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result1_type_pub1 = neo4jService.read_nodes_type(generic_publication);
        assertEquals(3, result1_type_res1.size());
        assertEquals(1, result1_type_pub1.size());
        List<Map<String, Object>> result1_depth_res1 = neo4jService.read_nodes_by_depth(generic_researcher, 1);
        List<Map<String, Object>> result1_depth_pub1 = neo4jService.read_nodes_by_depth(generic_publication, 1);
        assertEquals(1, result1_depth_res1.size());
        assertEquals(3, result1_depth_pub1.size());

        neo4jService.deleteNodeWithRelationships(generic_publication);

        /* Post-delete */
        List<Map<String, Object>> result2_type_res1 = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result2_type_pub1 = neo4jService.read_nodes_type(generic_publication);
        assertEquals(3, result2_type_res1.size());
        assertTrue(result2_type_pub1.isEmpty());
        List<Map<String, Object>> result2_depth_res1 = neo4jService.read_nodes_by_depth(generic_researcher, 1);
        List<Map<String, Object>> result2_depth_pub1 = neo4jService.read_nodes_by_depth(generic_publication, 1);
        assertTrue(result2_depth_res1.isEmpty());
        assertTrue(result2_depth_pub1.isEmpty());
    }

    /**
     * Test 8: Change all metadata of the node and update node
     * 
     */
    @Test
    public void UpdateNodeAfterEditAllMetadataTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        neo4jService.createUpdateNode(researcher_1);

        /* Pre-edit metadata */
        Map<String, Object> pre_edit = neo4jService.read_node_by_id(generic_researcher);
        assertEquals(
                "{properties(node)="
                        + "{IDDB=1, dc_department=[Oxford University, Roma Tre], dc_name=[Steve], dc_surname=[Smith]}}",
                pre_edit.toString());
        assertEquals(1, pre_edit.size());

        researcher_1.setMetadata(metadata_res2);
        neo4jService.createUpdateNode(researcher_1);

        /* Post-edit metadata */
        Map<String, Object> post_edit = neo4jService.read_node_by_id(generic_researcher);
        assertEquals(
                "{properties(node)="
                        + "{IDDB=1, dc_department=[Bocconi, Sapienza], dc_name=[Claire], dc_surname=[Williams]}}",
                post_edit.toString());
        assertEquals(1, post_edit.size());
    }

    /**
     * Test 9: Change some metadata (not all) of the node and update node
     * 
     */
    @Test
    public void UpdateNodeAfterEditMetadataTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        neo4jService.createUpdateNode(researcher_1);

        /* Pre-edit metadata */
        Map<String, Object> pre_edit = neo4jService.read_node_by_id(generic_researcher);
        assertEquals(
                "{properties(node)="
                        + "{IDDB=1, dc_department=[Oxford University, Roma Tre], dc_name=[Steve], dc_surname=[Smith]}}",
                pre_edit.toString());
        assertEquals(1, pre_edit.size());

        /* Change researcher_1 surname */
        researcher_1.getMetadata().get("dc.surname").set(0, "Brown");
        neo4jService.createUpdateNode(researcher_1);

        /* Post-edit metadata */
        Map<String, Object> post_edit1 = neo4jService.read_node_by_id(generic_researcher);
        assertEquals(
                "{properties(node)="
                        + "{IDDB=1, dc_department=[Oxford University, Roma Tre], dc_name=[Steve], dc_surname=[Brown]}}",
                post_edit1.toString());
        assertEquals(1, post_edit1.size());

        /* Change another metadata researcher_1 department index 1 */
        researcher_1.getMetadata().get("dc.department").set(1, "Harvard");
        neo4jService.createUpdateNode(researcher_1);
        /* Post-edit metadata */
        Map<String, Object> post_edit2 = neo4jService.read_node_by_id(generic_researcher);
        assertEquals(
                "{properties(node)="
                        + "{IDDB=1, dc_department=[Oxford University, Harvard], dc_name=[Steve], dc_surname=[Brown]}}",
                post_edit2.toString());
        assertEquals(1, post_edit2.size());
    }

    /**
     * Test 10: read relationship properties by two node (generic_researcher and
     * generic_publication)
     * 
     */
    @Test
    public void ReadRelationshipPropertiesBetweenTwoNodesTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "3", metadata_res3, null);
        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel2);
        DSpaceRelation rel3_pub1 = new DSpaceRelation("collaboration", researcher_3, metadata_rel3);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(rel1_pub1);
        list_rel_pub1.add(rel2_pub1);
        list_rel_pub1.add(rel3_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        neo4jService.createUpdateNode(publication_1);

        Map<String, Object> result_properties = neo4jService.read_properties_rel(generic_researcher,
                generic_publication);
        assertEquals("{properties(rel)={rel_date=[13/01/2020], rel_place=[Italy, Usa, Spain]}}",
                result_properties.toString());
        assertFalse(result_properties.isEmpty());
        assertEquals(1, result_properties.size());
    }

    /**
     * Test 11: create two nodes and make a relationship between them and read the
     * nodes by type to see if they have been inserted in the graph
     * 
     */
    @Test
    public void ReadNodesByTypeTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceRelation single_relation = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(single_relation);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);

        List<Map<String, Object>> result1_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result1_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertTrue(result1_type_res.isEmpty());
        assertTrue(result1_type_pub.isEmpty());

        neo4jService.createUpdateNode(publication_1);

        List<Map<String, Object>> result2_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result2_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertEquals(1, result2_type_res.size());
        assertEquals(1, result2_type_pub.size());

        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        DSpaceNode publication_2 = new DSpaceNode("Publication", "102", metadata_pub2, null);
        neo4jService.createUpdateNode(researcher_2);
        neo4jService.createUpdateNode(publication_2);

        List<Map<String, Object>> result3_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result3_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertEquals(2, result3_type_res.size());
        assertEquals(2, result3_type_pub.size());

    }

    /**
     * Test 12: create two nodes and make a relationship between them and read the
     * properties of the start and the end node
     * 
     */
    @Test
    public void ReadPropertiesStartEndNodesTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceRelation single_relation = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(single_relation);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        neo4jService.createUpdateNode(publication_1);

        Map<String, Object> result_id_res1 = neo4jService.read_node_by_id(generic_researcher);
        Map<String, Object> result_id_pub1 = neo4jService.read_node_by_id(generic_publication);
        assertEquals(
                "{properties(node)="
                        + "{IDDB=1, dc_department=[Oxford University, Roma Tre], dc_name=[Steve], dc_surname=[Smith]}}",
                result_id_res1.toString());
        assertEquals("{properties(node)="
                + "{dc_type=[Magazine], IDDB=101, dc_title=[Web Research]}}", result_id_pub1.toString());
    }

}
