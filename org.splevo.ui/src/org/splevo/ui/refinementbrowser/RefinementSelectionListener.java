package org.splevo.ui.refinementbrowser;


import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.splevo.vpm.refinement.Refinement;

public class RefinementSelectionListener implements ISelectionChangedListener {

	/** The composite to present the Refinement details in. */
	private RefinementDetailsView refinementDetailView;


	/**
	 * Constructor to set the required references.
	 * 
	 * @param refinementDetailsView
	 *            The refinement area to present the details in.
	 * @param input
	 *            The input of the browser.
	 */
	public RefinementSelectionListener(RefinementDetailsView refinementDetailsView,
			VPMRefinementBrowserInput input, FormToolkit toolkit) {
		this.refinementDetailView = refinementDetailsView;
	}

	
	/**
	 * Process the event of a selected refinement. 
	 * Opens the detailed information about the refinement in 
	 * the details area.
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		
		
		Object selectedElement = ((StructuredSelection) event.getSelection()).getFirstElement();
		
		// break if the event is not about an refinement
		if (!(selectedElement instanceof Refinement)) {
			return;
		}

		Refinement refinement = (Refinement) selectedElement;
		
		refinementDetailView.showRefinement(refinement);
	}
}
