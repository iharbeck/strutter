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

package sample.action.advanced.ajaxframe;

import org.apache.struts.action.ActionForward;

import strutter.action.FormlessDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.helper.ActionHelper;

public class AjaxmainAction extends FormlessDispatchAction implements ConfigInterface
{
	public void config(ActionConfig struts) {
		struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);
		
		struts.addForward("ajax_view.jsp");
	}

	public ActionForward doView() throws Exception
	{
		return ActionHelper.findForward("view");
	}
}