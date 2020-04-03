package org.dspace.neo4j;

import org.apache.logging.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Consumer;
import org.dspace.event.Event;

public class IndexEventConsumer implements Consumer {

    /**
    * Log4j Logger
    * 
    */
    private static Logger log = org.apache.logging.log4j.LogManager.getLogger(IndexEventConsumer.class);

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void consume(Context ctx, Event event) throws Exception {
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
                    log.debug("consume() adding event to update in neo4j: " + event.toString());
                }
                break;

            case Event.DELETE:
                if (event.getSubjectType() == -1 || event.getSubjectID() == null) {
                    log.warn("got null subject type and/or ID on DELETE event, skipping it.");
                } else {
                    String detail = event.getSubjectType() + "-" + event.getSubjectID().toString();
                    log.debug("consume() adding event to delete queue: " + event.toString());
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


    @Override
    public void end(Context ctx) throws Exception {

    }

    @Override
    public void finish(Context ctx) throws Exception {

    }
}