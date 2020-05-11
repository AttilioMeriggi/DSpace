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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.model.neo4j.AuthorNGraph;
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
    private Map<String, List<String>> metadata_res4;

    private Map<String, List<String>> metadata_pub1;
    private Map<String, List<String>> metadata_pub2;
    private Map<String, List<String>> metadata_pub3;
    private Map<String, List<String>> metadata_pub4;

    private Map<String, List<String>> metadata_rel1;
    private Map<String, List<String>> metadata_rel2;
    private Map<String, List<String>> metadata_rel3;
    private Map<String, List<String>> metadata_rel4;

    @Override
    @Before
    public void setUp() throws Exception {
        try {
            super.setUp();

            authDriver = new AuthenticationDriver(neo4j.boltURI().toString(), null, null);
            neo4jService.setAuthDriver(authDriver);

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

            /* Metadata researcher_4 */
            metadata_res4 = new HashMap<String, List<String>>();
            List<String> list1_res4 = new ArrayList<>();
            list1_res4.add("Daniel Brown");
            List<String> list2_res4 = new ArrayList<>();
            list2_res4.add("Richard King");
            List<String> list3_res4 = new ArrayList<String>();
            list3_res4.add("Berkeley University");
            metadata_res4.put("dc.contributor.author", list1_res4);
            metadata_res4.put("dc.contributor.editor", list2_res4);
            metadata_res4.put("dc.relation.orgunit", list3_res4);

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

            /* Metadata publication_4 */
            metadata_pub4 = new HashMap<String, List<String>>();
            List<String> list1_pub4 = new ArrayList<>();
            list1_pub4.add("Microeconomics");
            List<String> list2_pub4 = new ArrayList<>();
            list2_pub4.add("Item");
            metadata_pub4.put("dc.title", list1_pub4);
            metadata_pub4.put("dc.type", list2_pub4);

            /* Metadata relationship_1 */
            metadata_rel1 = new HashMap<String, List<String>>();
            metadata_rel1.put("dc.title", list1_pub1);
            metadata_rel1.put("dc.type", list2_pub1);

            /* Metadata relationship_2 */
            metadata_rel2 = new HashMap<String, List<String>>();
            metadata_rel2.put("dc.title", list1_pub2);
            metadata_rel2.put("dc.type", list2_pub2);

            /* Metadata relationship_3 */
            metadata_rel3 = new HashMap<String, List<String>>();
            metadata_rel3.put("dc.title", list1_pub3);
            metadata_rel3.put("dc.type", list2_pub3);

            /* Metadata relationship_4 */
            metadata_rel4 = new HashMap<String, List<String>>();
            metadata_rel4.put("dc.title", list1_pub4);
            metadata_rel4.put("dc.type", list2_pub4);

            /* clean up neo4j */
            neo4jService.deleteGraph(context);
        } catch (Exception ex) {
            log.error("Error during test initialization", ex);
        }
    }

    @Test
    public void createTest() throws Exception {
        DSpaceNode publication_1 = new DSpaceNode("Publication", "4d68f32e-6122-428c-81a9-6bcf03d1abad", metadata_pub1,
                null);
        DSpaceRelation rel1_res1 = new DSpaceRelation("coauthor", publication_1, metadata_rel1);
        List<DSpaceRelation> list_rel_res1 = new ArrayList<DSpaceRelation>();
        list_rel_res1.add(rel1_res1);
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "72ab2970-de17-4797-a307-49c5921ce351", metadata_res1,
                list_rel_res1);

        ObjectMapper mapper = new ObjectMapper();
        String authToken = getAuthToken(admin.getEmail(), password);

        getClient(authToken).perform(put("/api/app/neo4j/dspacenode").content(mapper.writeValueAsBytes(researcher_1))
                .contentType(contentType)).andExpect(status().isOk());

        DSpaceNode expected = neo4jService.readNodeById(context, researcher_1.getIDDB());
        assertEquals(expected, researcher_1);
    }

    @Test
    public void readTest() throws Exception {
        DSpaceNode publication_1 = new DSpaceNode("Publication", "4d68f32e-6122-428c-81a9-6bcf03d1abad", metadata_pub1,
                null);
        DSpaceRelation rel1_res1 = new DSpaceRelation("coauthor", publication_1, metadata_rel1);
        List<DSpaceRelation> list_rel_res1 = new ArrayList<DSpaceRelation>();
        list_rel_res1.add(rel1_res1);
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "72ab2970-de17-4797-a307-49c5921ce351", metadata_res1,
                list_rel_res1);

        neo4jService.createUpdateNode(context, researcher_1);

        String authToken = getAuthToken(admin.getEmail(), password);

        MvcResult result = getClient(authToken).perform(get("/api/app/neo4j/dspacenode/" + researcher_1.getIDDB()))
                .andExpect(status().isOk()).andReturn();

        DSpaceNode expected = DSpaceNode.build(result.getResponse().getContentAsString());

        assertEquals(expected, researcher_1);
    }

    /**
     * Read Author Network Graph
     * 
     * @throws Exception
     * @throws SQLException
     */
    @Test
    public void readAuthorGraph() throws SQLException, Exception {
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "72ab2970-de17-4797-a307-49c5921ce351", metadata_res1,
                null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "853630d2-f04f-439d-b397-afbadf9ce80c", metadata_res2,
                null);
        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "547a412a-9949-4c37-904c-df82b662b718", metadata_res3,
                null);

        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel1);
        DSpaceRelation rel3_pub1 = new DSpaceRelation("coauthor", researcher_3, metadata_rel1);
        List<DSpaceRelation> relations_pub1 = new ArrayList<DSpaceRelation>();
        relations_pub1.add(rel1_pub1);
        relations_pub1.add(rel2_pub1);
        relations_pub1.add(rel3_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "4d68f32e-6122-428c-81a9-6bcf03d1abad", metadata_pub1,
                relations_pub1);
        neo4jService.createUpdateNode(context, publication_1);

        DSpaceRelation rel1_pub2 = new DSpaceRelation("coauthor", researcher_1, metadata_rel2);
        DSpaceRelation rel2_pub2 = new DSpaceRelation("coauthor", researcher_3, metadata_rel2);
        List<DSpaceRelation> relations_pub2 = new ArrayList<DSpaceRelation>();
        relations_pub2.add(rel1_pub2);
        relations_pub2.add(rel2_pub2);
        DSpaceNode publication_2 = new DSpaceNode("Publication", "5f1e17da-8a1b-4640-922c-46ee70aed071", metadata_pub2,
                relations_pub2);
        neo4jService.createUpdateNode(context, publication_2);

        DSpaceRelation rel1_pub3 = new DSpaceRelation("coauthor", researcher_1, metadata_rel3);
        DSpaceRelation rel2_pub3 = new DSpaceRelation("coauthor", researcher_3, metadata_rel3);
        List<DSpaceRelation> relations_pub3 = new ArrayList<DSpaceRelation>();
        relations_pub3.add(rel1_pub3);
        relations_pub3.add(rel2_pub3);
        DSpaceNode publication_3 = new DSpaceNode("Publication", "e3b73bf3-0517-4528-9a63-ae0ff56b54dc", metadata_pub3,
                relations_pub3);
        neo4jService.createUpdateNode(context, publication_3);

        DSpaceRelation rel1_pub4 = new DSpaceRelation("coauthor", researcher_3, metadata_rel4);
        List<DSpaceRelation> relations_pub4 = new ArrayList<DSpaceRelation>();
        relations_pub4.add(rel1_pub4);
        DSpaceNode publication_4 = new DSpaceNode("Publication", "612d472f-a1b9-4a10-a6fe-d26266077ee3", metadata_pub4,
                relations_pub4);
        neo4jService.createUpdateNode(context, publication_4);

        DSpaceRelation rel1_res4 = new DSpaceRelation("coauthor", publication_4, metadata_rel4);
        List<DSpaceRelation> relations_res4 = new ArrayList<DSpaceRelation>();
        relations_res4.add(rel1_res4);
        DSpaceNode researcher_4 = new DSpaceNode("Researcher", "d5ed4470-8969-4a15-a0c3-5536e91edbf6", metadata_res4,
                relations_res4);
        neo4jService.createUpdateNode(context, researcher_4);

        MvcResult result = getClient()
                .perform(get("/api/app/neo4j/authorngraph/" + researcher_1.getIDDB()).param("depth", "5")
                        .param("metadata", "dc_contributor_author").param("relationMetadata", "dc_title,dc_type"))
                .andExpect(status().isOk()).andReturn();

        AuthorNGraph authorNGraph = AuthorNGraph.build(result.getResponse().getContentAsString());

        assertEquals("72ab2970-de17-4797-a307-49c5921ce351", authorNGraph.getId());
        assertEquals("[Steve Smith]", authorNGraph.getName());

        assertEquals(2, authorNGraph.getChildren().size());
        assertEquals("[Tom Taylor]", authorNGraph.getChildren().get(0).getName());
        assertEquals("dc_title:[Web Research], dc_type:[Magazine]",
                authorNGraph.getChildren().get(1).getData().getRelation());
        assertEquals("[Claire Williams]", authorNGraph.getChildren().get(1).getName());
        assertEquals(
                "dc_title:[Cluster Analysis], dc_type:[Item]; dc_title:[Software Research], dc_type:[Item]; dc_title:[Web Research], dc_type:[Magazine]",
                authorNGraph.getChildren().get(0).getData().getRelation());

        assertEquals(2, authorNGraph.getChildren().get(0).getChildren().size());
        assertEquals(0, authorNGraph.getChildren().get(1).getChildren().size());

        assertEquals("[Daniel Brown]", authorNGraph.getChildren().get(0).getChildren().get(0).getName());
        assertEquals("[Claire Williams]", authorNGraph.getChildren().get(0).getChildren().get(1).getName());
    }

    /**
     * Read DSpaceNode Graph
     * 
     * @throws Exception
     * @throws SQLException
     */
    @Test
    public void readDSpaceNodeGraph() throws SQLException, Exception {
        DSpaceNode researcher_1 = new DSpaceNode("Researcher", "72ab2970-de17-4797-a307-49c5921ce351", metadata_res1,
                null);
        DSpaceNode researcher_2 = new DSpaceNode("Researcher", "853630d2-f04f-439d-b397-afbadf9ce80c", metadata_res2,
                null);
        DSpaceNode researcher_3 = new DSpaceNode("Researcher", "547a412a-9949-4c37-904c-df82b662b718", metadata_res3,
                null);

        DSpaceRelation rel1_pub1 = new DSpaceRelation("coauthor", researcher_1, metadata_rel1);
        DSpaceRelation rel2_pub1 = new DSpaceRelation("coauthor", researcher_2, metadata_rel1);
        DSpaceRelation rel3_pub1 = new DSpaceRelation("coauthor", researcher_3, metadata_rel1);
        List<DSpaceRelation> relations_pub1 = new ArrayList<DSpaceRelation>();
        relations_pub1.add(rel1_pub1);
        relations_pub1.add(rel2_pub1);
        relations_pub1.add(rel3_pub1);
        DSpaceNode publication_1 = new DSpaceNode("Publication", "4d68f32e-6122-428c-81a9-6bcf03d1abad", metadata_pub1,
                relations_pub1);
        neo4jService.createUpdateNode(context, publication_1);

        DSpaceRelation rel1_pub2 = new DSpaceRelation("coauthor", researcher_1, metadata_rel2);
        DSpaceRelation rel2_pub2 = new DSpaceRelation("coauthor", researcher_3, metadata_rel2);
        List<DSpaceRelation> relations_pub2 = new ArrayList<DSpaceRelation>();
        relations_pub2.add(rel1_pub2);
        relations_pub2.add(rel2_pub2);
        DSpaceNode publication_2 = new DSpaceNode("Publication", "5f1e17da-8a1b-4640-922c-46ee70aed071", metadata_pub2,
                relations_pub2);
        neo4jService.createUpdateNode(context, publication_2);

        DSpaceRelation rel1_pub3 = new DSpaceRelation("coauthor", researcher_1, metadata_rel3);
        DSpaceRelation rel2_pub3 = new DSpaceRelation("coauthor", researcher_3, metadata_rel3);
        List<DSpaceRelation> relations_pub3 = new ArrayList<DSpaceRelation>();
        relations_pub3.add(rel1_pub3);
        relations_pub3.add(rel2_pub3);
        DSpaceNode publication_3 = new DSpaceNode("Publication", "e3b73bf3-0517-4528-9a63-ae0ff56b54dc", metadata_pub3,
                relations_pub3);
        neo4jService.createUpdateNode(context, publication_3);

        DSpaceRelation rel1_pub4 = new DSpaceRelation("coauthor", researcher_3, metadata_rel4);
        List<DSpaceRelation> relations_pub4 = new ArrayList<DSpaceRelation>();
        relations_pub4.add(rel1_pub4);
        DSpaceNode publication_4 = new DSpaceNode("Publication", "612d472f-a1b9-4a10-a6fe-d26266077ee3", metadata_pub4,
                relations_pub4);
        neo4jService.createUpdateNode(context, publication_4);

        DSpaceRelation rel1_res4 = new DSpaceRelation("coauthor", publication_4, metadata_rel4);
        List<DSpaceRelation> relations_res4 = new ArrayList<DSpaceRelation>();
        relations_res4.add(rel1_res4);
        DSpaceNode researcher_4 = new DSpaceNode("Researcher", "d5ed4470-8969-4a15-a0c3-5536e91edbf6", metadata_res4,
                relations_res4);
        neo4jService.createUpdateNode(context, researcher_4);

        MvcResult result = getClient()
                .perform(get("/api/app/neo4j/ngraph/" + researcher_1.getIDDB()).param("depth", "5"))
                .andExpect(status().isOk()).andReturn();

        DSpaceNode dspaceNodeGraph = DSpaceNode.build(result.getResponse().getContentAsString());

        assertEquals("72ab2970-de17-4797-a307-49c5921ce351", dspaceNodeGraph.getIDDB());
        assertEquals("Researcher", dspaceNodeGraph.getEntityType());
        assertEquals("[Steve Smith]", dspaceNodeGraph.getMetadata().get("dc_contributor_author").toString());
        assertEquals("[Steve Smith]", dspaceNodeGraph.getMetadata().get("dc_contributor_editor").toString());
        assertEquals("[Oxford University, Roma Tre]",
                dspaceNodeGraph.getMetadata().get("dc_relation_orgunit").toString());
        assertEquals(3, dspaceNodeGraph.getRelations().size());
    }

}