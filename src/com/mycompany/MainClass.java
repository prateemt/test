package hydrograph.ui.dataviewer.filter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;

	public class MainClass {
	public static void main(String[] args) {
		String relationalOperators[] = new String[]{"and", "or"};
		Map<String,String> fieldsAndTypes = new HashMap<>();
		fieldsAndTypes.put("firstName", "java.lang.String");
		fieldsAndTypes.put("lastName", "java.lang.String");
		fieldsAndTypes.put("age", "java.lang.Integer");
		fieldsAndTypes.put("dateOfBirth", "java.util.Date");
		
		FilterWithTables test = new FilterWithTables(Display.getDefault().getActiveShell());
		test.setRelationalOperators(relationalOperators);
		test.setFieldsAndTypes(fieldsAndTypes);
		test.open();
	}
}