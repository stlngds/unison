package unison;

import java.io.FileWriter;
import java.io.IOException;

public class WriteToFile {
	public static void main(String myString) {
		try {
			FileWriter myWriter = new FileWriter("uniuml.json");
			myWriter.write(myString);
			myWriter.close();
			System.out.println("Wrote: " + myString);
		}
		catch (IOException e) {
			System.out.println("An error occurred in WriteToFile.");
			e.printStackTrace();
		}
	}
}
