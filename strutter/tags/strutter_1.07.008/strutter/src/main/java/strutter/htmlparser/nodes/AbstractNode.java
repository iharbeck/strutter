// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Somik Raha
//
// Revision Control Information
//
// $URL: https://htmlparser.svn.sourceforge.net/svnroot/htmlparser/trunk/lexer/src/main/java/org/htmlparser/nodes/AbstractNode.java $
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

package strutter.htmlparser.nodes;

import java.io.Serializable;

import strutter.htmlparser.lexer.Page;
import strutter.htmlparser.util.NodeList;
import strutter.htmlparser.util.SimpleNodeIterator;
import strutter.htmlparser.util.exception.ParserException;

/**
 * The concrete base class for all types of nodes (tags, text remarks).
 * This class provides basic functionality to hold the {@link Page}, the
 * starting and ending position in the page, the parent and the list of
 * {@link NodeList children}.
 */
public abstract class AbstractNode implements Serializable, Node
{
	/**
	 * The page this node came from.
	 */
	protected Page mPage;

	/**
	 * The beginning position of the tag in the line
	 */
	protected int nodeBegin;

	/**
	 * The ending position of the tag in the line
	 */
	protected int nodeEnd;

	/**
	 * The parent of this node.
	 */
	protected Node parent;

	/**
	 * The children of this node.
	 */
	protected NodeList children;

	/**
	 * Create an abstract node with the page positions given.
	 * Remember the page and start & end cursor positions.
	 * @param page The page this tag was read from.
	 * @param start The starting offset of this node within the page.
	 * @param end The ending offset of this node within the page.
	 */
	public AbstractNode(Page page, int start, int end)
	{
		mPage = page;
		nodeBegin = start;
		nodeEnd = end;
		parent = null;
		children = null;
	}

	/**
	 * Clone this object.
	 * Exposes java.lang.Object clone as a public method.
	 * @return A clone of this object.
	 * @exception CloneNotSupportedException This shouldn't be thrown since
	 * the {@link Node} interface extends Cloneable.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return(super.clone());
	}

	/**
	 * Returns a string representation of the node.
	 * It allows a simple string transformation
	 * of a web page, regardless of node type.<br>
	 * Typical application code (for extracting only the text from a web page)
	 * would then be simplified to:<br>
	 * <pre>
	 * Node node;
	 * for (Enumeration e = parser.elements (); e.hasMoreElements (); )
	 * {
	 *     node = (Node)e.nextElement();
	 *     System.out.println (node.toPlainTextString ());
	 *     // or do whatever processing you wish with the plain text string
	 * }
	 * </pre>
	 * @return The 'browser' content of this node.
	 */
	public abstract String toPlainTextString();

	/**
	 * Return the HTML for this node.
	 * This should be the sequence of characters that were encountered by
	 * the parser that caused this node to be created. Where this breaks down is
	 * where broken nodes (tags and remarks) have been encountered and fixed.
	 * Applications reproducing html can use this method on nodes which are to
	 * be used or transferred as they were received or created.
	 * @return The sequence of characters that would cause this node
	 * to be returned by the parser or lexer.
	 */
	public String toHtml()
	{
		return(toHtml(false));
	}

	/**
	 * Return the HTML for this node.
	 * This should be the exact sequence of characters that were encountered by
	 * the parser that caused this node to be created. Where this breaks down is
	 * where broken nodes (tags and remarks) have been encountered and fixed.
	 * Applications reproducing html can use this method on nodes which are to
	 * be used or transferred as they were received or created.
	 * @param verbatim If <code>true</code> return as close to the original
	 * page text as possible.
	 * @return The (exact) sequence of characters that would cause this node
	 * to be returned by the parser or lexer.
	 */
	public abstract String toHtml(boolean verbatim);

	/**
	 * Return a string representation of the node.
	 * Subclasses must define this method, and this is typically to be used in the manner<br>
	 * <pre>System.out.println(node)</pre>
	 * @return A textual representation of the node suitable for debugging
	 */
	public abstract String toString();

	/**
	 * Get the page this node came from.
	 * @return The page that supplied this node.
	 */
	public Page getPage()
	{
		return(mPage);
	}

	/**
	 * Set the page this node came from.
	 * @param page The page that supplied this node.
	 */
	public void setPage(Page page)
	{
		mPage = page;
	}

	/**
	 * Gets the starting position of the node.
	 * @return The start position.
	 */
	public int getStartPosition()
	{
		return(nodeBegin);
	}

	/**
	 * Sets the starting position of the node.
	 * @param position The new start position.
	 */
	public void setStartPosition(int position)
	{
		nodeBegin = position;
	}

	/**
	 * Gets the ending position of the node.
	 * @return The end position.
	 */
	public int getEndPosition()
	{
		return(nodeEnd);
	}

	/**
	 * Sets the ending position of the node.
	 * @param position The new end position.
	 */
	public void setEndPosition(int position)
	{
		nodeEnd = position;
	}

