// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Somik Raha
//
// Revision Control Information
//
// $URL: https://htmlparser.svn.sourceforge.net/svnroot/htmlparser/trunk/parser/src/main/java/org/htmlparser/Parser.java $
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

import java.io.Serializable;
import java.net.URLConnection;

import org.w3c.dom.traversal.NodeFilter;

import strutter.htmlparser.lexer.Lexer;
import strutter.htmlparser.lexer.Page;
import strutter.htmlparser.nodes.interfaces.Node;
import strutter.htmlparser.util.NodeIteratorImpl;
import strutter.htmlparser.util.NodeIterator;
import strutter.htmlparser.util.NodeList;
import strutter.htmlparser.util.exception.ParserException;

/**
 * The main parser class.
 * This is the primary class of the HTML Parser library. It provides
 * constructors that take a {@link #Parser(String) String},
 * a {@link #Parser(URLConnection) URLConnection}, or a
 * {@link #Parser(Lexer) Lexer}.  In the case of a String, 
 * a check is made to see if the first non-whitespace character is a &lt;, in
 * which case it is assumed to be HTML. Otherwise an
 * attempt is made to open it as a URL, and if that fails it assumes it is a
 * local disk file. If you want to parse a String after using the
 * {@link #Parser() no-args} constructor, use 
 * {@link #setInputHTML setInputHTML()}, or you can use {@link #createParser}.
 * <p>The Parser provides access to the contents of the
 * page, via a {@link #elements() NodeIterator}, a
 * {@link #parse(NodeFilter) NodeList}.
 * <p>Typical usage of the parser is:
 * <code>
 * <pre>
 * Parser parser = new Parser ("http://whatever");
 * NodeList list = parser.parse (null);
 * // do something with your list of nodes.
 * </pre>
 * </code></p>
 * <p>What types of nodes and what can be done with them is dependant on the
 * setup, but in general a node can be converted back to HTML and it's
 * children (enclosed nodes) and parent can be obtained, because nodes are
 * nested. See the {@link Node} interface.</p>
 * <p>For example, if the URL contains:<br>
 * <code>
 * {@.html
 * <html>
 * <head>
 * <title>Mondays -- What a bad idea.</title>
 * </head>
 * <body BGCOLOR="#FFFFFF">
 * Most people have a pathological hatred of Mondays...
 * </body>
 * </html>}
 * </code><br>
 * and the example code above is used, the list contain only one element, the
 * {@.html <html>} node.  This node is a {@link strutter.htmlparser.tags tag},
 * which is an object of class
 * {@link strutter.htmlparser.tags.HtmlTag Html} if the default {@link NodeFactory}
 * --- is used.</p>
 * <p>To get at further content, the children of the top
 * level nodes must be examined. When digging through a node list one must be
 * conscious of the possibility of whitespace between nodes, e.g. in the example
 * above:
 * <code>
 * <pre>
 * Node node = list.elementAt (0);
 * NodeList sublist = node.getChildren ();
 * System.out.println (sublist.size ());
 * </pre>
 * </code>
 * would print out 5, not 2, because there are newlines after {@.html <html>},
 * {@.html </head>} and {@.html </body>} that are children of the HTML node
 * besides the {@.html <head>} and {@.html <body>} nodes.</p>
 * <p>Because processing nodes is so common, two interfaces are provided to
 * ease this task, {@link strutter.htmlparser.filters filters}
 * and {@link strutter.htmlparser.visitors visitors}.
 */
public class Parser implements Serializable
{
	protected Lexer mLexer;

	//
	// Constructors
	//

	/**
	 * Zero argument constructor.
	 * The parser is in a safe but useless state parsing an empty string.
	 * Set the lexer or connection using {@link #setLexer}
	 * or {@link #setConnection}.
	 * @see #setLexer(Lexer)
	 * @see #setConnection(URLConnection)
	 */
	public Parser()
	{
		setLexer(new Lexer(new Page("")));
	}

	//
	// Bean patterns
	//

	/**
	 * Set the encoding for the page this parser is reading from.
	 * @param encoding The new character set to use.
	 * @throws ParserException If the encoding change causes characters that
	 * have already been consumed to differ from the characters that would
	 * have been seen had the new encoding been in force.
	 * @see #getEncoding
	 */
	public void setEncoding(String encoding) throws ParserException
	{
		mLexer.getPage().setEncoding(encoding);
	}

	/**
	 * Get the encoding for the page this parser is reading from.
	 * This item is set from the HTTP header but may be overridden by meta
	 * tags in the head, so this may change after the head has been parsed.
	 * @return The encoding currently in force.
	 * @see #setEncoding
	 */
	public String getEncoding()
	{
		return(mLexer.getPage().getEncoding());
	}

	/**
	 * Set the lexer for this parser.
	 * The current NodeFactory is transferred to (set on) the given lexer,
	 * since the lexer owns the node factory object.
	 * It does not adjust the <code>feedback</code> object.
	 * @param lexer The lexer object to use.
	 * @see #setNodeFactory
	 * @see #getLexer
	 * @exception IllegalArgumentException if <code>lexer</code> is <code>null</code>.
	 */
	public void setLexer(Lexer lexer)
	{
		NodeFactory factory;

		if(null == lexer)
			throw new IllegalArgumentException("lexer cannot be null");
		// move a node factory that's been set to the new lexer
		factory = null;
		if(null != mLexer)
			factory = mLexer.getNodeFactory();
		if(null != factory)
			lexer.setNodeFactory(factory);
		mLexer = lexer;
	}

	/**
	 * Get the current node factory.
	 * @return The current lexer's node factory.
	 * @see #setNodeFactory
	 */
	public NodeFactory getNodeFactory()
	{
		return(mLexer.getNodeFactory());
	}

