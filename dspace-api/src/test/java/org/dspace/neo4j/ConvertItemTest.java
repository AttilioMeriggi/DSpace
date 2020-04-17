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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.AbstractNeo4jTest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataSchemaEnum;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.dspace.neo4j.factory.Neo4jFactory;
import org.dspace.neo4j.service.Neo4jService;
import org.junit.Before;
import org.junit.Test;

public class ConvertItemTest extends AbstractNeo4jTest {
    private static final Logger log = LogManager.getLogger(ConvertItemTest.class);

    private EPersonService ePersonService = EPersonServiceFactory.getInstance().getEPersonService();
    private CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();
    private CollectionService collectionService = ContentServiceFactory.getInstance().getCollectionService();
    private WorkspaceItemService workspaceItemService = ContentServiceFactory.getInstance().getWorkspaceItemService();
    private ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    private Neo4jService neo4jService = Neo4jFactory.getInstance().getNeo4jService();
    private AuthenticationDriver authDriver = null;

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

    @Test
    public void convertItem() throws IOException {
        try {
            context.turnOffAuthorisationSystem();

            neo4jService.deleteGraph(context);

            // use ePerson as submitter
            EPerson eperson = ePersonService.create(context);
            eperson.setEmail("attilio@sample.ue");
            eperson.setFirstName(context, "Attilio");
            eperson.setLastName(context, "Meriggi");
            ePersonService.setPassword(eperson, "test");
            ePersonService.update(context, eperson);
            context.setCurrentUser(eperson);

            // create the community and a collection
            Community owningCommunity = communityService.create(null, context);
            communityService.setMetadataSingleValue(context, owningCommunity, MetadataSchemaEnum.DC.getName(), "title",
                    null, null, "Main Community");
            communityService.update(context, owningCommunity);

            Collection collection = collectionService.create(context, owningCommunity);
            collectionService.setMetadataSingleValue(context, collection, MetadataSchemaEnum.DC.getName(), "title",
                    null, null, "My Collection");
            collectionService.update(context, collection);

            // create an ItemPerson
            WorkspaceItem wiPerson = workspaceItemService.create(context, collection, false);
            Item itemPerson = wiPerson.getItem();
            itemService.setMetadataSingleValue(context, itemPerson, MetadataSchemaEnum.DC.getName(), "title", null,
                    null, "Attilio Meriggi");
            // itemService.setMetadataSingleValue(context, itemPerson, "crisrp", "email",
            // null, null, "attilio@sample.ue");
            itemService.setMetadataSingleValue(context, itemPerson, "relationship", "type", null, null, "person");
            itemService.update(context, itemPerson);

            // create an ItemPublication
            WorkspaceItem wiPublication = workspaceItemService.create(context, collection, false);
            Item itemPublication = wiPublication.getItem();
            itemService.setMetadataSingleValue(context, itemPublication, MetadataSchemaEnum.DC.getName(), "title", null,
                    null, "Sample article");
            itemService.addMetadata(context, itemPublication, MetadataSchemaEnum.DC.getName(), "contributor", "author",
                    null, "Attilio Meriggi", itemPerson.getID().toString(), 0);
            itemService.setMetadataSingleValue(context, itemPublication, "relationship", "type", null, null,
                    "publication");
            itemService.update(context, itemPublication);

            // Perform test
            neo4jService.insertUpdateItem(context, itemPublication.getID());
            neo4jService.insertUpdateItem(context, itemPerson.getID());

            DSpaceNode sampleNodePerson = new DSpaceNode("person");
            DSpaceNode sampleNodePublication = new DSpaceNode("publication");
            Map<String, DSpaceNode> numbPerson = neo4jService.readNodesByType(context,
                    sampleNodePerson.getEntityType());
            Map<String, DSpaceNode> numbPublication = neo4jService.readNodesByType(context,
                    sampleNodePublication.getEntityType());
            assertEquals(1, numbPerson.size());
            assertEquals(1, numbPublication.size());

            // TODO: error: relationship not created
            DSpaceRelation resultRelation = neo4jService.readPropertiesRel(context, itemPerson.getID().toString(),
                    itemPublication.getID().toString());
            assertNotNull(resultRelation);
            Map<String, DSpaceNode> result_by_depth = neo4jService.readNodesByDepth(context,
                    itemPerson.getID().toString(), 1);
            assertEquals(1, result_by_depth.size());

            DSpaceNode itemPersonNode = neo4jService.readNodeById(context, itemPerson.getID().toString());
            assertEquals(itemPerson.getID().toString(), itemPersonNode.getIDDB());
            assertEquals("[Attilio Meriggi]", itemPersonNode.getMetadata().get("dc_title").toString());
            assertEquals("[person]", itemPersonNode.getMetadata().get("relationship_type").toString());

            DSpaceNode itemPublicationNode = neo4jService.readNodeById(context, itemPublication.getID().toString());
            assertEquals(itemPublication.getID().toString(), itemPublicationNode.getIDDB());
            assertEquals("[Sample article]", itemPublicationNode.getMetadata().get("dc_title").toString());
            assertEquals("[Attilio Meriggi]",
                    itemPublicationNode.getMetadata().get("dc_contributor_author").toString());
            assertEquals("[publication]", itemPublicationNode.getMetadata().get("relationship_type").toString());

            neo4jService.deleteItem(context, itemPublication.getID());

            DSpaceNode itemPublicationNodeAfterDeletePublication = neo4jService.readNodeById(context,
                    itemPublication.getID().toString());
            assertNull(itemPublicationNodeAfterDeletePublication);

            DSpaceNode itemPersonNodeAfterDeletePublication = neo4jService.readNodeById(context,
                    itemPerson.getID().toString());
            assertNotNull(itemPersonNodeAfterDeletePublication);

            neo4jService.deleteItem(context, itemPerson.getID());
            DSpaceNode itemPersonNodeAfterDeletePerson = neo4jService.readNodeById(context,
                    itemPerson.getID().toString());
            assertNull(itemPersonNodeAfterDeletePerson);

            Map<String, DSpaceNode> numbPersonAfterDelete = neo4jService.readNodesByType(context,
                    sampleNodePerson.getEntityType());
            assertTrue(numbPersonAfterDelete.isEmpty());

            Map<String, DSpaceNode> numbPublicationAfterDelete = neo4jService.readNodesByType(context,
                    sampleNodePublication.getEntityType());
            assertTrue(numbPublicationAfterDelete.isEmpty());

            context.restoreAuthSystemState();

        } catch (SQLException | AuthorizeException ex) {
            throw new RuntimeException(ex);
        } finally {
            context.restoreAuthSystemState();
        }
    }

