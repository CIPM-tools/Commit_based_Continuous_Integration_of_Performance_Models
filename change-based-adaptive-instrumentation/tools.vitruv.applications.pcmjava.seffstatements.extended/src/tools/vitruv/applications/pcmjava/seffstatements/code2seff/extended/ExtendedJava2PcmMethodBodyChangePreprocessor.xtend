package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended

import org.emftext.language.java.members.Method
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Java2PcmMethodBodyChangePreprocessor
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.ClassMethodBodyChangedTransformation
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding
import tools.vitruv.domains.pcm.PcmDomainProvider
import cipm.consistency.domains.java.AdjustedJavaDomainProvider

class ExtendedJava2PcmMethodBodyChangePreprocessor extends Java2PcmMethodBodyChangePreprocessor {

	new() {
		super(new CommitIntegrationCodeToSeffFactory, new AdjustedJavaDomainProvider().domain, new PcmDomainProvider().domain)
	}

	protected override ClassMethodBodyChangedTransformation createTransformation(Method newMethod,
		BasicComponentFinding basicComponentFinding, AbstractFunctionClassificationStrategy classification,
		InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory,
		ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		return new ExtendedClassMethodBodyChangedTransformation(newMethod, basicComponentFinding,
			classification, interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding)
	}
}
