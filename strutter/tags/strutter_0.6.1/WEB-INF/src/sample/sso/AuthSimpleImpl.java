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

package sample.sso;

import SSO.Auth;

public class AuthSimpleImpl implements Auth {

	public String getUserRoles(String user, String pass) throws Exception 
	{
		if( !("admin".equals(user) && "admin".equals(pass)) )
			throw new Exception();
		
		return "ADMIN";
	}

}
