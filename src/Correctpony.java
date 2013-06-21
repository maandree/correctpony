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
 * Mane class
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
     * Mane method
     * 
     * @param  args  Command line arguments, excluding exec
     * 
     * @throws  IOException  On I/O error
     */
    public static void main(final String... args) throws IOException
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
	int rcbuf = 128;
	
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
		    if (rcptr == rcbuf)
			System.arraycopy(rc, 0, rc = new String[rcbuf <<= 1], 0, rcptr);
		    file = (dir + "/" + file).replace("//", "/");
		    if ((new File(file)).isFile())
			rc[rcptr++] = file;
		}
	}
	
	System.arraycopy(rc, 0, rc = new String[rcptr], 0, rcptr);
	return rc;
    }
    
}

