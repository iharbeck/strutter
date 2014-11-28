// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Derrick Oswald
//
// Revision Control Information
//
// $URL: https://htmlparser.svn.sourceforge.net/svnroot/htmlparser/trunk/lexer/src/main/java/org/htmlparser/NodeFactory.java $
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

package strutter.htmlparser;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import strutter.htmlparser.lexer.Page;
import strutter.htmlparser.nodes.NodeAttribute;
import strutter.htmlparser.nodes.RemarkNode;
import strutter.htmlparser.nodes.TagNode;
import strutter.htmlparser.nodes.TextNode;

/**
 * This interface defines the methods needed to create new nodes.
 * <p>The factory is used when lexing to generate the nodes passed
 * back to the caller. By implementing this interface, and setting
 * that concrete object as the node factory for the
 * {@link strutter.htmlparser.lexer.Lexer#setNodeFactory lexer} (perhaps via the
 * {@link Parser#setNodeFactory parser}), the way that nodes are generated
 * can be customized.</p>
 * <p>In general, replacing the factory with a custom factory is not required
 * because of the flexibility of the ---.</p>
 * <p>Creation of Text and Remark nodes is straight forward, because essentially
 * they are just sequences of characters extracted from the page. Creation of a
 * Tag node requires that the attributes from the tag be remembered as well.
 * ---
 */
