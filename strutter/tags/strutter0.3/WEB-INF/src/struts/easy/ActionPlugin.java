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

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
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
	
	
	/**
	 *  Initialisieren
	 */
	public void init(ActionServlet servlet, ModuleConfig module) throws ServletException 
	{
		this.servlet = servlet;
		this.module  = module;
		
		String[] packages = packageroot.split(";");
		
		//Package Root f�r Form Action Klassen
		for(int i=0; i < packages.length; i++)
		{
			String packagepath = packages[i];
			
			packagepath = normalizePath(packagepath.replace('.', '/'));
			String path = servlet.getServletContext().getRealPath("/WEB-INF/classes" + packagepath);
	
			try{
				File f = new File(path);
				processClassFolder(f, packageroot);
			}catch (Exception e){
				servlet.log("Klassenverarbeitung", e);
			}
		}
	}

	public void destroy() { 
	}
	
	/**
	 * Klassen untersuchen
	 */

	
	void processClassFolder(File f, String pack)
	{
		if(!f.isDirectory()) return;
		
		File[] files = f.listFiles();
		for(int i = 0; i < files.length; i++) {
			File sub = files[i];
			String name = pack + (pack.equals("") ? "" : ".") + sub.getName();
			
			if(sub.isDirectory()){
				processClassFolder(sub,  name);
			} else {
				//if(!name.endsWith(ACTION + ".class")) 
				//	continue;
				if(name.endsWith(aliasaction + ".class")) 
				{
					String cname = name.substring(0, name.length() - ".class".length());
					try{
						registAction(module, Class.forName(cname));
					}catch(ClassNotFoundException e){
						System.out.println(cname + " not found");
					}
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
			ActionMapping actionmapping = new ActionMapping();
			
			String fqn = actionClass.getName();
			
			actionmapping.setType(fqn);
			actionmapping.setUnknown(action.isUnknown());
			actionmapping.setParameter(action.getParameter());
			actionmapping.setScope(action.getScope());
			actionmapping.setInput(action.getInput());
			
			// Autopath oder �berschrieben
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
				// FORM name f�r ActionBasedForm
				actionmapping.setAttribute(getClassName(actionClass.getName()));
			}
			
			moduleConfig.addActionConfig(actionmapping);
			
			Forward[] forwards = action.getForwards();

			// FORWARDS
			for(int i=0; i < forwards.length; i++) 
			{
				Forward forward = forwards[i];
				
			 	ActionForward aforward = new ActionForward();
				aforward.setName(forward.getName());
				aforward.setPath(forward.getPath().replaceAll("\\{PATH\\}", path.substring(1)));
				aforward.setRedirect(forward.isRedirect());
				
				//System.out.print((forward.isGlobal() ? "G " : "F "));
				//System.out.println(forward.getName() + " (" + forward.getPath() + ")");
				if(forward.isGlobal())
					moduleConfig.addForwardConfig(aforward);
				else
					actionmapping.addForwardConfig(aforward);
			}

			System.out.println(fqn + " -> " + path +  (actionmapping.getName() == null ? " [NO FORM]" : ""));
		}
		return true;
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
		if(!isSubclass(formClass, ActionForm.class)) 
			return null;
		
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
		
		className = className.substring(0, className.length() - getAliasaction().length());
		
		return packageName + "." + className + getAliasform();
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

}
