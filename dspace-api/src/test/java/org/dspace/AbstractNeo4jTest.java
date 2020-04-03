package org.dspace;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

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

//    /**
//     * This method will be run before every test as per @Before. It will initialize
//     * resources required for each individual unit test.
//     *
//     * Other methods can be annotated with @Before here or in subclasses but no
//     * execution order is guaranteed
//     */
//    @Before
//    @Override
//    public void init() {
//        super.init();
//    }
}
