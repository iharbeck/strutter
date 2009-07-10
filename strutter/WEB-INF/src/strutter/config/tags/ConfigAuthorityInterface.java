package strutter.config.tags;

public interface ConfigAuthorityInterface 
{
	public String getUsername();
	public boolean hasRole(String role);
	public boolean isAuthorized();
}
