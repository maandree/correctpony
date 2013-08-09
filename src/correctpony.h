/**
 * correctpony - a passphrase generator inspired by http://xkcd.com/936/
 * Copyright (C) 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
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
#ifndef CORRECTPONY_H
#define CORRECTPONY_H

#define _POSIX_C_SOURCE 2
#define _XOPEN_SOURCE 500

#define WORDS_MAX 10
#define CHARS_MAX 1024

#define CHARS_MIN_DEFAULT 12
#define WORDS_MIN_DEFAULT 4

#define UWORDS_MAX WORDS_MIN_DEFAULT

#define WLISTS_MAX 100
#define WORDLIST_DEFAULT "english"

#ifndef WORDLIST_DIR
#define WORDLIST_DIR "/usr/share/correctpony"
#endif

#define STR1(x) # x
#define STR(x) STR1(x)

#endif
