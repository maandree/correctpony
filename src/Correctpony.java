/**
 * correctpony — a passphrase generator inspired by xkcd 936
 * 
 * Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import argparser.ArgParser;

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
    /* The makefile changes this line [DICT_DIRS], do not edit the value or name.  */
    
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
	String usage = "[-p] [-r DEVICE] [-j...] [-s SEP...] [-u] [-c COUNT] [-w COUNT] [-i WORD...] [-l LIST...] [COUNT | -z]\n"
	             + "-h | --copying | --warranty | --version | --wordlists [--full] | --wordcount [-l LIST...]";
	for (final String symbol : new String[] { "[", "]", "(", ")", "|" })
	    usage = usage.replace(symbol, "\033[02m" + symbol + "\033[22m");
	usage = "\033[34mcorrectpony\033[00m " + usage.replace("\n", "\n\033[34mcorrectpony\033[00m ");
	ArgParser parser = new ArgParser("passphrase generator inspired by xkcd 936", usage,
					 "correctpony generates a phrase with real words, making it easy to remember\n" + 
					 "but hard to brute force. But the strenghten your security you may want to\n" +
					 "do an entroy check on the phrase, it should be enought to just read the phrase\n" +
					 "and make sure no close pairs is a common combination in read text. And if you\n" +
					 "are paranoid do one or two simple modifications to the generated phrase.");
	
	parser.add(new ArgParser.Argumentless(           1, "-h", "--help"),       "Print this help information");
	parser.add(new ArgParser.Argumentless(           0, "--copying"),          "Print copyright information");
	parser.add(new ArgParser.Argumentless(           0, "--warranty"),         "Print warranty information");
	parser.add(new ArgParser.Argumentless(           0, "--version"),          "Print the program's name and version");
	parser.add(new ArgParser.Argumentless(           0, "--wordlists"),        "Print all available wordlists");
	parser.add(new ArgParser.Argumentless(           0, "--full"),             "Print full file path");
	parser.add(new ArgParser.Argumentless(           0, "--wordcount"),        "Print the number of unique words available");
	parser.add(new ArgParser.Argumentless(           1, "-p", "--nocolour"),   "Do not print with colours");
	parser.add(new ArgParser.Argumented("DEVICE",    1, "-r", "--random"),     "Random number generator to use");
	parser.add(new ArgParser.Argumentless(           1, "-j", "--join"),       "Add word joining as a separator");
	parser.add(new ArgParser.Argumented("SEPARATOR", 1, "-s", "--separator"),  "Add a separator");
	parser.add(new ArgParser.Argumentless(           1, "-u", "--camelcase"),  "Capitalise first letter of each word");
	parser.add(new ArgParser.Argumented("COUNT",     1, "-c", "--characters"), "Least number of characters");
	parser.add(new ArgParser.Argumented("COUNT",     1, "-w", "--words"),      "Least number of words");
	parser.add(new ArgParser.Argumented("WORD",      1, "-i", "--include"),    "Word that must be included");
	parser.add(new ArgParser.Argumented("WORDLIST",  1, "-l", "--list"),       "Word list to use");
	parser.add(new ArgParser.Argumentless(           1, "-z", "--security"),   "Show stats and security estimates");
	
	parser.parse(args);
	final Map<String, String[]> opts = parser.opts;
	
	parser.testFiles(0, opts.get("--security") != null ? 0 : 1, 1);
	
	
	if (opts.get("--help") != null)
	{   parser.help();
	    return;
	}
	if (opts.get("--version") != null)
	{   System.out.println("correctpony " + VERSION);
	    return;
	}
	if (opts.get("--copying") != null)
	{
	    System.out.println("correctpony — a passphrase generator inspired by xkcd 936\n");
	    System.out.println("\n");
	    System.out.println("Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)\n");
	    System.out.println("\n");
	    System.out.println("This program is free software: you can redistribute it and/or modify\n");
	    System.out.println("it under the terms of the GNU Affero General Public License as published by\n");
	    System.out.println("the Free Software Foundation, either version 3 of the License, or\n");
	    System.out.println("(at your option) any later version.\n");
	    System.out.println("\n");
	    System.out.println("This program is distributed in the hope that it will be useful,\n");
	    System.out.println("but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
	    System.out.println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n");
	    System.out.println("GNU Affero General Public License for more details.\n");
	    System.out.println("\n");
	    System.out.println("You should have received a copy of the GNU Affero General Public License\n");
	    System.out.println("along with this program.  If not, see <http://www.gnu.org/licenses/>.\n");
	    return;
	}
	if (opts.get("--warranty") != null)
	{
	    System.out.println("This program is distributed in the hope that it will be useful,\n");
	    System.out.println("but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
	    System.out.println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n");
	    System.out.println("GNU Affero General Public License for more details.\n");
	    return;
	}
	if (opts.get("--wordlists") != null)
	{
	    final boolean full = opts.get("--full") != null;
	    for (final String dictionary : getDictionaries())
		if (full)
		    System.out.println(dictionary);
		else
		    System.out.println(dictionary.substring(dictionary.lastIndexOf('/') + 1));
	    return;
	}
	if (opts.get("--wordcount") != null)
	{
	    String[] wordlists = new String[0];
	    if (opts.get("--list") != null)
		for (final String list : opts.get("--list"))
		{   System.arraycopy(wordlists, 0, wordlists = new String[wordlists.length + 1], 0, wordlists.length - 1);
		    wordlists[wordlists.length - 1] = list;
		}
	    String[] dictionaries = getDictionaries();
	    if (wordlists.length != 0)
		dictionaries = getFiles(wordlists, dictionaries);
	    final String[] dictionary = getWords(dictionaries);
	    System.out.println(dictionary.length);
	    return;
	}
	
	
	final boolean colours = opts.get("--nocolour") == null;
	final boolean camelcase = opts.get("--camelcase") != null;
	String[] separators = new String[0];
	String[] words = new String[0];
	String[] wordlists = new String[0];
	int minChars = opts.get("--characters") != null ? Integer.parseInt(opts.get("--characters")[0]) : 12;
	int minWords = opts.get("--words") != null ? Integer.parseInt(opts.get("--words")[0]) : 4;
	int count = parser.files.size() == 0 ? 1 : Integer.parseInt(parser.files.get(0));
	String randomgen = opts.get("--random") != null ? opts.get("--random")[0] : "/dev/urandom";
	
	if (opts.get("--join") != null)
	    for (final String _ : opts.get("--join"))
	    {	System.arraycopy(separators, 0, separators = new String[separators.length + 1], 0, separators.length - 1);
		separators[separators.length - 1] = "";
	    }
	
	if (opts.get("--separator") != null)
	    for (final String separator : opts.get("--separator"))
	    {	System.arraycopy(separators, 0, separators = new String[separators.length + 1], 0, separators.length - 1);
		separators[separators.length - 1] = separator;
	    }
	
	if (opts.get("--include") != null)
	    for (final String word : opts.get("--include"))
	    {	System.arraycopy(words, 0, words = new String[words.length + 1], 0, words.length - 1);
		words[words.length - 1] = word;
	    }
	
	if (opts.get("--list") != null)
	    for (final String list : opts.get("--list"))
	    {	System.arraycopy(wordlists, 0, wordlists = new String[wordlists.length + 1], 0, wordlists.length - 1);
		wordlists[wordlists.length - 1] = list;
	    }
	
	if (separators.length == 0)
	    separators = DEFAULT_SEPARATORS;
	if (wordlists.length == 0)
	    wordlists = null;
	
	
	if (opts.get("--security") != null)
	{
	    String[] dictionaries = getDictionaries();
	    if (wordlists != null)
		dictionaries = getFiles(wordlists, dictionaries);
	    final String[] dictionary = getWords(dictionaries);
	    final int wordcount = dictionary.length;
	    final int procent = wordcount / 5000;
	    
	    System.out.println();
	    System.out.println("You have a total of " + wordcount + " words in all your selected word lists,");
	    System.out.println("The English language contains about 500000 words. That is c:a " + procent + " %.");
	    System.out.println();
	    
	    double time = (double)wordcount;
	    int minwords = Math.max(0, minWords - words.length);
	    time = Math.pow(time, minwords);
	    for (int i = 1; i <= words.length; i++)
		time *= minwords + i;
	    time *= Math.pow(separators.length, minwords + words.length - 1);
	    time /= 60 * 60 * 24 * 365.2425;
	    
	    System.out.println("Trying to crack the passphrase knowing all settings and the dictionaries,");
	    System.out.println("it would take about " + time / 1000000 + " years at most, making 1 000 000 guesses per seconds.");
	    System.out.println();
	    
	    System.out.println("Trying to crack the passphrase knowing all settings and the dictionaries,");
	    System.out.println("it would take about " + time / 1000 + " years at most, making 1000 guesses per seconds.");
	    System.out.println();
	    
	    System.out.println("Trying to crack the passphrase knowing all settings and the dictionaries,");
	    System.out.println("it would take about " + time * 5 + " years at most, making 1 guess every fifth second.");
	    System.out.println();
	    
	    return;
	}
	
	Correctpony.randomgen = new FileInputStream(randomgen);
	try
	{   final String[] passphrases = generatePassphrases(colours, camelcase, separators, words, wordlists, minChars, minWords, count);
	    for (final String passphrase : passphrases)
		System.out.println(passphrase);
	}
	finally
	{    Correctpony.randomgen.close();
	}
    }
    
    
    
    /**
     * Random number generatore character device input stream
     */
    public static InputStream randomgen = null;
    
    
    
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
     * Gets dictionaries' filename by they name
     * 
     * @parma   dictionaries  The dictionaries by names
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
	
	String[] rc = new String[dictionaries.length];
	int ptr = 0;
	
	for (String dictionary : dictionaries)
	{   dictionary = "/" + dictionary;
	    for (final String candidate : candidates)
		if (candidate.endsWith(dictionary))
		{
		    if (ptr == rc.length)
			System.arraycopy(rc, 0, rc = new String[rc.length << 1], 0, ptr);
		    rc[ptr++] = candidate;
	}	}
	
	System.arraycopy(rc, 0, rc = new String[ptr], 0, ptr);
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
		int ptr = 0;
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
			    System.arraycopy(buf, 0, buf = new byte[buf.length << 1], 0, ptr);
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
			unique.add(word/*.toLowerCase()*/);
	    }
	
	final String[] rc = new String[unique.size()];
	int ptr = 0;
	for (final String word : unique)
	     rc[ptr++] = word;
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
	if (colour)
	    rc.append("\033[01m");
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
	int rc = Correctpony.randomgen.read() & 127;
	rc = (rc << 8) | Correctpony.randomgen.read();
	rc = (rc << 8) | Correctpony.randomgen.read();
	rc = (rc << 8) | Correctpony.randomgen.read();
	if (rc == -1)
	{   System.err.println("End of random number generator file stream reached. USE A REAL RANDOM DEVICE!");
	    System.exit(2);
	}
	return rc % max;
    }
    
    
    /**
     * Generate passphrases
     * 
     * @param  colours     Include colours
     * @param  camelcase   Capitalise first letter of each word
     * @param  separators  Words separators
     * @param  words       Words that must be included
     * @param  wordlists   Word lists to use
     * @param  minChars    Minimum lenght
     * @param  minWords    Minimum number of words
     * @param  count       Number of passphrases to generate
     * 
     * @throws  IOException  On I/O error
     */
    public static String[] generatePassphrases(boolean colours, boolean camelcase, String[] separators, String[] words, String[] wordlists, int minChars, int minWords, int count) throws IOException
    {
	String[] dictionaries = getDictionaries();
	if (wordlists != null)
	    dictionaries = getFiles(wordlists, dictionaries);
	final String[] dictionary = getWords(dictionaries);
	
	int joinMinimum = (1 << 31) - 1;
	for (final String separator : separators)
	    if (joinMinimum > length(separator))
		joinMinimum = length(separator);
	
	final String[] passphrases = new String[count];
	for (int i = 0; i < count; i++)
	{
	    final ArrayList<String> passphrase = new ArrayList<String>();
	    int passphraseLength = -joinMinimum;
	    
	    minWords -= words.length;
	    for (final String word : words)
		minChars -= length(word);
	    minChars -= passphraseLength * words.length;
	    
	    while ((passphraseLength < minChars) || (passphrase.size() < minWords))
	    {	final String word = dictionary[random(dictionary.length)].toLowerCase();
		passphraseLength += length(word) + joinMinimum;
		passphrase.add(word);
	    }
	    
	    for (final String word : words)
	    {	int position = random(passphrase.size() + 1);
		passphrase.add(position, word.toLowerCase());
	    }
	    
	    final String[] passphrase_array = new String[passphrase.size()];
	    passphrase.toArray(passphrase_array);
	    passphrases[i] = join(passphrase_array, separators, colours);
	}
	return passphrases;
    }
    
    
    /**
     * Gets the actual length of string
     * 
     * @param   text  The string
     * @return        The length of the string
     */
    public static int length(String text)
    {
	/* This code is Java specific, Java encodes in UTF-16 */
	
	int rc = 0;
	for (int i = 0, n = text.length(); i < n; i++)
	{   char c = text.charAt(i);
	    if ((c & 0xFC00) != 0xDC00)
		rc++;
	}
	return rc;
    }
    
    
    /* TODO determine hyphenation for words to determine which letters may be uppercase */
    /* TODO occationally use letter translation */
    
    
}

