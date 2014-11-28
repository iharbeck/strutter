// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Derrick Oswald
//
// Revision Control Information
//
// $URL: https://htmlparser.svn.sourceforge.net/svnroot/htmlparser/trunk/lexer/src/main/java/org/htmlparser/util/sort/Sort.java $
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

package strutter.htmlparser.util.sort;

import java.util.Vector;

/**
 * A quick sort algorithm to sort Vectors or arrays.
 * Provides sort and binary search capabilities.
 *<p>
 * This all goes away in JDK 1.2.
 * <p>
 * @author James Gosling
 * @author Kevin A. Smith
 * @author Derrick Oswald
 * @version 1.4, 11 June, 1997
 */
public class Sort
{
	/**
	 * No object of this class need ever be instantiated.
	 * All methods are static.
	 */
	private Sort()
	{
	}

	/**
	 * Binary search for an object
	 * @param set The collection of <code>Ordered</code> objects.
	 * @param ref The name to search for.
	 * @param lo The lower index within which to look.
	 * @param hi The upper index within which to look.
	 * @return The index at which reference was found or is to be inserted.
	 */
	public static int bsearch(Sortable set, Ordered ref, int lo, int hi)
	{
		int num;
		int mid;
		Ordered ordered;
		int half;
		int result;
		int ret;

		ret = -1;

		num = (hi - lo) + 1;
		ordered = null;
		while((-1 == ret) && (lo <= hi))
		{
			half = num / 2;
			mid = lo + ((0 != (num & 1)) ? half : half - 1);
			ordered = set.fetch(mid, ordered);
			result = ref.compare(ordered);
			if(0 == result)
			{
				ret = mid;
			}
			else if(0 > result)
			{
				hi = mid - 1;
				num = ((0 != (num & 1)) ? half : half - 1);
			}
			else
			{
				lo = mid + 1;
				num = half;
			}
		}
		if(-1 == ret)
		{
			ret = lo;
		}

		return(ret);
	}

	/**
	 * Binary search for an object
	 * @param set The collection of <code>Ordered</code> objects.
	 * @param ref The name to search for.
	 * @return The index at which reference was found or is to be inserted.
	 */
	public static int bsearch(Sortable set, Ordered ref)
	{
		return(bsearch(set, ref, set.first(), set.last()));
	}

	/**
	 * Binary search for an object
	 * @param vector The vector of <code>Ordered</code> objects.
	 * @param ref The name to search for.
	 * @param lo The lower index within which to look.
	 * @param hi The upper index within which to look.
	 * @return The index at which reference was found or is to be inserted.
	 */
	public static int bsearch(Vector<Object> vector, Ordered ref, int lo, int hi)
	{
		int num;
		int mid;
		int half;
		int result;
		int ret;

		ret = -1;

		num = (hi - lo) + 1;
		while((-1 == ret) && (lo <= hi))
		{
			half = num / 2;
			mid = lo + ((0 != (num & 1)) ? half : half - 1);
			result = ref.compare(vector.elementAt(mid));
			if(0 == result)
			{
				ret = mid;
			}
			else if(0 > result)
			{
				hi = mid - 1;
				num = ((0 != (num & 1)) ? half : half - 1);
			}
			else
			{
				lo = mid + 1;
				num = half;
			}
		}
		if(-1 == ret)
		{
			ret = lo;
		}

		return(ret);
	}

	/**
	 * Binary search for an object
	 * @param vector The vector of <code>Ordered</code> objects.
	 * @param ref The name to search for.
	 * @return The index at which reference was found or is to be inserted.
	 */
	public static int bsearch(Vector<Object> vector, Ordered ref)
	{
		return(bsearch(vector, ref, 0, vector.size() - 1));
	}

	/**
	 * Binary search for an object
	 * @param array The array of <code>Ordered</code> objects.
	 * @param ref The name to search for.
	 * @param lo The lower index within which to look.
	 * @param hi The upper index within which to look.
	 * @return The index at which reference was found or is to be inserted.
	 */
	public static int bsearch(Ordered[] array, Ordered ref, int lo, int hi)
	{
		int num;
		int mid;
		int half;
		int result;
		int ret;

		ret = -1;

		num = (hi - lo) + 1;
		while((-1 == ret) && (lo <= hi))
		{
			half = num / 2;
			mid = lo + ((0 != (num & 1)) ? half : half - 1);
			result = ref.compare(array[mid]);
			if(0 == result)
			{
				ret = mid;
			}
			else if(0 > result)
			{
				hi = mid - 1;
				num = ((0 != (num & 1)) ? half : half - 1);
			}
			else
			{
				lo = mid + 1;
				num = half;
			}
		}
		if(-1 == ret)
		{
			ret = lo;
		}

		return(ret);
	}

	/**
	 * Binary search for an object
	 * @param array The array of <code>Ordered</code> objects.
	 * @param ref The name to search for.
	 * @return The index at which reference was found or is to be inserted.
	 */
	public static int bsearch(Ordered[] array, Ordered ref)
	{
		return(bsearch(array, ref, 0, array.length - 1));
	}
}
