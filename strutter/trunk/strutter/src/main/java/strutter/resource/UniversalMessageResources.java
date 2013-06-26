package strutter.resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.struts.util.PropertyMessageResources;

// properties
// or
// properties;gawky.database.DB#getConnection("my", "name", "is"):sql
public class UniversalMessageResources extends PropertyMessageResources
{
	private static final long serialVersionUID = 1L;

	static class Config
	{
		static String SQL;
		static String clazzname;
		static String methodname;
		static String array[];

		public static String init(String action)
		{
			Pattern pattern = Pattern.compile("^([0-9a-zA-Z\\._-]*)(;(.*)#(.*)\\((.*)\\):(.*))?", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(action);

			if(matcher.find())
			{
				action = matcher.group(1).replaceFirst("\\.properties", "");
				clazzname = matcher.group(3);
				methodname = matcher.group(4);
				String param = matcher.group(5);
				SQL = matcher.group(6);

				// build String[] of parameter
				ArrayList params = new ArrayList();

				Pattern parampattern = Pattern.compile("\"(.*?)\"", Pattern.CASE_INSENSITIVE);
				Matcher parammatcher = parampattern.matcher(param);
				while(parammatcher.find())
				{
					params.add(parammatcher.group(1));
				}

				array = (String[])params.toArray(new String[params.size()]);
			}

			return action;
		}

	}

	public UniversalMessageResources(UniversalMessageResourcesFactory factory, String config)
	{
		super(factory, Config.init(config));
	}

	public String getMessage(Locale locale, String key)
	{
		if(locale == null)
		{
			locale = Locale.getDefault();
		}

		return super.getMessage(locale, key);
	}

	public void reload()
	{
		locales.clear();
		messages.clear();
		formats.clear();
	}

	protected synchronized void loadLocale(String localekey)
	{

		if(locales.get(localekey) != null)
			return;

		super.loadLocale(localekey);

		if(log.isTraceEnabled())
		{
			log.trace("Getting properties from database");
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = getConnection();

			ps = conn.prepareStatement(Config.SQL);
			ps.setString(1, localekey);
			rs = ps.executeQuery();

			while(rs.next())
			{
				String key = rs.getString(1);
				String val = rs.getString(2);
				messages.put(messageKey(localekey, key), val);
			}
		}
		catch(Exception e)
		{
		}
		finally
		{
			try
			{
				rs.close();
				ps.close();
				conn.close();
			}
			catch(Exception ignoreMe)
			{
			}
		}
	}

	private Connection getConnection() throws Exception
	{
		return (Connection)runStaticMethod(Config.clazzname, Config.methodname, Config.array);
	}

	// runStaticMethod("gawky.database.DB", "getConnection", null)
	public Object runStaticMethod(String clazzname, String methodname, Object[] params)
	{
		Class[] clazzes = null;

		if(params != null && params.length > 0)
		{
			clazzes = new Class[params.length];

			for(int i = 0; i < clazzes.length; i++)
			{
				clazzes[i] = String.class;
			}
		}

		try
		{
			Class clazz = Class.forName(clazzname, false, this.getClass().getClassLoader());
			return (Connection)clazz.getMethod(methodname, clazzes).invoke(clazz, params);
		}
		catch(Exception e)
		{
			System.out.println("STRUTTER DB MESSAGE LOOKUP: " + e);
		}

		return null;
	}

}
