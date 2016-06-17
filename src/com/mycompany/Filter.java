package com.bitwise.app.graph.propertywindow;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class Filter extends Dialog {
	private Text text;
	private Composite allRowsComposite;
	
	private String[] fields = new String[]{"firstName", "lastName", "salary", "birthDate"};
	
	public static void main(String[] args) {
		Filter test = new Filter(Display.getDefault().getActiveShell());
		test.open();
	}
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public Filter(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
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
		okButton.setText("Ok");
		
		Button cancelButton = new Button(buttonComposite, SWT.NONE);
		cancelButton.setText("Cancel");
		
		addRow(getComposite());
		disableFirstComposite();
		return container;
	}

	private Composite getComposite(){
		Composite rowComposite = new Composite(allRowsComposite, SWT.NONE);
		rowComposite.setLayout(new GridLayout(7, false));
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gridData.widthHint = 1176;
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
				newComposite.moveAbove(((Button)e.getSource()).getParent());
				
				allRowsComposite.getShell().layout(new Control[] { newComposite });
				disableFirstComposite();
			}
		});
		addButton.setText("+");
		
		Button removeButton = new Button(composite, SWT.NONE);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = new Button(((Button)e.getSource()).getParent(), SWT.NONE);
				button.setText("Something");
				button.moveAbove(((Button)e.getSource()));
				allRowsComposite.getShell().layout(new Control[] { button });
				disableFirstComposite();
				/*Button button = ((Button)e.getSource());
				button.getParent().dispose();
				allRowsComposite.layout(true);*/
			}
		});
		removeButton.setText("*");
		
		Button checkbox = new Button(composite, SWT.CHECK);
		
		Combo conditionCombo = new Combo(composite, SWT.NONE);
		conditionCombo.setItems(new String[] {"and", "or"});
		conditionCombo.select(0);
		conditionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Combo fieldCombo = new Combo(composite, SWT.NONE);
		fieldCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fieldCombo.setItems(fields);
		new AutoCompleteField(fieldCombo, new ComboContentAdapter(), fields);
		
		Combo operatorCombo = new Combo(composite, SWT.NONE);
		operatorCombo.setItems(getOperatorsBasedOnDataType("java.lang.String"));
		operatorCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

	private String[] getOperatorsBasedOnDataType(String dataType) {
		switch(dataType){
		case "java.lang.String":
			return new String[] {"like"};
		case "java.lang.BigDecimal":
			return new String[] {">", ">=", "<", "<=", "!=", "=="};
		}
		return null;
	}

	private void disableFirstComposite(){
		Control[] children = allRowsComposite.getChildren();
		for (int index = 0; index < children.length; index++) {
			boolean setVisible = true;
			if(index == 0){
				setVisible = false;
			}
			Composite composite = (Composite) children[index];
			composite.getChildren()[2].setVisible(setVisible);
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
}
