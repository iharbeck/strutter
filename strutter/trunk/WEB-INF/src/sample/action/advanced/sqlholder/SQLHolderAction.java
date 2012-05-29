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

package sample.action.advanced.sqlholder;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;

import strutter.action.FormlessDispatchAction;
import strutter.config.tags.ConfigZeroInterface;
import strutter.helper.ActionHelper;
import strutter.helper.SqlHolder;

public class SQLHolderAction extends FormlessDispatchAction implements ConfigZeroInterface
{
	private static Log log = LogFactory.getLog(SQLHolderAction.class);

	public ActionForward doView() throws Exception
	{

		log.debug("SQLHolder");

		HashMap m = new HashMap();
		m.put("damdam", "lslslsl");

		System.out.println(SqlHolder.getSql("main", m));

		Type type = getClass().getDeclaredMethods()[0].getGenericReturnType();

		log.debug(type.toString());

		return ActionHelper.findForward("view");
	}
}