package org.splevo.diffing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.gmt.modisco.java.Statement;
import org.eclipse.modisco.java.composition.javaapplication.JavaApplication;
import org.junit.Test;
import org.splevo.diffing.emfcompare.java2kdmdiff.StatementInsert;
import org.splevo.modisco.util.KDMUtil;

/**
 * Unit test to prove the differencing of method declarations.
 */
public class StatementTest extends AbstractDiffingTest {

    /** The logger for this class. */
    private Logger logger = Logger.getLogger(StatementTest.class);

    /** Source path to the original implementation. */
    private static final File TEST_FILE_1 = new File("testmodels/implementation/statements/a/a_java2kdm.xmi");

    /** Source path to the modified implementation. */
    private static final File TEST_FILE_2 = new File("testmodels/implementation/statements/b/b_java2kdm.xmi");

    /**
     * Test method to detect changes in the method declarations.
     * 
     * @throws IOException
     *             Identifies that either the source models could not be loaded or the diff model
     *             could not be serialized.
     * @throws InterruptedException
     *             identifies the diffing has been externally interrupted.
     */
    @Test
    public void testDoDiff() throws IOException, InterruptedException {
        JavaApplication leadingModel = KDMUtil.loadKDMModel(TEST_FILE_1);
        JavaApplication integrationModel = KDMUtil.loadKDMModel(TEST_FILE_2);

        Java2KDMDiffingService diffingService = new Java2KDMDiffingService();
        diffingService.getIgnorePackages().add("java");
        diffingService.getIgnorePackages().add("java.lang");
        diffingService.getIgnorePackages().add("java.io");

        DiffModel diff = diffingService.doDiff(integrationModel, leadingModel);

        EList<DiffElement> differences = diff.getDifferences();
        assertEquals("Wrong number of differences detected", 1, differences.size());

        for (DiffElement diffElement : differences) {
            if (diffElement instanceof StatementInsert) {
                StatementInsert statementInsert = ((StatementInsert) diffElement);
                Statement statement = statementInsert.getStatementLeft();
                assertNotNull("The inserted statement should not be null.", statement);

            } else {
                fail("No other diff elements than StatementInsert should have been detected.");
            }
            logger.debug(diffElement.getKind() + ": " + diffElement.getClass().getName());
        }
        logger.debug("Found Differences: " + differences.size());
    }
}
