package edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.transformations.pcm2java.repository

import de.uka.ipd.sdq.pcm.repository.Repository
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.PCMJaMoPPNamespace
import edu.kit.ipd.sdq.vitruvius.framework.run.transformationexecuter.EmptyEObjectMappingTransformation
import edu.kit.ipd.sdq.vitruvius.framework.run.transformationexecuter.TransformationUtils
import org.apache.log4j.Logger
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.util.EcoreUtil
import org.emftext.language.java.containers.ContainersFactory
import org.emftext.language.java.containers.Package
import edu.kit.ipd.sdq.vitruvius.framework.meta.correspondence.Correspondence
import edu.kit.ipd.sdq.vitruvius.framework.meta.correspondence.EObjectCorrespondence
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent
import de.uka.ipd.sdq.pcm.repository.Interface
import de.uka.ipd.sdq.pcm.repository.DataType
import java.util.Set
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.TransformationChangeResult
import edu.kit.ipd.sdq.vitruvius.framework.meta.correspondence.datatypes.TUID
import org.emftext.language.java.containers.JavaRoot

class RepositoryMappingTransformation extends EmptyEObjectMappingTransformation {

	val private static final Logger logger = Logger.getLogger(RepositoryMappingTransformation.name)

	override getClassOfMappedEObject() {
		return typeof(Repository)
	}

	override void setCorrespondenceForFeatures() {
		var repositoryNameAttribute = RepositoryFactory.eINSTANCE.createRepository.eClass.getEAllAttributes.filter[attribute|
			attribute.name.equalsIgnoreCase(PCMJaMoPPNamespace.PCM::PCM_ATTRIBUTE_ENTITY_NAME)].iterator.next
		var packageNameAttribute = ContainersFactory.eINSTANCE.createPackage.eClass.getEAllAttributes.filter[attribute|
			attribute.name.equalsIgnoreCase(PCMJaMoPPNamespace.JaMoPP::JAMOPP_ATTRIBUTE_NAME)].iterator.next
		featureCorrespondenceMap.put(repositoryNameAttribute, packageNameAttribute)
	}

	/**
	 * called when a repository is created
	 * creates following corresponding objects
	 * 1) main-package for repository
	 * 2) contracts package for interfaces in main-package
	 * 3) datatypes package for datatypes in main-package
	 */
	override createEObject(EObject eObject) {
		val Repository repository = eObject as Repository
		val Package jaMoPPPackage = ContainersFactory.eINSTANCE.createPackage
		jaMoPPPackage.name = repository.entityName
		val Package contractsPackage = ContainersFactory.eINSTANCE.createPackage
		contractsPackage.namespaces.add(jaMoPPPackage.name)
		contractsPackage.name = "contracts"
		val Package datatypesPackage = ContainersFactory.eINSTANCE.createPackage
		datatypesPackage.namespaces.add(jaMoPPPackage.name)
		datatypesPackage.name = "datatypes"
		return #[jaMoPPPackage, contractsPackage, datatypesPackage]
	}

	override createRootEObject(EObject newRootEObject, EObject[] newCorrespondingEObjects) {
		if (newCorrespondingEObjects.nullOrEmpty) {
			return TransformationUtils.createEmptyTransformationChangeResult
		}
		val transResult = TransformationUtils.
			createTransformationChangeResultForNewRootEObjects(newCorrespondingEObjects)
		for (correspondingEObject : newCorrespondingEObjects) {
			transResult.addNewCorrespondence(correspondenceInstance, newRootEObject, correspondingEObject, null)
		}
		return transResult
	}

	override removeEObject(EObject eObject) {
		val Repository repository = eObject as Repository

		//Remove corresponding packages
		val jaMoPPPackages = correspondenceInstance.getCorrespondingEObjectsByType(repository, Package)
		for (jaMoPPPackage : jaMoPPPackages) {
			EcoreUtil.remove(jaMoPPPackage)
		}

		//remove corresponding instance
		correspondenceInstance.removeAllCorrespondences(repository)
		return null
	}

	override deleteRootEObject(EObject oldRootEObject, EObject[] oldCorrespondingEObjectsToDelete) {
		return TransformationUtils.createEmptyTransformationChangeResult
	}

