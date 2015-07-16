package edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.transformations.jamopp2pcm;

import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.CompositeComponent;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.system.System;
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.transformations.java2pcm.ClassMappingTransformation;
import edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.transformations.utils.PCM2JaMoPPTestUtils;

public class ClassMappingTransformationTest extends JaMoPP2PCMTransformationTest {

    /**
     * Class that in mapped package and same name as component + impl--> should be the new
     * implementing class for the component
     *
     * @throws Exception
     */
    @Test
    public void testAddComponentClassInPackageWithCorrespondingComponent() throws Throwable {
        final Repository repo = super.addFirstPackage();
        final BasicComponent bc = super.addSecondPackageCorrespondsToBasicComponent();

        this.testUserInteractor.addNextSelections(0);
        final BasicComponent bcForClass = super.addClassInSecondPackage(BasicComponent.class);

        super.assertRepositoryAndPCMName(repo, bcForClass, bc.getEntityName());
    }

    @Test
    public void testAddCompositeComponentClassInPackageWithCorrespondingCompositeComponent() throws Throwable {
        final Repository repo = super.addFirstPackage();
        final CompositeComponent cc = super.addSecondPackageCorrespondsToCompositeComponent();

        this.testUserInteractor.addNextSelections(0);
        final CompositeComponent ccForClass = this.addClassInSecondPackage(CompositeComponent.class);

        super.assertRepositoryAndPCMName(repo, ccForClass, cc.getEntityName());
    }

    @Test
    public void testAddSystemClassInPackageWithCorrespondingSystem() throws Throwable {
        super.addFirstPackage();
        final System pcmSystem = super.addSecondPackageCorrespondsToSystem();

        this.testUserInteractor.addNextSelections(0);
        final System systemForClass = super.addClassInSecondPackage(System.class);

        super.assertPCMNamedElement(systemForClass, pcmSystem.getEntityName());
    }

    /**
     * Test ii) class in non corresponding package --> should not be mapped to a Basic Component
     *
     * @throws Throwable
     */
    @Test
    public void testAddClassInPackageWithoutCorrespondingComponent() throws Throwable {
        super.addFirstPackage();
        super.addSecondPackageCorrespondsWithoutCorrespondences();

        this.testUserInteractor.addNextSelections(ClassMappingTransformation.SELECT_NO_CORRESPONDENCE);
        try {
            final EObject eObject = super.addClassInPackage(this.secondPackage, EObject.class);
            fail("The class should not have any correspondences, but it has a correspondence to eObject: " + eObject);
        } catch (final RuntimeException re) {
            // ecpected exception
        }
    }

    @Test
    public void testAddBasicComponentClassInPackageWithoutCorrespondence() throws Throwable {
        final Repository repo = this.addFirstPackage();
        super.addSecondPackageCorrespondsWithoutCorrespondences();

        this.testUserInteractor.addNextSelections(ClassMappingTransformation.SELECT_CREATE_BASIC_COMPONENT);
        final BasicComponent newBc = super.addClassInSecondPackage(BasicComponent.class);

        super.assertRepositoryAndPCMName(repo, newBc, PCM2JaMoPPTestUtils.IMPLEMENTING_CLASS_NAME);
    }

    @Test
    public void testAddCompositeComponentClassInPackageWithoutCorrespondence() throws Throwable {
        final Repository repo = this.addFirstPackage();
        super.addSecondPackageCorrespondsWithoutCorrespondences();

        this.testUserInteractor.addNextSelections(ClassMappingTransformation.SELECT_CREATE_COMPOSITE_COMPONENT);
        final CompositeComponent cc = super.addClassInSecondPackage(CompositeComponent.class);

        super.assertRepositoryAndPCMName(repo, cc, PCM2JaMoPPTestUtils.IMPLEMENTING_CLASS_NAME);
    }

    @Test
    public void testAddSystemClassInPackageWithoutCorrespondence() throws Throwable {
        this.addFirstPackage();
        super.addSecondPackageCorrespondsWithoutCorrespondences();

        this.testUserInteractor.addNextSelections(ClassMappingTransformation.SELECT_CREATE_SYSTEM);
        final System pcmSystem = super.addClassInSecondPackage(System.class);

        this.assertPCMNamedElement(pcmSystem, PCM2JaMoPPTestUtils.IMPLEMENTING_CLASS_NAME);
    }

    @Test
    public void testAddCompsiteDatatypeClassInDatatypePackage() throws Throwable {
        final Repository repo = this.addFirstPackage();

        final CompositeDataType cdt = this.addClassThatCorrespondsToCompositeDatatype();

        this.assertRepositoryAndPCMNameForDatatype(repo, cdt, PCM2JaMoPPTestUtils.IMPLEMENTING_CLASS_NAME);
    }

    @Test
    public void testAddCollectionDatatypeClassInDatatypePackage() throws Throwable {
        final Repository repo = this.addFirstPackage();

        this.testUserInteractor.addNextSelections(ClassMappingTransformation.SELECT_CREATE_COLLECTION_DATA_TYPE);
        final CollectionDataType collection = super.addClassInPackage(this.getDatatypesPackage(),
                CollectionDataType.class);

        this.assertRepositoryAndPCMNameForDatatype(repo, collection, PCM2JaMoPPTestUtils.IMPLEMENTING_CLASS_NAME);
    }

    @Test
    public void testAddClassInDatatypePackage() throws Throwable {
        this.addFirstPackage();
        try {
            this.testUserInteractor.addNextSelections(ClassMappingTransformation.SELECT_DO_NOT_CREATE_DATA_TYPE);
            final EObject eObject = super.addClassInPackage(this.getDatatypesPackage(), EObject.class);
            fail("The class should not have any datatype correspondences, but it has a correspondence to eObject: "
                    + eObject);
        } catch (final RuntimeException re) {
            // expected Exception
        }
    }

    @Test
    public void testRenameBasicComponentClass() throws Throwable {
        final Repository repo = this.addFirstPackage();
        this.addSecondPackageCorrespondsWithoutCorrespondences();
        this.testUserInteractor.addNextSelections(ClassMappingTransformation.SELECT_CREATE_BASIC_COMPONENT);
        final BasicComponent basicComponent = this.addClassInSecondPackage(BasicComponent.class);

        final BasicComponent newBasicComponent = super.renameClassifierWithName(basicComponent.getEntityName(),
                PCM2JaMoPPTestUtils.BASIC_COMPONENT_NAME + PCM2JaMoPPTestUtils.RENAME, BasicComponent.class);

        this.assertRepositoryAndPCMName(repo, newBasicComponent, PCM2JaMoPPTestUtils.BASIC_COMPONENT_NAME
                + PCM2JaMoPPTestUtils.RENAME);
    }

}
