package org.dspace.neo4j;

import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.AbstractNeo4jTest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataSchemaEnum;
import org.dspace.content.MetadataValue;
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

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void convertItem() throws IOException {
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

            // create an Item
            WorkspaceItem wi = workspaceItemService.create(context, collection, false);
            Item item = wi.getItem();
            itemService.setMetadataSingleValue(context, item, MetadataSchemaEnum.DC.getName(), "title", null, null,
                    "sample item");
            itemService.update(context, item);

            // Perform test convertItem
            UUID id = item.getID();
            DSpaceNode converted_item = neo4jService.convertItem(context, id);
            List<MetadataValue> metadata = item.getMetadata();
            // assertEquals(converted_item.getEntityType(), item.getType().toString());
            assertEquals(converted_item.getIDDB(), id.toString());
            assertEquals(converted_item.getMetadata().get("dc.name").get(0), metadata.get(0).getValue());

            context.restoreAuthSystemState();

        } catch (SQLException | AuthorizeException ex) {
            throw new RuntimeException(ex);
        } finally {
            context.restoreAuthSystemState();
        }
    }
}
