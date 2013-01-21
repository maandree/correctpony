/**
 * correctpony - a passphrase generator inspired by http://xkcd.com/936/
 * Copyright (C) 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 */
#include "correctpony.h"

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

#ifdef _GNU_SOURCE
#include <getopt.h>
#endif

#include "wlist.h"

static size_t rand_index(size_t n);
static void rand_perm(size_t* dest, size_t n);
static void print_usage(char* argv0);
static void print_version(char* argv0);


static size_t rand_index(size_t n)
{
  FILE* file = fopen("/dev/urandom", "r");
  
  int value = 0, i;
  for (i = 0; i < 4; i++)
    value = (value << 8) | fgetc(file);
  
  fclose(file);
  
  return value % n;
}

static void rand_perm(size_t* dest, size_t n)
{
  size_t i, j;
  int* check = malloc(n * sizeof *check);
  
  if (!check)
    return;
  
  memset(check, 0, n * sizeof *check);
  
  for (i = 0; i < n; ++i)
    {
      for (j = rand_index(n); check[j]; j = (j+1) % n)
	;
      
      dest[i] = j;
      check[j] = 1;
    }
  
  free(check);
}

static void print_usage(char* argv0)
{
  fprintf(stderr, "usage: %s [options] [count]\n", argv0);
  fprintf(stderr, "generates [count] passphrases, or 1 if no number specified\n");
  fprintf(stderr, "\n");
  
  #ifdef _GNU_SOURCE
    fprintf(stderr, "Available options: \n"
                    "-h, --help             print this text and exit\n"
                    "-v, --version          print version and exit\n"
	            "-p, --nocolour         do not colour the words in the passphrase\n"
                    "-j, --join             join all words\n"
                    "-i, --include <word>   include <word> in passphrase\n"
                    "-l, --list <list>      use wordlist <list>.\n");
    fprintf(stderr, "                       must be either absolute path or path relative to " WORDLIST_DIR ".\n"
                    "                       max. " STR(WLISTS_MAX) " wordlists can be specified;\n"
	            "                       if omitted, the default wordlist " WORDLIST_DIR "/" WORDLIST_DEFAULT " will be used.\n");
    fprintf(stderr, "-c, --char <n>         passphrase must be at least <n> characters long (default: " STR(CHARS_MIN_DEFAULT) ")\n"
                    "-w, --words <n>        passphrase must consist of at least <n> words (default: " STR(WORDS_MIN_DEFAULT) ")\n"
                    "-s, --sep <char>       use <char> as separator between words\n"
                    "-u, --camelcase        capitalize first letter of each word\n"
           );
  #else
    fprintf(stderr, "available options: \n"
                    "-h         print this text and exit\n"
                    "-v         print version and exit\n"
                    "-p         do not colour the words in the passphrase\n"
                    "-j         join all words\n"
                    "-i <word>  include <word> in passphrase\n"
                    "-l <list>  use wordlist <list>.\n"
                    "           must be either absolute path or path relative to " WORDLIST_DIR ".\n"
                    "           max. " STR(WLISTS_MAX) " wordlists can be specified;\n");
    fprintf(stderr, "           if omitted, the default wordlist " WORDLIST_DIR "/" WORDLIST_DEFAULT " will be used.\n"
                    "-c <n>     passphrase must be at least <n> characters long (default: " STR(CHARS_MIN_DEFAULT) ")\n"
                    "-w <n>     passphrase must consist of at least <n> words (default: " STR(WORDS_MIN_DEFAULT) ")\n"
                    "-s <char>  use <char> as separator between words\n"
                    "-u         capitalize first letter of each word\n"
           );
  #endif
}

static void print_version(char* argv0)
{
  printf("%s Version: " VERSION "\n", argv0);
  printf("Copyright (C) 2012, 2013  Mattias Andrée (maandree@member.fsf.org)\n");
  printf("License: WTFPL 2.0 http://sam.zoy.org/wtfpl/COPYING\n");
}