	/**
	 * Get the parent of this node.
	 * This will always return null when parsing without scanners,
	 * i.e. if semantic parsing was not performed.
	 * The object returned from this method can be safely cast to a <code>CompositeTag</code>.
	 * @return The parent of this node, if it's been set, <code>null</code> otherwise.
	 */
	public Node getParent()
	{
		return(parent);
	}

	/**
	 * Sets the parent of this node.
	 * @param node The node that contains this node. Must be a <code>CompositeTag</code>.
	 */
	/* See bug: https://sourceforge.net/tracker/?func=detail&aid=1755537&group_id=24399&atid=381399
	 * A check needs to be performed to see that a tag cannot be its own parent or child and if it
	 * is then just ignore it 
	 */
	public void setParent(Node node)
	{
		if(this != node)
		{
			parent = node;
		}
	}

	/**
	 * Get the children of this node.
	 * @return The list of children contained by this node, if it's been set, <code>null</code> otherwise.
	 */
	public NodeList getChildren()
	{
		return(children);
	}

	/**
	 * Set the children of this node.
	 * @param children The new list of children this node contains.
	 */
	/* See bug: https://sourceforge.net/tracker/?func=detail&aid=1755537&group_id=24399&atid=381399
	 * A check needs to be performed to see that a tag cannot be its own parent
	 * or child and if it is the case then just ignore it 
	 */
	public void setChildren(NodeList children)
	{
		/* Always Initialize the children field as in the constructor its being
		 * initialized to null
		 */
		this.children = new NodeList();
		/* Do nothing if the children node list contains the node
		 * (i.e. the node whose children is being set) itself
		 */
		for(SimpleNodeIterator it = children.elements(); it.hasMoreNodes();)
		{
			Node nodetoadd = it.nextNode();
			if(this != nodetoadd)
			{
				this.children.add(nodetoadd);
			}
		}
		//this.children = children;
	}

	/**
	 * Get the first child of this node.
	 * @return The first child in the list of children contained by this node,
	 * <code>null</code> otherwise.
	 */
	public Node getFirstChild()
	{
		if(children == null)
			return null;
		if(children.size() == 0)
			return null;
		return children.elementAt(0);
	}

	/**
	 * Get the last child of this node.
	 * @return The last child in the list of children contained by this node,
	 * <code>null</code> otherwise.
	 */
	public Node getLastChild()
	{
		if(children == null)
			return null;
		int numChildren = children.size();
		if(numChildren == 0)
			return null;
		return children.elementAt(numChildren - 1);
	}

	/**
	 * Get the previous sibling to this node.
	 * @return The previous sibling to this node if one exists,
	 * <code>null</code> otherwise.
	 */
	public Node getPreviousSibling()
	{
		Node parentNode = this.getParent();
		if(parentNode == null)//root node
			return null;
		NodeList siblings = parentNode.getChildren();
		if(siblings == null)//this should actually be an error
			return null;
		int numSiblings = siblings.size();
		if(numSiblings < 2)//need at least one other node to have a chance of having any siblings
			return null;
		int positionInParent = -1;
		for(int i = 0; i < numSiblings; i++)
		{
			if(siblings.elementAt(i) == this)
			{
				positionInParent = i;
				break;
			}
		}
		if(positionInParent < 1)//no previous siblings
			return null;
		return siblings.elementAt(positionInParent - 1);
	}

	/**
	 * Get the next sibling to this node.
	 * @return The next sibling to this node if one exists,
	 * <code>null</code> otherwise.
	 */
	public Node getNextSibling()
	{
		Node parentNode = this.getParent();
		if(parentNode == null)//root node
			return null;
		NodeList siblings = parentNode.getChildren();
		if(siblings == null)//this should actually be an error
			return null;
		int numSiblings = siblings.size();
		if(numSiblings < 2)//need at least one other node to have a chance of having any siblings
			return null;
		int positionInParent = -1;
		for(int i = 0; i < numSiblings; i++)
		{
			if(siblings.elementAt(i) == this)
			{
				positionInParent = i;
				break;
			}
		}
		if(positionInParent == -1)//this should actually be an error
			return null;
		if(positionInParent == (numSiblings - 1))//no next sibling
			return null;
		return siblings.elementAt(positionInParent + 1);
	}

	/**
	 * Returns the text of the node.
	 * @return The text of this node. The default is <code>null</code>.
	 */
	public String getText()
	{
		return null;
	}

	/**
	 * Sets the string contents of the node.
	 * @param text The new text for the node.
	 */
	public void setText(String text)
	{
	}

	/**
	 * Perform the meaning of this tag.
	 * The default action is to do nothing.
	 * @exception ParserException <em>Not used.</em> Provides for subclasses
	 * that may want to indicate an exceptional condition.
	 */
	public void doSemanticAction()
	        throws
	        ParserException
	{
	}
}
