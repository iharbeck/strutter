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
 
import java.util.ArrayList;
import java.util.HashMap;

public class Action2Config 
{
	String input 	 = "";
	String path 	 = "";
	
	boolean unknown  = false;
	boolean validate = false;
	
	HashMap params = new HashMap();
	
	ArrayList results = new ArrayList();
	ArrayList interceptors = new ArrayList();
	
	public Result[] getResults() {
		return (Result[])results.toArray(new Result[results.size()]);
	}
	public Interceptor[] getInterseptors() {
		return (Interceptor[])interceptors.toArray(new Interceptor[interceptors.size()]);
	}
	
	public void clearResultss() {
		results.clear();
	}
	
	public void addResult(String name, String path, String type) {
		addResult(new Result(name, path, type));
	}

	public void addResult(String name, String path, String type, HashMap params) {
		addResult(new Result(name, path, type, params));
	}

	public void addForward(String name, String path) {
		addResult(new Result(name, path));
	}

	public void addForward(String name, String path, HashMap params) {
		addResult(new Result(name, path, params));
	}

	
	public void addGlobalResult(String name, String path, String type) {
		addResult(new Result(name, path, type).setGlobal(true));
	}
	
	public void addGlobalResult(String name, String path) {
		addResult(new Result(name, path).setGlobal(true));
	}

	private void addResult(Result result) {
		this.results.add(result);
	}
	
	public void addInterseptor(String name) {
		addInterceptor(new Interceptor(name));
	}
	
	public void addInterseptor(String name, HashMap params) {
		addInterceptor(new Interceptor(name, params));
	}
	
	private void addInterceptor(Interceptor interceptor) {
		this.interceptors.add(interceptor);
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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
	public HashMap getParams() {
		return params;
	}
	public void setParams(HashMap params) {
		this.params = params;
	}	
} 

class Result  
{
	String name = "";
	String path = "";
	boolean global = false;
	
	HashMap params = new HashMap();
	String type = "dispatcher";

	//General
	public Result(String name, String path, String type, HashMap params) {
		this.name = name;
		this.path = path;
		this.type = type;
		this.params = params;
	}
	public Result(String name, String path, String type) {
		this.name = name;
		this.path = path;
		this.type = type;
	}
	//Forward
	public Result(String name, String path, HashMap params) {
		this.name = name;
		this.path = path;
		this.params = params;
	}
	public Result(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public boolean isGlobal() {
		return global;
	}
	public Result setGlobal(boolean global) {
		this.global = global;
		return this;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public HashMap getParams() {
		return params;
	}
	public void setParams(HashMap params) {
		this.params = params;
	}
}


class Interceptor  
{
	String name = "";
	HashMap params = new HashMap();
	
	public Interceptor(String name, HashMap params) {
		this.name = name;
		this.params = params;
	}
	public Interceptor(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public HashMap getParams() {
		return params;
	}
	public void setParams(HashMap params) {
		this.params = params;
	}
}
