package tools.vitruv.applications.pcmjava.seffstatements.code2seff

import org.emftext.language.java.members.Method
import org.palladiosimulator.pcm.repository.BasicComponent
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding
import tools.vitruv.domains.pcm.PcmDomainProvider
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference
import tools.vitruv.framework.correspondence.CorrespondenceModel
import tools.vitruv.framework.userinteraction.UserInteractor
import tools.vitruv.framework.propagation.impl.AbstractChangePropagationSpecification
import tools.vitruv.framework.propagation.ResourceAccess
import tools.vitruv.framework.change.echange.EChange
import tools.vitruv.applications.pcmjava.commitintegration.propagation.OldMethodAdapter
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy
import tools.vitruv.domains.java.JavaDomain
import tools.vitruv.domains.pcm.PcmDomain
import tools.vitruv.domains.java.JavaDomainProvider

class Java2PcmMethodBodyChangePreprocessor extends AbstractChangePropagationSpecification {
	val Code2SeffFactory code2SeffFactory;
	
	new(Code2SeffFactory code2SEFFfactory) {
		this(code2SEFFfactory, new JavaDomainProvider().domain, new PcmDomainProvider().domain);
	}
	
	new(Code2SeffFactory code2SEFFfactory, JavaDomain sourceDomain, PcmDomain targetDomain) {
		super(sourceDomain, targetDomain)
		this.code2SeffFactory = code2SEFFfactory
	}

	override propagateChange(EChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		if (doesHandleChange(change, correspondenceModel)) {
			val insertionChange = change as InsertEReference<?, ?>;
			executeClassMethodBodyChangeRefiner(correspondenceModel, userInteractor, insertionChange);
		}
	}

	override doesHandleChange(EChange change, CorrespondenceModel correspondenceModel) {
		if (!(change instanceof InsertEReference)) {
			return false;
		} 
		
		val insertionChange = change as InsertEReference<?, ?>;
		return insertionChange.newValue instanceof Method
	}

	private def void executeClassMethodBodyChangeRefiner(CorrespondenceModel correspondenceModel,
		UserInteractor userInteracting, InsertEReference<?, ?> insertChange) {
		val newMethod = insertChange.newValue as Method;
		val oldAdapter = newMethod.eAdapters.filter(OldMethodAdapter).last
		newMethod.eAdapters.remove(oldAdapter)
		val oldMethod = oldAdapter?.oldMethod
		val basicComponentFinding = code2SeffFactory.createBasicComponentFinding
		val BasicComponent myBasicComponent = basicComponentFinding.findBasicComponentForMethod(newMethod,
			correspondenceModel);
		val classification = code2SeffFactory.createAbstractFunctionClassificationStrategy(basicComponentFinding,
			correspondenceModel, myBasicComponent);
		val InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory = code2SeffFactory.
			createInterfaceOfExternalCallFindingFactory(correspondenceModel, myBasicComponent);
		val ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding = code2SeffFactory.
			createResourceDemandingBehaviourForClassMethodFinding(correspondenceModel);
		val ClassMethodBodyChangedTransformation methodBodyChanged = createTransformation(
			oldMethod, newMethod, basicComponentFinding, classification, interfaceOfExternalCallFinderFactory,
			resourceDemandingBehaviourForClassMethodFinding);
		methodBodyChanged.execute(correspondenceModel, userInteracting);
	}
	
	protected def ClassMethodBodyChangedTransformation createTransformation(Method oldMethod, Method newMethod,
		BasicComponentFinding basicComponentFinding, AbstractFunctionClassificationStrategy classification,
		InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory,
		ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		return new ClassMethodBodyChangedTransformation(oldMethod, newMethod, basicComponentFinding, classification,
			interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding)
	}
}
