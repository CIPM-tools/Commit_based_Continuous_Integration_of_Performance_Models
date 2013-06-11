package org.splevo.ui.wizards.vpmanalysis;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.splevo.vpm.analyzer.VPMAnalyzer;
import org.splevo.vpm.analyzer.config.ConfigDefinition;

/**
 * Wizard page to select and configure the vpm analyzers to be executed.
 * 
 * @author Benjamin Klatt
 */
public class VPMAnalyzerConfigurationPage extends WizardPage {

    /** The logger for this class. */
    private Logger logger = Logger.getLogger(VPMAnalyzerConfigurationPage.class);

    /** A list viewer for configured analyzes. */
    private ListViewer listViewerAnalysis = null;

    /** A table viewer for the configurations of a selected analyzer. */
    private TableViewer configTableViewer = null;

    /** The configured analyzer instances. */
    private Set<VPMAnalyzer> analyzers = new HashSet<VPMAnalyzer>();

    /** The editing support to manipulate a configuration table cell. */
    private VPMAnalyzerConfigurationEditingSupport configEditingSupport = null;

    /**
     * Create the wizard page to let the user select the analyzes to be performed.
     */
    public VPMAnalyzerConfigurationPage() {
        super("wizardPage");
        setTitle("Analyzer Configuration");
        setDescription("Select and configure the variation point model analyzer to be executed.");
    }

    /**
     * Create contents of the wizard.
     * 
     * @param parent
     *            The parent ui element this control should be placed in.
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.FILL);

        setControl(container);

        Label lblAvailableAnalayses = new Label(container, SWT.NONE);
        lblAvailableAnalayses.setBounds(10, 10, 211, 20);
        lblAvailableAnalayses.setText("Variation Point Model Analysis");

        listViewerAnalysis = new ListViewer(container, SWT.BORDER | SWT.FILL | SWT.V_SCROLL);
        listViewerAnalysis.setContentProvider(ArrayContentProvider.getInstance());
        listViewerAnalysis.setInput(analyzers);
        listViewerAnalysis.setLabelProvider(new VPMAnalyzerLabelProvider());
        listViewerAnalysis.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {

                VPMAnalyzer analyzer = getSelectedAnalyzer();

                if (analyzer == null) {
                    configTableViewer.getTable().setEnabled(false);
                } else {
                    configTableViewer.getTable().setEnabled(true);
                    configTableViewer.setContentProvider(ArrayContentProvider.getInstance());
                    configTableViewer.setInput(analyzer.getAvailableConfigurations().entrySet());
                    configEditingSupport.setAnalyzer(analyzer);
                }
            }
        });
        List list = listViewerAnalysis.getList();
        list.setBounds(10, 34, 222, 193);

        Button btnAdd = new Button(container, SWT.NONE);
        btnAdd.addMouseListener(new VPMAnalyzerSelectionDialogListener(this));
        btnAdd.setBounds(237, 36, 90, 30);
        btnAdd.setText("add");

        Button btnRemove = new Button(container, SWT.NONE);
        btnRemove.addMouseListener(new MouseAdapter() {

            /**
             * Remove the selected VP analyzer when the button is clicked.
             * 
             * @param e
             *            The event triggered the mouse adapter
             */
            @Override
            public void mouseUp(MouseEvent e) {
                VPMAnalyzer analyzer = getSelectedAnalyzer();
                removeAnalyzer(analyzer);
            }
        });
        btnRemove.setBounds(237, 72, 90, 30);
        btnRemove.setText("remove");

        Label lblConfiguration = new Label(container, SWT.NONE);
        lblConfiguration.setText("Configuration");
        lblConfiguration.setBounds(333, 10, 211, 20);

        configTableViewer = new TableViewer(container, SWT.BORDER | SWT.FILL | SWT.FULL_SELECTION);
        Table table = configTableViewer.getTable();
        table.setEnabled(false);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setBounds(333, 34, 329, 193);

        TableViewerColumn tableViewerColumnSetting = new TableViewerColumn(configTableViewer, SWT.FILL);
        TableColumn tblclmnSetting = tableViewerColumnSetting.getColumn();
        tblclmnSetting.setWidth(129);
        tblclmnSetting.setText("Setting");
        tableViewerColumnSetting.setLabelProvider(new CellLabelProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public void update(ViewerCell cell) {
                Entry<String, ConfigDefinition> e = (Entry<String, ConfigDefinition>) cell.getElement();
                String label = getSelectedAnalyzer().getConfigurationLabels().get(e.getKey());
                if (label == null) {
                    logger.warn("Label not specified for setting " + e.getKey() + " in Analyzer "
                            + getSelectedAnalyzer());
                    cell.setText(e.getKey());
                } else {
                    cell.setText(label);
                }
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(configTableViewer, SWT.FILL);
        TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
        tblclmnValue.setWidth(187);
        tblclmnValue.setText("Value");
        tableViewerColumnValue.setLabelProvider(new CellLabelProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public void update(ViewerCell cell) {
                Entry<String, ConfigDefinition> e = (Entry<String, ConfigDefinition>) cell.getElement();
                Map<String, Object> configurations = getSelectedAnalyzer().getConfigurations();
                if (configurations.containsKey(e.getKey()) && configurations.get(e.getKey()) != null) {
                    cell.setText(configurations.get(e.getKey()).toString());
                } else if (e.getValue().getDefaultValue() != null) {
                    cell.setText(e.getValue().getDefaultValue().toString());
                } else {
                    cell.setText("");
                }
            }
        });
        configEditingSupport = new VPMAnalyzerConfigurationEditingSupport(configTableViewer);
        tableViewerColumnValue.setEditingSupport(configEditingSupport);
    }

    /**
     * Get the analyzer currently selected in the analyzer list viewer.
     * 
     * @return Returns the first selected analyzer or null if none is selected.
     */
    private VPMAnalyzer getSelectedAnalyzer() {

        VPMAnalyzer analyzer = null;

        if (listViewerAnalysis.getSelection() instanceof StructuredSelection) {
            Object selection = ((StructuredSelection) listViewerAnalysis.getSelection()).getFirstElement();
            if (selection != null) {
                analyzer = (VPMAnalyzer) selection;
            }
        } else {
            logger.error("Invalid selection type in AnalyzerSelectionPage dialog");
        }
        return analyzer;

    }

    @Override
    public boolean isPageComplete() {
        return (listViewerAnalysis.getList().getItems().length > 0);
    }

    @Override
    public boolean canFlipToNextPage() {
        return isPageComplete();
    }

    /**
     * Add an analyzer to the list of configured analyzers.
     * 
     * @param analyzer
     *            The analyzer instance to be added.
     */
    public void addAnalyzer(VPMAnalyzer analyzer) {
        this.analyzers.add(analyzer);
        listViewerAnalysis.refresh();
        getWizard().getContainer().updateButtons();
    }

    /**
     * Remove an analyzer from the list of configured analyzers.
     * 
     * @param analyzer
     *            The analyzer to be removed.
     */
    public void removeAnalyzer(VPMAnalyzer analyzer) {
        this.analyzers.remove(analyzer);
        listViewerAnalysis.refresh();
        getWizard().getContainer().updateButtons();
    }

    /**
     * Get the set of configured analyzer instances.
     * 
     * @return The list of analyzers.
     */
    public Set<VPMAnalyzer> getAnalyzers() {
        return analyzers;
    }

}
