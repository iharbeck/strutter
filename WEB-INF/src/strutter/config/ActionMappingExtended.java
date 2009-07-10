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

import org.apache.struts.action.ActionMapping;

/*
 * Security is currently action based.
 * Method based security my be an option.
 */

public class ActionMappingExtended extends ActionMapping
{
	private static final long serialVersionUID = 1L;
	
	boolean wsaction = false;
	boolean heading = true;
	
	String unspecified = "view";
	
	ArrayList interceptors = new ArrayList();
	
	public void addInterceptors(ArrayList interceptors) {
		this.interceptors.addAll(interceptors);
	}

	public ArrayList getInterceptors() {
		return interceptors;
	}

	public boolean isWsaction() {
		return wsaction;
	}

	public void setWsaction(boolean wsaction) {
		this.wsaction = wsaction;
	}

	public String getUnspecified() {
		return unspecified;
	}

	public void setUnspecified(String unspecified) {
		this.unspecified = unspecified;
	}

	public boolean isHeading() {
		return heading;
	}

	public void setHeading(boolean heading) {
		this.heading = heading;
	}
}