	/**
	 * Set the current node factory.
	 * @param factory The new node factory for the current lexer.
	 * @see #getNodeFactory
	 * @exception IllegalArgumentException if <code>factory</code> is <code>null</code>.
	 */
	public void setNodeFactory(NodeFactory factory)
	{
		if(null == factory)
			throw new IllegalArgumentException("node factory cannot be null");
		mLexer.setNodeFactory(factory);
	}

	//
	// Public methods
	//

	/**
	 * Reset the parser to start from the beginning again.
	 * This assumes support for a reset from the underlying
	 * {@link strutter.htmlparser.lexer.Source} object.
	 * <p>This is cheaper (in terms of time) than resetting the URL, i.e.
	 * <pre>
	 * parser.setURL (parser.getURL ());
	 * </pre>
	 * because the page is not refetched from the internet.
	 * <em>Note: the nodes returned on the second parse are new
	 * nodes and not the same nodes returned on the first parse. If you
	 * want the same nodes for re-use, collect them in a NodeList with
	 * {@link #parse(NodeFilter) parse(null)} and operate on the NodeList.</em>
	 */
	public void reset()
	{
		mLexer.reset();
	}

	/**
	 * Returns an iterator (enumeration) over the html nodes.
	 * {@link strutter.htmlparser.nodes Nodes} can be of three main types:
	 * <ul>
	 * <li>{@link strutter.htmlparser.nodes.TagNode TagNode}</li>
	 * <li>{@link strutter.htmlparser.nodes.TextNode TextNode}</li>
	 * <li>{@link strutter.htmlparser.nodes.RemarkNode RemarkNode}</li>
	 * </ul>
	 * In general, when parsing with an iterator or processing a NodeList,
	 * you will need to use recursion. For example:
	 * <code>
	 * <pre>
	 * void processMyNodes (Node node)
	 * {
	 *     if (node instanceof TextNode)
	 *     {
	 *         // downcast to TextNode
	 *         TextNode text = (TextNode)node;
	 *         // do whatever processing you want with the text
	 *         System.out.println (text.getText ());
	 *     }
	 *     if (node instanceof RemarkNode)
	 *     {
	 *         // downcast to RemarkNode
	 *         RemarkNode remark = (RemarkNode)node;
	 *         // do whatever processing you want with the comment
	 *     }
	 *     else if (node instanceof TagNode)
	 *     {
	 *         // downcast to TagNode
	 *         TagNode tag = (TagNode)node;
	 *         // do whatever processing you want with the tag itself
	 *         // ...
	 *         // process recursively (nodes within nodes) via getChildren()
	 *         NodeList nl = tag.getChildren ();
	 *         if (null != nl)
	 *             for (NodeIterator i = nl.elements (); i.hasMoreElements (); )
	 *                 processMyNodes (i.nextNode ());
	 *     }
	 * }
	 *
	 * Parser parser = new Parser ("http://www.yahoo.com");
	 * for (NodeIterator i = parser.elements (); i.hasMoreElements (); )
	 *     processMyNodes (i.nextNode ());
	 * </pre>
	 * </code>
	 * @throws ParserException If a parsing error occurs.
	 * @return An iterator over the top level nodes (usually {@.html <html>}).
	 */
	public NodeIterator elements() throws ParserException
	{
		return(new NodeIteratorImpl(mLexer));
	}

	/**
	 * Parse the given resource, using the filter provided.
	 * This can be used to extract information from specific nodes.
	 * When used with a <code>null</code> filter it returns an
	 * entire page which can then be modified and converted back to HTML
	 * (Note: the synthesis use-case is not handled very well; the parser
	 * is more often used to extract information from a web page).
	 * <p>For example, to replace the entire contents of the HEAD with a
	 * single TITLE tag you could do this:
	 * <pre>
	 * NodeList nl = parser.parse (null); // here is your two node list
	 * NodeList heads = nl.extractAllNodesThatMatch (new TagNameFilter ("HEAD"))
	 * if (heads.size () > 0) // there may not be a HEAD tag
	 * {
	 *     Head head = heads.elementAt (0); // there should be only one
	 *     head.removeAll (); // clean out the contents
	 *     Tag title = new TitleTag ();
	 *     title.setTagName ("title");
	 *     title.setChildren (new NodeList (new TextNode ("The New Title")));
	 *     Tag title_end = new TitleTag ();
	 *     title_end.setTagName ("/title");
	 *     title.setEndTag (title_end);
	 *     head.add (title);
	 * }
	 * System.out.println (nl.toHtml ()); // output the modified HTML
	 * </pre>
	 * @return The list of matching nodes (for a <code>null</code>
	 * filter this is all the top level nodes).
	 * @param filter The filter to apply to the parsed nodes,
	 * or <code>null</code> to retrieve all the top level nodes.
	 * @throws ParserException If a parsing error occurs.
	 */
	public NodeList parse() throws ParserException
	{
		NodeIterator e;
		Node node;
		NodeList ret;

		ret = new NodeList();
		for(e = elements(); e.hasMoreNodes();)
		{
			node = e.nextNode();
			ret.add(node);
		}

		return(ret);
	}

	/**
	 * Initializes the parser with the given input HTML String.
	 * @param inputHTML the input HTML that is to be parsed.
	 * @throws ParserException If a error occurs in setting up the
	 * underlying Lexer.
	 * @exception IllegalArgumentException if <code>inputHTML</code> is <code>null</code>.
	 */
	public void setInputHTML(String inputHTML) throws ParserException
	{
		if(null == inputHTML)
			throw new IllegalArgumentException("html cannot be null");
		if(!"".equals(inputHTML))
			setLexer(new Lexer(new Page(inputHTML)));
	}
}
