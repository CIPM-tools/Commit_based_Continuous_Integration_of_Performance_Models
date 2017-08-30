package mir.reactions.reactionsJavaToPcm.packageMappingIntegration;

import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.compound.RemoveAndDeleteNonRoot;
import tools.vitruv.framework.change.echange.feature.reference.RemoveEReference;

@SuppressWarnings("all")
class RemoveMethodEventReaction extends AbstractReactionRealization {
  public void executeReaction(final EChange change) {
    RemoveEReference<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member> typedChange = ((RemoveAndDeleteNonRoot<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member>)change).getRemoveChange();
    org.emftext.language.java.classifiers.ConcreteClassifier affectedEObject = typedChange.getAffectedEObject();
    EReference affectedFeature = typedChange.getAffectedFeature();
    org.emftext.language.java.members.Member oldValue = typedChange.getOldValue();
    mir.routines.packageMappingIntegration.RoutinesFacade routinesFacade = new mir.routines.packageMappingIntegration.RoutinesFacade(this.executionState, this);
    mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveMethodEventReaction.ActionUserExecution userExecution = new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveMethodEventReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(affectedEObject, affectedFeature, oldValue, routinesFacade);
  }
  
  public static Class<? extends EChange> getExpectedChangeType() {
    return RemoveAndDeleteNonRoot.class;
  }
  
  private boolean checkChangeProperties(final EChange change) {
    RemoveEReference<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member> relevantChange = ((RemoveAndDeleteNonRoot<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member>)change).getRemoveChange();
    if (!(relevantChange.getAffectedEObject() instanceof org.emftext.language.java.classifiers.ConcreteClassifier)) {
    	return false;
    }
    if (!relevantChange.getAffectedFeature().getName().equals("members")) {
    	return false;
    }
    if (!(relevantChange.getOldValue() instanceof org.emftext.language.java.members.Member)) {
    	return false;
    }
    return true;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (!(change instanceof RemoveAndDeleteNonRoot)) {
    	return false;
    }
    getLogger().debug("Passed change type check of reaction " + this.getClass().getName());
    if (!checkChangeProperties(change)) {
    	return false;
    }
    getLogger().debug("Passed change properties check of reaction " + this.getClass().getName());
    RemoveEReference<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member> typedChange = ((RemoveAndDeleteNonRoot<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member>)change).getRemoveChange();
    org.emftext.language.java.classifiers.ConcreteClassifier affectedEObject = typedChange.getAffectedEObject();
    EReference affectedFeature = typedChange.getAffectedFeature();
    org.emftext.language.java.members.Member oldValue = typedChange.getOldValue();
    if (!checkUserDefinedPrecondition(affectedEObject, affectedFeature, oldValue)) {
    	return false;
    }
    getLogger().debug("Passed complete precondition check of reaction " + this.getClass().getName());
    return true;
  }
  
  private boolean checkUserDefinedPrecondition(final ConcreteClassifier affectedEObject, final EReference affectedFeature, final Member oldValue) {
    if ((oldValue instanceof Method)) {
      return true;
    }
    return false;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final ConcreteClassifier affectedEObject, final EReference affectedFeature, final Member oldValue, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.removedMethodEvent(((Method) oldValue));
    }
  }
}
