package tools.vitruv.applications.pcmjava.commitintegration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Package;

import tools.vitruv.applications.pcmjava.tests.util.java2pcm.CompilationUnitManipulatorHelper;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

/**
 * Class for applying changes contained in Git commits on a project. The commits must be contained in a {@link #gitRepository}. 
 * The changes are applied on the JDT Model of a given {@link IProject}.
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 * @author Martin Armbruster
 */
public class GitChangeApplier extends CommitChangePropagator {
	private static final Logger logger = Logger.getLogger(GitChangeApplier.class.getSimpleName());
	private String lineDelimiter = System.lineSeparator();
	private IProject currentProject;
	
	public GitChangeApplier(File repositoryPath, String localRepositoryPath, InternalVirtualModel vsum) {
		super(repositoryPath, localRepositoryPath, vsum);
	}
	
	public GitChangeApplier(String repositoryPath, String localRepositoryPath, InternalVirtualModel vsum) {
		super(repositoryPath, localRepositoryPath, vsum);
	}
	
	public void setProject(IProject proj) {
		currentProject = proj;
	}
	
	@Override
	protected void processDiff(DiffEntry diff) {
		// Classify changes and call an appropriate routine.
		switch (diff.getChangeType()) {
		// Add a new file.
		case ADD:
			try {
				String pathToAddedFile = diff.getNewPath();
				String fileContent = getWrapper().getNewContentOfFileFromDiffEntry(diff);
				// JGit returns the content of files and uses "\n" as line separator within the content.
				// Therefore, replace all occurrences of "\n" with the system line separator.
				fileContent = replaceAllLineDelimitersWithSystemLineDelimiters(fileContent);
				addElementToProject(currentProject, pathToAddedFile, fileContent);
			} catch (IOException e) {
				logger.error(e);
			}
			break;
		// Copy an existing file.
		case COPY:
			copyElementInProject(diff.getOldPath(), diff.getNewPath(), currentProject);
			break;
		// Remove an existing file.
		case DELETE:
			removeElementFromProject(currentProject, diff.getOldPath());
			break;
		// Modify an existing file.
		case MODIFY:
			try {
				OutputStream oldElementContent = getWrapper().getOldContentOfFileFromDiffEntryInOutputStream(diff);
				OutputStream newElementContent = getWrapper().getNewContentOfFileFromDiffEntryInOutputStream(diff);
				String oldElementPath = diff.getOldPath();
				String newElementPath = diff.getNewPath();
				// Compute changed lines in the given file. 
				EditList editList = getWrapper().computeEditListFromDiffEntry(diff);
				modifyElementInProject(currentProject, oldElementContent, newElementContent,
						oldElementPath, newElementPath, editList);
			} catch (IOException e) {
				logger.error(e);
			}
			break;
		// Rename an existing file.
		case RENAME:
			renameElementInProject(diff.getOldPath(), diff.getNewPath(), currentProject);
			break;
		default:
			// Error.
			System.out.println("Changes for the DiffEntry " + diff + "could not be classified");
			break;
		}
	}
	
	/**
	 * Creates a copy of an existing file with <code>oldPath</code> in <code>newPath</code> in <code>project</code>.
	 * 
	 * @param oldPath path to the original file.
	 * @param newPath path to the copy.
	 * @param project project that contains the original file and its copy.
	 * @exception CoreException if this resource could not be copied. Reasons include:
	 * <ul>
	 * <li> This resource does not exist.</li>
	 * <li> This resource or one of its descendents is not local.</li>
	 * <li> The source or destination is the workspace root.</li>
	 * <li> The source is a project but the destination is not.</li>
	 * <li> The destination is a project but the source is not.</li>
	 * <li> The resource corresponding to the parent destination path does not exist.</li>
	 * <li> The resource corresponding to the parent destination path is a closed project.</li>
	 * <li> A resource at destination path does exist.</li>
	 * <li> This resource or one of its descendents is out of sync with the local file
	 *      system and <code>force</code> is <code>false</code>.</li>
	 * <li> The workspace and the local file system are out of sync
	 *      at the destination resource or one of its descendents.</li>
	 * <li> The source resource is a file and the destination path specifies a project.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 * @exception OperationCanceledException if the operation is canceled.
	 * Cancellation can occur even if no progress monitor is provided.
	 */
	private void copyElementInProject(String oldPath, String newPath, IProject project) {
		//Convert old path from String into Path. For convenience only.
		IPath tempOldPath = new Path(oldPath);
		//Get rid of the project name in the path
		tempOldPath = tempOldPath.removeFirstSegments(1);
		
		//Convert new path from String into Path. For convenience only.
		IPath tempNewPath = new Path(newPath);
		//Get rid of the project name in the path
		tempNewPath = tempNewPath.removeFirstSegments(1);
		
		IFile originalFile = project.getFile(tempOldPath);
		if (originalFile.exists()) {
			IFile copiedFile = project.getFile(tempNewPath);
			if (!copiedFile.exists()) {
				try {
					originalFile.copy(copiedFile.getFullPath(), true, new NullProgressMonitor());
				} catch (CoreException e) {
					logger.error(e);
				}
			}
		}
	}

