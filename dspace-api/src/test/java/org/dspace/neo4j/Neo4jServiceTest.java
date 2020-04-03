/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j;

import static org.junit.Assert.assertEquals;

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
        list3_res2.add("Oxford University");
        list3_res2.add("Johns Hopkins University");
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
    public void insertNodeWithOneRelationshipTest() {
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
        assertEquals(0, result1_type_pub.size());

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
    public void insertTwoNodeAndCreateRelationshipsTest() {
        neo4jService.deleteGraph();
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "2", metadata_res2, null);
        /* Insert the researcher nodes individually (without relationships) */
        neo4jService.createUpdateNode(researcher_1);
        neo4jService.createUpdateNode(researcher_2);
        List<Map<String, Object>> result1_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result1_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertEquals(2, result1_type_res.size());
        assertEquals(0, result1_type_pub.size());

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
        /* Read by depth */
        List<Map<String, Object>> result_depth_one = neo4jService.read_nodes_by_depth(generic_researcher, 1);
        List<Map<String, Object>> result_depth_two = neo4jService.read_nodes_by_depth(generic_researcher, 2);
        assertEquals(2, result_depth_one.size());
        assertEquals(4, result_depth_two.size());

    }

    /**
     * Delete one nodes with its relationships
     * 
     */
    @Test
    public void deleteNodesWithRelationshipsTest() {
        neo4jService.deleteGraph();
        insertTwoNodeAndCreateRelationshipsTest();
        /* Read by type */
        List<Map<String, Object>> result_type_res = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result_type_pub = neo4jService.read_nodes_type(generic_publication);
        assertEquals(3, result_type_res.size());
        assertEquals(1, result_type_pub.size());

        /* Read by depth (node publication) */
        List<Map<String, Object>> result_depth = neo4jService.read_nodes_by_depth(generic_publication, 1);
        assertEquals(4, result_depth.size());

        /* Delete researcher_3 with its relationships (one relationship) */
        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "3"); // Generic node researcher_3
        neo4jService.deleteNodeWithRelationships(researcher_3);
        /* Read by type */
        List<Map<String, Object>> result_type_res_delete = neo4jService.read_nodes_type(generic_researcher);
        List<Map<String, Object>> result_type_pub_delete = neo4jService.read_nodes_type(generic_publication);
        //TODO: failed, to be verified
        assertEquals(2, result_type_res_delete.size());
        assertEquals(1, result_type_pub_delete.size());

        /* Read by depth (node publication) */
        List<Map<String, Object>> result_depth_delete = neo4jService.read_nodes_by_depth(generic_publication, 1);
        assertEquals(3, result_depth_delete.size());

    }

}
