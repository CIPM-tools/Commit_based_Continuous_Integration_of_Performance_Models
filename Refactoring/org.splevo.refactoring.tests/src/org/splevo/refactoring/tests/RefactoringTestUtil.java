package org.splevo.refactoring.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.resource.JaMoPPUtil;
import org.emftext.language.refactoring.refactoring_specification.RefactoringSpecification;
import org.emftext.language.refactoring.refactoring_specification.RefactoringSpecificationPackage;
import org.emftext.language.refactoring.rolemapping.RoleMapping;
import org.emftext.language.refactoring.rolemapping.RoleMappingModel;
import org.emftext.language.refactoring.rolemapping.RolemappingPackage;
import org.emftext.language.refactoring.rolemapping.resource.rolemapping.mopp.RolemappingResourceFactory;
import org.emftext.language.refactoring.roles.RoleModel;
import org.emftext.language.refactoring.roles.RolesPackage;
import org.emftext.language.refactoring.roles.resource.rolestext.mopp.RolestextResourceFactory;
import org.emftext.language.refactoring.specification.resource.mopp.RefspecResourceFactory;
import org.emftext.refactoring.registry.refactoringspecification.IRefactoringSpecificationRegistry;
import org.emftext.refactoring.registry.refactoringspecification.exceptions.RefSpecAlreadyRegisteredException;
import org.emftext.refactoring.registry.rolemapping.IRoleMappingRegistry;
import org.emftext.refactoring.registry.rolemapping.exceptions.RoleMappingAlreadyRegistered;
import org.emftext.refactoring.registry.rolemodel.IRoleModelRegistry;
import org.emftext.refactoring.registry.rolemodel.exceptions.RoleModelAlreadyRegisteredException;

public class RefactoringTestUtil {
	/**
     * Initializes JaMoPP, required packages and factories.
     */
    public static final void initialize() {
        JaMoPPUtil.initialize();
        EPackage.Registry.INSTANCE.put(RolesPackage.eNS_URI, RolesPackage.eINSTANCE);
        EPackage.Registry.INSTANCE.put(RolemappingPackage.eNS_URI, RolemappingPackage.eINSTANCE);
        EPackage.Registry.INSTANCE.put(RefactoringSpecificationPackage.eNS_URI,
                RefactoringSpecificationPackage.eINSTANCE);
        Map<String, Object> extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
        extensionToFactoryMap.put("rolestext", new RolestextResourceFactory());
        extensionToFactoryMap.put("rolemapping", new RolemappingResourceFactory());
        extensionToFactoryMap.put("refspec", new RefspecResourceFactory());
    }
	
	/**
	 * Registers the role model of the given name.
	 * 
	 * @param modelFile
	 *            The modelFile.
	 * @throws RoleModelAlreadyRegisteredException
	 *             If the model is already registered.
	 * @throws FileNotFoundException
	 *             thrown if no file was found at the given path.
	 */
	public static void registerRoleModel(File modelFile)
			throws RoleModelAlreadyRegisteredException, FileNotFoundException {
		RoleModel roleModel = getModelByType(modelFile, RoleModel.class);
		IRoleModelRegistry.INSTANCE.registerRoleModel(roleModel);
	}

	/**
	 * Registers the ref specs of the given name.
	 * 
	 * @param modelFile
	 *            The modelFile.
	 * @throws RefSpecAlreadyRegisteredException
	 *             If the model is already registered.
	 * @throws FileNotFoundException
	 *             thrown if no file was found at the given path.
	 */
	public static void registerRefSpec(File modelFile)
			throws RefSpecAlreadyRegisteredException, FileNotFoundException {
		RefactoringSpecification refSpec = getModelByType(modelFile,
				RefactoringSpecification.class);
		IRefactoringSpecificationRegistry.INSTANCE.registerRefSpec(refSpec);
	}

	/**
	 * Registers the role mappings of a given name and related post processors.
	 * 
	 * @param modelFile
	 *            The modelFile.
	 * @throws RoleMappingAlreadyRegistered
	 *             If the model is already registered.
	 * @throws FileNotFoundException
	 *             thrown if no file was found at the given path.
	 */
	public static void registerRoleMapping(File modelFile)
			throws RoleMappingAlreadyRegistered, FileNotFoundException {
		RoleMappingModel roleMappingModel = getModelByType(modelFile,
				RoleMappingModel.class);
		RoleMapping roleMapping = roleMappingModel.getMappings().get(0);
		IRoleMappingRegistry.INSTANCE.registerRoleMapping(roleMapping);
	}

	/**
	 * Gets a model of a specific type from a specified path.
	 * 
	 * @param path
	 *            The path.
	 * @param type
	 *            The model type.
	 * @param <T>
	 *            The model type.
	 * @return The loaded model.
	 * @throws FileNotFoundException
	 *             thrown if no file was found at the given path.
	 */
	private static <T> T getModelByType(File modelFile, Class<T> type)
			throws FileNotFoundException {
		if (modelFile == null || type == null) {
			throw new IllegalArgumentException();
		}

		// check whether files exists; throw exception otherwise
		if (!modelFile.exists()) {
			throw new FileNotFoundException();
		}

		// get resource; throw exception if not found
		ResourceSet rs = new ResourceSetImpl();
		URI uri = URI.createFileURI(modelFile.getAbsolutePath());
		Resource resource = rs.getResource(uri, true);
		if (resource == null) {
			throw new NullPointerException("Resource couldn't be loaded");
		}

		EObject model = resource.getContents().get(0);
		if (model == null) {
			throw new NullPointerException("Resource is empty");
		}
		if (!type.isAssignableFrom(model.getClass())) {
			throw new NullPointerException("Model must be an instance of "
					+ type.getName());
		}

		return type.cast(model);
	}
}
