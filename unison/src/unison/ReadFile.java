package unison;

import java.io.File;
import java.io.FileNotFoundException;
//import java.io.IOException;
import java.util.Scanner;
//import java.nio.charset.StandardCharsets;

public class ReadFile {
  public static String main(String filename) {
    try {
      File myFile = new File(filename);
      Scanner myReader = new Scanner(myFile);
      String strang = null;
      while (myReader.hasNextLine()) {
        strang = strang + myReader.nextLine();
      }
      myReader.close();
      return strang;
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred in ReadFile.");
      e.printStackTrace();
    }
    return null;
  }
}