// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Somik Raha
//
// Revision Control Information
//
// $URL: https://htmlparser.svn.sourceforge.net/svnroot/htmlparser/trunk/parser/src/main/java/org/htmlparser/tags/Div.java $
// $Author: derrickoswald $
// $Date: 2011-04-25 11:39:12 +0200 (Mo, 25 Apr 2011) $
// $Revision: 74 $
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the Common Public License; either
// version 1.0 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// Common Public License for more details.
//
// You should have received a copy of the Common Public License
// along with this library; if not, the license is available from
// the Open Source Initiative (OSI) website:
//   http://opensource.org/licenses/cpl1.0.php

package strutter.htmlparser.tags;

/**
 * A div tag.
 */
public class DivTag extends CompositeTag
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[] { "DIV" };

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[] { "BODY", "HTML" };

	/**
	 * Create a new div tag.
	 */
	public DivTag()
	{
	}

	/**
	 * Return the set of names handled by this tag.
	 * @return The names to be matched that create tags of this type.
	 */
	@Override
	public String[] getIds()
	{
		return(mIds);
	}

	/**
	 * Return the set of end tag names that cause this tag to finish.
	 * @return The names of following end tags that stop further scanning.
	 */
	@Override
	public String[] getEndTagEnders()
	{
		return(mEndTagEnders);
	}
}
