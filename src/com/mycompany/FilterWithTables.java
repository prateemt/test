package com.bitwise.app.graph.propertywindow;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;

public class FilterWithTables extends Dialog {
	private TabItem remoteTabItem;
	private TableViewer remoteTableViewer;
	private Table remoteTable;
	private Button remoteOkButton;
	private Button remoteCancelButton;
	private GridData gdRemoteTableComposite; 
	private GridData gdRemoteButtonComposite;
	private Composite remoteButtonComposite;
	private Composite remoteTableContainerComposite;
	private Composite remoteTabFolderComposite;
	
	private TabItem localTabItem;
	private TableViewer localTableViewer;
	private Table localTable;
	private Button localOkButton;
	private Button localCancelButton;
	private GridData gdLocalTableComposite; 
	private GridData gdLocalButtonComposite;
	private Composite localButtonComposite;
	private Composite localTableContainerComposite;
	private Composite localTabFolderComposite;
	private CellEditor[] editors;
	
	public static void main(String[] args) {
		FilterWithTables test = new FilterWithTables(Display.getDefault().getActiveShell());
		test.open();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		//super.createButtonsForButtonBar(parent);
	}
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public FilterWithTables(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		parent.getShell().setText("Viewer");
		container.setLayout(new GridLayout(1, false));
		
		Composite mainComposite = new Composite(container, SWT.NONE);
		GridData gdMainComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gdMainComposite.heightHint = 355;
		gdMainComposite.widthHint = 682;
		mainComposite.setLayoutData(gdMainComposite);
		
		TabFolder tabFolder = new TabFolder(mainComposite, SWT.NONE);
		tabFolder.setBounds(10, 10, 662, 335);
		
		remoteTabItem = new TabItem(tabFolder, SWT.NONE);
		remoteTabItem.setText("Remote");
		
		createTabItem(tabFolder, remoteTabItem, remoteTable, remoteTableViewer, "Remote", remoteOkButton, remoteCancelButton,
				remoteTabFolderComposite, remoteTableContainerComposite, remoteButtonComposite, gdRemoteTableComposite, gdRemoteButtonComposite);
		
		localTabItem = new TabItem(tabFolder, SWT.NONE);
		localTabItem.setText("Local");
		
		createTabItem(tabFolder, localTabItem, localTable, localTableViewer, "Local", localOkButton, localCancelButton,
				localTabFolderComposite, localTableContainerComposite, localButtonComposite, gdLocalTableComposite, gdLocalButtonComposite);
		
		return container;
	}
	
	private void createTabItem(TabFolder tabFolder, TabItem tabItem, Table table, TableViewer tableViewer, 
			String tabName, Button okButton, Button cancelButton, Composite tabFolderComposite,
			Composite tableContainerComposite, Composite buttonComposite, GridData gdTableComposite, GridData gdButtonComposite) {
		
		tabFolderComposite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(tabFolderComposite);
		tabFolderComposite.setLayout(new GridLayout(2, false));
		
		tableContainerComposite = new Composite(tabFolderComposite, SWT.NONE);
		tableContainerComposite.setLayout(new GridLayout(1, false));
		gdTableComposite = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gdTableComposite.heightHint = 232;
		gdTableComposite.widthHint = 640;
		tableContainerComposite.setLayoutData(gdTableComposite);
		
		tableViewer = new TableViewer(tableContainerComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		new Label(tabFolderComposite, SWT.NONE);
		
		buttonComposite = new Composite(tabFolderComposite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		gdButtonComposite = new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1);
		gdButtonComposite.heightHint = 31;
		buttonComposite.setLayoutData(gdButtonComposite);
		
		
		getButton(okButton, "Ok", buttonComposite);
		getButton(cancelButton, "Cancel", buttonComposite);
	}

	private void getButton(Button button, String buttonName, Composite buttonComposite){
		button = new Button(buttonComposite, SWT.NONE);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		button.setText(buttonName);
	}
	
	class FilterConditions{
		private String fieldName;
		private String relationalOperator;
		private String conditionalOperator;
		private String value;
	}
}
