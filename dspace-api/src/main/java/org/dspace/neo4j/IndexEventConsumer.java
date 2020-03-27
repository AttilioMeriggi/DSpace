/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.discovery.IndexableObject;
import org.dspace.discovery.IndexingService;
import org.dspace.discovery.indexobject.factory.IndexObjectFactoryFactory;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.services.factory.DSpaceServicesFactory;

public class IndexEventConsumer implements Consumer {
    /**
     * log4j logger
     */
    private static Logger log = org.apache.logging.log4j.LogManager.getLogger(IndexEventConsumer.class);

    // collect Items, Collections, Communities that need indexing
    private Set<IndexableObject> objectsToUpdate = new HashSet<>();

    // unique search IDs to delete
    private Set<String> uniqueIdsToDelete = new HashSet<>();

    IndexingService indexer = DSpaceServicesFactory.getInstance().getServiceManager()
                                                   .getServiceByName(IndexingService.class.getName(),
                                                                     IndexingService.class);

    IndexObjectFactoryFactory indexObjectServiceFactory = IndexObjectFactoryFactory.getInstance();

    @Override
    public void initialize() throws Exception {

    }

    /**
     * Consume a content event -- just build the sets of objects to add (new) to
     * the index, update, and delete.
     *
     * @param ctx   DSpace context
     * @param event Content event
     */
    @Override
    public void consume(Context ctx, Event event) throws Exception {

        if (objectsToUpdate == null) {
            objectsToUpdate = new HashSet<>();
            uniqueIdsToDelete = new HashSet<>();
        }

        int st = event.getSubjectType();
        if (!(st == Constants.ITEM)) {
            log
                .warn("IndexConsumer should not have been given this kind of Subject in an event, skipping: "
                          + event.toString());
            return;
        }

        DSpaceObject subject = event.getSubject(ctx);

        DSpaceObject object = event.getObject(ctx);

        int et = event.getEventType();

        switch (et) {
            case Event.CREATE:
            case Event.MODIFY:
            case Event.MODIFY_METADATA:
                if (subject == null) {
                    log.warn(event.getEventTypeAsString() + " event, could not get object for "
                                 + event.getSubjectTypeAsString() + " id="
                                 + event.getSubjectID()
                                 + ", perhaps it has been deleted.");
                } else {
                    log.debug("consume() adding event to update queue: " + event.toString());
                    objectsToUpdate.addAll(indexObjectServiceFactory.getIndexableObjects(ctx, subject));
                }
                break;

            case Event.REMOVE:
            case Event.ADD:
                if (object == null) {
                    log.warn(event.getEventTypeAsString() + " event, could not get object for "
                                 + event.getObjectTypeAsString() + " id="
                                 + event.getObjectID()
                                 + ", perhaps it has been deleted.");
                } else {
                    log.debug("consume() adding event to update queue: " + event.toString());
                    objectsToUpdate.addAll(indexObjectServiceFactory.getIndexableObjects(ctx, subject));
                }
                break;

            case Event.DELETE:
                if (event.getSubjectType() == -1 || event.getSubjectID() == null) {
                    log.warn("got null subject type and/or ID on DELETE event, skipping it.");
                } else {
                    String detail = event.getSubjectType() + "-" + event.getSubjectID().toString();
                    log.debug("consume() adding event to delete queue: " + event.toString());
                    uniqueIdsToDelete.add(detail);
                }
                break;
            default:
                log
                    .warn("IndexConsumer should not have been given a event of type="
                              + event.getEventTypeAsString()
                              + " on subject="
                              + event.getSubjectTypeAsString());
                break;
        }
    }

    /**
     * Process sets of objects to add, update, and delete in index. Correct for
     * interactions between the sets -- e.g. objects which were deleted do not
     * need to be added or updated, new objects don't also need an update, etc.
     */
    @Override
    public void end(Context ctx) throws Exception {

        try {
            // update the changed Items not deleted because they were on create list
            for (IndexableObject iu : objectsToUpdate) {
                /* we let all types through here and
                 * allow the search indexer to make
                 * decisions on indexing and/or removal
                 */
                iu.setIndexedObject(ctx.reloadEntity(iu.getIndexedObject()));
                String uniqueIndexID = iu.getUniqueIndexID();
                if (uniqueIndexID != null && !uniqueIdsToDelete.contains(uniqueIndexID)) {
                    try {
                        indexer.indexContent(ctx, iu, true, false);
                        log.debug("Indexed "
                                + iu.getTypeText()
                                + ", id=" + iu.getID()
                                + ", unique_id=" + uniqueIndexID);
                    } catch (Exception e) {
                        log.error("Failed while indexing object: ", e);
                    }
                }
            }

            for (String uid : uniqueIdsToDelete) {
                try {
                    indexer.unIndexContent(ctx, uid, false);
                    if (log.isDebugEnabled()) {
                        log.debug("UN-Indexed Item, handle=" + uid);
                    }
                } catch (Exception e) {
                    log.error("Failed while UN-indexing object: " + uid, e);
                }
            }
        } finally {
            if (!objectsToUpdate.isEmpty() || !uniqueIdsToDelete.isEmpty()) {

                indexer.commit();

                // "free" the resources
                objectsToUpdate.clear();
                uniqueIdsToDelete.clear();
            }
        }
    }

    @Override
    public void finish(Context ctx) throws Exception {
        // No-op

    }

}

