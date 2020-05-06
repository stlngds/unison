/*
 * Greyson Biggs
 * Clinton Jeffery
 * CS 210
 * 5/8/2020
 * HW #5 - Unicon class-to-JSON Java parser
 */

package unison;

public class UniMain {
	public static void main(String args[]) {
		/*
		 * Parses Unicon classes to JSON
		 * Will wig out if it runs into syntax errors
		 * In JSON format, identify classes, superclasses, fields, and methods and their args
		 * e.g. 
		 * { "class": "A",
		 * 	"super": ["B"],
		 * 	"fields": ["x", "y", "z"],
		 * 	"methods": {
		 * 		"equals": ["other", "seen"],
		 * 		"hash_code": ["depth", "seen"]
		 * 	}
		 * }
		 * 
		 * Takes multiple .icn files as input, outputs JSON to uniuml.json
		 * 
		 * Scan each icn file, stepping through the file word by word (delimited by whitespace and carriage returns and such).
		 * If scanner hits 'class A' write '{ "class": "A"'
		 *	Then if it hits a colon, 
		 *		the next word will be a superclass name, so write ',\n"super": ["superclassnameA"'
		 * 		Repeat: If it encounters another colon after the preceding classname, write ', "superclassnameB"', otherwise write ']' and break
		 * If open paren follows, write '\n"fields": ['
		 *	 If next word is first field name, write '"fieldname A"'
		 *	 Else if next word is close paren, write ']' and break, else next word is a non-first fieldname, so write ', "fieldnameB"'
		 * Next, if word is "method" and it's the first we've seen write '\n"methods": {\n'
		 * 	Repeat: If word is "method" then next word is methodname followed by (param1, param2, ...). Write much the same as classes.
		 * 	Then, scan for "end" and break when you hit it. Then keep scanning for "method" again.
		 * ...or if you hit another "end" before hitting "method", break, from the class.
		 * If you hit EOF before seeing end of a class, throw an exception.
		 * 
		 * 
		 * 
		 * Unicon class syntax (things enclosed in asterisks are non-literal):
		 * 	
		 */
	}
}