int main(int argc, char** argv)
{
  int c;
  size_t i;
  
  size_t n_phrases = 1;
  
  size_t out_idx[WORDS_MAX];
  
  /* words to choose from */
  char* words[WORDS_MAX];
  size_t n_words = 0, n_chars = 0;
  
  /* minimum lengths to generate */
  size_t chars_min = CHARS_MIN_DEFAULT;
  size_t words_min = WORDS_MIN_DEFAULT;
  
  /* user-specified wordlists */
  struct wlist* wlists[WLISTS_MAX];
  size_t i_wlists = 0, n_wlists = 0;
  
  /* user-specified words */
  char* uwords[UWORDS_MAX];
  size_t i_uwords = 0, n_uwords = 0;
  
  /* sep character between words, -1 -> none */
  int sep = 32; /* ' ' */
  int camel = 0;
  
  /* for strtoul */
  char* endptr;
  
  /* colour each word */
  int colour = 1;
  
  /* arguments */
  
  #ifdef _GNU_SOURCE
    struct option long_opts[] = {
        { "include",     required_argument, NULL, 'i' },
        { "list",        required_argument, NULL, 'l' },
        { "wordlist",    required_argument, NULL, 'l' },
        { "word-list",   required_argument, NULL, 'l' },
        { "chars",       required_argument, NULL, 'c' },
        { "words",       required_argument, NULL, 'w' },
        { "word",        required_argument, NULL, 'w' },
        { "sep",         required_argument, NULL, 's' },
        { "separator",   required_argument, NULL, 's' },
        { "uppercase",   no_argument, NULL, 'u' },
        { "camelcase",   no_argument, NULL, 'u' },
        { "upper-case",  no_argument, NULL, 'u' },
        { "camel-case",  no_argument, NULL, 'u' },
        { "join",        no_argument, NULL, 'j' },
        { "help",        no_argument, NULL, 'h' },
        { "version",     no_argument, NULL, 'v' },
        { "nocolour",    no_argument, NULL, 'p' },
        { "nocolor",     no_argument, NULL, 'p' },
        { "no-colour",   no_argument, NULL, 'p' },
        { "no-color",    no_argument, NULL, 'p' },
        { "nocolours",   no_argument, NULL, 'p' },
        { "nocolors",    no_argument, NULL, 'p' },
        { "no-colours",  no_argument, NULL, 'p' },
        { "no-colors",   no_argument, NULL, 'p' },
        { "pipe",        no_argument, NULL, 'p' },
    };
    while ((c = getopt_long(argc, argv, "hvjpi:l:c:w:s:u", long_opts, NULL)) != -1)
  #else
    while ((c = getopt(argc, argv, "hvjpi:l:c:w:s:u")) != -1)
  #endif
    {
      switch (c)
        {
	case 'h':
	  print_usage(argv[0]);
	  exit(EXIT_SUCCESS);
	  
	case 'v':
	  print_version(argv[0]);
	  exit(EXIT_SUCCESS);
	  
	case 'j':
	  sep = -1;
	  break;
	  
	case 'p':
	  colour = 0;
	  break;
	  
	case 'i':
	  uwords[i_uwords] = optarg;
	  n_uwords = (n_uwords < UWORDS_MAX) ? n_uwords + 1 : UWORDS_MAX;
	  i_uwords = (i_uwords + 1) % UWORDS_MAX;
	  break;
	  
	case 'l':
	  wlists[i_wlists] = wlist_read(optarg);
	  if (wlists[i_wlists])
	    {
	      n_wlists = (n_wlists < WLISTS_MAX) ? n_wlists + 1 : WLISTS_MAX;
	      i_wlists = (i_wlists + 1) % WLISTS_MAX;
	    }
	  break;
	  
	case 'c':
	  chars_min = strtoul(optarg, &endptr, 10);
	  if (*endptr != '\0')
	    {
	      fprintf(stderr, "invalid number: %s\n", optarg);
	      exit(EXIT_FAILURE);
	    }
	  chars_min = (chars_min > CHARS_MAX) ? CHARS_MAX : chars_min;
	  break;
	  
	case 'w':
	  words_min = strtoul(optarg, &endptr, 10);
	  if (*endptr != '\0')
	    {
	      fprintf(stderr, "invalid number: %s\n", optarg);
	      exit(EXIT_FAILURE);
	    }
	  words_min = (words_min > WORDS_MAX) ? WORDS_MAX : words_min;
	  break;
	  
	case 's':
	  sep = *optarg;
	  break;
	  
	case 'u':
	  camel = 1;
	  break;
	  
	case '?':
	  fprintf(stderr, "uknown option \"-%c\"\n", optopt);
	  exit(EXIT_FAILURE);
        }
    }
    
  if (optind < argc)
    {
      n_phrases = strtoul(argv[optind], &endptr, 10);
      if (*endptr != '\0')
	{
          fprintf(stderr, "invalid number: %s\n", argv[optind]);
	  exit(EXIT_FAILURE);
        }
    }
    
  /* no user-specified wordlists, use default */
  if (!n_wlists)
    {
      wlists[0] = wlist_read(WORDLIST_DEFAULT);
      if (!wlists[0])
	{
          fprintf(stderr, "couldn't read default wordlist\n");
          exit(EXIT_FAILURE);
	}
      n_wlists = 1;
    }
  
  while (n_phrases--)
    {
      n_chars = 0;
      n_words = 0;
      
      for (; n_words < n_uwords; ++n_words)
        {
          words[n_words] = uwords[n_words];
          n_chars += strlen(words[n_words]);
        }
      
      for (; n_words < words_min || n_chars < chars_min; ++n_words)
        {
          size_t iwl, iw;
          iwl = rand_index(n_wlists);
          
          iw = rand_index(wlists[iwl]->len);
          
          words[n_words] = wlist_get(wlists[iwl], iw);
          n_chars += strlen(words[n_words]);
        }
      
      if (camel)
        {
          for (i = 0; i < n_words; ++i)
            *words[i] = toupper(*words[i]);
        }
      
      rand_perm(out_idx, n_words);
      
      if (colour)
	printf("\033[0;1m");
      
      for (i = 0; i < n_words; ++i)
        {
          if (i && sep != -1)
	    printf("%c", sep);
	  
	  if (!colour)
	    printf("%s", words[out_idx[i]]);
	  else if (i & 1)
	    printf("\033[34m%s", words[out_idx[i]]);
	  else
	    printf("\033[33m%s", words[out_idx[i]]);
        }
      
      if (colour)
	printf("\033[0m\n");
      else
	printf("\n");
  }
  
  for (i = 0; i < n_wlists; ++i)
    wlist_free(wlists[i]);
  
  return EXIT_SUCCESS;
}

