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

package struts.easy;
 
import java.util.ArrayList;

public class ActionConfig 
{
	public static final String SCOPE_SESSION = "session";
	public static final String SCOPE_REQUEST = "request";
	
	String input 	 = "";
	String path 	 = "";
	String scope 	 = SCOPE_SESSION;
	String parameter = "action";
	Class  form 	 = null;
	
	boolean unknown  = false;
	boolean validate = false;
	//boolean redirect = false;
	
	ArrayList forwards = new ArrayList();
	
	public Class getForm() {
		return form;
	}
	public void setForm(Class form) {
		this.form = form;
	}
	public Forward[] getForwards() {
		return (Forward[])forwards.toArray(new Forward[forwards.size()]);
	}
	
	public void clearForwards() {
		forwards.clear();
	}
	
	public void addForward(String name, String path, boolean redirect) {
		addForward(new Forward(name, path, redirect));
	}
	
	public void addForward(String name, String path) {
		addForward(new Forward(name, path));
	}

	public void addGlobalForward(String name, String path, boolean redirect) {
		addForward(new Forward(name, path, redirect).setGlobal(true));
	}
	
	public void addGlobalForward(String name, String path) {
		addForward(new Forward(name, path).setGlobal(true));
	}

	private void addForward(Forward forward) {
		this.forwards.add(forward);
	}
	
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		if(!path.startsWith("/"))
			this.path = "/" + path;
		else
			this.path = path;
	}
//	public boolean isRedirect() {
//		return redirect;
//	}
//	public void setRedirect(boolean redirect) {
//		this.redirect = redirect;
//	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public boolean isUnknown() {
		return unknown;
	}
	public void setUnknown(boolean unknown) {
		this.unknown = unknown;
	}
	public boolean isValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}	
} 

class Forward  
{
	String name = "";
	String path = "";
	boolean redirect = false;
	boolean global = false;

	public Forward(String name, String path, boolean redirect) {
		this.name = name;
		this.path = path;
		this.redirect = redirect;
	}
	public Forward(String name, String path) {
		this.name = name;
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public boolean isRedirect() {
		return redirect;
	}
	public boolean isGlobal() {
		return global;
	}
	public Forward setGlobal(boolean global) {
		this.global = global;
		return this;
	}
}



