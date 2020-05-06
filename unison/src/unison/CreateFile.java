package unison;

import java.io.File;
import java.io.IOException;

public class CreateFile {
	public static void main() {
		try {
			File myObj = new File("uniuml.json");
			if (myObj.createNewFile()) {
				System.out.println("uniuml.json created.");
			}
			else {
				System.out.println("uniuml.json already exists!");
			}
		}
		catch (IOException e) {
			System.out.println("An error has occurred in CreateFile.");
			e.printStackTrace();
		}
	}
}
