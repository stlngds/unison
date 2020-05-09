/*
 * Greyson Biggs
 * Clinton Jeffery
 * CS 210
 * 5/8/2020
 * HW #5 - Unicon class-to-JSON Java parser
 */

package unison;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;

public class UniMain {
	public static void main(String args[]) throws IOException {
		/*
		 * Parses Unicon classes to JSON
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
		FileWriter myWriter = new FileWriter("uniuml.json");
		for(int i = 0; i < args.length; i++) { //Go through each file in order.
	//		String filetext = ReadFile.main(args[i]); //Read file into text
	//		String lines[] = filetext.split("\\r?\\n\\s+"); //Split file text into a string array, using return delimiters
	//		List<String> linesal = new ArrayList<String>(); //convert string array into arraylist
	//		linesal = Arrays.asList(lines); 

			//variable initialization
			boolean inClass = false;
			boolean inMethod = false;
			int superclassCount = 0;
			int methodCount = 0;
			int classFieldCount = 0;
			int methodParamCount = 0;
			String lastWordType = "";
			
			ArrayList<String> linesal = new ArrayList<>();
			try (Scanner s = new Scanner(new File(args[i])).useDelimiter("\\r?\\n")) {
				while (s.hasNext()) {
					linesal.add(s.next());
				}
			}
			catch (FileNotFoundException e) {
				System.out.println("File not found.");
			}
			
			System.out.println("Entering loop.");
			
			//-------------------------------------------------------------------------------------------------------
			
			//*the* loop
			for (int j = 0; j < linesal.size(); j++) { //go through each line
				if(((linesal.get(j)).startsWith("#") == false) & (linesal.get(j) != "")) { //ignore comment lines, empty lines
					//The following splits the line into words. Certain characters are both delimiters and "words"
					//For example, we need to parse 'class Button:Toggle(...' the same as 'class Button : Toggle (...'
					//Thus in the former case, we treat : or ( as their own "words"
					String currlinet[] = (linesal.get(j)).split("((?<=[,:\\(\\)])|(?=[,:\\(\\)]))|(\\s+)");
					List <String> currline = new java.util.ArrayList<String>(Arrays.asList(currlinet));
					
					for (int x = 0; (x < currline.size()) & (currline.size() != 0); x++) {
						currline.set(x, currline.get(x).replaceAll("\\s", ""));
					}
					currline.removeAll(Collections.singleton(null));
					currline.removeAll(Collections.singleton(""));
					System.out.println(currline);

					if (currline.size() != 0)
					for (int x = 0; (x < currline.size()); x++) { if (!currline.get(x).contentEquals("#")) {
						//step through each word until you hit EOL or a comment
						//TODO: Fix it not entering "class" (and every other if stmnt that isn't the base case).
						//System.out.println("In loop: " + currline.get(x));
						
						//encounter class
						if(currline.get(x).contentEquals("class")) {
							myWriter.write("\n{ \"class\": ");
							System.out.println("in class");
							inClass = true;
							lastWordType = "class";
						}
						
						//after a class, we expect a classname
						else if(lastWordType.contentEquals("class")) {
							System.out.println("last: class");
							myWriter.write("\"" + currline.get(x) + "\"");
							lastWordType = "classname";
						}
						
						//encounter method
						else if(currline.get(x).contentEquals("method")) {
							if (inMethod == true) {
								System.out.println("Encountered 'method' while inMethod, expected 'end'. Ignoring...");
							}
							if (methodCount == 0) {
								myWriter.write("\n\"methods\": {\n");
							}
							inMethod = true;
							methodCount++;
							
							lastWordType = "method";
						}
						
						//encounter end
						else if(currline.get(x).contentEquals("end")) {
							if ((inClass == true) & (inMethod == false)) {
								inClass = false;
								if (methodCount != 0) {
									myWriter.write("\n}");
								}
								methodCount = 0;
								classFieldCount = 0;
								myWriter.write("\n}");
							}
							else if ((inClass == true) & (inMethod == true)) {
								inMethod = false;
							}
							else if ((inClass == false)) {
								System.out.println("Encountered loose 'end'. Ignoring...");
							}
							lastWordType = "end";
						}

						//class stuff
						//encounter colon
						else if (currline.get(x).contentEquals(":")) {
							if (superclassCount == 0) {
								myWriter.write(",\n\"super\": [");
							}
							lastWordType = ":";
						}
						
						//after colons, we expect superclass names
						else if (lastWordType.contentEquals(":")) {
							myWriter.write("\"" + currline.get(x) + "\"");
							if (currline.get(x+1).contentEquals(":")) {
								myWriter.write(", ");
							}
							else {
								myWriter.write("]");
							}
							superclassCount++;
							lastWordType = "superclassname";
						}
						
						//method stuff
						//after methods we expect methodnames
						else if (lastWordType.contentEquals("method")) {
							if (methodCount != 0) {
								myWriter.write(",\n");
							}
							myWriter.write("\"" + currline.get(x) + "\": ");
							lastWordType = "methodname";
						}

						//open parenthesis could signal the beginning of method parameters, or class fields
						//we need to check whether the previous word was a methodname or super/classname to identify what
						//the open paren actually means
						else if (currline.get(x).contentEquals("(")) { 
							if (lastWordType.contentEquals("methodname")) {
								lastWordType = "beginmethodparams";
							}
							else if ((lastWordType.contentEquals("classname")) | (lastWordType.contentEquals("superclassname"))) {
								lastWordType = "beginclassfields";
							}
							else lastWordType = ""; //irrelevant
						}
						
						//after methodnames and open parens, expect zero or more methodparams
						else if ((lastWordType.contentEquals("beginmethodparams")) | (lastWordType.contentEquals("m,"))) {
							if (!currline.get(x).contentEquals(")")) {
								if (methodParamCount == 0) {
									myWriter.write("[");
								}
								
								methodParamCount++;
							}
							else { //currline[x] == ")"
								myWriter.write("]");
								lastWordType = "endmethodparams";
								methodParamCount = 0;
							}
						}
						
						//after classnames and open parens, expect zero or more classfields
						else if ((lastWordType.contentEquals("beginclassfields")) | (lastWordType.contentEquals("c,"))) {
							if (!currline.get(x).contentEquals(")")) {
								if (classFieldCount == 0) {
									myWriter.write("\n\"fields\": [");
								}
								myWriter.write("\"" + currline.get(x) + "\"");
								classFieldCount++;
								lastWordType = "classfield";
							}
							else { //currline[x] == ")" 
								myWriter.write("]");
								lastWordType = "endclassfields";
								classFieldCount = 0;
							}
						}
						
						//after 'classname/methodparam,', expect another classname/methodparam
						else if (currline.get(x).contentEquals(",")) {
							if (lastWordType.contentEquals("classfield")) {
								myWriter.write(", ");
								lastWordType = "c,";
							}
							else if (lastWordType.contentEquals("methodparam")) {
								myWriter.write(", ");
								lastWordType = "m,";
							}
						}
						
						/*else if ((currline.get(x) == "") | (currline.get(x) == " ")) {
							//change nothing
							System.out.println("In whitespace.");
						}*/
							
						//base case, move along
						else {
							lastWordType = ""; //irrelevant
						}
						
						System.out.println("lastWordType: " + lastWordType);
					}}
				}
			}
		}
	System.out.println("Completed parse. Exiting...");
	myWriter.close();
	}
}