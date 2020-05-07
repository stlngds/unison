/*
 * Greyson Biggs
 * Clinton Jeffery
 * CS 210
 * 5/8/2020
 * HW #5 - Unicon class-to-JSON Java parser
 */

package unison;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;

public class UniMain {
	public static void main(String args[]) {
		/*
		 * Parses Unicon classes to JSON
		 * TODO: Handle syntax errors / non-unicon files
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
		 * 	class className : superClassName : superClassName2 ( field1, field2, field3 )
		 * 		method methodName ( field1, field2)
		 * 			<method stuff>
		 * 		end
		 * 	end
		 * 
		 * (There may or may not be whitespace between punctuation like , : or () )
		 */
		CreateFile.main(); //Program should append if file already exists
		for(int i = 0; i < args.length; i++) { //Go through each file in order.
			String filetext = ReadFile.main(args[i]); //Read file into text
			String lines[] = filetext.split("\\r?\\n"); //Split file text into a string array, using return delimiters
			List<String> linesal = new ArrayList<String>(); //convert string array into arraylist
			linesal = Arrays.asList(lines); 

			//variable initialization
			boolean inClass = false;
			boolean inMethod = false;
			int superclassCount = 0;
			int methodCount = 0;
			String lastWordType = "";
			
			//*the* loop
			for (int j = 0; j < linesal.size(); j++) { //go through each line
				if((linesal.get(j)).startsWith("#") == false) { //ignore comment lines
					//The following splits the line into words. Certain characters are both delimiters and "words"
					//For example, we need to parse 'class Button:Toggle(...' the same as 'class Button : Toggle (...'
					//Thus in the former case, we treat : or ( as their own "words"
					String currline[] = (linesal.get(j)).split("((?<=:)|(?=:)|(?<=()|(?=()|(?<=))|(?=))|(?<=,)|(?=,))");
					for (int x = 0; (x < currline.length) & (currline[x] != "#"); x++) { //step through each word until you hit EOL or a comment
						//TODO: Determine if we need to handle string literals that include # (e.g. rare-ish points in code where # doesn't denote a comment)
						//Same goes for other keywords like class, method, et al.
						//May or may not even be needed given what we're doing?
						
						if(currline[x] == "class") {
							WriteToFile.main("\n{ \"class\": ");
							inClass = true;
							//we now expect a classname to follow immediately after
							lastWordType = "class";
						}
						
						else if(lastWordType == "class") {
							WriteToFile.main("\"" + currline[x] + "\"");
							lastWordType = "classname";
						}
						
						else if(currline[x] == "method") {
							if (inMethod == true) { //source missed an 'end'
								System.out.println("Encountered 'method' while inMethod, expected 'end'. Ignoring...");
							}
							if (methodCount == 0) {
								WriteToFile.main("\n\"methods\": {\n");
							}
							inMethod = true;
							methodCount++;
							
							lastWordType = "method";
						}
						
						else if(currline[x] == "end") {
							if ((inClass == true) & (inMethod == false)) {
								inClass = false;
							}
							else if ((inClass == true) & (inMethod == true)) {
								inMethod = false;
							}
							else if ((inClass == false)) {
								System.out.println("Encountered loose 'end'. Ignoring...");
							}
							lastWordType = "end";
						}

						
						else if (currline[x] == ":") { //after colons, we expect a supertype name
							if (superclassCount == 0) {
								WriteToFile.main(",\n\"super\": [");
							}
							lastWordType = ":";
						}
						
						else if (lastWordType == ":") {
							WriteToFile.main("\"" + currline[x] + "\"");
							if (currline[x+1] == ":") {
								WriteToFile.main(", ");
							}
							else {
								WriteToFile.main("]");
							}
							superclassCount++;
							lastWordType = "superclassname";
						}
						
						else if (currline[x] == "(" ) { //could be the start of class fields, or method parameters
							//check if we just had a class definition or a method definition
							lastWordType = "(";
							//remember, fields may span multiple lines
						}
						
						
						
						else {
							lastWordType = ""; //irrelevant
						}
						
					}
				}
			}
		}
	}
}