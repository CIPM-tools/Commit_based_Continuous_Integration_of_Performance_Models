package org.splevo.ui.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.splevo.extraction.ExtractionService;
import org.splevo.project.SPLevoProject;

import de.uka.ipd.sdq.workflow.AbstractJob;
import de.uka.ipd.sdq.workflow.exceptions.JobFailedException;
import de.uka.ipd.sdq.workflow.exceptions.RollbackFailedException;
import de.uka.ipd.sdq.workflow.exceptions.UserCanceledException;

/**
 * Job to extract a software model from an eclipse java project.
 */
public class ExtractionJob extends AbstractJob {

    /** The splevo project to store the required data to. */
    private SPLevoProject splevoProject;

    /** Flag whether the leading or the integration project should be extracted. */
    private boolean processLeading;

    /**
     * Constructor to create an extraction job with the required references.
     * 
     * @param splevoProject
     *            The splevo project to get and store required information
     * @param processLeading
     *            True/false wether this job is responsible for the leading implementation.
     */
    public ExtractionJob(SPLevoProject splevoProject, boolean processLeading) {
        this.splevoProject = splevoProject;
        this.processLeading = processLeading;
    }

    /**
     * Runs the long running operation.
     * 
     * @param monitor
     *            the progress monitor
     * @return the status of the job. This should be OK or CANCLED
     */
    public IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Software Model Extraction", 100);

        List<String> projectNames = null;
        String variantName = null;
        if (processLeading) {
            projectNames = splevoProject.getLeadingProjects();
            variantName = splevoProject.getVariantNameLeading();
        } else {
            projectNames = splevoProject.getIntegrationProjects();
            variantName = splevoProject.getVariantNameIntegration();
        }

        IJavaProject mainProject = null;
        try {
            mainProject = getJavaProject(projectNames.get(0));
        } catch (CoreException ce) {
            return Status.CANCEL_STATUS;
        }

        List<IJavaProject> additionalProjects = new ArrayList<IJavaProject>();
        List<String> additionalProjectNames = new ArrayList<String>();
        for (int i = 1; i < projectNames.size(); i++) {
            try {
                additionalProjects.add(getJavaProject(projectNames.get(i)));
                additionalProjectNames.add(projectNames.get(i));
            } catch (CoreException e) {
                return Status.CANCEL_STATUS;
            }
        }

        // prepare the target path
        URI targetURI = buildTargetURI(variantName);

        logger.info("Extraction target: " + targetURI);
        logger.info("Main Project: " + mainProject.getElementName());
        logger.info("Additional Projects: " + additionalProjectNames);

        // check if the process was canceled
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        // extract model
        ExtractionService extractionService = new ExtractionService();
        try {
            monitor.subTask("Extract Model for project: " + variantName);
            extractionService.extractProject(mainProject, additionalProjects, monitor, targetURI);
        } catch (Exception e) {
            e.printStackTrace();
            return Status.CANCEL_STATUS;
        }

        monitor.subTask("Update SPLevo project information");

        if (processLeading) {
            splevoProject.setSourceModelPathLeading(targetURI.path());
        } else {
            splevoProject.setSourceModelPathIntegration(targetURI.path());
        }

        // finish run
        monitor.done();

        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }

    /**
     * Get the JavaProject for a specific project name.
     * 
     * @param projectName
     *            The name to search for.
     * @return The Identified project if a java one is found. Null otherwise.
     * @throws CoreException
     *             Identifies that the project's nature could not be checked.
     */
    private IJavaProject getJavaProject(String projectName) throws CoreException {
        IJavaProject javaProject = null;
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IProject project = workspace.getRoot().getProject(projectName);
        if (project.hasNature(JavaCore.NATURE_ID)) {
            javaProject = JavaCore.create(project);
        }
        return javaProject;
    }

    /**
     * Build the target uri for the model extraction.
     * 
     * @param variantName
     *            The name of the variant to extract.
     * @return The prepared URI.
     */
    private URI buildTargetURI(String variantName) {
        String basePath = getBasePath(splevoProject);
        String targetPath = basePath + variantName + "/" + variantName + "_java2kdm.xmi";
        URI targetURI = URI.createURI(targetPath);
        return targetURI;
    }

    /**
     * Build the base path for the target models.
     * 
     * @param splevoProject
     *            The SPLevo project to interact with.
     * @return The base path to store the extracted models at.
     */
    private String getBasePath(SPLevoProject splevoProject) {
        return splevoProject.getWorkspace() + "models/sourcemodels/";
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        run(monitor);
    }

    @Override
    public void rollback(IProgressMonitor monitor) throws RollbackFailedException {
        // no rollback possible
    }

    /**
     * Get the name of the extraction job. This depends on whether the leading or the integration
     * job should be extracted.
     * 
     * @return The name of the job.
     */
    @Override
    public String getName() {
        if (processLeading) {
            return "Model Extraction Job " + splevoProject.getVariantNameLeading();
        } else {
            return "Model Extraction Job " + splevoProject.getVariantNameIntegration();
        }
    }
}
