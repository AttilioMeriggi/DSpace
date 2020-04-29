/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.ctask.neo4j;

import java.io.IOException;
import java.text.MessageFormat;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.curate.Suspendable;
import org.dspace.neo4j.factory.Neo4jFactory;
import org.dspace.neo4j.service.Neo4jService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Suspendable(invoked = Curator.Invoked.INTERACTIVE)
public class Neo4jCurationTask extends AbstractCurationTask {

    private static final Logger log = LoggerFactory.getLogger(Neo4jCurationTask.class);
    private Neo4jService neo4jService = Neo4jFactory.getInstance().getNeo4jService();

    protected int status = Curator.CURATE_UNSET;
    protected final String UPDATE_FAILED_MESSAGE = "Updated failed";
    protected final String UPDATE_SUCCESS_MESSAGE = "Updated successfully in neo4j";
    protected final String ERROR_MESSAGE = "Error in updating {}, with Handle {} in neo4j";
    protected final String NEW_ITEM_HANDLE = "in workflow";

    @Override
    public int perform(DSpaceObject dso) throws IOException {
        status = Curator.CURATE_SKIP;
        if (log.isDebugEnabled())
            log.debug("The target dso is " + dso.getName());

        if (dso instanceof Item) {
            status = Curator.CURATE_SUCCESS;
            Item item = (Item) dso;
            try {
                Context context = Curator.curationContext();

                neo4jService.insertUpdateItem(context, item.getID());
            } catch (Exception e) {
                String message = MessageFormat.format(ERROR_MESSAGE, dso.getID(), getItemHandle(item));
                setResult(message);

                log.error(message, e);
                return Curator.CURATE_ERROR;
            }

            formatResults(item);
        }
        return status;
    }

    protected void formatResults(Item item) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Item: ").append(getItemHandle(item)).append(" ");
        if (status == Curator.CURATE_FAIL) {
            sb.append(UPDATE_FAILED_MESSAGE);
        } else {
            sb.append(UPDATE_SUCCESS_MESSAGE);
        }
        setResult(sb.toString());
    }

    protected String getItemHandle(Item item) {
        String handle = item.getHandle();
        return (handle != null) ? handle : NEW_ITEM_HANDLE;
    }
}
