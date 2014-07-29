package edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.transformations;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.JaMoPPTUIDCalculatorAndResolver;
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.PCMJavaNamespace;
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.transformations.ChangeSynchronizer;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.CorrespondenceInstance;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.CorrespondenceInstanceImpl;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.Mapping;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.Metamodel;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.VURI;
import edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.jamoppuidcalculatorandresolver.TestUtils;

public class JaMoPPPCMTransformationTest {

    private static int nrOfCorrespondenceInstances;
    private static Mapping mapping;

    protected static ChangeSynchronizer changeSynchronizer;
    protected static ResourceSet resourceSet;

    protected CorrespondenceInstance correspondenceInstance;

    protected static void setUp() {
        // init synchronizer
        TestUtils.registerMetamodels();
        changeSynchronizer = new ChangeSynchronizer();
        final Repository repFac = RepositoryFactory.eINSTANCE.createRepository();
        final Metamodel pcm = new Metamodel(repFac.eClass().getEPackage().getNsURI(), VURI.getInstance(repFac.eClass()
                .getEPackage().getNsURI()), "repository");

        final VURI jaMoPPURI = VURI.getInstance(PCMJavaNamespace.JAMOPP_METAMODEL_NAMESPACE);
        final Set<String> jamoppNSURIs = new HashSet<String>();
        jamoppNSURIs.add(PCMJavaNamespace.JAMOPP_METAMODEL_NAMESPACE_URI);
        jamoppNSURIs.add(PCMJavaNamespace.JAMPPP_METAMODEL_NAMESPACE_URI_CONTAINER);
        final Metamodel jamopp = new Metamodel(jamoppNSURIs, jaMoPPURI, new JaMoPPTUIDCalculatorAndResolver(),
                PCMJavaNamespace.JAVA_FILE_EXTENSION);

        mapping = new Mapping(pcm, jamopp);
        resourceSet = new ResourceSetImpl();
    }

    protected void createNewCorrespondenceInstance() {
        final VURI correspondenceInstanceURI = VURI.getInstance("/tmp/correspondenceInstance" + "_"
                + nrOfCorrespondenceInstances++ + ".xmi");
        final Resource resource = resourceSet.createResource(correspondenceInstanceURI.getEMFUri());
        this.correspondenceInstance = new CorrespondenceInstanceImpl(mapping, correspondenceInstanceURI, resource);
        changeSynchronizer.setCorrespondenceInstance(this.correspondenceInstance);

    }
}
