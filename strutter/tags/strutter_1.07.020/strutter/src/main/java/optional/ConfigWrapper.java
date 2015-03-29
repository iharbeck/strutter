package optional;

import strutter.config.ActionConfig;

public class ConfigWrapper
{
	Class actionclass;
	ActionConfig config;

	public Class getActionclass()
	{
		return actionclass;
	}

	public void setActionclass(Class actionclass)
	{
		this.actionclass = actionclass;
	}

	public ActionConfig getConfig()
	{
		return config;
	}

	public void setConfig(ActionConfig config)
	{
		this.config = config;
	}
}
