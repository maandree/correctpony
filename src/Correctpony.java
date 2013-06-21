/**
 * correctpony — a passphrase generator inspired by xkcd 936
 * 
 * Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.*;
import java.util.*;


/**
 * Mane class for correctpony
 * 
 * Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class Correctpony
{
    /**
     * ‘:’ separated list of directionary directories
     */
    public static final String DICT_DIRS = "/usr/share/dict:/usr/local/share/dict:~/share/dict";
    
    /**
     * The version of the program
     */
    public static final String VERSION = "2.0";
    
    /**
     * Default word separators
     */
    public static final String[] DEFAULT_SEPARATORS = { " " };
    
    /**
     * Default dictionaries
     */
    public static final String[] DEFAULT_DICTIONARIES = null;
    
    
    
    /**
     * Mane method
     * 
     * @param  args  Command line arguments, excluding exec
     * 
     * @throws  IOException  On I/O error
     */
    public static void main(String... args) throws IOException
    {
    }
    
    
    
    /**
     * Gets all dictionary files
     * 
     * @return  All dictionary files
     * 
     * @throws  IOException  On I/O error
     */
    public static String[] getDictionaries() throws IOException
    {
	String[] rc = new String[128];
	int rcptr = 0;
	
	String HOME = System.getenv("HOME");
	if ((HOME == null) || (HOME.length() == 0))
	    HOME = System.getProperty("user.home");
	
	for (String dir : DICT_DIRS.split(":"))
	{
	    if (dir.startsWith("~"))
		dir = HOME + dir.substring(1);
	    File fdir = new File(dir);
	    if (fdir.exists())
		fdir = fdir.getCanonicalFile();
	    if (fdir.exists() && fdir.isDirectory())
		for (String file : fdir.list())
		{
		    if (rcptr == rc.length)
			System.arraycopy(rc, 0, rc = new String[rc.length << 1], 0, rcptr);
		    file = (dir + "/" + file).replace("//", "/");
		    if ((new File(file)).isFile())
			rc[rcptr++] = file;
		}
	}
	
	System.arraycopy(rc, 0, rc = new String[rcptr], 0, rcptr);
	return rc;
    }
    
    
    /**
     * Gets all unique words in any number of dictionaries
     * 
     * @param   dictionaries  Dictionaries by filename, {@code null} for all
     * @return                Unique words, in lower case
     * 
     * @throws  IOException  On I/O error
     */
    public static String[] getWords(String[] dictionaries) throws IOException
    {
	if (dictionaries == null)
	    dictionaries = getDictionaries();
	
	final HashSet<String> unique = new HashSet<String>();
	for (final String dictionary : dictionaries)
	    {
		final InputStream is = new FileInputStream(dictionary);
		byte[] buf;
		int ptr;
		try
		{   buf = new byte[is.available()];
		    if (buf.length == 0)
			buf = new byte[1 << 13];
		    forever:
		        for (int n;;)
			{   while (ptr < buf.length)
			    {
				ptr += n = is.read(buf, ptr, buf.length - ptr);
				if (n <= 0)
				    break forever;
			    }
			    System.arraycopy(buf, 0, buf = new String[buf.length << 1], 0, ptr);
		}	}
		finally
		{   try
		    {   is.close();
		    }
		    catch (final Throwable ignore)
		    {   /* ignore */
		}   }
		for (final String word : (new String(buf, 0, ptr, "UTF-8")).split("\n"))
		    if (word.length() != 0)
			unique.add(word.toLowerCase());
	    }
	
	final String[] rc = new String[unique.size()];
	int ptr = 0;
	for (final String word : unique)
	     rc[ptr++] = word;
	return rc;
    }
    
    
    /**
     * Gets a dictionary's filename by its name
     * 
     * @parma   dictionaries  The dictionary by names
     * @param   files         All dictionaries by filename
     * @return                The found dictionaries by filename
     * 
     * @throws  IOException  On I/O error
     */
    public static String[] getFiles(String[] dictionaries, String[] files) throws IOException
    {
	final String[] candidates = new String[files.length];
	for (int i = 0, n = files.length; i < n; i++)
	    candidates[i] = "/" + files[i];
	
	String[] rc = new String[dicts.length];
	int ptr = 0;
	
	for (String dict : dicts)
	{   dict = "/" + dict;
	    for (final String candidate : candidates)
		if (candidate.endsWith(dict))
		{
		    if (ptr == rc.length)
			System.arraycopy(rc, 0, rc = new String[rc.length << 1], 0, ptr);
		    rc[ptr++] = candidate;
	}	}
	
	System.arraycopy(rc, 0, rc = new String[ptr], 0, ptr);
	return rc;
    }
    
    
    /**
     * Joins words
     * 
     * @param   words       The words to join
     * @param   separators  Separators
     * @param   colour      Whether to use colours
     * @return              The result
     * 
     * @throws  IOException  On I/O error
     */
    public static String join(String[] words, String[] separators, boolean colour) throws IOException
    {
	final StringBuilder rc = new StringBuilder();
	int i = 0;
	for (final String word : words)
	{
	    if (i > 0)
	    {	if (colour)
		    rc.append("\033[31m");
		rc.append(separators[random(separators.length)]);
	    }
	    if (colour)
		rc.append((i & 1) == 0 ? "\033[34m" : "\033[33m");
	    rc.append(word);
	    i++;
	}
	if (colour)
	    rc.append("\033[00m");
	return rc.toString();
    }
    
    
    /**
     * Gets a random integer
     * 
     * @param   max  The exclusive upper bound
     * @return       A random non-negative integer
     * 
     * @throws  IOException  On I/O error
     */
    public static int random(int max) throws IOException
    {
	return 0;
    }
    
    
}

