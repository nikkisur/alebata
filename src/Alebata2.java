import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Alebata2 {
		
		// variables for checking validity of inputted variable names
		static String character = "abcdefghijklmnopqrstuvwxyz";
		static String character2 = "0123456789-_abcdefghjiklmnopqrstuvwxyz";
		static String number = "0123456789-";
		
		// a list for storing inputted variable names so they can be checked for validity
		static HashMap<String, String> variables = new HashMap<String, String>();
				
		// a variable for storing one inputted line
		static String line;
		
		// a variable for scanning the contents of one inputted line
		static Scanner scan, scanLine;
		static File lookUp;
		static HashMap<String, String> reservedWords = new HashMap<String, String>();
		
		public static void main(String args[]){
			// a variable for scanning every line of user input
//			File file = new File(args[0]);
			File file = new File("ale.bata");
			try{
				scan = new Scanner(file);
				
			}
			catch(Exception e){
				System.out.println("File not found.");
			}
			addReservedWords();
			action();
		}
		
		public static void action(){
			while(scan.hasNext()){
				String[] words = scan.nextLine().split("(?<=\\.)|(?=\\.)| ");
				for(String word : words){
					if(reservedWords.get(word) != null){
						System.out.println(word + "			" + reservedWords.get(word));
					}
					else{
						System.out.println(word + "			" + "IDENT");
					}
				}
			}
			
		}

	// method for checking validity of variable name
	// returns true
	public static Boolean checkVar(String var){
		// store variable names into a String array
		String[] vars = var.split(",");

		// iterate through each variable name
		for(int a = 0; a < vars.length; a++){
			String temp = vars[a];

			// the case where two commas appeared without a variable name in between them and it's neither the first nor the last item in the array [in which case it would be okay]
			if(temp.isEmpty() && a != 0 && a != (vars.length-1)){
				System.out.println("Hindi maaring null o walang pangalan ang variable.");
				return true;
			}

			// the case where a variable name exists for checking
			if(!temp.isEmpty()){
				// the case where the starting character is not in the listed characters that are valid
				if(character.indexOf(String.valueOf(temp.charAt(0))) == -1){
					System.out.println("	Kailangang magsimula ang variable sa a-z.");
				}
				else{
					// iterate through each character in the variable name
					for(int b = 1; b < temp.length(); b++){
						// the case where a character in the variable name is not in the set of valid characters
						if(character2.indexOf(String.valueOf(temp.charAt(b))) == -1){
							// the inner case where that character is the last character of the input line
							if(a == vars.length-1 && b == temp.length()-1){
								// the inner case where that character is a period
								if(temp.charAt(temp.length()-1) == '.'){
									if(variables.get(temp.substring(0, b)) != null)
										System.out.println("	Mayroon nang variable na ang pangalan ay " + temp);
									else{
										variables.put(temp.substring(0, b), "");
										System.out.println("	Gumawa ka ng variable na ang pangalan ay " + temp);
									}
									return true;
								}
							}
							// this is only reached if an invalid character is reached
							// the only invalid character allowed is a period and that's if it's the last character of the line -- signifying the end of a line in the "Ale, Bata" language
							System.out.println("	Maari lamang ang a-z, 0-9,- at _.");
							break;
						}
						// the case where all characters in the variable name are valid and we're iterating through the last character of said name
						else if(b == temp.length()-1){
							if(variables.get(temp) != null)
								System.out.println("	Mayroon nang variable na ang pangalan ay " + temp + ".");
							else{
								variables.put(temp, "");
								System.out.println("	Gumawa ka ng variable na ang pangalan ay " + temp + ".");
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	//method to add the reserved words to a hashmap
	public static void addReservedWords(){
		try{
			lookUp = new File("ReservedWords.txt");
			Scanner scanLook = new Scanner(lookUp);
			while(scanLook.hasNext()){
				reservedWords.put(scanLook.next(), scanLook.next());
			}
		}catch(Exception e){
			System.out.println("Can't find 'ReservedWords.txt'");
		}
		
	}
}