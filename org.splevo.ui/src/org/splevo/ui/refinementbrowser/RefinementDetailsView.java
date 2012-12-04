package org.splevo.ui.refinementbrowser;

import org.eclipse.gmt.modisco.java.ASTNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.splevo.vpm.refinement.Refinement;
import org.splevo.vpm.variability.Variant;
import org.splevo.vpm.variability.VariationPoint;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class RefinementDetailsView extends Composite {
	
	private TreeViewer variationPointTreeViewer;

	public RefinementDetailsView(Composite parent) {
		super(parent, SWT.FILL);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		variationPointTreeViewer = new TreeViewer(this, SWT.BORDER);
		variationPointTreeViewer.setLabelProvider(new ViewerLabelProvider());
		variationPointTreeViewer.setContentProvider(new TreeContentProvider());
		
		initContextMenu();
	}

	/**
	 * Create contents of the details page.
	 * 
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setEnabled(true);

	}

	public void showRefinement(Refinement refinement) {
		variationPointTreeViewer.setInput(refinement);
	}
	
	/**
	 * The content provider for the tree providing access to
	 * the variation points and their child elements
	 */
	private static class TreeContentProvider implements ITreeContentProvider {
		
		private Refinement refinement = null;
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.refinement = (Refinement) newInput;
		}
		public Object[] getElements(Object inputElement) {
			return refinement.getVariationPoints().toArray();
		}
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof Refinement) {
				return ((Refinement) parentElement).getVariationPoints().toArray();
			} else if(parentElement instanceof VariationPoint) {
				return ((VariationPoint) parentElement).getVariants().toArray();
			} else if(parentElement instanceof Variant) {
				return ((Variant) parentElement).getSoftwareEntities().toArray();
			} else {
				return null;
			}
		}
		public Object getParent(Object element) {
			return null;
		}
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
		
		public void dispose() {}
	}
	
	/**
	 * Label Provider for a variation point element.
	 */
	private static class ViewerLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			return super.getImage(element);
		}
		
		/**
		 * Get the text label for a supplied element.
		 */
		public String getText(Object element) {
			if(element instanceof VariationPoint){
				return "VariationPoint in "+((VariationPoint) element).getSoftwareEntity().getClass().getSimpleName();
			} else if(element instanceof Variant){
				return "Variant: "+((Variant) element).getVariantId();
			} else if(element instanceof ASTNode){
				ASTNode astNode = (ASTNode) element;
				return astNode.getClass().getSimpleName()+"("+astNode.getOriginalCompilationUnit().getName()+")";
			} else {
				return super.getText(element);
			}
		}
	}
	
	private void initContextMenu() {
		// initalize the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				Action action = new Action() {
					public void run() {
						super.run();
					}
				};
				action.setText("Open Source in Editor");
				manager.add(action);
			}
		});
		Menu menu = menuMgr.createContextMenu(variationPointTreeViewer.getTree());
		variationPointTreeViewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuMgr, variationPointTreeViewer);
	}

	/**
	 * Get the active site to work with.
	 * @return The active site.
	 */
	private IWorkbenchPartSite getSite() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite();
	}
}
