// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Somik Raha
//
// Revision Control Information
//
// $URL: https://htmlparser.svn.sourceforge.net/svnroot/htmlparser/trunk/parser/src/main/java/org/htmlparser/util/IteratorImpl.java $
// $Author: derrickoswald $
// $Date: 2006-09-16 16:44:17 +0200 (Sa, 16 Sep 2006) $
// $Revision: 4 $
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

package strutter.htmlparser.util;

import strutter.htmlparser.lexer.Cursor;
import strutter.htmlparser.lexer.Lexer;
import strutter.htmlparser.lexer.Page;
import strutter.htmlparser.nodes.Node;
import strutter.htmlparser.nodes.TagNode;
import strutter.htmlparser.scanners.Scanner;
import strutter.htmlparser.util.NodeIterator;
import strutter.htmlparser.util.exception.ParserException;

public class NodeIteratorImpl implements NodeIterator
{
	Lexer mLexer;
	Cursor mCursor;

	public NodeIteratorImpl(Lexer lexer)
	{
		mLexer = lexer;
		mCursor = new Cursor(mLexer.getPage(), 0);
	}

	/**
	 * Check if more nodes are available.
	 * @return <code>true</code> if a call to <code>nextNode()</code> will succeed.
	 */
	public boolean hasMoreNodes() throws ParserException
	{
		boolean ret;

		mCursor.setPosition(mLexer.getPosition());
		ret = Page.EOF != mLexer.getPage().getCharacter(mCursor); // more characters?

		return(ret);
	}

	/**
	 * Get the next node.
	 * @return The next node in the HTML stream, or null if there are no more nodes.
	 * @exception ParserException If an unrecoverable error occurs.
	 */
	public Node nextNode() throws ParserException
	{
		TagNode tag;
		Scanner scanner;
		NodeList stack;
		Node ret;

		try
		{
			ret = mLexer.nextNode();
			if(null != ret)
			{
				// kick off recursion for the top level node
				if(ret instanceof TagNode)
				{
					tag = (TagNode)ret;
					if(!tag.isEndTag())
					{
						// now recurse if there is a scanner for this type of tag
						scanner = tag.getThisScanner();
						if(null != scanner)
						{
							stack = new NodeList();
							ret = scanner.scan(tag, mLexer, stack);
						}
					}
				}
			}
		}
		catch(ParserException pe)
		{
			throw pe; // no need to wrap an existing ParserException
		}
		catch(Exception e)
		{
			StringBuffer msgBuffer = new StringBuffer();
			msgBuffer.append("Unexpected Exception occurred while reading ");
			msgBuffer.append(mLexer.getPage().getUrl());
			msgBuffer.append(", in nextNode");
			// TODO: appendLineDetails (msgBuffer);
			ParserException ex = new ParserException(msgBuffer.toString(), e);
			throw ex;
		}

		return(ret);
	}
}