    /**
     * Assign the default type to an item
     * 
     * @throws IOException
     */
    @Test
    public void createItemWithoutMetadataType() throws IOException {
        try {
            context.turnOffAuthorisationSystem();

            neo4jService.deleteGraph(context);

            // use ePerson as submitter
            EPerson eperson = ePersonService.create(context);
            eperson.setEmail("attilio@sample.ue");
            eperson.setFirstName(context, "Attilio");
            eperson.setLastName(context, "Meriggi");
            ePersonService.setPassword(eperson, "test");
            ePersonService.update(context, eperson);
            context.setCurrentUser(eperson);

            // create the community and a collection
            Community owningCommunity = communityService.create(null, context);
            communityService.setMetadataSingleValue(context, owningCommunity, MetadataSchemaEnum.DC.getName(), "title",
                    null, null, "Main Community");
            communityService.update(context, owningCommunity);

            Collection collection = collectionService.create(context, owningCommunity);
            collectionService.setMetadataSingleValue(context, collection, MetadataSchemaEnum.DC.getName(), "title",
                    null, null, "My Collection");
            collectionService.update(context, collection);

            // create an ItemPerson
            WorkspaceItem wi = workspaceItemService.create(context, collection, false);
            Item itemWithoutType = wi.getItem();
            itemService.setMetadataSingleValue(context, itemWithoutType, MetadataSchemaEnum.DC.getName(), "title", null,
                    null, "Attilio Meriggi");
            itemService.update(context, itemWithoutType);

            /* "person" is default type */
            DSpaceNode sampleNodePerson = new DSpaceNode("person");
            Map<String, DSpaceNode> personTypeBeforeInsert = neo4jService.readNodesByType(context,
                    sampleNodePerson.getEntityType());
            assertTrue(personTypeBeforeInsert.isEmpty());

            // Perform test
            neo4jService.insertUpdateItem(context, itemWithoutType.getID());

            Map<String, DSpaceNode> personTypeAfterInsert = neo4jService.readNodesByType(context,
                    sampleNodePerson.getEntityType());
            assertEquals(1, personTypeAfterInsert.size());

            context.restoreAuthSystemState();

        } catch (SQLException | AuthorizeException ex) {
            throw new RuntimeException(ex);
        } finally {
            context.restoreAuthSystemState();
        }
    }

    /**
     * 
     * Create Items without relationship between them
     * 
     * @throws IOException
     */
    @Test
    public void createItemsWithoutRelationship() throws IOException {
        try {
            context.turnOffAuthorisationSystem();
            // use ePerson as submitter
            EPerson eperson = ePersonService.create(context);
            eperson.setEmail("attilio@sample.ue");
            eperson.setFirstName(context, "Attilio");
            eperson.setLastName(context, "Meriggi");
            ePersonService.setPassword(eperson, "test");
            ePersonService.update(context, eperson);
            context.setCurrentUser(eperson);

            // create the community and a collection
            Community owningCommunity = communityService.create(null, context);
            communityService.setMetadataSingleValue(context, owningCommunity, MetadataSchemaEnum.DC.getName(), "title",
                    null, null, "Main Community");
            communityService.update(context, owningCommunity);

            Collection collection = collectionService.create(context, owningCommunity);
            collectionService.setMetadataSingleValue(context, collection, MetadataSchemaEnum.DC.getName(), "title",
                    null, null, "My Collection");
            collectionService.update(context, collection);

            // TODO: perform test

            context.restoreAuthSystemState();

        } catch (SQLException | AuthorizeException ex) {
            throw new RuntimeException(ex);
        } finally {
            context.restoreAuthSystemState();
        }
    }

}
