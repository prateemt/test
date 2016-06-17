package hydrograph.ui.dataviewer.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class FilterWithTables extends Dialog {
	private static final String ROW_INDEX = "rowIndex";
	private String relationalOperators[];
	private String fieldNames[];
	private Map<String, String> fieldsAndTypes;
	private TableViewer remoteTableViewer;
	private TableViewer localTableViewer;
	private List<FilterConditions> conditions; 
	private Map<String,String[]> typeBasedConditionalOperators = new HashMap<>();
	
	public void setRelationalOperators(String[] relationalOperators) {
		this.relationalOperators = relationalOperators;
	}
	
	public void setFieldsAndTypes(Map<String, String> fieldsAndTypes) {
		this.fieldsAndTypes = fieldsAndTypes;
		fieldNames = (String[]) this.fieldsAndTypes.keySet().toArray(new String[this.fieldsAndTypes.size()]);
		Arrays.sort(fieldNames);
	}
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public FilterWithTables(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
		conditions = new ArrayList<>();
		
		typeBasedConditionalOperators.put("java.lang.String", new String[]{"like", "in", "not in"});
		typeBasedConditionalOperators.put("java.lang.Integer", new String[]{"<", "<=", ">", ">=", "!=", "="});
		typeBasedConditionalOperators.put("java.util.Date", new String[]{"<", "<=", ">", ">=", "!=", "="});
		typeBasedConditionalOperators.put("java.math.BigDecimal", new String[]{"<", "<=", ">", ">=", "!=", "="});
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
		mainComposite.setLayout(new GridLayout(1, false));
		GridData gdMainComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gdMainComposite.heightHint = 355;
		gdMainComposite.widthHint = 682;
		mainComposite.setLayoutData(gdMainComposite);
		
		TabFolder tabFolder = new TabFolder(mainComposite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createLocalTabItem(tabFolder);
		
		//createRemoteTabItem(tabFolder);
		
		return container;
	}

	private void createRemoteTabItem(TabFolder tabFolder) {
		TabItem tbtmRemote = new TabItem(tabFolder, SWT.NONE);
		tbtmRemote.setText("Remote");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmRemote.setControl(composite_1);
		composite_1.setLayout(new GridLayout(1, false));
		
		remoteTableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		Table table = remoteTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createTableColumns(remoteTableViewer, "");
		createTableColumns(remoteTableViewer, "");
		createTableColumns(remoteTableViewer, "Group");
		createTableColumns(remoteTableViewer, "Relational Operator");
		createTableColumns(remoteTableViewer, "Field Name");
		createTableColumns(remoteTableViewer, "Conditional Operator");
		createTableColumns(remoteTableViewer, "Value");
		
		final TableItem tableItem = new TableItem(table, SWT.NONE);
		
		addRow(table, remoteTableViewer, tableItem);
		
		Composite composite = new Composite(composite_1, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnOk_1 = new Button(composite, SWT.NONE);
		btnOk_1.setText("OK");
		
		Button btnCancel_1 = new Button(composite, SWT.NONE);
		btnCancel_1.setText("Cancel");
		
	}

	private void createLocalTabItem(TabFolder tabFolder) {
		TabItem tbtmLocal = new TabItem(tabFolder, SWT.NONE);
		tbtmLocal.setText("Local");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmLocal.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		
		localTableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		Table table = localTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createTableColumns(localTableViewer, "");
		createTableColumns(localTableViewer, "");
		createTableColumns(localTableViewer, "Group");
		createTableColumns(localTableViewer, "Relational Operator");
		createTableColumns(localTableViewer, "Field Name");
		createTableColumns(localTableViewer, "Conditional Operator");
		createTableColumns(localTableViewer, "Value");
		
		final TableItem tableItem = new TableItem(table, SWT.NONE);
		
		addRow(table, localTableViewer, tableItem);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnOk = new Button(composite_1, SWT.NONE);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.setText("Cancel");
		
	}
	
	private TableViewerColumn createTableColumns(TableViewer tableViewer, String columnLabel) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText(columnLabel);
		return tableViewerColumn;
	}

	private void addRow(Table table, TableViewer tableViewer, TableItem tableItem) {
		addButtonInTable(table, tableViewer, tableItem, "+", 0, false, addButtonListener());
		addButtonInTable(table, tableViewer, tableItem, "-", 1, false, removeButtonListener());
		addCheckButtonInTable(table, tableViewer, tableItem, "", 2, true, addButtonListener());
		
		addComboInTable(table, tableViewer, tableItem, "relationalOperators", 3, relationalOperators, 
				getRelationalOpSelectionListener());
		
		addComboInTable(table, tableViewer, tableItem, "fieldNames", 4, fieldNames, 
				getFieldNameSelectionListener());
		
		addComboInTable(table, tableViewer, tableItem, "conditionalOperators", 5, new String[]{}, 
				getConditionalOperatorSelectionListener());
		
		addTextBoxInTable(table, tableViewer, tableItem, "valueTextBox", 6, getTextBoxListener());
		conditions.add(new FilterConditions());
	}

	

	private void addTextBoxInTable(Table table, TableViewer tableViewer, TableItem tableItem, String textBoxName, 
			int columnIndex, Listener listener) {
		final Composite buttonPane = new Composite(table, SWT.NONE);
		buttonPane.setLayout(new FillLayout());
		final Text text = new Text(buttonPane, SWT.NONE);
		text.addListener(SWT.Verify, listener);
		text.setData(ROW_INDEX, table.indexOf(tableItem));
		tableItem.setData(textBoxName, text);
		
		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(buttonPane, tableItem, columnIndex);
		editor.layout();
	}
	
	private void addComboInTable(Table table, TableViewer tableViewer, TableItem tableItem, String comboName, int columnIndex,
			String[] relationalOperators, SelectionListener dropDownSelectionListener) {
		final Composite buttonPane = new Composite(table, SWT.NONE);
		buttonPane.setLayout(new FillLayout());
		final Combo combo = new Combo(buttonPane, SWT.NONE);
		combo.setItems(relationalOperators);
		combo.setData(ROW_INDEX, table.indexOf(tableItem));
		tableItem.setData(comboName, combo);
		combo.addSelectionListener(dropDownSelectionListener);
		
		new AutoCompleteField(combo, new ComboContentAdapter(), combo.getItems());
		
		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(buttonPane, tableItem, columnIndex);
		editor.layout();
	}

	private void addButtonInTable(Table table, TableViewer tableViewer, TableItem tableItem, String label, 
			int columnIndex, boolean checkbox, SelectionListener buttonSelectionListener) {
		final Composite buttonPane = new Composite(table, SWT.NONE);
		buttonPane.setLayout(new FillLayout());
		final Button button = new Button(buttonPane, SWT.NONE);
		button.setText(label);
		button.setData(ROW_INDEX, table.indexOf(tableItem));
		tableItem.setData(label, button);
		
		button.addSelectionListener(buttonSelectionListener);
		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(buttonPane, tableItem, columnIndex);
		editor.layout();
	}

	private void addCheckButtonInTable(Table table, TableViewer tableViewer, TableItem tableItem, String label, 
			int columnIndex, boolean checkbox, SelectionListener buttonSelectionListener) {
		final Composite buttonPane = new Composite(table, SWT.NONE);
		buttonPane.setLayout(new FillLayout());
		final Button button = new Button(buttonPane, SWT.CHECK);
		button.setText(label);
		button.setData(ROW_INDEX, table.indexOf(tableItem));
		
		
		//button.addSelectionListener(buttonSelectionListener);
		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(buttonPane, tableItem, columnIndex);
		editor.layout();
	}
	
	private  Listener getTextBoxListener() {
		Listener listener = new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				//String text = ;
				Text text = (Text)event.widget;
				int index = (int) text.getData(ROW_INDEX);
				FilterConditions filterConditions = conditions.get(index);
				String currentText = StringUtils.isNotBlank(filterConditions.getValue()) ? filterConditions.getValue() : "";
				filterConditions.setValue(currentText + event.text);
				System.out.println(filterConditions);
				System.out
						.println("FilterWithTables.getTextBoxListener().new Listener() {...}.handleEvent()" + index);
			}
		};
		return listener;
	}
	
	private SelectionListener getFieldNameSelectionListener() {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				int index = (int) source.getData(ROW_INDEX);
				FilterConditions filterConditions = conditions.get(index);
				String fieldName = source.getText();
				filterConditions.setFieldName(fieldName);
				
				if(StringUtils.isNotBlank(fieldName)){
					String fieldType = fieldsAndTypes.get(fieldName);
					TableItem item = localTableViewer.getTable().getItem(index);
					Combo conditionalCombo = (Combo) item.getData("conditionalOperators");
					conditionalCombo.setItems(typeBasedConditionalOperators.get(fieldType));
					
					System.out.println(filterConditions);
					System.out
							.println("FilterWithTables.getFieldNameSelectionListener().new SelectionListener() {...}.widgetSelected()" + index);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		};
		return listener;
	}
	
	private SelectionListener getConditionalOperatorSelectionListener() {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				int index = (int) source.getData(ROW_INDEX);
				FilterConditions filterConditions = conditions.get(index);
				filterConditions.setConditionalOperator(source.getText());
				System.out.println(filterConditions);
				System.out
						.println("FilterWithTables.getConditionalOperatorSelectionListener().new SelectionListener() {...}.widgetSelected()" + index);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		};
		return listener;
	}
	
	private SelectionListener getRelationalOpSelectionListener() {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				int index = (int) source.getData(ROW_INDEX);
				FilterConditions filterConditions = conditions.get(index);
				filterConditions.setRelationalOperator(source.getText());
				System.out.println(filterConditions);
				System.out
						.println("FilterWithTables.getRelationalOpSelectionListener().new SelectionListener() {...}.widgetSelected()" + index);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		};
		return listener;
	}
	
	private SelectionListener addButtonListener() {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.getSource();
				int data = (int) button.getData(ROW_INDEX);
				final TableItem tableItem = new TableItem(localTableViewer.getTable(), SWT.NONE, data);

				addRow(localTableViewer.getTable(), localTableViewer, tableItem);
				for (int index = 0; index < localTableViewer.getTable().getItemCount(); index++) {
					TableItem tabItem = localTableViewer.getTable().getItem(index);
					
					Button button2 = (Button) tabItem.getData("+");
					button2.setData(ROW_INDEX, index);
					System.out
							.println("FilterWithTables.addButtonListener().new SelectionListener() {...}.widgetSelected()" + index);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		};
		return listener;
	}

	private SelectionListener removeButtonListener() {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.getSource();
				int removeIndex = (int) button.getData(ROW_INDEX);
				
				TableItem tabItem = localTableViewer.getTable().getItem(removeIndex);
				((Button) tabItem.getData("+")).dispose();
				((Button) tabItem.getData("-")).dispose();
				conditions.remove(removeIndex);
				localTableViewer.getTable().redraw();
				System.out.println("FilterWithTables.removeButtonListener().new SelectionListener() {...}.widgetSelected()" + removeIndex);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		return listener;
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
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getRelationalOperator() {
			return relationalOperator;
		}
		public void setRelationalOperator(String relationalOperator) {
			this.relationalOperator = relationalOperator;
		}
		public String getConditionalOperator() {
			return conditionalOperator;
		}
		public void setConditionalOperator(String conditionalOperator) {
			this.conditionalOperator = conditionalOperator;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return "FilterConditions [fieldName=" + fieldName
					+ ", relationalOperator=" + relationalOperator
					+ ", conditionalOperator=" + conditionalOperator
					+ ", value=" + value + "]";
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		//super.createButtonsForButtonBar(parent);
	}
}
