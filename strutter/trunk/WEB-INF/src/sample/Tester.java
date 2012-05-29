package sample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tester
{
	public static void main(String[] args)
	{

		// find("EditSubscriptionAction", "([A-Z].*?)[A-Z]", 1);

		// find("SubscriptionEditAction", ".*([A-Z].*)Action$", 2);

		// find2("EditSubscriptionAction", "[A-Z][a-z0-9]*");

		// find2("SubscriptionEditAction", "[A-Z][a-z0-9]*Action$");
		findPattern2("SubscriptionEditAction", "Action");

	}

	public static void findPattern2(String action, String ActionAlias)
	{
		String pat = "[A-Z][a-z0-9]*" + ActionAlias + "$";
		System.out.println(action.replaceFirst(pat, ActionAlias));
	}

	public static void find2(String action, String pat)
	{
		System.out.println(action.replaceFirst(pat, ""));

	}

	public static void find(String action, String pat, int direction)
	{
		Pattern pattern = Pattern.compile(pat);
		Matcher matcher = pattern.matcher(action);
		if(matcher.find())
		{
			System.out.println(matcher.group(1));

			action = action.substring(0, action.length() - "Action".length());

			System.out.println(action);
		}
	}
}