	override deleteNonRootEObjectInList(EObject affectedEObject, EReference affectedReference, EObject oldValue,
		int index, EObject[] oldCorrespondingEObjectsToDelete) {

		//FIXME: add useful code here
		return TransformationUtils.createEmptyTransformationChangeResult
	}

	override updateSingleValuedEAttribute(EObject eObject, EAttribute affectedAttribute, Object oldValue,
		Object newValue) {
		val Repository repository = eObject as Repository
		val correspondingObjects = PCM2JaMoPPUtils.checkKeyAndCorrespondingObjects(repository, affectedAttribute, featureCorrespondenceMap, correspondenceInstance) 
		if (correspondingObjects.nullOrEmpty) {
			return TransformationUtils.createEmptyTransformationChangeResult
		}

		//val EStructuralFeature jaMoPPNameAttribute = correspondenceInstance.claimCorrespondingEObjectByTypeIfUnique(affectedAttribute, EStructuralFeature)
		val EStructuralFeature jaMoPPNameAttribute = featureCorrespondenceMap.claimValueForKey(affectedAttribute)
		val jaMoPPPackages = correspondingObjects.filter(typeof(Package))
		val jaMoPPPackage = jaMoPPPackages.filter[pack|!pack.name.equals("contracts") && !pack.name.equals("datatypes")].
			get(0)
		if (jaMoPPPackage == null) {
			logger.warn("No Package found that maps to repository: " + repository)
			return TransformationUtils.createEmptyTransformationChangeResult
		}
		
		val tcr = new TransformationChangeResult
		PCM2JaMoPPUtils.handleJavaRootNameChange(jaMoPPPackage, affectedAttribute, newValue, tcr, correspondenceInstance, false)
		return tcr
	}

	override createNonRootEObjectInList(EObject affectedEObject, EReference affectedReference, EObject newValue,
		int index, EObject[] newCorrespondingEObjects) {
		val Iterable<EObjectCorrespondence> correspondenceCandidates = correspondenceInstance.
			getAllCorrespondences(affectedEObject).filter(typeof(EObjectCorrespondence))
		val transformationResult = TransformationUtils.
			createTransformationChangeResultForNewRootEObjects(newCorrespondingEObjects.filter(typeof(JavaRoot)))
		for (jaMoPPElement : newCorrespondingEObjects) {
			val parrentCorrespondence = getParrentCorrespondence(newValue, correspondenceCandidates)
			transformationResult.addNewCorrespondence(correspondenceInstance, newValue, jaMoPPElement,
				parrentCorrespondence)
		}
		return transformationResult
	}

	def dispatch getParrentCorrespondence(EObject object, Iterable<EObjectCorrespondence> correspondences) {
		return null
	}

	def dispatch getParrentCorrespondence(RepositoryComponent component, Iterable<EObjectCorrespondence> correspondences) {
		for (correspondence : correspondences) {
			if (correspondence.elementATUID.equals(
				correspondenceInstance.calculateTUIDFromEObject(component.repository__RepositoryComponent)) || correspondence.
				elementBTUID.equals(
					correspondenceInstance.calculateTUIDFromEObject(component.repository__RepositoryComponent))) {
				return correspondence
			}
		}
		return null;
	}

	// package with name "contracts"
	def dispatch getParrentCorrespondence(Interface pcmIf, Iterable<EObjectCorrespondence> correspondences) {
		return findPackageWithName("contracts", correspondences)
	}

	//package with name "datatypes"
	def dispatch getParrentCorrespondence(DataType dataType, Iterable<EObjectCorrespondence> correspondences) {
		findPackageWithName("datatypes", correspondences)
	}

	def findPackageWithName(String packageName, Iterable<EObjectCorrespondence> correspondences) {
		for (correspondence : correspondences) {
			if (correspondence.elementATUID.toString.contains(packageName) ||
				correspondence.elementBTUID.toString.contains(packageName)) {
				return correspondence
			}
		}
		correspondences.get(0)
	}

	override createNonRootEObjectSingle(EObject affectedEObject, EReference affectedReference, EObject newValue,
		EObject[] newCorrespondingEObjects) {
		logger.warn(
			"method createNonRootEObjectSingle should not be called for " + RepositoryMappingTransformation.simpleName +
				" transformation")
		return null
	}

}
