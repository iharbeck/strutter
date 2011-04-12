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

package strutter.config;

import java.util.ArrayList;
import java.util.HashMap;

import strutter.interceptor.ActionInterceptorInterface;

public class ActionConfig
{
	public static final String SCOPE_SESSION = "session";
	public static final String SCOPE_REQUEST = "request";

	public static final String PACKAGEBY_FEATURE = "feature";
	public static final String PACKAGEBY_LAYER   = "layer";

	String input 	 = "";
	String path 	 = "";
	String scope 	 = null;
	String parameter = null;
	Class  form 	 = null;

	boolean unknown      = false;
	boolean validate     = false;
	boolean directaccess = false;

	String unspecified = "view";

	String packageby = null; 
	

	String roles	 = "";

	ArrayList interceptors = new ArrayList();

	ArrayList forwards = new ArrayList();

	ArrayList dwrpojos = new ArrayList();

	public Class getForm() {
		return form;
	}
	public void setForm(Class form) {
		this.form = form;
	}

	public void addRole(String role) {
		roles += "," + role;
		if(roles.charAt(0) == ',')
			roles = roles.substring(1);
	}


	public Forward[] getForwards() {
		return (Forward[])forwards.toArray(new Forward[forwards.size()]);
	}

	public void clearForwards() {
		forwards.clear();
	}

	public void addForward(String path, boolean redirect) {

		addForward(new Forward(extractName(path), path, redirect));
	}

	private final String extractName(String path) {

		String name;
		if(path.indexOf('_') == -1)
			name = "success";
		else
			name = path.substring(path.lastIndexOf('_')+1, path.lastIndexOf('.'));

		return name.toLowerCase();
	}

	public void addMethodRedirect(String method) {
		addForward(new Forward("#" + method, null, true));
	}
	
	public void addMethodRedirect(Class clazz, String method) {
		addForward(new Forward("#" + method, clazz.getName(), true));
	}
	
	public void addRedirect(String path) {
		addForward(path, true);
	}
	
	public void addForward(String path) {
		addForward(path, false);
	}

	public void addRedirect(String name, String path) {
		addForward(new Forward(name, path, true));
	}

	public void addForward(String name, String path, boolean redirect) {
		addForward(new Forward(name, path, redirect));
	}

	
	public void addForward(String name, String path) {
		addForward(new Forward(name, path));
	}


	public void addGlobalForward(String path, boolean redirect) {
		addForward(new Forward(extractName(path), path, redirect).setGlobal(true));
	}

	public void addGlobalRedirect(String path) {
		addGlobalForward(path, true);
	}

	public void addGlobalForward(String path) {
		addGlobalForward(path, false);
	}

	public void addGlobalForward(String name, String path, boolean redirect) {
		addForward(new Forward(name, path, redirect).setGlobal(true));
	}

	public void addGlobalRedirect(String name, String path) {
		addForward(new Forward(name, path, true).setGlobal(true));
	}
	
	public void addGlobalForward(String name, String path) {
		addForward(new Forward(name, path).setGlobal(true));
	}

	private void addForward(Forward forward) {
		this.forwards.add(forward);
	}
	
	public void addRemoting(Class clazz) {
		this.dwrpojos.add(clazz);
	}
	
	protected ArrayList getRemoting()
	{
		return dwrpojos;
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
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	
	HashMap<String, String> properties = new HashMap<String, String>();
	
	public void setProperty(String key, String value)
	{
		this.properties.put(key, value);
	}
	
	public HashMap<String, String> getProperties() {
		return properties;
	}
	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}


	public void addInterceptor(ActionInterceptorInterface interceptor) {
		this.interceptors.add(interceptor);
	}
	public ArrayList getInterceptors() {
		return interceptors;
	}
	public void setInterceptors(ArrayList interceptors) {
		this.interceptors = interceptors;
	}
	public String getUnspecified() {
		return unspecified;
	}
	public void setUnspecified(String unspecified) {
		this.unspecified = unspecified;
	}
	public String getPackageby()
	{
		return packageby;
	}
	public void setPackageby(String packageby)
	{
		this.packageby = packageby;
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
		setPath(path);
		this.redirect = redirect;
	}
	public Forward(String name, String path) {
		this.name = name;
		setPath(path);
	}
	public String getName() {
		return name;
	}

	public void setPath(String path) {
		if(path != null && !path.startsWith("/") && !path.startsWith("~"))
			this.path = "/" + path;
		else
			this.path = path;
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