	/**
	 * Computes the name (including the file extension) of the file from the given path to this file.
	 * 
	 * @param path path to the file.
	 * @return file name.
	 */
	public String getNameOfFileFromPath(String path) {
		return  path.substring(path.lastIndexOf("/") + 1);
	}
	
	/**
	 * Applies changes on the give file. If the file is a java file, only the changed lines will be replaced. 
	 * For all other file types, the entire old content will be replaced with the <code>newContent</code>. 
	 * 
	 * @param project
	 * @param oldElementContent
	 * @param newElementContent
	 * @param oldElementPath
	 * @param newElementPath
	 * @param editList 
 	 *
 	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> This resource does not exist.</li>
	 * <li> The corresponding location in the local file system
	 *       is occupied by a directory.</li>
	 * <li> The workspace is not in sync with the corresponding location
	 *       in the local file system and <code>FORCE</code> is not specified.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * <li> The file modification validator disallowed the change.</li>
	 * </ul>
	 * 
	 * @exception JavaModelException a failure in the Java model. See: 
	 * 	{@link ICompilationUnit#becomeWorkingCopy}
	 * 	{@link ICompilationUnit#applyTextEdit}
	 * 	{@link ICompilationUnit#reconcile}
	 * 	{@link ICompilationUnit#commitWorkingCopy}
	 * 	{@link ICompilationUnit#discardWorkingCopy}
	 * 
	 * @exception  IOException  if an I/O error occurs.
	 */
	private void modifyElementInProject(IProject project,
			OutputStream oldElementContent, OutputStream newElementContent, 
			String oldElementPath, String newElementPath,
			EditList editList) {
		
		//Check if the modified file is a java file
		if (oldElementPath.endsWith(".java")) {
			//Find the compilation unit
			ICompilationUnit compilationUnit = findICompilationUnitInProject(oldElementPath, project);
			String oldContent = oldElementContent.toString();
			String newContent = newElementContent.toString();
			try {
				oldElementContent.close();
				newElementContent.close();
			} catch (IOException e) {
				logger.error(e);
			}
			
			//Convert EditList into List<TextEdit>. Necessary because EditList is from JGit (see: org.eclipse.jgit.diff.EditList),
			//but changes must be applied on a JDT Model, what is only possible with TextEdit (see: org.eclipse.jdt.core.ICompilationUnit.applyTextEdit(TextEdit edit, IProgressMonitor monitor))	
			List<TextEdit> textEdits = transformEditListIntoTextEdits(editList, oldContent, newContent);
			//Apply changes on the given compilation unit
			try {
				CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, textEdits.toArray(new TextEdit[textEdits.size()]));
			} catch (JavaModelException e) {
				logger.error(e);
			}
		}
		//Find the non-java file and replace its entire content with the new one
		else {
			try {
				oldElementContent.close();
			} catch (IOException e) {
				logger.error(e);
			}
			IFile file = project.getFile(oldElementPath.substring(oldElementPath.indexOf("/") + 1));
			if (file.exists()) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) newElementContent).toByteArray());
				try {
					file.setContents(inputStream, IFile.FORCE, null);
				} catch (CoreException e) {
					logger.error(e);
				}
				try {
					newElementContent.close();
					//inputStream.close(); //has no effect
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}
	
	/**
	 * Creates a new file in the <code>project</code>.
	 * The <code>pathToElement</code> contains the path to the new file, but some folders on the path to the new file
	 * may not exist yet. The non-existing parent folders will also be created.
	 * 
	 * @param project project which the new file or folder will be created in
	 * @param pathToElement path to the new file
	 * @param elementContent file content
	 * 
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul> 
	 * 
	 * 	 * @exception JavaModelException a failure in the Java model. See: 
	 * 	{@link ICompilationUnit#becomeWorkingCopy}
	 * 	{@link ICompilationUnit#applyTextEdit}
	 * 	{@link ICompilationUnit#reconcile}
	 * 	{@link ICompilationUnit#commitWorkingCopy}
	 * 	{@link ICompilationUnit#discardWorkingCopy}
	 * 
	 */
	private void addElementToProject(IProject project, String pathToElement, String elementContent) {
		//Refresh the project to avoid inconsistency
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			logger.error(e);
		}
		//Convert path from String into Path. For convenience only.
		IPath tempPath = new Path(pathToElement);
		//Get rid of the project name in the path
		tempPath = tempPath.removeFirstSegments(1);
		//Get number of segments in the path
		int segmentCounter = tempPath.segmentCount();
		//Check if the path to the file is valid
		if (segmentCounter == 0) {
			logger.warn("Path: " + pathToElement + " is not valid");
			return;
		}
		//Check if the file to be created is a java file
		if (!pathToElement.endsWith(".java")) {
			createNonJavaFile(tempPath, elementContent, project);
		}
		else {
			String firstSegment = tempPath.segment(0);
			String lastSegment = tempPath.lastSegment();
			
			IJavaProject javaProject = null;
			IPackageFragmentRoot packageFragmentRoot = null;
			IPackageFragment packageFragment = null;
			
			//Check if we need to handle a IJavaProject
			//For more details, what a java project in JDT looks like, see https://www.vogella.com/tutorials/EclipseJDT/article.html
			//if (firstSegment.equals("src") || firstSegment.equals("bin") 
			//		|| fileExtension.equals("jar") || fileExtension.equals("zip")) {
			
			//Check if the project is a java project
			boolean javaNature = false;
			try {
				javaNature = project.hasNature(JavaCore.NATURE_ID);
			} catch (CoreException e) {
				logger.error(e);
			}
			if (javaNature) {
				javaProject = JavaCore.create(project);
				//A Java File must not be directly in the src folder. Instead it must be in a package, that is contained in the src folder.
				//Therefore, a path to a new java file must contain at least three segments.
				if (segmentCounter > 2) {
					//Find the package fragment root. If it does not exist, create it
					IPath packageFragmentRootPath = javaProject.getPath().append("/" + firstSegment);
					try {
						packageFragmentRoot = javaProject.findPackageFragmentRoot(packageFragmentRootPath);
					} catch (JavaModelException e) {
						logger.error(e);
					}//javaProject.getPackageFragmentRoot(firstSegment);
					if (!packageFragmentRoot.exists()) {
						packageFragmentRoot = createPacakgeFragmentRoot(firstSegment, javaProject);
					}
					//Find PackageFragment
					String packageFragmentName = tempPath.removeFirstSegments(1).removeLastSegments(1).toString().replace("/", ".");
					packageFragment = packageFragmentRoot.getPackageFragment(packageFragmentName);
					//Check if the PackageFragment exists. If not, create it
					if (!packageFragment.exists()) {
						createPackageWithPackageInfo(project, packageFragmentRoot, tempPath.removeFirstSegments(1).removeLastSegments(1).segments());
					}
					//packageFragment.makeConsistent(new NullProgressMonitor());
					ICompilationUnit compilationUnit = packageFragment.getCompilationUnit(lastSegment);
					//The new file must not exist yet.
					if (!compilationUnit.exists()) {
						//Create java file per JDT
						try {
							compilationUnit = packageFragment.createCompilationUnit(lastSegment, "", false/*true*/, new NullProgressMonitor());
						} catch (JavaModelException e) {
							logger.error(e);
						}
						//Thread.sleep(5000);
						//Set empty content on the new file. Necessary to inform Vitruv about the new created java file.
						InsertEdit edit = new InsertEdit(0, elementContent);
						try {
							CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, edit);
						} catch (JavaModelException e) {
							logger.error(e);
						}
						//project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					}
					else {
						logger.warn("The compilation unit with name " + lastSegment + " in package fragment "+ packageFragmentName + " already existed and therefore could not be created");
					}
				}
				else {
					logger.warn("The java file " + lastSegment + " could not be created because it's not in a Package Fragment");
				}
			}
		}
	}
	
	/**
	 * Creates a {@link IPackageFragmentRoot} with <code>packageFragmentRootPath</code> in <code>javaProject</code>.
	 * 
	 * @param packageFragmentRootPath path to the package fragment root
	 * @param javaProject project which the package fragment root must be created in
	 * @return created package fragment root
	 * 
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> This resource already exists in the workspace.</li>
	 * <li> The workspace contains a resource of a different type
	 *      at the same path as this resource.</li>
	 * <li> The parent of this resource does not exist.</li>
	 * <li> The parent of this resource is a project that is not open.</li>
	 * <li> The parent contains a resource of a different type
	 *      at the same path as this resource.</li>
	 * <li> The parent of this resource is virtual, but this resource is not.</li>
	 * <li> The name of this resource is not valid (according to
	 *    <code>IWorkspace.validateName</code>).</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a file (as opposed to a directory).</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a folder and <code>FORCE</code> is not specified.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification.  See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 * 
	 * @exception JavaModelException if this element does not exist or if an
	 *	exception occurs while accessing its corresponding resource
	 */
	private IPackageFragmentRoot createPacakgeFragmentRoot(String packageFragmentRootPath, IJavaProject javaProject) {
		IFolder rootFolder = javaProject.getProject().getFolder(packageFragmentRootPath);
		//Check if the PackageFragmentRoot exists. If not, create it
		if (!rootFolder.exists()) {
			try {
				rootFolder.create(false/*true*/, false/*true*/, new NullProgressMonitor());
			} catch (CoreException e) {
				logger.error(e);
			}		
		}

		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(rootFolder);
		
		IClasspathEntry[] oldEntries = null;
		try {
			oldEntries = javaProject.getRawClasspath();
		} catch (JavaModelException e) {
			logger.error(e);
		}
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
		try {
			javaProject.setRawClasspath(newEntries, null);
		} catch (JavaModelException e) {
			logger.error(e);
		}
		
		return root;
	}
	
	/**
	 * Creates a non-java {@link IFile} with <code>elementContent</code> in <code>project</code>.
	 * If there are file's parents folders that not exist yet, they will also be created.
	 * 
	 * @param pathToFile path to the file
	 * @param elementContent file content 
	 * @param project project which the file must be created in
	 
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> This resource already exists in the workspace.</li>
	 * <li> The parent of this resource does not exist.</li>
	 * <li> The parent of this resource is a virtual folder.</li>
	 * <li> The project of this resource is not accessible.</li>
	 * <li> The parent contains a resource of a different type
	 *      at the same path as this resource.</li>
	 * <li> The name of this resource is not valid (according to
	 *    <code>IWorkspace.validateName</code>).</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a directory.</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a file and <code>force </code> is <code>false</code>.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 */
	private void createNonJavaFile(IPath pathToFile, String elementContent, IProject project) {
		int segmentCounter = pathToFile.segmentCount();
		if(segmentCounter < 1) {
			logger.warn("Could not create a new file because of an invalid file path");
			return;
		}
		//Check if the file has no parent folders
		if (pathToFile.segmentCount() > 1) {
			//The file has parent folders. Create all parent folders if they are not exist yet
			for(int i = segmentCounter - 1; i > 0; i--) {
				IFolder folder = project.getFolder(pathToFile.removeLastSegments(i));
				if (!folder.exists()) {
					try {
						folder.create(false, true, new NullProgressMonitor());
					} catch (CoreException e) {
						logger.error(e);
					}
				}
			}
		}
		//Create the file if it is not exist yet
		IFile file = project.getFile(pathToFile);
		if (!file.exists()) {
			ByteArrayInputStream elementContentStream = new ByteArrayInputStream(elementContent.getBytes());
			try {
				file.create(elementContentStream, false, new NullProgressMonitor());
				//elementContentStream.close(); //has no effect
			} catch (CoreException e) {
				logger.error(e);
			}
		}
	}
	
	/**
	 * Renames an existing {@link IFile} in the <code>project</code>.
	 * 
	 * @param oldPath path to the file (including file name) before renaming
	 * @param newPath path to the file (including file name) after renaming
	 * @param project project which the file must be renamed in
	 *
	 * @exception CoreException if this resource could not be moved. Reasons include:
	 * <ul>
	 * <li> This resource does not exist.</li>
	 * <li> This resource or one of its descendents is not local.</li>
	 * <li> The source or destination is the workspace root.</li>
	 * <li> The source is a project but the destination is not.</li>
	 * <li> The destination is a project but the source is not.</li>
	 * <li> The resource corresponding to the parent destination path does not exist.</li>
	 * <li> The resource corresponding to the parent destination path is a closed
	 *      project.</li>
	 * <li> The source is a linked resource, but the destination is not a project
	 *      and  {@link #SHALLOW} is specified.</li>
	 * <li> A resource at destination path does exist.</li>
	 * <li> A resource of a different type exists at the destination path.</li>
	 * <li> This resource or one of its descendents is out of sync with the local file system
	 *      and <code>force</code> is <code>false</code>.</li>
	 * <li> The workspace and the local file system are out of sync
	 *      at the destination resource or one of its descendents.</li>
	 * <li> The source resource is a file and the destination path specifies a project.</li>
	 * <li> The location of the source resource on disk is the same or a prefix of
	 * the location of the destination resource on disk.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 * event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 */
	private void renameElementInProject(String oldPath, String newPath, IProject project) {
		//Convert old path from String into Path. For convenience only.
		IPath tempOldPath = new Path(oldPath);
		//Get rid of the project name in the path
		tempOldPath = tempOldPath.removeFirstSegments(1);
		
		//Convert new path from String into Path. For convenience only.
		IPath tempNewPath = new Path(newPath);
		//Get rid of the project name in the path
		tempNewPath = tempNewPath.removeFirstSegments(1);
		
		IFile fileBeforeRename = project.getFile(tempOldPath);
		if (fileBeforeRename.exists()) {		
			IFile fileAfterRename = project.getFile(tempNewPath);
			if (!fileAfterRename.exists()) {
				try {
					fileBeforeRename.move(fileAfterRename.getFullPath()/*tempNewPath*/, true, new NullProgressMonitor());
				} catch (CoreException e) {
					logger.error(e);
				}
			}
		}
	}
	
	/**
	 * Removes file or folder from the given <code>project</code>.
	 * 
	 * @param project given project which the file or folder must be removed from
	 * @param pathToElement path to the file or folder which must be removed
	 *
	 * @exception JavaModelException if this element could not be deleted. Reasons include:
	 * <ul>
	 * <li> This Java element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while updating an underlying resource (CORE_EXCEPTION)</li>
	 * <li> This element is read-only (READ_ONLY)</li>
	 * </ul>
	 */
	private void removeElementFromProject(IProject project, String pathToElement) {
		//Determine if a package, a compilation unit or a non-java file must be removed
		if (pathToElement.endsWith("package-info.java")) {
			//Do we need to delete package-info.java explicitly before we delete package?
			//ICompilationUnit compilationUnit = findICompilationUnitInProject(pathToElement, project);
			//compilationUnit.delete(true, null);
			//Find and delete the package
			IPackageFragment packageFragment = findIPackageFragmentInProject(pathToElement, project);
			try {
				packageFragment.delete(true, null);
			} catch (JavaModelException e) {
				logger.error(e);
			}
		}
		else if (pathToElement.endsWith(".java")) {
			ICompilationUnit compilationUnit = findICompilationUnitInProject(pathToElement, project);
			try {
				compilationUnit.delete(true/*false*/, null);
			} catch (JavaModelException e) {
				logger.error(e);
			}
		}
		else {
			IPath pathToElementInProject = new Path(pathToElement).removeFirstSegments(1);
			IFile fileToRemove = project.getFile(pathToElementInProject);
			if (fileToRemove.exists()) {
				try {
					fileToRemove.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					logger.error(e);
				}
			}
		}
	}
	
	/**
	 * Creates a new package with a containing file 'package-info.java' in <code>project</code>.
	 * 
	 * @param project project which a new package must be created in
	 * @param namespace package name space
	 * @return JaMoPP Model of the created package
	 *
	 * @exception IOException if an error occurred during saving
	 */
	protected Package createPackageWithPackageInfo(final IProject project, final IPackageFragmentRoot packageFragmentRoot, final String... namespace) {
		assert (namespace.length > 0);
		Package jaMoPPPackage = null;
		//Check if there are parent subpackages on the path, which don't exist yet. If so, create them before creating the target package.
		String parentPackage = namespace[0];
		for (int i = 0; i < namespace.length; i++) {
			if (!packageFragmentRoot.getPackageFragment(parentPackage).exists()) {
				String currentPackage = parentPackage.replace(".", "/") + "/package-info.java";
				jaMoPPPackage = ContainersFactory.eINSTANCE.createPackage();
				List<String> namespaceList = Arrays.asList(namespace);
				jaMoPPPackage.setName(namespaceList.get(namespaceList.size() - 1));
				jaMoPPPackage.getNamespaces().addAll(namespaceList.subList(0, namespaceList.size() - 1));
				
				ResourceSet resourceSet = new ResourceSetImpl();
				URI vuri = URI.createFileURI(project.getName() + "/src/" + currentPackage);
				final Resource resource = resourceSet.createResource(vuri);
				resource.getContents().add(jaMoPPPackage);
				try {
					resource.save(new HashMap<>());
				} catch (IOException e) {
					logger.error(e);
				}
			}
			
			parentPackage = i + 1 < namespace.length ? parentPackage + "." + namespace[i + 1] : parentPackage  ;
		}
		
		return jaMoPPPackage;
	}
	
	/**
	 * Computes a {@link URI} for the path to the file <code>elementName</code>.
	 * 
	 * @param packageFragment package that contains file <code>elementName</code>
	 * @param elementName file
	 * @return computed {@link URI}
	 */
	public URI getVURIForElementInPackage(final IPackageFragment packageFragment, final String elementName) {
		String vuriKey = packageFragment.getResource().getFullPath().toString() + "/" + elementName;
		if (vuriKey.startsWith("/")) {
			vuriKey = vuriKey.substring(1, vuriKey.length());
		}
		final URI vuri = URI.createFileURI(vuriKey);
		return vuri;
	}
	
	/**
	 * Returns a JaMoPP {@link ConcreteClassifier} for the given {@link URI}
	 * 
	 * @param vuri given {@link URI}
	 * @return found {@link ConcreteClassifier}
	 */
	protected ConcreteClassifier getJaMoPPClassifierForVURI(final URI vuri) {
		final CompilationUnit cu = this.getJaMoPPRootForVURI(vuri);
		final Classifier jaMoPPClassifier = cu.getClassifiers().get(0);
		return (ConcreteClassifier) jaMoPPClassifier;
	}

	/**
	 * Returns a JaMoPP {@link JavaRoot} for the given {@link URI}.
	 * 
	 * @param <T>
	 * @param vuri given {@link VURI}
	 * @return found {@link JavaRoot}
	 */
	private <T extends JavaRoot> T getJaMoPPRootForVURI(final URI vuri) {
		final Resource resource = new ResourceSetImpl().getResource(vuri, true);
		// unchecked is OK for the test.
		@SuppressWarnings("unchecked")
		final T javaRoot = (T) resource.getContents().get(0);
		return javaRoot;
	}

    /**
     * Finds a {@link ICompilationUnit} in the given project <code>project</code> based on <code>pathToCompilationUnit</code>.
     * 
     * @param pathToCompilationUnit path to the compilation unit
     * @param project given project
     * @return found {@link ICompilationUnit}
     * 
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
     */
    public static ICompilationUnit findICompilationUnitInProject(String pathToCompilationUnit,
            final IProject project) {
    	//Convert path to the compilation unit from Sting to Path
    	IPath path = new Path(pathToCompilationUnit);
    	//Get rid of the project name
    	path = path.removeFirstSegments(1);
    	//Determine names for the package fragment root, package fragment and compilation unit
    	String packageFragmentRootName = path.segment(0).replace("/", ".");
    	String packageFragmentName = path.removeFirstSegments(1).removeLastSegments(1).toString().replace("/", ".");
    	String compilationUnitName = path.lastSegment().replace("/", ".");
    	
        final IJavaProject javaProject = JavaCore.create(project);
        //Iterate over all package fragment roots
        try {
			for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getElementName().equals(packageFragmentRootName)) {
					final IJavaElement[] children = packageFragmentRoot.getChildren();
			        //Iterate over all package fragments
					for (final IJavaElement iJavaElement : children) {
			            if (iJavaElement instanceof IPackageFragment && iJavaElement.getElementName().equals(packageFragmentName)) {
			                final IPackageFragment fragment = (IPackageFragment) iJavaElement;
			                final IJavaElement[] javaElements = fragment.getChildren();
			                //Iterate over all compilation units
			                for (int k = 0; k < javaElements.length; k++) {
			                    final IJavaElement javaElement = javaElements[k];
			                    if (javaElement.getElementType() == IJavaElement.COMPILATION_UNIT && javaElement.getElementName().equals(compilationUnitName)) {
			                        final ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
			                            return compilationUnit;
			                    }
			                }
			            }
			        }
				} 
			}
		} catch (JavaModelException e) {
			logger.error(e);
		}
        throw new RuntimeException("Could not find a compilation unit with name " + pathToCompilationUnit);
    }
    
    /**
     * Finds a {@link IPackageFragment} in the given project <code>project</code> based on <code>pathToPackageFragment</code>.
     * 
     * @param pathToPackageFragment path to the package fragment
     * @param project given project
     * @return found {@link IPackageFragment}
     *
     * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
     */
    public static IPackageFragment findIPackageFragmentInProject(String pathToPackageFragment,
            final IProject project){
    	//Convert path to the compilation unit from Sting to Path
    	IPath path = new Path(pathToPackageFragment);
    	//Get rid of the project name
    	path = path.removeFirstSegments(1);
    	//Determine names for the package fragment root and package fragment
    	String packageFragmentRootName = path.segment(0).replace("/", ".");
    	String packageFragmentName = path.removeFirstSegments(1).removeLastSegments(1).toString().replace("/", ".");
    	
        final IJavaProject javaProject = JavaCore.create(project);
        //Iterate over all package fragment roots
        try {
			for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getElementName().equals(packageFragmentRootName)) {
					final IJavaElement[] children = packageFragmentRoot.getChildren();
					//Iterate over all package fragments
					for (final IJavaElement iJavaElement : children) {
			            if (iJavaElement instanceof IPackageFragment && iJavaElement.getElementName().equals(packageFragmentName)) {
			                final IPackageFragment fragment = (IPackageFragment) iJavaElement;
			                return fragment;
			            }
			        }
				} 
			}
		} catch (JavaModelException e) {
			logger.error(e);
		}
        throw new RuntimeException("Could not find a compilation unit with name " + pathToPackageFragment);
    }
	
	/**
	 * Replaces all possible kinds of line separators with one particular kind of line separator in <code>fileContent</code>
	 * 
	 * @param fileContent
	 * @return file content with replaced line separators
	 */
	private String replaceAllLineDelimitersWithSystemLineDelimiters(String fileContent) {
		return fileContent.replaceAll("\r\n|\n|\r", lineDelimiter);
	}
	
	/**
	 * Transforms {@link EditList} into {@link List} of {@link TextEdit}.
	 * There are two main problems to solve:
	 *  
	 * The first one is, an {@link Edit} contains information about content in form of line numbers, 
	 * whereas an {@link TextEdit} deals with offsets of content. Therefore, we have to compute offsets to line numbers.
	 * 
	 * The second problem is, {@link EditList} contains Edits that describe which lines in the old content have to be add/removed/replaced with
	 * lines from the new content or which lines from the new content have to be inserted in the old content. Edits are independent from each other.
	 * That means, all Edits on the old content have to be applied at the same time. Applying Edits one by one would cause shifting in the old content
	 * after each applying and would create wrong content, that does not match to the new content. On the other hand, TextEdits have to be applied one by one. 
	 * Therefore, while computing offset for a TextEdit we have to consider shifting caused by previously applied TextEdits.   
	 * 
	 * @param editList - list of Edits to be applied on the old context to transform the old context into the new context.
	 * @param oldFileContent - old content as plain text
	 * @param newFileContent - new content as plain text
	 * @return list of TextEdits, that have to be applied one by one (the applying order is important) on the old context to transform the old context into the new context.
	 */
	private List<TextEdit> transformEditListIntoTextEdits(EditList editList, String oldFileContent, String newFileContent) {
		
		ArrayList<TextEdit> textEdits = new ArrayList<>();
		
		//Split the content into separated lines
		List<String> oldContentLines = splitFileContentIntoLinesWithLineDelimitors(oldFileContent);
		List<String> newContentLines = splitFileContentIntoLinesWithLineDelimitors(newFileContent);
		//Determine begin offset for each line of contexts 
		List<Integer> oldContentStartOffsetsToLines = computeStartOffsetsToLineNumbers(oldContentLines);
		List<Integer> newContentStartOffsetsToLines = computeStartOffsetsToLineNumbers(newContentLines);
		//Contains shifting that appears while applying Edits one by one
		int shifting = 0;
	
		for(Edit edit : editList) {
			//begin line number in the old content
			int beginPositionOld = oldContentStartOffsetsToLines.get(edit.getBeginA());
			//end line number in the old content
			int endPositionOld = oldContentStartOffsetsToLines.get(edit.getEndA());
			//begin line number in the new content
			int beginPositionNew = newContentStartOffsetsToLines.get(edit.getBeginB());
			//end line number in the new content
			int endPositionNew = newContentStartOffsetsToLines.get(edit.getEndB());
			
			switch (edit.getType()) {
			case INSERT:
				int positionForInsert = beginPositionOld + shifting;
				//"- edit.getBeginB()" and "- edit.getEndB()" are needed because of the third problem described above in the java doc for the method.
				String textForInsert = concatenateContentLines(newContentLines, edit.getBeginB(), edit.getEndB());
				TextEdit insertEdit = new InsertEdit(positionForInsert, textForInsert);
				textEdits.add(insertEdit);
				shifting += endPositionNew - beginPositionNew;
				break;
			case DELETE:
				int positionForDelete = beginPositionOld + shifting;
				int lengthForDelete = endPositionOld - beginPositionOld;
				TextEdit deleteEdit = new DeleteEdit(positionForDelete, lengthForDelete);
				textEdits.add(deleteEdit);
				shifting -= lengthForDelete;
				break;
			case REPLACE:
				int positionForReplace = beginPositionOld + shifting;
				int lengthForReplace = endPositionOld - beginPositionOld;
				String textForReplace = concatenateContentLines(newContentLines, edit.getBeginB(), edit.getEndB());
				TextEdit replaceEdit = new ReplaceEdit(positionForReplace, lengthForReplace, textForReplace);
				textEdits.add(replaceEdit);
				shifting = shifting - lengthForReplace + (endPositionNew - beginPositionNew);
				break;
			case EMPTY:
				//do nothing
				break;
			default:
				//do nothing
				break;
			}
		}
		
		return textEdits;
	}
	
	/**
	 * Concatenate <code>contentLines</code> between [<code>beginLineNumber</code>, <code>endLineNumber</code>)
	 * 
	 * @param newContentLines - Lines of the content
	 * @param beginPositionNew - begin line number. Included
	 * @param endPositionNew - end line number. Not included.
	 * @return concatenated lines as String
	 */
	private String concatenateContentLines(List<String> contentLines, int beginLineNumber, int endLineNumber) {
		String content = "";
		
		for (int i = beginLineNumber;  i < endLineNumber; i++) {
			content += contentLines.get(i);
		}
		
		return content;
	}

	/**
	 * Splits <code>fileContent</code> into lines. Each split line except the last line contains a line delimiter at the end.
	 * The last line may contain a line delimiter at the end.
	 * @param fileContent
	 * @return List of content lines with line delimiters at the end
	 */
	private List<String> splitFileContentIntoLinesWithLineDelimitors(String fileContent) {
		
		List<String> contentLines = new ArrayList<String>();
		
		Stream<String> lines = fileContent.lines();
		Iterator<String> iterator = lines.iterator();
		
		//Determine begin offset for each line
		while (iterator.hasNext()) {
			String line = iterator.next() + lineDelimiter;
			//if the current line is the last line, check if the last line ends with a line delimiter
			if (!iterator.hasNext()) {
				if (!fileContent.endsWith(lineDelimiter)) {
					//remove line delimiter
					line = line.substring(0, line.length() - lineDelimiter.length());
				} 
			}
			contentLines.add(line);
		}
		lines.close();
		
		return contentLines;
	}
	
	/**
	 * Computes start offset for each line of <code>fileContent</code>. The file content is represented as List of content lines with line delimiters.
	 * The last element in the result list contains the length of the last content line. This implies, that the length
	 * of the result list equals the number of content lines + 1.
	 *  
	 * @param fileContent - file content.
	 * @return list of computed start offsets. The last element in the list contains the length of the last content line.
	 */
	private List<Integer> computeStartOffsetsToLineNumbers(List<String> fileContent) {
		//Index of each element in the list corresponds to the line number of the context
		List<Integer> startOffsets = new ArrayList<>();
		
		int offsetCounter = 0;
		
		for (String line : fileContent) {
			startOffsets.add(offsetCounter);
			offsetCounter += line.length();
		}
		//The last element in startOffsets contains length of the last line
		startOffsets.add(offsetCounter);
		
		return startOffsets;
	}
}
