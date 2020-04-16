/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    private Map<String, List<String>> metadata_pub3;

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

        /* Metadata publication_3 IDDB = 103 */
        metadata_pub3 = new HashMap<String, List<String>>();
        List<String> list1_pub3 = new ArrayList<>();
        list1_pub3.add("Cluster Analysis");
        List<String> list2_pub3 = new ArrayList<>();
        list2_pub3.add("Item");
        metadata_pub3.put("dc.title", list1_pub3);
        metadata_pub3.put("dc.type", list2_pub3);

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

        Map<String, DSpaceNode> result = neo4jService.readNodesByType(generic_researcher.getEntityType());
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

        Map<String, DSpaceNode> result_type_researcher = neo4jService
                .readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result_type_publication = neo4jService
                .readNodesByType(generic_publication.getEntityType());
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

        Map<String, DSpaceNode> result1_type_res = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result1_type_pub = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(2, result1_type_res.size());
        assertTrue(result1_type_pub.isEmpty());

        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel2);
        List<DSpaceRelation> list_rel_pub1 = new ArrayList<DSpaceRelation>();
        list_rel_pub1.add(rel1_pub1);
        list_rel_pub1.add(rel2_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        neo4jService.createUpdateNode(publication_1);

        Map<String, DSpaceNode> result2_type_res = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result2_type_pub = neo4jService.readNodesByType(generic_publication.getEntityType());
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

        Map<String, DSpaceNode> result1_type_res = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result1_type_pub = neo4jService.readNodesByType(generic_publication.getEntityType());
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
        Map<String, DSpaceNode> result2_type_res = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result2_type_pub = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(3, result2_type_res.size());
        assertEquals(1, result2_type_pub.size());

        /* Read by depth 1 */
        Map<String, DSpaceNode> result_depth_res = neo4jService.readNodesByDepth(generic_researcher.getIDDB(), 1);
        Map<String, DSpaceNode> result_depth_pub = neo4jService.readNodesByDepth(generic_publication.getIDDB(), 2);
        assertEquals(1, result_depth_res.size());
        assertEquals(3, result_depth_pub.size());
    }

    /**
     * Test 5: create a graph with three researcher nodes and one publication node
     * and then delete it
     * 
     */
    @Test
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
        Map<String, DSpaceNode> result1_type_res1 = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result1_type_pub1 = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(3, result1_type_res1.size());
        assertEquals(1, result1_type_pub1.size());

        neo4jService.deleteGraph();

        /* Post-delete */
        Map<String, DSpaceNode> result2_type_res1 = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result2_type_pub1 = neo4jService.readNodesByType(generic_publication.getEntityType());
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
        Map<String, DSpaceNode> result1_type_res1 = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result1_type_pub1 = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(3, result1_type_res1.size());
        assertEquals(1, result1_type_pub1.size());
        Map<String, DSpaceNode> result1_depth_res1 = neo4jService.readNodesByDepth(generic_researcher.getIDDB(), 1);
        Map<String, DSpaceNode> result1_depth_pub1 = neo4jService.readNodesByDepth(generic_publication.getIDDB(), 1);
        assertEquals(1, result1_depth_res1.size());
        assertEquals(3, result1_depth_pub1.size());

        neo4jService.deleteNodeWithRelationships(generic_researcher.getIDDB());

        /* Post-delete */
        Map<String, DSpaceNode> result2_type_res1 = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result2_type_pub1 = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(2, result2_type_res1.size());
        assertEquals(1, result2_type_pub1.size());
        Map<String, DSpaceNode> result2_depth_res1 = neo4jService.readNodesByDepth(generic_researcher.getIDDB(), 1);
        Map<String, DSpaceNode> result2_depth_pub1 = neo4jService.readNodesByDepth(generic_publication.getIDDB(), 1);
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
        Map<String, DSpaceNode> result1_type_res1 = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result1_type_pub1 = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(3, result1_type_res1.size());
        assertEquals(1, result1_type_pub1.size());
        Map<String, DSpaceNode> result1_depth_res1 = neo4jService.readNodesByDepth(generic_researcher.getIDDB(), 1);
        Map<String, DSpaceNode> result1_depth_pub1 = neo4jService.readNodesByDepth(generic_publication.getIDDB(), 1);
        assertEquals(1, result1_depth_res1.size());
        assertEquals(3, result1_depth_pub1.size());

        neo4jService.deleteNodeWithRelationships(generic_publication.getIDDB());

        /* Post-delete */
        Map<String, DSpaceNode> result2_type_res1 = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result2_type_pub1 = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(3, result2_type_res1.size());
        assertTrue(result2_type_pub1.isEmpty());
        Map<String, DSpaceNode> result2_depth_res1 = neo4jService.readNodesByDepth(generic_researcher.getIDDB(), 1);
        Map<String, DSpaceNode> result2_depth_pub1 = neo4jService.readNodesByDepth(generic_publication.getIDDB(), 1);
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
        DSpaceNode pre_edit = neo4jService.readNodeById(generic_researcher.getIDDB());
        assertNotNull(pre_edit);
        assertEquals("1", pre_edit.getIDDB());
        assertEquals("{dc_name=[Steve], dc_department=[Oxford University, Roma Tre], dc_surname=[Smith]}",
                pre_edit.getMetadata().toString());

        DSpaceNode not_exist = neo4jService.readNodeById(generic_publication.getIDDB());
        assertNull(not_exist);

        researcher_1.setMetadata(metadata_res2);
        neo4jService.createUpdateNode(researcher_1);

        /* Post-edit metadata */
        DSpaceNode post_edit = neo4jService.readNodeById(generic_researcher.getIDDB());
        assertNotNull(post_edit);
        assertEquals("1", post_edit.getIDDB());
        assertEquals("{dc_name=[Claire], dc_department=[Bocconi, Sapienza], dc_surname=[Williams]}",
                post_edit.getMetadata().toString());
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
        DSpaceNode pre_edit = neo4jService.readNodeById(generic_researcher.getIDDB());
        assertNotNull(pre_edit);
        assertEquals("1", pre_edit.getIDDB());
        assertEquals("{dc_name=[Steve], dc_department=[Oxford University, Roma Tre], dc_surname=[Smith]}",
                pre_edit.getMetadata().toString());

        /* Change researcher_1 surname */
        researcher_1.getMetadata().get("dc.surname").set(0, "Brown");
        neo4jService.createUpdateNode(researcher_1);

        /* Post-edit metadata */
        DSpaceNode post_edit1 = neo4jService.readNodeById(generic_researcher.getIDDB());
        assertNotNull(post_edit1);
        assertEquals("1", post_edit1.getIDDB());
        assertEquals("{dc_name=[Steve], dc_department=[Oxford University, Roma Tre], dc_surname=[Brown]}",
                post_edit1.getMetadata().toString());

        /* Change another metadata researcher_1 department index 1 */
        researcher_1.getMetadata().get("dc.department").set(1, "Harvard");
        neo4jService.createUpdateNode(researcher_1);
        /* Post-edit metadata */
        DSpaceNode post_edit2 = neo4jService.readNodeById(generic_researcher.getIDDB());
        assertNotNull(post_edit2);
        assertEquals("1", post_edit2.getIDDB());
        assertEquals("{dc_name=[Steve], dc_department=[Oxford University, Harvard], dc_surname=[Brown]}",
                post_edit2.getMetadata().toString());
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

        DSpaceRelation result_properties = neo4jService.readPropertiesRel(generic_researcher.getIDDB(),
                generic_publication.getIDDB());
        assertEquals("{rel_date=[13/01/2020], rel_place=[Italy, Usa, Spain]}",
                result_properties.getMetadata().toString());
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

        Map<String, DSpaceNode> result1_type_res = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result1_type_pub = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertTrue(result1_type_res.isEmpty());
        assertTrue(result1_type_pub.isEmpty());

        neo4jService.createUpdateNode(publication_1);

        Map<String, DSpaceNode> result2_type_res = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result2_type_pub = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(1, result2_type_res.size());
        assertEquals(1, result2_type_pub.size());

        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        DSpaceNode publication_2 = new DSpaceNode("Publication", "102", metadata_pub2, null);
        neo4jService.createUpdateNode(researcher_2);
        neo4jService.createUpdateNode(publication_2);

        Map<String, DSpaceNode> result3_type_res = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result3_type_pub = neo4jService.readNodesByType(generic_publication.getEntityType());
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

        DSpaceNode result_id_res1 = neo4jService.readNodeById(generic_researcher.getIDDB());
        DSpaceNode result_id_pub1 = neo4jService.readNodeById(generic_publication.getIDDB());
        assertNotNull(result_id_res1);
        assertNotNull(result_id_pub1);
        assertEquals("1", result_id_res1.getIDDB());
        assertEquals("{dc_name=[Steve], dc_department=[Oxford University, Roma Tre], dc_surname=[Smith]}",
                result_id_res1.getMetadata().toString());
        assertEquals("101", result_id_pub1.getIDDB());
        assertEquals("{dc_title=[Web Research], dc_type=[Magazine]}", result_id_pub1.getMetadata().toString());

    }

    /**
     * Test 13: read nodes by different depth the graph is composed of three
     * researcher nodes and three publication nodes
     * 
     */
    @Test
    public void readNodeByDepthTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "3", metadata_res3, null);

        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel2);
        DSpaceRelation rel3_pub1 = new DSpaceRelation("coauthor", researcher_3, metadata_rel3);
        List<DSpaceRelation> relations_pub1 = new ArrayList<DSpaceRelation>();
        relations_pub1.add(rel1_pub1);
        relations_pub1.add(rel2_pub1);
        relations_pub1.add(rel3_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, relations_pub1);
        neo4jService.createUpdateNode(publication_1);

        DSpaceRelation rel1_pub2 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub2 = new DSpaceRelation("coauthor", researcher_3, metadata_rel3);
        List<DSpaceRelation> relations_pub2 = new ArrayList<DSpaceRelation>();
        relations_pub2.add(rel1_pub2);
        relations_pub2.add(rel2_pub2);
        DSpaceNode publication_2 = new DSpaceNode("Publication", "102", metadata_pub2, relations_pub2);
        neo4jService.createUpdateNode(publication_2);

        DSpaceRelation rel1_pub3 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub3 = new DSpaceRelation("coauthor", researcher_3, metadata_rel3);
        List<DSpaceRelation> relations_pub3 = new ArrayList<DSpaceRelation>();
        relations_pub3.add(rel1_pub3);
        relations_pub3.add(rel2_pub3);
        DSpaceNode publication_3 = new DSpaceNode("Publication", "103", metadata_pub3, relations_pub3);
        neo4jService.createUpdateNode(publication_3);

        Map<String, DSpaceNode> result1_type_res = neo4jService.readNodesByType(generic_researcher.getEntityType());
        Map<String, DSpaceNode> result1_type_pub = neo4jService.readNodesByType(generic_publication.getEntityType());
        assertEquals(3, result1_type_res.size());
        assertEquals(3, result1_type_pub.size());

        /* Read depth = 1 (Start node = researcher_1) */
        Map<String, DSpaceNode> result1_depth1_res1 = neo4jService.readNodesByDepth(researcher_1.getIDDB(), 1);
        assertEquals(3, result1_depth1_res1.size());

        /* Read depth = 2 (Start node = researcher_1) */
        Map<String, DSpaceNode> result1_depth2_res1 = neo4jService.readNodesByDepth(researcher_1.getIDDB(), 2);
        assertEquals(5, result1_depth2_res1.size());

        /* Read depth = 1 (Start node = researcher_2) */
        Map<String, DSpaceNode> result1_depth1_res2 = neo4jService.readNodesByDepth(researcher_2.getIDDB(), 1);
        assertEquals(1, result1_depth1_res2.size());

        /* Read depth = 2 (Start node = researcher_2) */
        Map<String, DSpaceNode> result1_depth2_res2 = neo4jService.readNodesByDepth(researcher_2.getIDDB(), 2);
        assertEquals(3, result1_depth2_res2.size());

        /* Read depth = 3 (Start node = researcher_2) */
        Map<String, DSpaceNode> result1_depth3_res2 = neo4jService.readNodesByDepth(researcher_2.getIDDB(), 3);
        assertEquals(5, result1_depth3_res2.size());

        /* Read depth = 1 (Start node = researcher_3) */
        Map<String, DSpaceNode> result1_depth1_res3 = neo4jService.readNodesByDepth(researcher_3.getIDDB(), 1);
        assertEquals(3, result1_depth1_res3.size());

        /* Read depth = 2 (Start node = researcher_3) */
        Map<String, DSpaceNode> result1_depth2_res3 = neo4jService.readNodesByDepth(researcher_3.getIDDB(), 2);
        assertEquals(5, result1_depth2_res3.size());

        /* Read depth = 1 (Start node = publication_1) */
        Map<String, DSpaceNode> result1_depth1_pub1 = neo4jService.readNodesByDepth(publication_1.getIDDB(), 1);
        assertEquals(3, result1_depth1_pub1.size());

        /* Read depth = 2 (Start node = publication_1) */
        Map<String, DSpaceNode> result1_depth2_pub1 = neo4jService.readNodesByDepth(publication_1.getIDDB(), 2);
        assertEquals(5, result1_depth2_pub1.size());

        /* Read depth = 1 (Start node = publication_2) */
        Map<String, DSpaceNode> result1_depth1_pub2 = neo4jService.readNodesByDepth(publication_2.getIDDB(), 1);
        assertEquals(2, result1_depth1_pub2.size());

        /* Read depth = 2 (Start node = publication_2) */
        Map<String, DSpaceNode> result1_depth2_pub2 = neo4jService.readNodesByDepth(publication_2.getIDDB(), 2);
        assertEquals(4, result1_depth2_pub2.size());

        /* Read depth = 3 (Start node = publication_2) */
        Map<String, DSpaceNode> result1_depth3_pub2 = neo4jService.readNodesByDepth(publication_2.getIDDB(), 3);
        assertEquals(5, result1_depth3_pub2.size());

        /* Read depth = 0 (Start node = publication_3) */
        Map<String, DSpaceNode> result1_depth0_pub3 = neo4jService.readNodesByDepth(publication_3.getIDDB(), 0);
        assertTrue(result1_depth0_pub3.isEmpty());

        /* Read depth = 1 (Start node = publication_3) */
        Map<String, DSpaceNode> result1_depth1_pub3 = neo4jService.readNodesByDepth(publication_3.getIDDB(), 1);
        assertEquals(2, result1_depth1_pub3.size());

        /* Read depth = 2 (Start node = publication_3) */
        Map<String, DSpaceNode> result1_depth2_pub3 = neo4jService.readNodesByDepth(publication_3.getIDDB(), 2);
        assertEquals(4, result1_depth2_pub3.size());

        /* Read depth = 3 (Start node = publication_3) */
        Map<String, DSpaceNode> result1_depth3_pub3 = neo4jService.readNodesByDepth(publication_3.getIDDB(), 3);
        assertEquals(5, result1_depth3_pub3.size());

        /* Read depth = 4 (Start node = publication_3) */
        Map<String, DSpaceNode> result1_depth4_pub3 = neo4jService.readNodesByDepth(publication_3.getIDDB(), 4);
        assertEquals(5, result1_depth4_pub3.size());

        /* Delete a node publication_1 and read again nodes by depth */
        neo4jService.deleteNodeWithRelationships(publication_1.getIDDB());

        /* Read depth = 1 (Start node researcher_1) */
        Map<String, DSpaceNode> result2_depth1_res1 = neo4jService.readNodesByDepth(researcher_1.getIDDB(), 1);
        assertEquals(2, result2_depth1_res1.size());

        /* Read depth = 2 (Start node researcher_1 */
        Map<String, DSpaceNode> result2_depth2_res1 = neo4jService.readNodesByDepth(researcher_1.getIDDB(), 2);
        assertEquals(3, result2_depth2_res1.size());

        /* Read depth = 3 (Start node researcher_1 */
        Map<String, DSpaceNode> result2_depth3_res1 = neo4jService.readNodesByDepth(researcher_1.getIDDB(), 3);
        assertEquals(3, result2_depth3_res1.size());

        /* Read depth = 4 (Start node researcher_1 */
        Map<String, DSpaceNode> result2_depth4_res1 = neo4jService.readNodesByDepth(researcher_1.getIDDB(), 4);
        assertEquals(3, result2_depth4_res1.size());

        /* Read depth = 1 (Start node researcher_2) */
        Map<String, DSpaceNode> result2_depth1_res2 = neo4jService.readNodesByDepth(researcher_2.getIDDB(), 1);
        assertTrue(result2_depth1_res2.isEmpty());

        /* Read depth = 1 (Start node researcher_3) */
        Map<String, DSpaceNode> result2_depth1_res3 = neo4jService.readNodesByDepth(researcher_3.getIDDB(), 1);
        assertEquals(2, result2_depth1_res3.size());

        /* Read depth = 2 (Start node researcher_3) */
        Map<String, DSpaceNode> result2_depth2_res3 = neo4jService.readNodesByDepth(researcher_3.getIDDB(), 2);
        assertEquals(3, result2_depth2_res3.size());

        /* Read depth = 1 (Start node publication_2) */
        Map<String, DSpaceNode> result2_depth1_pub2 = neo4jService.readNodesByDepth(publication_2.getIDDB(), 1);
        assertEquals(2, result2_depth1_pub2.size());

        /* Read depth = 2 (Start node publication_2) */
        Map<String, DSpaceNode> result2_depth2_pub2 = neo4jService.readNodesByDepth(publication_2.getIDDB(), 2);
        assertEquals(3, result2_depth2_pub2.size());

        /* Read depth = 1 (Start node publication_3) */
        Map<String, DSpaceNode> result2_depth1_pub3 = neo4jService.readNodesByDepth(publication_3.getIDDB(), 1);
        assertEquals(2, result2_depth1_pub3.size());

        /* Read depth = 2 (Start node publication_3) */
        Map<String, DSpaceNode> result2_depth2_pub3 = neo4jService.readNodesByDepth(publication_3.getIDDB(), 2);
        assertEquals(3, result2_depth2_pub3.size());

        DSpaceRelation rel_pub1_pub2 = neo4jService.readPropertiesRel(publication_1.getIDDB(), publication_2.getIDDB());
        assertNull(rel_pub1_pub2);

        DSpaceRelation rel_res3_pub3 = neo4jService.readPropertiesRel(researcher_3.getIDDB(), publication_3.getIDDB());
        assertNotNull(rel_res3_pub3);
    }

    /**
     * Test 14: set all Metadata to null
     * 
     */
    @Test
    public void setNullMetadataTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", null, null);
        neo4jService.createUpdateNode(researcher_1);
        DSpaceNode result1_id_res1 = neo4jService.readNodeById(generic_researcher.getIDDB());
        assertEquals("1", result1_id_res1.getIDDB());
        assertEquals("{}", result1_id_res1.getMetadata().toString());

        researcher_1.setMetadata(metadata_res1);
        neo4jService.createUpdateNode(researcher_1);
        DSpaceNode result2_id_res1 = neo4jService.readNodeById(generic_researcher.getIDDB());
        assertEquals("1", result2_id_res1.getIDDB());
        assertEquals("{dc_name=[Steve], dc_department=[Oxford University, Roma Tre], dc_surname=[Smith]}",
                result2_id_res1.getMetadata().toString());

        researcher_1.setMetadata(null);
        neo4jService.createUpdateNode(researcher_1);
        DSpaceNode result3_id_res1 = neo4jService.readNodeById(generic_researcher.getIDDB());
        assertEquals("{}", result3_id_res1.getMetadata().toString());

        Map<String, DSpaceNode> result_type_res = neo4jService.readNodesByType(researcher_1.getEntityType());
        assertEquals(1, result_type_res.size());
    }

    /**
     * Test 15: create node with IDDB that starts with 0
     * 
     */
    @Test
    public void createNodeIDDBStartZero() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", null, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", null, null);
        neo4jService.createUpdateNode(researcher_1);
        neo4jService.createUpdateNode(researcher_2);
        DSpaceNode result_id_res1 = neo4jService.readNodeById(researcher_1.getIDDB());
        assertEquals("1", result_id_res1.getIDDB());
        DSpaceNode result_id_res2 = neo4jService.readNodeById(researcher_2.getIDDB());
        assertEquals("2", result_id_res2.getIDDB());
    }

}
