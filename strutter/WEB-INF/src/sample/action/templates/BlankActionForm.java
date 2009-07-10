/*
 * Copyright 2006 Ingo Harbeck.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.action.templates;

import org.apache.struts.action.ActionForm;

public class BlankActionForm extends ActionForm 
{
	private static final long serialVersionUID = 1L;
	
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

