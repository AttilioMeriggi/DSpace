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
import org.dspace.AbstractUnitTest;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Neo4jSerializeTest extends AbstractUnitTest {
    private static final Logger log = LogManager.getLogger(Neo4jSerializeTest.class);

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
    public void setUp() throws Exception {

        /* Metadata researcher_1 */
        metadata_res1 = new HashMap<String, List<String>>();
        List<String> list1_res1 = new ArrayList<>();
        list1_res1.add("Steve Smith");
        List<String> list2_res1 = new ArrayList<>();
        list2_res1.add("Steve Smith");
        List<String> list3_res1 = new ArrayList<String>();
        list3_res1.add("Oxford University");
        list3_res1.add("Roma Tre");
        metadata_res1.put("dc.contributor.author", list1_res1);
        metadata_res1.put("dc.contributor.editor", list2_res1);
        metadata_res1.put("dc.relation.orgunit", list3_res1);

        /* Metadata researcher_2 */
        metadata_res2 = new HashMap<String, List<String>>();
        List<String> list1_res2 = new ArrayList<>();
        list1_res2.add("Claire Williams");
        List<String> list2_res2 = new ArrayList<>();
        list2_res2.add("Claire Williams");
        List<String> list3_res2 = new ArrayList<String>();
        list3_res2.add("Bocconi");
        list3_res2.add("Sapienza");
        metadata_res2.put("dc.contributor.author", list1_res2);
        metadata_res2.put("dc.contributor.editor", list2_res2);
        metadata_res2.put("dc.relation.orgunit", list3_res2);

        /* Metadata researcher_3 */
        metadata_res3 = new HashMap<String, List<String>>();
        List<String> list1_res3 = new ArrayList<>();
        list1_res3.add("Tom Taylor");
        List<String> list2_res3 = new ArrayList<>();
        list2_res3.add("Tom Taylor");
        List<String> list3_res3 = new ArrayList<String>();
        list3_res3.add("Oxford University");
        metadata_res3.put("dc.contributor.author", list1_res3);
        metadata_res3.put("dc.contributor.editor", list2_res3);
        metadata_res3.put("dc.relation.orgunit", list3_res3);

        /* Metadata publication_1 */
        metadata_pub1 = new HashMap<String, List<String>>();
        List<String> list1_pub1 = new ArrayList<>();
        list1_pub1.add("Web Research");
        List<String> list2_pub1 = new ArrayList<>();
        list2_pub1.add("Magazine");
        metadata_pub1.put("dc.title", list1_pub1);
        metadata_pub1.put("dc.type", list2_pub1);

        /* Metadata publication_2 */
        metadata_pub2 = new HashMap<String, List<String>>();
        List<String> list1_pub2 = new ArrayList<>();
        list1_pub2.add("Software Research");
        List<String> list2_pub2 = new ArrayList<>();
        list2_pub2.add("Item");
        metadata_pub2.put("dc.title", list1_pub2);
        metadata_pub2.put("dc.type", list2_pub2);

        /* Metadata publication_3 */
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
        metadata_rel1.put("dc.date.issued", list1_rel1);
        metadata_rel1.put("dc.coverage.spatial", list2_rel1);

        /* Metadata relationship_2 */
        metadata_rel2 = new HashMap<String, List<String>>();
        List<String> list1_rel2 = new ArrayList<>();
        list1_rel2.add("20/01/2020");
        List<String> list2_rel2 = new ArrayList<>();
        list2_rel2.add("Italy");
        list2_rel2.add("Usa");
        list2_rel2.add("Japan");
        metadata_rel2.put("dc.date.issued", list1_rel2);
        metadata_rel2.put("dc.coverage.spatial", list2_rel2);

        /* Metadata relationship_3 */
        metadata_rel3 = new HashMap<String, List<String>>();
        List<String> list1_rel3 = new ArrayList<>();
        list1_rel3.add("24/07/2020");
        List<String> list2_rel3 = new ArrayList<>();
        list2_rel3.add("Argentina");
        metadata_rel3.put("dc.date.issued", list1_rel3);
        metadata_rel3.put("dc.coverage.spatial", list2_rel3);
    }

    /**
     * Test 1: serialize a graph with three researcher nodes and one publication
     * node and then delete it
     * 
     * @throws JsonProcessingException
     */
    @Test
    public void serializeTest() throws JsonProcessingException {
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

        String jsonString = publication_1.toJson();

        assertEquals(
                "{\"entityType\":\"Publication\",\"metadata\":{\"dc.type\":[\"Magazine\"],\"dc.title\":[\"Web Research\"]}"
                        + ",\"relations\":[{\"type\":\"coauthor\",\"metadata\":{\"dc.coverage.spatial\":[\"Italy\",\"Usa\",\"Spain\"],"
                        + "\"dc.date.issued\":[\"13/01/2020\"]},\"target\":{\"entityType\":\"Researcher\","
                        + "\"metadata\":{\"dc.contributor.author\":[\"Steve Smith\"],\"dc.contributor.editor\":[\"Steve Smith\"],"
                        + "\"dc.relation.orgunit\":[\"Oxford University\",\"Roma Tre\"]},\"relations\":null,\"iddb\":\"1\"}},"
                        + "{\"type\":\"coauthor\",\"metadata\":{\"dc.coverage.spatial\":[\"Italy\",\"Usa\",\"Japan\"],"
                        + "\"dc.date.issued\":[\"20/01/2020\"]},\"target\":{\"entityType\":\"Researcher\",\"metadata\":"
                        + "{\"dc.contributor.author\":[\"Claire Williams\"],\"dc.contributor.editor\":[\"Claire Williams\"],"
                        + "\"dc.relation.orgunit\":[\"Bocconi\",\"Sapienza\"]},\"relations\":null,\"iddb\":\"2\"}},"
                        + "{\"type\":\"collaboration\",\"metadata\":{\"dc.coverage.spatial\":[\"Argentina\"],\"dc.date.issued\""
                        + ":[\"24/07/2020\"]},\"target\":{\"entityType\":\"Researcher\",\"metadata\":{\"dc.contributor.author\":[\"Tom Taylor\"],"
                        + "\"dc.contributor.editor\":[\"Tom Taylor\"],\"dc.relation.orgunit\":[\"Oxford University\"]},"
                        + "\"relations\":null,\"iddb\":\"3\"}}],\"iddb\":\"101\"}",
                jsonString);
    }

    /**
     * Test 2: deserialize a graph with three researcher nodes and one publication
     * node and then delete it
     * 
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    public void deserializeTest() throws JsonMappingException, JsonProcessingException {
        String jsonNodeString = "{\"entityType\":\"Publication\",\"metadata\":"
                + "{\"dc.type\":[\"Magazine\"],\"dc.title\":[\"Web Research\"]},"
                + "\"relations\":[{\"type\":\"coauthor\",\"metadata\":{\"dc.date.issued\":[\"13/01/2020\"],"
                + "\"dc.coverage.spatial\":[\"Italy\",\"Usa\",\"Spain\"]},"
                + "\"target\":{\"entityType\":\"Researcher\",\"metadata\":"
                + "{\"dc.contributor.author\":[\"Steve Smith\"],\"dc.contributor.editor\":[\"Steve Smith\"],"
                + "\"dc.relation.orgunit\":[\"Oxford University\",\"Roma Tre\"]},"
                + "\"relations\":null,\"iddb\":\"1\"}},{\"type\":\"coauthor\",\"metadata\":"
                + "{\"dc.date.issued\":[\"20/01/2020\"],\"dc.coverage.spatial\":[\"Italy\",\"Usa\",\"Japan\"]},"
                + "\"target\":{\"entityType\":\"Researcher\",\"metadata\":{\"dc.contributor.author\":[\"Claire Williams\"],"
                + "\"dc.contributor.editor\":[\"Claire Williams\"],\"dc.relation.orgunit\":[\"Bocconi\",\"Sapienza\"]},"
                + "\"relations\":null,\"iddb\":\"2\"}},{\"type\":\"collaboration\",\"metadata\":"
                + "{\"dc.date.issued\":[\"24/07/2020\"],\"dc.coverage.spatial\":[\"Argentina\"]},"
                + "\"target\":{\"entityType\":\"Researcher\",\"metadata\":{\"dc.contributor.author\":[\"Tom Taylor\"],"
                + "\"dc.contributor.editor\":[\"Tom Taylor\"],\"dc.relation.orgunit\":[\"Oxford University\"]},"
                + "\"relations\":null,\"iddb\":\"3\"}}],\"iddb\":\"101\"}";

        DSpaceNode publication_1 = DSpaceNode.build(jsonNodeString);

        /* expected */
        DSpaceNode expected = null;
        {
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
            expected = new DSpaceNode("Publication", "101", metadata_pub1, list_rel_pub1);
        }

        assertEquals(expected, publication_1);
    }
}
