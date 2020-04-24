/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.neo4j;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.test.AbstractNeo4jIntegrationTest;
import org.dspace.neo4j.AuthenticationDriver;
import org.dspace.neo4j.DSpaceNode;
import org.dspace.neo4j.DSpaceRelation;
import org.dspace.neo4j.factory.Neo4jFactory;
import org.dspace.neo4j.service.Neo4jService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Integration Tests against the /api/core/communities endpoint (including any
 * subpaths)
 */
public class Neo4jRestIT extends AbstractNeo4jIntegrationTest {
    private static final Logger log = LogManager.getLogger(Neo4jRestIT.class);
    private Neo4jService neo4jService = Neo4jFactory.getInstance().getNeo4jService();
    private AuthenticationDriver authDriver = null;

    private Map<String, List<String>> metadata_res1;
    private Map<String, List<String>> metadata_res2;
    private Map<String, List<String>> metadata_res3;

    private Map<String, List<String>> metadata_pub1;
    private Map<String, List<String>> metadata_pub2;
    private Map<String, List<String>> metadata_pub3;

    private Map<String, List<String>> metadata_rel1;
    private Map<String, List<String>> metadata_rel2;
    private Map<String, List<String>> metadata_rel3;

    @Override
    @Before
    public void setUp() throws Exception {
        try {
            super.setUp();

            authDriver = new AuthenticationDriver(neo4j.boltURI().toString(), null, null);
            neo4jService.setAuthDriver(authDriver);

            /* Metadata researcher_1 IDDB = 1 */
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

            /* Metadata researcher_2 IDDB = 2 */
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

            /* Metadata researcher_3 IDDB = 3 */
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

            /* clean up neo4j */
            neo4jService.deleteGraph(context);
        } catch (Exception ex) {
            log.error("Error during test initialization", ex);
        }
    }

    @Test
    public void createTest() throws Exception {
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, null);
        DSpaceRelation rel1_res1 = new DSpaceRelation("coauthor", publication_1, metadata_rel1);
        List<DSpaceRelation> list_rel_res1 = new ArrayList<DSpaceRelation>();
        list_rel_res1.add(rel1_res1);
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, list_rel_res1);

        ObjectMapper mapper = new ObjectMapper();
        String authToken = getAuthToken(admin.getEmail(), password);

        getClient(authToken).perform(put("/api/app/neo4j/dspacenode").content(mapper.writeValueAsBytes(researcher_1))
                .contentType(contentType)).andExpect(status().isOk());

        DSpaceNode expected = neo4jService.readNodeById(context, researcher_1.getIDDB());
        assertEquals(expected, researcher_1);
    }
    
    @Test
    public void readTest() throws Exception {
        DSpaceNode publication_1 = new DSpaceNode("Publication", "101", metadata_pub1, null);
        DSpaceRelation rel1_res1 = new DSpaceRelation("coauthor", publication_1, metadata_rel1);
        List<DSpaceRelation> list_rel_res1 = new ArrayList<DSpaceRelation>();
        list_rel_res1.add(rel1_res1);
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "1", metadata_res1, list_rel_res1);
        
        neo4jService.createUpdateNode(context, researcher_1);
        
        String authToken = getAuthToken(admin.getEmail(), password);

        MvcResult result = getClient(authToken).perform(get("/api/app/neo4j/dspacenode/" + researcher_1.getIDDB()))
                .andExpect(status().isOk()).andReturn();
        
        DSpaceNode expected = DSpaceNode.build(result.getResponse().getContentAsString());
        
        assertEquals(expected, researcher_1);
    }
}
