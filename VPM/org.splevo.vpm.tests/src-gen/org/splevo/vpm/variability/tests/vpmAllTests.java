/**
 */
package org.splevo.vpm.variability.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;
import org.splevo.vpm.software.tests.SoftwareTests;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>vpm</b></em>' model.
 * <!-- end-user-doc -->
 * @generated
 */
public class vpmAllTests extends TestSuite {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Test suite() {
        TestSuite suite = new vpmAllTests("vpm Tests");
        suite.addTest(SoftwareTests.suite());
        return suite;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public vpmAllTests(String name) {
        super(name);
    }

} //vpmAllTests
