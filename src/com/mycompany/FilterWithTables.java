package hydrograph.ui.dataviewer.window;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

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
	
	public static void main(String[] args) {
		FilterWithTables test = new FilterWithTables(Display.getDefault().getActiveShell());
		test.open();
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
			String tabName, Button okButton, Button cancelButton, 
			Composite tabFolderComposite, Composite tableContainerComposite, Composite buttonComposite, 
			GridData gdTableComposite, GridData gdButtonComposite) {
		
		tabFolderComposite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(tabFolderComposite);
		tabFolderComposite.setLayout(new GridLayout(2, false));
		
		tableContainerComposite = new Composite(tabFolderComposite, SWT.NONE);
		tableContainerComposite.setLayout(new GridLayout(1, false));
		gdTableComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gdTableComposite.heightHint = 244;
		gdTableComposite.widthHint = 640;
		tableContainerComposite.setLayoutData(gdTableComposite);
		
		tableViewer = new TableViewer(tableContainerComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("New Column");
		
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText("New TableItem");
		new Label(tabFolderComposite, SWT.NONE);
		
		buttonComposite = new Composite(tabFolderComposite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		gdButtonComposite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gdButtonComposite.heightHint = 31;
		buttonComposite.setLayoutData(gdButtonComposite);
		
		
		okButton = new Button(buttonComposite, SWT.NONE);
		okButton.setText("Ok");
		
		cancelButton = new Button(buttonComposite, SWT.NONE);
		cancelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		cancelButton.setText("Cancel");
		new Label(tabFolderComposite, SWT.NONE);
	}
}

class FilterConditions{
	private String fieldName;
	private String relationalOperator;
	private String conditionalOperator;
	private String value;
}
