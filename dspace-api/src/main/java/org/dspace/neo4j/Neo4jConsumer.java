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
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.neo4j.factory.Neo4jFactory;
import org.dspace.neo4j.service.Neo4jService;

public class Neo4jConsumer implements Consumer {

    /**
     * Log4j Logger
     * 
     */
    private static Logger log = org.apache.logging.log4j.LogManager.getLogger(Neo4jConsumer.class);
    private Neo4jService neo4jService = Neo4jFactory.getInstance().getNeo4jService();

    // collect Items, Collections, Communities that need indexing
    private Set<UUID> objectsToUpdate = new HashSet<>();

    // unique search IDs to delete
    private Set<UUID> uniqueIdsToDelete = new HashSet<>();

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void consume(Context ctx, Event event) throws Exception {

        if (objectsToUpdate == null) {
            objectsToUpdate = new HashSet<>();
            uniqueIdsToDelete = new HashSet<>();
        }

        int st = event.getSubjectType();
        if (!(st == Constants.ITEM)) {
            log.warn("IndexConsumer should not have been given this kind of Subject in an event, skipping: "
                    + event.toString());
            return;
        }

        DSpaceObject subject = event.getSubject(ctx);

        DSpaceObject object = event.getObject(ctx);

        // If event subject is a Bundle and event was Add or Remove,
        // transform the event to be a Modify on the owning Item.
        // It could be a new bitstream in the TEXT bundle which
        // would change the index.
        int et = event.getEventType();

        switch (et) {
            case Event.CREATE:
            case Event.MODIFY:
            case Event.MODIFY_METADATA:
                if (subject == null) {
                    log.warn(event.getEventTypeAsString() + " event, could not get object for "
                            + event.getSubjectTypeAsString() + " id=" + event.getSubjectID()
                            + ", perhaps it has been deleted.");
                } else {
                    log.debug("consume() adding event to update queue: " + event.toString());
                    objectsToUpdate.add(subject.getID());
                }
                break;

            case Event.DELETE:
                if (event.getSubjectType() == -1 || event.getSubjectID() == null) {
                    log.warn("got null subject type and/or ID on DELETE event, skipping it.");
                } else {
                    // TODO: ?????
                    // String detail = event.getSubjectType() + "-" +
                    // event.getSubjectID().toString();
                    log.debug("consume() adding event to delete queue: " + event.toString());
                    uniqueIdsToDelete.add(event.getSubjectID());
                }
                break;
            default:
                log.warn("IndexConsumer should not have been given a event of type=" + event.getEventTypeAsString()
                        + " on subject=" + event.getSubjectTypeAsString());
                break;
        }
    }

    @Override
    public void end(Context ctx) throws Exception {

        try {
            for (UUID iu : objectsToUpdate) {
                neo4jService.insertUpdateItem(ctx, iu);
                if (log.isDebugEnabled()) {
                    log.debug("Inserted " + ", id=" + iu);
                }
            }

            for (UUID uid : uniqueIdsToDelete) {
                neo4jService.deleteItem(ctx, uid);
                if (log.isDebugEnabled()) {
                    log.debug("Removed Item, handle=" + uid);
                }
            }
        } finally {
            if (!objectsToUpdate.isEmpty() || !uniqueIdsToDelete.isEmpty()) {

                objectsToUpdate.clear();
                uniqueIdsToDelete.clear();
            }
        }
    }

    @Override
    public void finish(Context ctx) throws Exception {

    }
}