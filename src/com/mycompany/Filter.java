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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class Filter extends Dialog {
	private Text text;
	private Composite allRowsComposite;
	private List<Condition> conditionsList;
	private Map<String,String[]> typeBasedConditionalOperators = new HashMap<>();
	private String relationalOperators[];
	private String fieldNames[];
	private Map<String, String> fieldsAndTypes;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public Filter(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
		conditionsList = new ArrayList<>();
		
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
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabFolder tabFolder = new TabFolder(composite_1, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmRemote = new TabItem(tabFolder, SWT.NONE);
		tbtmRemote.setText("Remote");
		
		allRowsComposite = new Composite(tabFolder, SWT.NONE);
		allRowsComposite.setLayout(new GridLayout(1, false));
		allRowsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		tbtmRemote.setControl(allRowsComposite);
		
		Composite buttonComposite = new Composite(container, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		Button okButton = new Button(buttonComposite, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StringBuffer buffer = new StringBuffer();
				Control[] children = allRowsComposite.getChildren();
				for(int index = 0; index < children.length; index++){
					Condition condition = conditionsList.get(index);
					Composite composite = (Composite) children[index];
					
					Combo combo = (Combo) composite.getChildren()[3];
					condition.setRelationalOperator(combo.getText());
					
					combo = (Combo) composite.getChildren()[4];
					condition.setFieldName(combo.getText());
					
					combo = (Combo) composite.getChildren()[5];
					condition.setConditionalOperator(combo.getText());
					
					Text text = (Text) composite.getChildren()[6];
					condition.setValue(text.getText());
					
					if(index !=0){
						buffer.append(" ").append(condition.getRelationalOperator()).append(" ");
					}
					buffer.append(condition.getFieldName()).append(" ").append(condition.getConditionalOperator()).append(" ").append(condition.getValue());
				}
				
				System.out.println(buffer);
			}
		});
		okButton.setText("Ok");
		
		Button cancelButton = new Button(buttonComposite, SWT.NONE);
		cancelButton.setText("Cancel");
		
		conditionsList.add(0, new Condition());
		addRow(getComposite());
		disableFirstComposite();
		return container;
	}

	private Composite getComposite(){
		Composite rowComposite = new Composite(allRowsComposite, SWT.NONE);
		GridLayout layout = new GridLayout(7, false);
		
		rowComposite.setLayout(layout);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 1161;
		rowComposite.setLayoutData(gridData);
		return rowComposite;
	}
	
	private void addRow(final Composite composite) {
		Button addButton = new Button(composite, SWT.NONE);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Composite newComposite = getComposite();
				addRow(newComposite);
				Composite currentComposite = ((Button)e.getSource()).getParent();
				newComposite.moveAbove(currentComposite);
				
				allRowsComposite.getShell().layout(new Control[] { newComposite });
				disableFirstComposite();
				conditionsList.add( (int) currentComposite.getData("rowIndex"), new Condition());
				updateReferences();
			}

		});
		addButton.setText("+");
		
		Button removeButton = new Button(composite, SWT.NONE);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(allRowsComposite.getChildren().length > 1){
					Button button = ((Button)e.getSource());
					button.getParent().dispose();
					allRowsComposite.layout(true);
					disableFirstComposite();
					updateReferences();
				}
			}
		});
		removeButton.setText("*");
		
		Button checkbox = new Button(composite, SWT.CHECK);
		
		Combo conditionCombo = new Combo(composite, SWT.NONE);
		conditionCombo.setItems(relationalOperators);
		conditionCombo.select(0);
		conditionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Combo fieldCombo = new Combo(composite, SWT.NONE);
		fieldCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fieldCombo.setItems(fieldNames);
		new AutoCompleteField(fieldCombo, new ComboContentAdapter(), fieldNames);
		
		Combo operatorCombo = new Combo(composite, SWT.NONE);
		fieldCombo.addSelectionListener(getFieldComboListener(operatorCombo));
		operatorCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		composite.setData("rowIndex", 0);
	}

	private SelectionListener getFieldComboListener(final Combo operatorCombo) {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				String fieldName = ((Combo)e.widget).getText();
				if(StringUtils.isNotBlank(fieldName)){
					String fieldType = fieldsAndTypes.get(fieldName);
					operatorCombo.setItems(typeBasedConditionalOperators.get(fieldType));
					AutoCompleteField autoComplete= new AutoCompleteField(operatorCombo, new ComboContentAdapter(), typeBasedConditionalOperators.get(fieldType));
					autoComplete.setProposals(typeBasedConditionalOperators.get(fieldType));
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		};
	}

	private void updateReferences() {
		Control[] children = allRowsComposite.getChildren();
		for(int i = 0; i < children.length; i++){
			((Composite)children[0]).setData("rowIndex", i);
		}
	}
	
	private void disableFirstComposite(){
		Control[] children = allRowsComposite.getChildren();
		for (int index = 0; index < children.length; index++) {
			boolean setVisible = true;
			if(index == 0){
				setVisible = false;
			}
			Composite composite = (Composite) children[index];
			composite.getChildren()[3].setVisible(setVisible);
		}
	}
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		//createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,	true);
	//	createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1216, 692);
	}
	public void setRelationalOperators(String[] relationalOperators) {
		this.relationalOperators = relationalOperators;
	}
	public void setFieldsAndTypes(Map<String, String> fieldsAndTypes) {
		this.fieldsAndTypes = fieldsAndTypes;
		fieldNames = (String[]) this.fieldsAndTypes.keySet().toArray(new String[this.fieldsAndTypes.size()]);
		Arrays.sort(fieldNames);
	}
}
