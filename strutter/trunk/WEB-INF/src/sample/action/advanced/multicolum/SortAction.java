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

package sample.action.advanced.multicolum;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.upload.FormFile;

import sample.dao.Address;
import strutter.action.FormlessDispatchAction;
import strutter.config.ActionConfig;
import strutter.config.tags.ConfigInterface;
import strutter.helper.ActionHelper;

public class SortAction extends FormlessDispatchAction implements ConfigInterface
{
    private static Log log = LogFactory.getLog(SortAction.class);

    public void config(ActionConfig struts)
    {
	struts.setPackageby(ActionConfig.PACKAGEBY_FEATURE);
	struts.addForward("sort_view.jsp");
    }

    public void reset()
    {
    }
    
    private String column;
    private boolean multi;
    

    private ArrayList<String> columns = new ArrayList<String>();

    public ActionForward doView() throws Exception
    {
	log.debug("view simpler");

	return ActionHelper.findForward("view");
    }
    
    public ActionForward doSort() throws Exception
    {
	int pos = findColumn(column);

	if(multi == false && (columns.size() > 1 || pos < 0) )
	{
	    columns.clear();
	    pos = -1;
	}
	
	if(pos < 0)
	{
	    columns.add(column);
	}
	else
	{
	    String old = columns.get(pos);
	    
	    if(old.endsWith(" desc"))
		columns.set(pos, column);
	    else
		columns.set(pos, column + " desc");
	}

	for(String v : columns)
	{
	    System.out.println(v);
	}
	
	return ActionHelper.findForward("view");
    }

    private int findColumn(String val)
    {
	for(int i=0; i < columns.size(); i++)
	{
	    if(columns.get(i).startsWith(val))
		return i;
	}
	
	return -1;
    }
    
    public String findPosition(String val)
    {
	for(int i=0; i < columns.size() && columns.size() > 1; i++)
	{
	    if(columns.get(i).startsWith(val))
		return "" + (i+1);
	}
	
	return "";
    }
    
    public String lookupColumnOrder(String column)
    {
	int pos = findColumn(column);
	
	if(pos < 0)
	    return "";
	
	String old = columns.get(pos);
	    
	if(old.endsWith(" desc"))
	    return "desc";
	else
	    return "asc";
    }

    public String buildOrderString()
    {
	StringBuilder buf = new StringBuilder();

	buf.append("ORDER BY ");
	
	for(int i=0; i < columns.size(); i++)
	{
	    buf.append(columns.get(i))
	       .append(", ");
	}
	
	return buf.substring(0, buf.length()-2);
    }
    
    
    public String getColumn()
    {
        return column;
    }

    public void setColumn(String column)
    {
        this.column = column;
    }

    public boolean isMulti()
    {
        return multi;
    }

    public void setMulti(boolean multi)
    {
        this.multi = multi;
    }
}