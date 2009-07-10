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

package sample.action.advanced.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import strutter.config.ActionConfig;
import strutter.optional.FormlessPlainDispatchAction;
import strutter.optional.controller.DirectInterface;

public class DirectAction extends FormlessPlainDispatchAction implements DirectInterface
{
	private static Log log = LogFactory.getLog(DirectAction.class);

	public static ActionConfig struts = new ActionConfig();

	static {
		struts.addForward("/formlesssimple_view.jsp");
	}

	public void doexecute(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("dir");
	}
}