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

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ModuleConfig;

public class ActionPlugin implements PlugIn
{
	ActionServlet servlet;
	ModuleConfig module;
	
	private String packageroot = "";

	// Default ClassSuffix
	private String aliasaction = "Action";
	private String aliasform   = "ActionForm";
	
	private String pathformat  = "{PATH}";
	private String pathlower   = "1";
	
	private String parameter   = "action";
	private String scope       = ActionConfig.SCOPE_SESSION;
	
	private String aliasview  = "view,list,update,create,success";
	
	/**
	 *  Initialisieren
	 */
	public void init(ActionServlet servlet, ModuleConfig module) throws ServletException 
	{
		this.servlet = servlet;
		this.module  = module;

		System.out.println("### Strutter start ###");
		
		try 
		{
			String[] packages = packageroot.split(",");
			
			// Package Root für Form Action Klassen
			// read all classes from package folder
			ArrayList list = new ArrayList();
			
			for(int i=0; i < packages.length; i++)
			{
				String packagepath = packages[i];
				
				packagepath = normalizePath(packagepath.replace('.', '/'));
				String path = servlet.getServletContext().getRealPath("/WEB-INF/classes" + packagepath);
		
				list.addAll( ActionPlugin.getClasses(path, packages[i]) );
			}
			
			// register classes
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				String cname = (String) iterator.next();
	
				try {
					registAction(module, Class.forName(cname));
				} catch(Exception e) {
				}
			}
		} catch (Exception e) {
			System.out.println("Strutter Init Exception: " + e);
		}
		System.out.println("### Strutter initialized ###");
	}

	public void destroy() { 
	}
	
	/**
	 * Klassen untersuchen
	 */

	public static void main(String[] args) {
		
		ArrayList list  = getClasses("c:/work/strutter/WEB-INF/classes", "sample.action");
		
		System.out.println(list.size());
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			String clazz = (String) iterator.next();
			System.out.println(clazz);
		}
	}
	
	static ArrayList getClasses(String path) {
		return getClasses(path, ""); 
	}
	
	public static ArrayList getClasses(String path, String mainpackage) 
	{
		ArrayList list = new ArrayList();
	
		listFolder(list, new File(path + "/" + mainpackage.replace('.', '/')), mainpackage);
		
		return list;
	}
	
	static void listFolder(ArrayList list, File f, String pack)
	{
		if(!f.isDirectory()) return;
		
		File[] files = f.listFiles();
		for(int i = 0; i < files.length; i++) 
		{
			File sub = files[i];
			String name = pack + (pack.equals("") ? "" : ".") + sub.getName();
			
			if(sub.isDirectory()){
				listFolder(list, sub, name);
			} else {
				if(name.endsWith(".class")) 
				{
					String cname = name.substring(0, name.length() - ".class".length());
					list.add(cname);
				}
			}
		}
	}
	
	/**
	 *  Aktion registrieren
	 */
	boolean registAction(ModuleConfig moduleConfig, Class actionClass)
	{
		if(actionClass.isInterface()) 
			return false;
		if((actionClass.getModifiers() & Modifier.ABSTRACT) != 0) 
			return false;
		if(!isSubclass(actionClass, Action.class)) 
			return false;
		
		ActionConfig conf = null;
		 
		try {
			conf = (ActionConfig)actionClass.getField("struts").get(actionClass);
		} catch(Exception e) {
			return false;
		}

//		System.out.println("-----");
//		System.out.println(actionClass.getName());

		// vielleicht mal mehre Actions 
		List salist = new ArrayList();
		
		salist.add(conf);
				
		for(Iterator it = salist.iterator(); it.hasNext(); )
		{
			ActionConfig action = (ActionConfig) it.next();
			ActionMappingExtended actionmapping = new ActionMappingExtended();
			
			String fqn = actionClass.getName();
			
			actionmapping.setType(fqn);
			actionmapping.setUnknown(action.isUnknown());
			
			// DispatcherAction actionalias
			if(action.getParameter() != null)
				actionmapping.setParameter(action.getParameter());
			else if(getParameter() != null) 
				actionmapping.setParameter(getParameter());
			else
				actionmapping.setParameter("action");
			
			if(action.getScope() != null)
				actionmapping.setScope(action.getScope());
			else if(getParameter() != null) 
				actionmapping.setScope(getScope());
			else
				actionmapping.setScope("session");
			
			actionmapping.setInput(action.getInput());
			
			// Autopath oder überschrieben
			String path = action.getPath();
			
			if(isEmpty(path))
				path = getPathFromClassName(fqn, getAliasaction());
			
			actionmapping.setPath(path);
			
			
			Class form = action.getForm();
			
			String formName = null;

			if(form == null) { // Default Formklasse ermitteln
				String formClass = getFormFromAction(fqn);
				
				try{
					form = Class.forName(formClass);
					formName = registForm(moduleConfig, form);
				} catch(ClassNotFoundException e ) {
					
				}
			} else {
				formName = registForm(moduleConfig, form);
			}

			// wenn Formklasse vorhanden Name und eventuell Validation setzen
			if(!isEmpty(formName)) {
				actionmapping.setName(formName);
				actionmapping.setValidate(action.isValidate());
			} else {
				// FORM name für ActionBasedForm
				
				// instance of !!!
//				if(isSubclass(actionClass, FormlessDispatchAction.class) 
//				 || isSubclass(actionClass, FormlessAction.class) )
//				{
//				formName = registForm(moduleConfig, actionClass);
//				actionmapping.setName(formName);  // Name der Klasse
//				actionmapping.setValidate(action.isValidate());
//				}
				actionmapping.setAttribute(getClassName(actionClass.getName()));
			}
			
			if(!isEmpty(action.getRoles()))
				actionmapping.setRoles(action.getRoles());
			
			moduleConfig.addActionConfig(actionmapping);

			System.out.println(fqn + " -> " + path +  (actionmapping.getName() == null ? " [NO FORM]" : ""));

			
			// FORWARDS
			Forward[] forwards = action.getForwards();

			for(int i=0; i < forwards.length; i++) 
			{
				Forward forward = forwards[i];
				
			 	ActionForward aforward = new ActionForward();
				aforward.setName(forward.getName());
				//aforward.setPath(forward.getPath().replaceAll("\\{PATH\\}", path.substring(1)));
				aforward.setPath(forward.getPath());
				aforward.setRedirect(forward.isRedirect());
				
				if(forward.isGlobal())
					moduleConfig.addForwardConfig(aforward);
				else
					actionmapping.addForwardConfig(aforward);
				
				printForward(aforward);
			}
			
			if(forwards.length == 0)
			{
				String [] defaultpages = aliasview.split(",");
				
				for(int i=0; i < defaultpages.length; i++) 
				{
					String file = path + "_" + defaultpages[i] + ".jsp";

					if(!new File(servlet.getServletContext().getRealPath(file)).exists())
						continue;
					
					ActionForward aforward = new ActionForward();
					aforward.setName(defaultpages[i]);
					aforward.setPath(file);
					aforward.setRedirect(false);
					
					actionmapping.addForwardConfig(aforward);
					printForward(aforward);
				}
			}
			
			actionmapping.addInterceptors(action.getInterceptors());
		}
		return true;
	}
	
	private void printForward(ActionForward aforward)
	{
		System.out.println("  " + aforward.getPath() + " [" + aforward.getName() + "]");
	}
	
	
	/**
	 *  Form registieren
	 */
	String registForm(ModuleConfig moduleConfig, Class formClass)
	{
		if(formClass.isInterface()) 
			return null;
		if((formClass.getModifiers() & Modifier.ABSTRACT) != 0) 
			return null;
//		if(!isSubclass(formClass, ActionForm.class)) 
//			return null;
		
		FormBeanConfig formBean = new FormBeanConfig();
		
		String fqn = formClass.getName();
		
		// Formname ermitteln 
		String formName = getClassName(fqn);
		
		formBean.setName(formName);
		formBean.setType(fqn);
		
		moduleConfig.addFormBeanConfig(formBean);
		
		return formName;
	}
	
	final String cutAlias(String classname, String patternstr) 
	{
		return classname.substring(0, classname.indexOf(patternstr));
	}
	
	/**
	 *  Path aus Klassennamen ermitteln
	 */
	String getPathFromClassName(String fqn, String alias) 
	{
		String className = getClassName(fqn);
	
		className = cutAlias(className, alias);
		
		// Default toLowerCase
		if("1".equals(this.getPathlower()))
			className = className.toLowerCase();
		
		// Eventuell formatieren des PATH 
 		className = getPathformat().replaceAll("\\{PATH\\}", className);
 		
		String path = normalizePath(className);
		
		return path;
	}
	
	/**
	 * Form-Klassenname aus Action-Klassennamen ermitteln
	 */
	private String getFormFromAction(String actionClass)
	{
		String className   = getClassName(actionClass);
		String packageName = getPackageName(actionClass);
	
		//Action abschneiden
		String formclass = className.substring(0, className.length() - getAliasaction().length());
		
		formclass = packageName + "." + formclass + getAliasform();
		 
		try {
			Class.forName(formclass);
		} catch(ClassNotFoundException e) {
			formclass = packageName + "." + findPattern2(className, getAliasaction()) + getAliasform();
		}
		 
		return formclass;
	}
	
	public String findPattern2 (String action, String ActionAlias) {
		String pat = "[A-Z][a-z0-9]*" + ActionAlias + "$";
		return action.replaceFirst(pat, "");
	}
	
	
	
	/**
	 * Subclass checker
	 */
	boolean isSubclass(Class target, Class superclass) {
		for(Class s = target; s != Object.class; s = s.getSuperclass()){
			if(s == superclass) return true;
		}
		return false;
	}

	boolean isInterfaceImplementer(Class target, Class superclass) {
		Class[] interfaces = target.getInterfaces();
		for(int i=0; i < interfaces.length; i++) {
			if(interfaces[i] == superclass) return true;
		}
		return false;
	}

	
	/**
	 * FQN Klassennamen ermitteln
	 */
	private String getClassName(String fqn) {
		String className = fqn;
		if(className.indexOf('.') > 0){
			className = className.substring(className.lastIndexOf('.') + 1);
		}
		return className;
	}
	
	/** 
	 * FQN Package ermitteln
	 */
	private String getPackageName(String fqn) {
		if(fqn.indexOf('.') < 0) 
			return "";
		return fqn.substring(0, fqn.lastIndexOf('.'));
	}
	 
	private boolean isEmpty(String str){
		return str == null || str.length() == 0;
	}

	private String normalizePath(String path) {
		String ret = path;
		
		if(ret == null) 
			return "";
		if(!ret.startsWith("/")) 
			ret = "/" + ret;
		if(ret.endsWith("/")) 
			ret = ret.substring(0, ret.length() - 1);
		if(ret.equals("/")) 
			ret = "";

		return ret;
	}
	
	
	public void setPackageroot(String packageroot) {
		this.packageroot = packageroot;
	}

	public String getAliasaction() {
		return aliasaction;
	}

	public void setAliasaction(String action) {
		aliasaction = action;
	}

	public String getAliasform() {
		return aliasform;
	}

	public void setAliasform(String actionform) {
		aliasform = actionform;
	}
	
	public String getPathformat() {
		return pathformat;
	}

	public void setPathformat(String pathformat) {
		this.pathformat = pathformat;
	}

	public String getPathlower() {
		return pathlower;
	}

	public void setPathlower(String pathlower) {
		this.pathlower = pathlower;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getAliasview() {
		return aliasview;
	}

	public void setAliasview(String defaultforwards) {
		this.aliasview = defaultforwards;
	}

}
