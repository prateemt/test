package com.bitwise.app.graph.propertywindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
	private static final String VALUE_TEXT_BOX = "valueTextBox";
	private static final String CONDITIONAL_OPERATORS = "conditionalOperators";
	private static final String FIELD_NAMES = "fieldNames";
	private static final String RELATIONAL_OPERATORS = "relationalOperators";
	private static final String REMOVE = "-";
	private static final String ADD = "+";
	private static final String GROUP_CHECKBOX = "groupCheckBox";
	private static final String ROW_INDEX = "rowIndex";
	
	private static final String ADD_BUTTON_PANE = "addButtonPane";
	private static final String REMOVE_BUTTON_PANE = "removeButtonPane";
	private static final String GROUP_CHECKBOX_PANE = "groupCheckBoxPane";
	private static final String RELATIONAL_COMBO_PANE = "relationalComboPane";
	private static final String FIELD_COMBO_PANE = "fieldComboPane";
	private static final String CONDITIONAL_COMBO_PANE = "conditionalComboPane";
	private static final String VALUE_TEXT_PANE = "valueTextPane";
	
	private static final String ADD_EDITOR = "add_editor";
	private static final String REMOVE_EDITOR = "remove_editor";
	private static final String GROUP_EDITOR = "group_editor";
	private static final String RELATIONAL_EDITOR = "relational_editor";
	private static final String FIELD_EDITOR = "field_editor";
	private static final String CONDITIONAL_EDITOR = "conditional_editor";
	private static final String VALUE_EDITOR = "vale_editor";
	
	private Map<String,String[]> typeBasedConditionalOperators = new HashMap<>();
	
	private String relationalOperators[];
	private String fieldNames[];
	private Map<String, String> fieldsAndTypes;
	private TableViewer remoteTableViewer;
	private TableViewer localTableViewer;
	
	private List<Condition> localConditionsList; 
	private List<Condition> remoteConditionsList; 
	
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
		localConditionsList = new ArrayList<>();
		remoteConditionsList = new ArrayList<>();
		
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
		
		createLocalTabItem(tabFolder, localTableViewer);
		createRemoteTabItem(tabFolder, remoteTableViewer);
		
		return container;
	}


	private void createRemoteTabItem(TabFolder tabFolder, TableViewer tableViewer) {
		TabItem tbtmLocal = new TabItem(tabFolder, SWT.NONE);
		tbtmLocal.setText("Remote");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmLocal.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		tableViewer.setContentProvider(new ArrayContentProvider());
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		TableViewerColumn addButtonTableViewerColumn = createTableColumns(tableViewer, "");
		addButtonTableViewerColumn.setLabelProvider(getAddButtonCellProvider(tableViewer, remoteConditionsList));
		
		TableViewerColumn removeButtonTableViewerColumn = createTableColumns(tableViewer, "");
		removeButtonTableViewerColumn.setLabelProvider(getRemoveButtonCellProvider(tableViewer, remoteConditionsList));
		
		TableViewerColumn groupButtonTableViewerColumn = createTableColumns(tableViewer, "Group");
		groupButtonTableViewerColumn.setLabelProvider(getGroupCheckCellProvider(tableViewer, remoteConditionsList));
		
		TableViewerColumn relationalDropDownColumn = createTableColumns(tableViewer, "Relational Operator");
		relationalDropDownColumn.setLabelProvider(getRelationalCellProvider(tableViewer, remoteConditionsList));
		
		
		TableViewerColumn fieldNameDropDownColumn = createTableColumns(tableViewer, "Field Name");
		fieldNameDropDownColumn.setLabelProvider(getFieldNamecellProvider(tableViewer, remoteConditionsList));
		
		TableViewerColumn conditionalDropDownColumn = createTableColumns(tableViewer, "Conditional Operator");
		conditionalDropDownColumn.setLabelProvider(getConditionalCellProvider(tableViewer, remoteConditionsList));
		
		TableViewerColumn valueTextBoxColumn = createTableColumns(tableViewer, "Value");
		valueTextBoxColumn.setLabelProvider(getValueCellProvider(tableViewer, remoteConditionsList));
		
		tableViewer.setInput(remoteConditionsList);
		remoteConditionsList.add(0, new Condition());
		tableViewer.refresh();
		
		
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnOk = new Button(buttonComposite, SWT.NONE);
		btnOk.setText("OK");
		btnOk.addSelectionListener(getOkButtonListener(remoteConditionsList));
		
		Button btnCancel = new Button(buttonComposite, SWT.NONE);
		btnCancel.setText("Cancel");
		
	}

	private void createLocalTabItem(TabFolder tabFolder, TableViewer tableViewer) {
		TabItem tbtmLocal = new TabItem(tabFolder, SWT.NONE);
		tbtmLocal.setText("Local");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmLocal.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		tableViewer.setContentProvider(new ArrayContentProvider());
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		TableViewerColumn addButtonTableViewerColumn = createTableColumns(tableViewer, "");
		addButtonTableViewerColumn.setLabelProvider(getAddButtonCellProvider(tableViewer, localConditionsList));
		
		TableViewerColumn removeButtonTableViewerColumn = createTableColumns(tableViewer, "");
		removeButtonTableViewerColumn.setLabelProvider(getRemoveButtonCellProvider(tableViewer, localConditionsList));
		
		TableViewerColumn groupButtonTableViewerColumn = createTableColumns(tableViewer, "Group");
		groupButtonTableViewerColumn.setLabelProvider(getGroupCheckCellProvider(tableViewer, localConditionsList));
		
		TableViewerColumn relationalDropDownColumn = createTableColumns(tableViewer, "Relational Operator");
		relationalDropDownColumn.setLabelProvider(getRelationalCellProvider(tableViewer, localConditionsList));
		
		
		TableViewerColumn fieldNameDropDownColumn = createTableColumns(tableViewer, "Field Name");
		fieldNameDropDownColumn.setLabelProvider(getFieldNamecellProvider(tableViewer, localConditionsList));
		
		TableViewerColumn conditionalDropDownColumn = createTableColumns(tableViewer, "Conditional Operator");
		conditionalDropDownColumn.setLabelProvider(getConditionalCellProvider(tableViewer, localConditionsList));
		
		TableViewerColumn valueTextBoxColumn = createTableColumns(tableViewer, "Value");
		valueTextBoxColumn.setLabelProvider(getValueCellProvider(tableViewer, localConditionsList));
		
		tableViewer.setInput(localConditionsList);
		localConditionsList.add(0, new Condition());
		tableViewer.refresh();
		
		
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Button btnOk = new Button(buttonComposite, SWT.NONE);
		btnOk.setText("OK");
		btnOk.addSelectionListener(getOkButtonListener(localConditionsList));
		
		Button btnCancel = new Button(buttonComposite, SWT.NONE);
		btnCancel.setText("Cancel");
		
	}

	private CellLabelProvider getValueCellProvider(final TableViewer tableViewer, final List<Condition> conditionsList) {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				// DO NOT REMOVE THIS CONDITION. The condition is return to
				// prevent multiple updates on single item
				if (item.getData("UPDATED7") == null) {
					item.setData("UPDATED7", "TRUE");
				} else {
					return;
				}
				addTextBoxInTable(tableViewer, item, VALUE_TEXT_BOX, VALUE_TEXT_PANE, VALUE_EDITOR, cell.getColumnIndex(), getTextBoxListener(conditionsList));
				item.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						Text valueText = (Text) item.getData(VALUE_TEXT_BOX);
						((TableEditor)valueText.getData(VALUE_EDITOR)).dispose();
						valueText.dispose();
						
						Composite composite = (Composite)item.getData(VALUE_TEXT_PANE);
						composite.dispose();
					}
				});
			}
		};
	}

	private CellLabelProvider getConditionalCellProvider(final TableViewer tableViewer, final List<Condition> conditionsList) {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				// DO NOT REMOVE THIS CONDITION. The condition is return to
				// prevent multiple updates on single item
				if (item.getData("UPDATED6") == null) {
					item.setData("UPDATED6", "TRUE");
				} else {
					return;
				}
				addComboInTable(tableViewer, item, CONDITIONAL_OPERATORS, CONDITIONAL_COMBO_PANE, CONDITIONAL_EDITOR, cell.getColumnIndex(), new String[]{}, 
						getConditionalOperatorSelectionListener(conditionsList));
				item.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						Combo combo = (Combo) item.getData(CONDITIONAL_OPERATORS);
						((TableEditor)combo.getData(CONDITIONAL_EDITOR)).dispose();
						combo.dispose();
						
						Composite composite = (Composite)item.getData(CONDITIONAL_COMBO_PANE);
						composite.dispose();
					}
				});
			}
		};
	}

	private CellLabelProvider getFieldNamecellProvider(final TableViewer tableViewer, final List<Condition> conditionsList) {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				// DO NOT REMOVE THIS CONDITION. The condition is return to
				// prevent multiple updates on single item
				if (item.getData("UPDATED5") == null) {
					item.setData("UPDATED5", "TRUE");
				} else {
					return;
				}
				addComboInTable(tableViewer, item, FIELD_NAMES, FIELD_COMBO_PANE, FIELD_EDITOR, cell.getColumnIndex(), fieldNames, 
						getFieldNameSelectionListener(tableViewer, conditionsList));
				item.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						Combo combo = (Combo) item.getData(FIELD_NAMES);
						((TableEditor)combo.getData(FIELD_EDITOR)).dispose();
						combo.dispose();
						
						Composite composite = (Composite)item.getData(FIELD_COMBO_PANE);
						composite.dispose();
					}
				});
			}
		};
	}

	private CellLabelProvider getRelationalCellProvider(final TableViewer tableViewer, final List<Condition> conditionsList) {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				// DO NOT REMOVE THIS CONDITION. The condition is return to
				// prevent multiple updates on single item
				if (item.getData("UPDATED4") == null) {
					item.setData("UPDATED4", "TRUE");
				} else {
					return;
				}
				addComboInTable(tableViewer, item, RELATIONAL_OPERATORS, RELATIONAL_COMBO_PANE, RELATIONAL_EDITOR, cell.getColumnIndex(), relationalOperators, 
						getRelationalOpSelectionListener(conditionsList));
				Combo combo = (Combo) item.getData(RELATIONAL_OPERATORS);
				if(tableViewer.getTable().indexOf(item) == 0){
					combo.setVisible(false);
				}
				else {
					combo.setVisible(true);
				}
				item.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						Combo combo = (Combo) item.getData(RELATIONAL_OPERATORS);
						((TableEditor)combo.getData(RELATIONAL_EDITOR)).dispose();
						combo.dispose();
						
						Composite composite = (Composite)item.getData(RELATIONAL_COMBO_PANE);
						composite.dispose();
					}
				});
			}
		};
	}

	private CellLabelProvider getGroupCheckCellProvider(final TableViewer tableViewer, final List<Condition> conditionsList) {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				// DO NOT REMOVE THIS CONDITION. The condition is return to
				// prevent multiple updates on single item
				if (item.getData("UPDATED3") == null) {
					item.setData("UPDATED3", "TRUE");
				} else {
					return;
				}
				addCheckButtonInTable(tableViewer, item, GROUP_CHECKBOX, GROUP_CHECKBOX_PANE, GROUP_EDITOR, cell.getColumnIndex(), 
						removeButtonListener(tableViewer, conditionsList));
				item.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						Button groupButton = (Button) item.getData(GROUP_CHECKBOX);
						((TableEditor)groupButton.getData(GROUP_EDITOR)).dispose();
						groupButton.dispose();
						
						Composite composite = (Composite)item.getData(GROUP_CHECKBOX_PANE);
						composite.dispose();
					}
				});
			}
		};
	}

	private CellLabelProvider getRemoveButtonCellProvider(final TableViewer tableViewer, final List<Condition> conditionsList) {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				// DO NOT REMOVE THIS CONDITION. The condition is return to
				// prevent multiple updates on single item
				if (item.getData("UPDATED2") == null) {
					item.setData("UPDATED2", "TRUE");
				} else {
					return;
				}
				addButtonInTable(tableViewer, item, REMOVE, REMOVE_BUTTON_PANE, REMOVE_EDITOR, cell.getColumnIndex(), removeButtonListener(tableViewer, conditionsList));
				item.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						Button removeButton = (Button) item.getData(REMOVE);
						((TableEditor)removeButton.getData(REMOVE_EDITOR)).dispose();
						removeButton.dispose();
						
						Composite composite = (Composite)item.getData(REMOVE_BUTTON_PANE);
						composite.dispose();
					}
				});
			}
		};
	}

	private CellLabelProvider getAddButtonCellProvider(final TableViewer tableViewer, final List<Condition> conditionsList) {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				// DO NOT REMOVE THIS CONDITION. The condition is return to
				// prevent multiple updates on single item
				if (item.getData("UPDATED1") == null) {
					item.setData("UPDATED1", "TRUE");
				} else {
					return;
				}
				addButtonInTable(tableViewer, item, ADD, ADD_BUTTON_PANE, ADD_EDITOR, cell.getColumnIndex(), addButtonListener(tableViewer,conditionsList));
				item.addDisposeListener(new DisposeListener() {
					
					@Override
					public void widgetDisposed(DisposeEvent e) {
						Button addButton = (Button) item.getData(ADD);
						((TableEditor)addButton.getData(ADD_EDITOR)).dispose();
						addButton.dispose();
						
						Composite composite = (Composite)item.getData(ADD_BUTTON_PANE);
						composite.dispose();
					}
				});
			}
			
		};
	}
	
	private TableViewerColumn createTableColumns(TableViewer tableViewer, String columnLabel) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(100);
		tableColumn.setText(columnLabel);
		return tableViewerColumn;
	}

	private void addTextBoxInTable(TableViewer tableViewer, TableItem tableItem, String textBoxName, 
			String valueTextPane, String editorName, int columnIndex, Listener listener) {
		final Composite buttonPane = new Composite(tableViewer.getTable(), SWT.NONE);
		buttonPane.setLayout(new FillLayout());
		final Text text = new Text(buttonPane, SWT.NONE);
		text.addListener(SWT.Verify, listener);
		text.setData(ROW_INDEX, tableViewer.getTable().indexOf(tableItem));
		tableItem.setData(textBoxName, text);
		tableItem.setData(valueTextPane, buttonPane);
		
		final TableEditor editor = new TableEditor(tableViewer.getTable());
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(buttonPane, tableItem, columnIndex);
		editor.layout();
		text.setData(editorName, editor);
	}
	
	private void addComboInTable(TableViewer tableViewer, TableItem tableItem, String comboName, String comboPaneName, String editorName, int columnIndex,
			String[] relationalOperators, SelectionListener dropDownSelectionListener) {
		final Composite buttonPane = new Composite(tableViewer.getTable(), SWT.NONE);
		buttonPane.setLayout(new FillLayout());
		final Combo combo = new Combo(buttonPane, SWT.NONE);
		combo.setItems(relationalOperators);
		combo.setData(ROW_INDEX, tableViewer.getTable().indexOf(tableItem));
		tableItem.setData(comboName, combo);
		tableItem.setData(comboPaneName, buttonPane);
		combo.addSelectionListener(dropDownSelectionListener);
		
		new AutoCompleteField(combo, new ComboContentAdapter(), combo.getItems());
		
		final TableEditor editor = new TableEditor(tableViewer.getTable());
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(buttonPane, tableItem, columnIndex);
		editor.layout();
		combo.setData(editorName, editor);
	}

	private void addButtonInTable(TableViewer tableViewer, TableItem tableItem, String columnName, 
			String buttonPaneName, String editorName, int columnIndex, SelectionListener buttonSelectionListener) {
		final Composite buttonPane = new Composite(tableViewer.getTable(), SWT.NONE);
		buttonPane.setLayout(new FillLayout());
		final Button button = new Button(buttonPane, SWT.NONE);
		button.setText(columnName);
		button.setData(ROW_INDEX, tableViewer.getTable().indexOf(tableItem));
		tableItem.setData(columnName, button);
		tableItem.setData(buttonPaneName, buttonPane);
		
		button.addSelectionListener(buttonSelectionListener);
		final TableEditor editor = new TableEditor(tableViewer.getTable());
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(buttonPane, tableItem, columnIndex);
		editor.layout();
		button.setData(editorName, editor);
	}

	private void addCheckButtonInTable(TableViewer tableViewer, TableItem tableItem, String columnName, 
			String groupPaneName, String editorName, int columnIndex, SelectionListener buttonSelectionListener) {
		final Composite buttonPane = new Composite(tableViewer.getTable(), SWT.NONE);
		buttonPane.setLayout(new FillLayout());
		final Button button = new Button(buttonPane, SWT.CHECK);
		button.setData(ROW_INDEX, tableViewer.getTable().indexOf(tableItem));
		tableItem.setData(columnName, button);
		tableItem.setData(groupPaneName, buttonPane);
		
		//button.addSelectionListener(buttonSelectionListener);
		final TableEditor editor = new TableEditor(tableViewer.getTable());
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(buttonPane, tableItem, columnIndex);
		editor.layout();
		button.setData(editorName, editor);
	}
	
	private  Listener getTextBoxListener(final List<Condition> conditionsList) {
		Listener listener = new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				//String text = ;
				Text text = (Text)event.widget;
				int index = (int) text.getData(ROW_INDEX);
				Condition filterConditions = conditionsList.get(index);
				String currentText = StringUtils.isNotBlank(filterConditions.getValue()) ? filterConditions.getValue() : "";
				filterConditions.setValue(currentText + event.text);
				System.out.println(filterConditions);
				System.out
						.println("FilterWithTables.getTextBoxListener().new Listener() {...}.handleEvent()" + index);
			}
		};
		return listener;
	}
	
	private SelectionListener getFieldNameSelectionListener(final TableViewer tableViewer, final List<Condition> conditionsList) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				int index = (int) source.getData(ROW_INDEX);
				Condition filterConditions = conditionsList.get(index);
				String fieldName = source.getText();
				filterConditions.setFieldName(fieldName);
				
				if(StringUtils.isNotBlank(fieldName)){
					String fieldType = fieldsAndTypes.get(fieldName);
					TableItem item = tableViewer.getTable().getItem(index);
					Combo conditionalCombo = (Combo) item.getData(CONDITIONAL_OPERATORS);
					conditionalCombo.setItems(typeBasedConditionalOperators.get(fieldType));
					
					System.out.println(filterConditions);
					System.out.println("FilterWithTables.getFieldNameSelectionListener().new SelectionListener() {...}.widgetSelected()" + index);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		};
		return listener;
	}
	
	private SelectionListener getConditionalOperatorSelectionListener(final List<Condition> conditionsList) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				int index = (int) source.getData(ROW_INDEX);
				Condition filterConditions = conditionsList.get(index);
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
	
	private SelectionListener getRelationalOpSelectionListener(final List<Condition> conditionsList) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				int index = (int) source.getData(ROW_INDEX);
				Condition filterConditions = conditionsList.get(index);
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
	
	private SelectionListener addButtonListener(final TableViewer tableViewer, final List<Condition> conditionsList) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.getSource();
				
				conditionsList.add(conditionsList.size(), new Condition());
				tableViewer.refresh();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		};
		return listener;
	}

	private SelectionListener removeButtonListener(final TableViewer tableViewer, final List<Condition> conditionsList) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(conditionsList.size() > 1){
					Button button = (Button) e.getSource();
					int removeIndex = (int) button.getData(ROW_INDEX);
					
					conditionsList.remove(removeIndex);
					System.out.println("FilterWithTables.removeButtonListener().new SelectionListener() {...}.widgetSelected()" + removeIndex);
				}
				tableViewer.refresh();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		return listener;
	}
	
	private SelectionListener getOkButtonListener(final List<Condition> conditionsList) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				StringBuffer buffer = new StringBuffer();
				for(int index = 0; index < conditionsList.size(); index++){
					Condition condition = conditionsList.get(index);
					if(index !=0){
						buffer.append(" ").append(condition.getRelationalOperator()).append(" ");
					}
					buffer.append(condition.getFieldName()).append(" ").append(condition.getConditionalOperator()).append(" ").append(condition.getValue());
				}
				
				System.out.println(buffer);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		return listener;
	}
	
	class Condition{
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
		//super.createButtonsForButtonBar(parent);
	}
}