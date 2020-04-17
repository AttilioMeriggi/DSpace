/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace;

import org.junit.BeforeClass;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
public class AbstractNeo4jTest extends AbstractUnitTest {

    protected static Neo4j neo4j;

    /**
     * This method will be run before the first test as per @BeforeClass. It will
     * initialize shared resources required for all tests of this class.
     * <p>
     * NOTE: Per JUnit, "The @BeforeClass methods of superclasses will be run before
     * those the current class." http://junit.org/apidocs/org/junit/BeforeClass.html
     * <p>
     * This method initialize the in-memory neo4j-harness database.
     */
    @BeforeClass
    public static void initNeo4j() {
        neo4j = Neo4jBuilders.newInProcessBuilder().build();
    }
}
