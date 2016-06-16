package com.bitwise.app.graph.propertywindow;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;

public class Filter extends Dialog {
	private Text text;
	Composite allRowsComposite;
	
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
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		
		allRowsComposite = new Composite(container, SWT.NONE);
		allRowsComposite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_composite.heightHint = 497;
		allRowsComposite.setLayoutData(gd_composite);
		addRow(getComposite());
		
		return container;
	}

	private Composite getComposite(){
		Composite composite_2 = new Composite(allRowsComposite, SWT.NONE);
		composite_2.setLayout(new GridLayout(7, false));
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite_2.widthHint = 1176;
		composite_2.setLayoutData(gd_composite_2);
		return composite_2;
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

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1216, 692);
	}
}
