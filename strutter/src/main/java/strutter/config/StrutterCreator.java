package strutter.config;

import org.directwebremoting.WebContextFactory;
import org.directwebremoting.extend.AbstractCreator;
import org.directwebremoting.extend.Creator;

/**
 * StrutsCreator
 * 
 * @author Ariel O. Falduto
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class StrutterCreator extends AbstractCreator implements Creator
{
	public StrutterCreator()
	{
	}

	private String actionclass;
	private Class<?> clazz = null;

	@Override
	public Class<?> getType()
	{

		if(clazz == null)
		{
			try
			{
				clazz = getInstance().getClass();
			}
			catch(InstantiationException ex)
			{
				System.out.println("Failed to instansiate object to detect type." + ex);
			}
		}

		return clazz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.directwebremoting.Creator#getInstance()
	 */
	@Override
	public Object getInstance() throws InstantiationException
	{
		Object form = null;

		try
		{
			try
			{
				if(true || getScope().equalsIgnoreCase("session"))
				{
					form = WebContextFactory.get().getSession().getAttribute(actionclass.substring(actionclass.lastIndexOf('.') + 1));
				}
			}
			catch(Exception e)
			{
			}
			if(form == null)
			{
				form = Class.forName(actionclass).newInstance();
			}
		}
		catch(Exception e)
		{
			throw new InstantiationException("Can't find formInstance  for " + actionclass);
		}

		return form;
	}

	public String getActionclass()
	{
		return actionclass;
	}

	public void setActionclass(String actionclass)
	{
		this.actionclass = actionclass;
	}
}
