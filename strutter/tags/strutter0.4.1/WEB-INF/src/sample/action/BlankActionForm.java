package sample.action;

import org.apache.struts.action.ActionForm;

public class BlankActionForm extends ActionForm 
{
	/**
	 * ADD FORM PROPERTIES HERE
	 */
	
	//	String [] anreden = new String[] { "Herr", "Frau", "Dr." };
	//	LabelValueBean [] anreden = new LabelValueBean[] { 
	//			new LabelValueBean("Herr", "1"), 
	//			new LabelValueBean("Frau", "2"), 
	//			new LabelValueBean("Dr.",  "3" )
	//	};
	
	String field1; 

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}
	
}

