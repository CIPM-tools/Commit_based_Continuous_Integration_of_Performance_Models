package mir.reactions.reactionsJavaToPcm.packageMappingIntegration;

import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.Parameter;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.compound.CreateAndInsertNonRoot;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;

@SuppressWarnings("all")
class CreateMetodParameterEventReaction extends AbstractReactionRealization {
  public void executeReaction(final EChange change) {
    InsertEReference<org.emftext.language.java.members.Method, org.emftext.language.java.parameters.Parameter> typedChange = ((CreateAndInsertNonRoot<org.emftext.language.java.members.Method, org.emftext.language.java.parameters.Parameter>)change).getInsertChange();
    org.emftext.language.java.members.Method affectedEObject = typedChange.getAffectedEObject();
    EReference affectedFeature = typedChange.getAffectedFeature();
    org.emftext.language.java.parameters.Parameter newValue = typedChange.getNewValue();
    mir.routines.packageMappingIntegration.RoutinesFacade routinesFacade = new mir.routines.packageMappingIntegration.RoutinesFacade(this.executionState, this);
    mir.reactions.reactionsJavaToPcm.packageMappingIntegration.CreateMetodParameterEventReaction.ActionUserExecution userExecution = new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.CreateMetodParameterEventReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(affectedEObject, affectedFeature, newValue, routinesFacade);
  }
  
  public static Class<? extends EChange> getExpectedChangeType() {
    return CreateAndInsertNonRoot.class;
  }
  
  private boolean checkChangeProperties(final EChange change) {
    InsertEReference<org.emftext.language.java.members.Method, org.emftext.language.java.parameters.Parameter> relevantChange = ((CreateAndInsertNonRoot<org.emftext.language.java.members.Method, org.emftext.language.java.parameters.Parameter>)change).getInsertChange();
    if (!(relevantChange.getAffectedEObject() instanceof org.emftext.language.java.members.Method)) {
    	return false;
    }
    if (!relevantChange.getAffectedFeature().getName().equals("parameters")) {
    	return false;
    }
    if (!(relevantChange.getNewValue() instanceof org.emftext.language.java.parameters.Parameter)) {
    	return false;
    }
    return true;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (!(change instanceof CreateAndInsertNonRoot)) {
    	return false;
    }
    getLogger().debug("Passed change type check of reaction " + this.getClass().getName());
    if (!checkChangeProperties(change)) {
    	return false;
    }
    getLogger().debug("Passed change properties check of reaction " + this.getClass().getName());
    getLogger().debug("Passed complete precondition check of reaction " + this.getClass().getName());
    return true;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final Method affectedEObject, final EReference affectedFeature, final Parameter newValue, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.createdMethodParameterEvent(affectedEObject, newValue);
    }
  }
}
