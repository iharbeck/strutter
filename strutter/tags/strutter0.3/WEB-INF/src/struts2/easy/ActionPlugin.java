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

package struts2.easy;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.FilterConfig;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.entities.ResultTypeConfig;
import com.opensymphony.xwork.config.providers.InterceptorBuilder;


public class ActionPlugin 
{
	FilterConfig fc;
	//ModuleConfig module;
	
	private String packageroot = "";

	// Default ClassSuffix
	private String aliasaction = "Action";
	
	private String pathformat  = "{PATH}";
	private String pathlower   = "1";
	
	
	
	/**
	 *  Initialisieren
	 */
	public void init(FilterConfig fc) 
	{
		this.fc = fc;
		
		String[] packages = packageroot.split(";");
		
		//Package Root für Form Action Klassen
		for(int i=0; i < packages.length; i++)
		{
			String packagepath = packages[i];
			
			packagepath = normalizePath(packagepath.replace('.', '/'));
			String path = fc.getServletContext().getRealPath("/WEB-INF/classes" + packagepath);
	
			try{
				File f = new File(path);
				processClassFolder(f, packageroot);
			}catch (Exception e){
				fc.getServletContext().log("Klassenverarbeitung", e);
			}
		}
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
						registAction(fc, Class.forName(cname));
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
	boolean registAction(FilterConfig filterConfig, Class actionClass)
	{
		if(actionClass.isInterface()) 
			return false;
		if((actionClass.getModifiers() & Modifier.ABSTRACT) != 0) 
			return false;
		if(!isSubclass(actionClass, ActionSupport.class)) 
			return false;
		
		Action2Config conf = null;
		 
		try {
			conf = (Action2Config)actionClass.getField("struts").get(actionClass);
		} catch(Exception e) {
			return false;
		}

		// vielleicht mal mehre Actions 
		List salist = new ArrayList();
		
		salist.add(conf);
			
		// ASF2 config holen
		Configuration config = ConfigurationManager.getConfiguration();

		//PackageConfig defaultPackageConfig = new PackageConfig();
		PackageConfig defaultPackageConfig = config.getPackageConfig("webwork-default");
		
		
		for(Iterator it = salist.iterator(); it.hasNext(); )
		{
			Action2Config action = (Action2Config) it.next();
			
			String fqn = actionClass.getName();
			
			// Autopath oder überschrieben
			String path = action.getPath();
			
			if(isEmpty(path))
				path = getPathFromClassName(fqn, getAliasaction());
			
			
			// eventuell Validation setzen

			Result[] rezults = action.getResults();

			// RESULTS

			HashMap results = new HashMap();

			for(int i=0; i < rezults.length; i++) 
			{
				Result rezult = rezults[i];
				
				HashMap params = rezult.getParams();
				
				// FORWARD CASE
				if(rezult.getType().equals("dispatcher"))
					params.put("location", rezult.getPath().replaceAll("\\{PATH\\}", path.substring(1)));

				Class resultClass = null; //ServletDispatcherResult.class;

				//Resulttype like REDIRECT default "dispatcher"
				ResultTypeConfig typeconfig = (ResultTypeConfig) defaultPackageConfig.getAllResultTypeConfigs().get(rezult.getType());
				if(typeconfig != null)
				{
					if(typeconfig.getParams() != null)
						params.putAll(typeconfig.getParams());
					resultClass = typeconfig.getClazz();
				}

				// Create Result
				ResultConfig result = new ResultConfig(rezult.getName(), resultClass, params);
				results.put(rezult.getName(), result);

				
				//TODO: GLOBAL
				//if(forward.isGlobal())
				//moduleConfig.addForwardConfig(aforward);
				//else
			}

			//INTERCEPTORS
			Interceptor[] interzeptors = action.getInterseptors();

			List interceptors = new ArrayList();
			
			for(int i=0; i < interzeptors.length; i++) 
			{
				Interceptor interzeptor = interzeptors[i];
				
				List l = InterceptorBuilder.constructInterceptorReference(defaultPackageConfig, interzeptor.getName(), interzeptor.getParams());

				interceptors.addAll(l);
			}
			
			// PARAMS der Action
			HashMap actionparams = action.getParams();
			
			ActionConfig actionconfig = new ActionConfig("execute", 
												   actionClass, 
					                               actionparams, 
					                               results, 
					                               interceptors);
			
			defaultPackageConfig.addActionConfig(path, actionconfig);
			
			//TODO:PACKAGENAME
			config.addPackageConfig("webwork-default", defaultPackageConfig);

			
			System.out.println(fqn + " -> " + path + " [AF2] ");
		}
		
		// Flush dynamic configuration
		config.rebuildRuntimeConfiguration();

		// end dynamic configuration

		return true;
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
	
	private boolean isEmpty(String str){
		return str == null || str.length() == 0;
	}

	private String normalizePath(String path) {
		String ret = path;
		
		if(ret == null) 
			return "";
//		if(!ret.startsWith("/")) 
//			ret = "/" + ret;
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