public class NodeFactory
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The prototypical text node.
	 */
	protected TextNode mText;

	/**
	 * The prototypical remark node.
	 */
	protected RemarkNode mRemark;

	/**
	 * The prototypical tag node.
	 */
	protected TagNode mTag;

	/**
	 * The list of tags to return.
	 * The list is keyed by tag name.
	 */
	protected Map<String, TagNode> mBlastocyst;

	/**
	 * Create a new factory.
	 * @param empty If <code>true</code>, creates an empty factory,
	 * otherwise create a new factory with all tags registered.
	 */
	public NodeFactory()
	{
		clear();
		mText = new TextNode(null, 0, 0);
		mRemark = new RemarkNode(null, 0, 0);
		mTag = new TagNode(null, 0, 0, null);
	}

	/**
	 * Adds a tag to the registry.
	 * @param id The name under which to register the tag.
	 * <strong>For proper operation, the id should be uppercase so it
	 * will be matched by a Map lookup.</strong>
	 * @param tag The tag to be returned from a {@link #createTagNode} call.
	 * @return The tag previously registered with that id if any,
	 * or <code>null</code> if none.
	 */
	public TagNode put(String id, TagNode tag)
	{
		return(mBlastocyst.put(id, tag));
	}

	/**
	 * Gets a tag from the registry.
	 * @param id The name of the tag to return.
	 * @return The tag registered under the <code>id</code> name,
	 * or <code>null</code> if none.
	 */
	public TagNode get(String id)
	{
		return(mBlastocyst.get(id));
	}

	/**
	 * Remove a tag from the registry.
	 * @param id The name of the tag to remove.
	 * @return The tag that was registered with that <code>id</code>,
	 * or <code>null</code> if none.
	 */
	public TagNode remove(String id)
	{
		return(mBlastocyst.remove(id));
	}

	/**
	 * Clean out the registry.
	 */
	public void clear()
	{
		mBlastocyst = new Hashtable<String, TagNode>();
	}

	/**
	 * Get the list of tag names.
	 * @return The names of the tags currently registered.
	 */
	public Set<String> getTagNames()
	{
		return(mBlastocyst.keySet());
	}

	/**
	 * Register a tag.
	 * Registers the given tag under every {@link TagNode#getIds() id} that the
	 * tag has (i.e. all names returned by {@link TagNode#getIds() tag.getIds()}.
	 * <p><strong>For proper operation, the ids are converted to uppercase so
	 * they will be matched by a Map lookup.</strong>
	 * @param tag The tag to register.
	 */
	public void registerTag(TagNode tag)
	{
		String[] ids;

		ids = tag.getIds();
		for(String id : ids)
		{
			put(id.toUpperCase(Locale.ENGLISH), tag);
		}
	}

	/**
	 * Get the object that is cloned to generate text nodes.
	 * @return The prototype for {@link TextNode} nodes.
	 * @see #setTextPrototype
	 */
	public TextNode getTextPrototype()
	{
		return(mText);
	}

	/**
	 * Set the object to be used to generate text nodes.
	 * @param text The prototype for {@link TextNode} nodes.
	 * If <code>null</code> the prototype is set to the default
	 * ({@link TextNode}).
	 * @see #getTextPrototype
	 */
	public void setTextPrototype(TextNode text)
	{
		if(null == text)
		{
			mText = new TextNode(null, 0, 0);
		}
		else
		{
			mText = text;
		}
	}

	/**
	 * Get the object that is cloned to generate remark nodes.
	 * @return The prototype for {@link RemarkNode} nodes.
	 * @see #setRemarkPrototype
	 */
	public RemarkNode getRemarkPrototype()
	{
		return(mRemark);
	}

	/**
	 * Set the object to be used to generate remark nodes.
	 * @param remark The prototype for {@link RemarkNode} nodes.
	 * If <code>null</code> the prototype is set to the default
	 * ({@link RemarkNode}).
	 * @see #getRemarkPrototype
	 */
	public void setRemarkPrototype(RemarkNode remark)
	{
		if(null == remark)
		{
			mRemark = new RemarkNode(null, 0, 0);
		}
		else
		{
			mRemark = remark;
		}
	}

	/**
	 * Get the object that is cloned to generate tag nodes.
	 * Clones of this object are returned from {@link #createTagNode} when no
	 * specific tag is found in the list of registered tags.
	 * @return The prototype for {@link TagNode} nodes.
	 * @see #setTagPrototype
	 */
	public TagNode getTagPrototype()
	{
		return(mTag);
	}

	//
	// NodeFactory interface
	//

	/**
	 * Create a new string node.
	 * @param page The page the node is on.
	 * @param start The beginning position of the string.
	 * @param end The ending position of the string.
	 * @return A text node comprising the indicated characters from the page.
	 */
	public TextNode createStringNode(Page page, int start, int end)
	{
		TextNode ret;

		try
		{
			ret = (TextNode)(getTextPrototype().clone());
			ret.setPage(page);
			ret.setStartPosition(start);
			ret.setEndPosition(end);
		}
		catch(CloneNotSupportedException cnse)
		{
			ret = new TextNode(page, start, end);
		}

		return(ret);
	}

	/**
	 * Create a new remark node.
	 * @param page The page the node is on.
	 * @param start The beginning position of the remark.
	 * @param end The ending positiong of the remark.
	 * @return A remark node comprising the indicated characters from the page.
	 */
	public RemarkNode createRemarkNode(Page page, int start, int end)
	{
		RemarkNode ret;

		try
		{
			ret = (RemarkNode)(getRemarkPrototype().clone());
			ret.setPage(page);
			ret.setStartPosition(start);
			ret.setEndPosition(end);
		}
		catch(CloneNotSupportedException cnse)
		{
			ret = new RemarkNode(page, start, end);
		}

		return(ret);
	}

	/**
	 * Create a new tag node.
	 * Note that the attributes vector contains at least one element,
	 * which is the tag name (standalone attribute) at position zero.
	 * This can be used to decide which type of node to create, or
	 * gate other processing that may be appropriate.
	 * @param page The page the node is on.
	 * @param start The beginning position of the tag.
	 * @param end The ending positiong of the tag.
	 * @param attributes The attributes contained in this tag.
	 * @return A tag node comprising the indicated characters from the page.
	 */
	public TagNode createTagNode(Page page, int start, int end, Vector<NodeAttribute> attributes)
	{
		NodeAttribute attribute;
		String id;
		TagNode prototype;
		TagNode ret;

		ret = null;

		if(0 != attributes.size())
		{
			attribute = attributes.elementAt(0);
			id = attribute.getName();
			if(null != id)
			{
				try
				{
					id = id.toUpperCase(Locale.ENGLISH);
					if(!id.startsWith("/"))
					{
						if(id.endsWith("/"))
						{
							id = id.substring(0, id.length() - 1);
						}
						prototype = mBlastocyst.get(id);
						if(null != prototype)
						{
							ret = (TagNode)prototype.clone();
							ret.setPage(page);
							ret.setStartPosition(start);
							ret.setEndPosition(end);
							ret.setAttributesEx(attributes);
						}
					}
				}
				catch(CloneNotSupportedException cnse)
				{
					// default to creating a generic one
				}
			}
		}
		if(null == ret)
		{ // generate a generic node
			try
			{
				ret = (TagNode)getTagPrototype().clone();
				ret.setPage(page);
				ret.setStartPosition(start);
				ret.setEndPosition(end);
				ret.setAttributesEx(attributes);
			}
			catch(CloneNotSupportedException cnse)
			{
				ret = new TagNode(page, start, end, attributes);
			}
		}

		return(ret);
	}
}
