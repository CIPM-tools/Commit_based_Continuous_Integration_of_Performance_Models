package mir.responses.mocks.org.palladiosimulator.pcm.seff;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.AbstractLoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;

@SuppressWarnings("all")
public class ResourceDemandingInternalBehaviourContainerMock implements ResourceDemandingInternalBehaviour {
  public ResourceDemandingInternalBehaviourContainerMock(final ResourceDemandingInternalBehaviour containedObject, final EObject containerObject) {
    super();
    this.containedObject = containedObject;
    this.containerObject = containerObject;
  }
  
  private ResourceDemandingInternalBehaviour containedObject;
  
  private EObject containerObject;
  
  public BasicComponent getBasicComponent_ResourceDemandingInternalBehaviour() {
    return containedObject.getBasicComponent_ResourceDemandingInternalBehaviour();
  }
  
  public void setBasicComponent_ResourceDemandingInternalBehaviour(final BasicComponent arg0) {
    containedObject.setBasicComponent_ResourceDemandingInternalBehaviour(arg0);
  }
  
  public boolean EachActionExceptStartActionandStopActionMustHhaveAPredecessorAndSuccessor(final DiagnosticChain arg0, final Map arg1) {
    return containedObject.EachActionExceptStartActionandStopActionMustHhaveAPredecessorAndSuccessor(arg0, arg1);
  }
  
  public AbstractLoopAction getAbstractLoopAction_ResourceDemandingBehaviour() {
    return containedObject.getAbstractLoopAction_ResourceDemandingBehaviour();
  }
  
  public void setAbstractLoopAction_ResourceDemandingBehaviour(final AbstractLoopAction arg0) {
    containedObject.setAbstractLoopAction_ResourceDemandingBehaviour(arg0);
  }
  
  public boolean ExactlyOneStartAction(final DiagnosticChain arg0, final Map arg1) {
    return containedObject.ExactlyOneStartAction(arg0, arg1);
  }
  
  public boolean ExactlyOneStopAction(final DiagnosticChain arg0, final Map arg1) {
    return containedObject.ExactlyOneStopAction(arg0, arg1);
  }
  
  public EList getSteps_Behaviour() {
    return containedObject.getSteps_Behaviour();
  }
  
  public void setAbstractBranchTransition_ResourceDemandingBehaviour(final AbstractBranchTransition arg0) {
    containedObject.setAbstractBranchTransition_ResourceDemandingBehaviour(arg0);
  }
  
  public AbstractBranchTransition getAbstractBranchTransition_ResourceDemandingBehaviour() {
    return containedObject.getAbstractBranchTransition_ResourceDemandingBehaviour();
  }
  
  public String getId() {
    return containedObject.getId();
  }
  
  public void setId(final String arg0) {
    containedObject.setId(arg0);
  }
  
  public CDORevision cdoRevision(final boolean arg0) {
    return containedObject.cdoRevision(arg0);
  }
  
  public CDORevision cdoRevision() {
    return containedObject.cdoRevision();
  }
  
  public CDOState cdoState() {
    return containedObject.cdoState();
  }
  
  public CDOResource cdoDirectResource() {
    return containedObject.cdoDirectResource();
  }
  
  public CDOView cdoView() {
    return containedObject.cdoView();
  }
  
  public CDOLockState cdoLockState() {
    return containedObject.cdoLockState();
  }
  
  public CDOID cdoID() {
    return containedObject.cdoID();
  }
  
  public boolean cdoConflict() {
    return containedObject.cdoConflict();
  }
  
  public CDOLock cdoReadLock() {
    return containedObject.cdoReadLock();
  }
  
  public void cdoPrefetch(final int arg0) {
    containedObject.cdoPrefetch(arg0);
  }
  
  public CDOPermission cdoPermission() {
    return containedObject.cdoPermission();
  }
  
  public CDOLock cdoWriteLock() {
    return containedObject.cdoWriteLock();
  }
  
  public CDOObjectHistory cdoHistory() {
    return containedObject.cdoHistory();
  }
  
  public boolean cdoInvalid() {
    return containedObject.cdoInvalid();
  }
  
  public CDOLock cdoWriteOption() {
    return containedObject.cdoWriteOption();
  }
  
  public void cdoReload() {
    containedObject.cdoReload();
  }
  
  public CDOResource cdoResource() {
    return containedObject.cdoResource();
  }
  
  public EObject eContainer() {
    return containerObject;
  }
  
  public EClass eClass() {
    return containedObject.eClass();
  }
  
  public Object eInvoke(final EOperation arg0, final EList arg1) throws InvocationTargetException {
    return containedObject.eInvoke(arg0, arg1);
  }
  
  public boolean eIsSet(final EStructuralFeature arg0) {
    return containedObject.eIsSet(arg0);
  }
  
  public boolean eIsProxy() {
    return containedObject.eIsProxy();
  }
  
  public void eUnset(final EStructuralFeature arg0) {
    containedObject.eUnset(arg0);
  }
  
  public Resource eResource() {
    return containerObject.eResource();
  }
  
  public void eSet(final EStructuralFeature arg0, final Object arg1) {
    containedObject.eSet(arg0, arg1);
  }
  
  public Object eGet(final EStructuralFeature arg0, final boolean arg1) {
    return containedObject.eGet(arg0, arg1);
  }
  
  public Object eGet(final EStructuralFeature arg0) {
    return containedObject.eGet(arg0);
  }
  
  public TreeIterator eAllContents() {
    return containedObject.eAllContents();
  }
  
  public EList eContents() {
    return containedObject.eContents();
  }
  
  public EList eCrossReferences() {
    return containedObject.eCrossReferences();
  }
  
  public EReference eContainmentFeature() {
    return containedObject.eContainmentFeature();
  }
  
  public EStructuralFeature eContainingFeature() {
    return containedObject.eContainingFeature();
  }
  
  public void eNotify(final Notification arg0) {
    containedObject.eNotify(arg0);
  }
  
  public void eSetDeliver(final boolean arg0) {
    containedObject.eSetDeliver(arg0);
  }
  
  public boolean eDeliver() {
    return containedObject.eDeliver();
  }
  
  public EList eAdapters() {
    return containedObject.eAdapters();
  }
  
  public String getEntityName() {
    return containedObject.getEntityName();
  }
  
  public void setEntityName(final String arg0) {
    containedObject.setEntityName(arg0);
  }
}
