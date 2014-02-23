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
	static HashMap<String, Integer> arrays = new HashMap<String, Integer>();

	// a variable for storing one inputted line
	static String line;

	// a variable for scanning the contents of one inputted line
	private static Scanner scan, scanLine;
	private static File lookUp;
	private static HashMap<String, String> reservedWords = new HashMap<String, String>();
	private static ArrayList<String> tokens = new ArrayList<String>();
	private static ArrayList<String> lexemes = new ArrayList<String>();
	private static int index = 0;
	private static String token;
	private static float numValue;
	private static float equation = 0;
	private static boolean errFlag = false;
	private static String var;
	private static String varValue;

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
		getNextToken();
		S();
	}

	//lexical analyzer
	public static void action(){
		int lineNum = 0;
		while(scan.hasNext()){
			String line = scan.nextLine();
			lineNum++;
			String[] words = line.split("(?<=[ \\!\"()*/%^+-])|(?=[ \\!\"()*/%^+-])| ");
			String value = "";

			System.out.println("---line: " + line);
			if(!line.isEmpty()) //current line in program should not be empty
			{
				if(line.charAt(line.length()-1) != '!'){
					System.out.println("Error at line#" + lineNum + ": should end with ! ");
					close();
				}
				for(String word: words){
					if(reservedWords.get(word) != null){
						tokens.add(reservedWords.get(word));
						lexemes.add(word);
						//					System.out.println(word + "			"+ reservedWords.get(word));
					}
					else{
						if(!word.equals("")){
							if(isNumeric(word))
							{
								tokens.add("NUMBER");
							}	
							else if(word.equals(" ")){
								tokens.add("SPACE");
							}
							else {
								tokens.add("IDENT");
							}
							lexemes.add(word);
							//						System.out.println(word + "			IDENT");
						}
					}
				}
			}
		}
		for(int a = 0; a < tokens.size(); a++){
			System.out.println(tokens.get(a) + "\t\t" + lexemes.get(a));
		}
	}

	public static void getNextToken(){
		if(index == tokens.size())
			token = null;
		else
			token = tokens.get(index++);
	}

	// S -> A
	public static void S()
	{
		A();
		skipSpace();
		// if( token.equals("") && !errFlag )
		// 	System.out.println( "program accepted" );
		// else
		// {	
		// 	System.out.printf( "program not accepted (error on or before position %d)\n", index);
		// }
	}

	//A -> 'GAWA' B | IDENT C | 'ILABAS' D 
	public static void A(){
		while(token != null){
			if(token.equals("GAWA")){
				getNextToken();
				skipSpace();

				if(token == null){
					System.out.println("Invalid syntax for GAWA");
				}
				B();
			}
			else if(token.equals("IDENT")){
				var = lexemes.get(index-1);
				getNextToken();
				skipSpace();
				if(token == null){
					System.out.println("Invalid syntax");
				}
				C();
				var = "";
			}
			else if(token.equals("ILABAS")){
				getNextToken();
				skipSpace();
				if(token == null){
					System.out.println("Invalid syntax for ILABAS");
				}
				D();
			}
			getNextToken();
		}
	}

	//B -> 'NG' IDENT -- var declarations
	public static void B(){
		if(token != null){
			if(token.equals("NG")){
				getNextToken();
				skipSpace();
				if(token == null){
					System.out.println("Invalid syntax");
					close();
				}
				if(token.equals("NUMBER"))
				{
					int arrayNum = Integer.parseInt(lexemes.get(index-1));
					getNextToken();
					skipSpace();
					if(token != null && token.equals("NA")){
						getNextToken();
						skipSpace();
						var = lexemes.get(index-1);
						if(token != null && (token.equals("IDENT") || token.equals("NUMBER")))
						{
							checkVar(var, true, arrayNum);
							getNextToken();
							skipSpace();
						}
						else{
							System.out.println("Wrong syntax: GAWA NG <num> NA <var>");
							close();
						}
					}
					else{
						System.out.println("Wrong syntax");
						close();
					}
				}
				else if(token.equals("IDENT")){
					var = "";
					while(token != null && !token.equals("TERMINATOR")){
						var += lexemes.get(index-1);
						getNextToken();
						skipSpace();
					}
					checkVar(var, false, 0);
				}

				if(token == null || !token.equals("TERMINATOR")){
					System.out.println("Missing ! at the end of line");
					close();
				}
			}

		}
		else{
			System.out.println("Syntax should be: GAWA NG <var_name>!");
			close();
		}
	}

	// var assignment
	//C -> 'AY'
	public static void C(){
		if(variables.get(var) != null){
			if(token.equals("AY")){
				getNextToken();
				skipSpace();
				if(token.equals("DQUOTE")){
					getNextToken();
					String value = "";
					while(!token.equals("DQUOTE")){
						value += lexemes.get(index-1);
						getNextToken();
						if(token != null && token.equals("DQUOTE") && value.charAt(value.length()-1) == '\\'){
							value = value.substring(0, value.length()-1) + "\"";
							getNextToken();
						}
					}
					variables.put(var, value);
				}
				else if(token.equals("IDENT") || token.equals("TAMA") || token.equals("MALI") || token.equals("NUMBER") || token.equals("HINDI")){
					String tempVar = "";
					Boolean value = null;
					BaryaBall varType = null;
					if(token.equals("IDENT")){
						tempVar = lexemes.get(index-1);
						if(variables.get(tempVar) != null){
							varType = new BaryaBall("", variables.get(tempVar));
							if(varType.type.equals("boolean")){
								value = checkTrue(null, tempVar);
							}
						}
					}
					else if(token.equals("TAMA")){
						value = checkTrue("TAMA", "");
					}
					else if(token.equals("MALI")){
						value = checkTrue("MALI", "");
					}
					else if(token.equals("HINDI")){
						getNextToken();
						skipSpace();
						if(token == null){
							System.out.println("Syntax Error for HINDI");
							close();
						}
						if(token.equals("IDENT")){
							tempVar = lexemes.get(index-1);
							if(variables.get(tempVar) != null){
								varType = new BaryaBall("", variables.get(tempVar));
								if(varType.type.equals("boolean")){
									value = !checkTrue(null, tempVar);
								}
							}
						}
						else if(token.equals("TAMA")){
							value = !checkTrue("TAMA", "");
						}
						else if(token.equals("MALI")){
							value = !checkTrue("MALI", "");
						}
						else{
							System.out.println("Can't compare non-boolean types");
							close();
						}
					}
					
					if(value != null){
						getNextToken();
						skipSpace();
						while(token != null && !token.equals("TERMINATOR")){
							if(token.equals("AT")){
								getNextToken();
								skipSpace();
								if(token.equals("IDENT")){
									tempVar = lexemes.get(index-1);
									if(variables.get(tempVar) != null){
										varType = new BaryaBall("", variables.get(tempVar));
										if(varType.type.equals("boolean")){
											value = value && checkTrue(null, tempVar);
										}
										else{
											System.out.println("Can't compare non-boolean types");
											close();
										}
									}
									else{
										System.out.println("Declare " + tempVar);
										close();
									}
								}
								else if(token.equals("TAMA")){
									value = value && checkTrue("TAMA", null);
								}
								else if(token.equals("MALI")){
									value = value && checkTrue("MALI", null);
								}
								else if(token.equals("HINDI")){
									getNextToken();
									skipSpace();
									if(token == null){
										System.out.println("Syntax Error for HINDI");
										close();
									}
									if(token.equals("IDENT")){
										tempVar = lexemes.get(index-1);
										if(variables.get(tempVar) != null){
											varType = new BaryaBall("", variables.get(tempVar));
											if(varType.type.equals("boolean")){
												value = value && !checkTrue(null, tempVar);
											}
										}
									}
									else if(token.equals("TAMA")){
										value = value &&!checkTrue("TAMA", "");
									}
									else if(token.equals("MALI")){
										value = value &&!checkTrue("MALI", "");
									}
									else{
										System.out.println("Can't compare non-boolean types");
										close();
									}
								}
								else{
									System.out.println("Can't use non-boolean types for AT");
									close();
								}
							}
							else if(token.equals("O")){
								getNextToken();
								skipSpace();
								if(token.equals("IDENT")){
									tempVar = lexemes.get(index-1);
									if(variables.get(tempVar) != null){
										varType = new BaryaBall("", variables.get(tempVar));
										if(varType.type.equals("boolean")){
											value = value || checkTrue(null, tempVar);
										}
										else{
											System.out.println("Can't compare non-boolean types");
											close();
										}
									}
									else{
										System.out.println("Declare " + tempVar);
										close();
									}
								}
								else if(token.equals("TAMA")){
									value = value || checkTrue("TAMA", null);
								}
								else if(token.equals("MALI")){
									value = value || checkTrue("MALI", null);
								}
								else if(token.equals("HINDI")){
									getNextToken();
									skipSpace();
									if(token == null){
										System.out.println("Syntax Error for HINDI");
										close();
									}
									if(token.equals("IDENT")){
										tempVar = lexemes.get(index-1);
										if(variables.get(tempVar) != null){
											varType = new BaryaBall("", variables.get(tempVar));
											if(varType.type.equals("boolean")){
												value = value || !checkTrue(null, tempVar);
											}
										}
									}
									else if(token.equals("TAMA")){
										value = value || !checkTrue("TAMA", "");
									}
									else if(token.equals("MALI")){
										value = value || !checkTrue("MALI", "");
									}
									else{
										System.out.println("Can't compare non-boolean types");
										close();
									}
								}
								else{
									System.out.println("Can't use non-boolean types for AT");
									close();
								}
							}
							else{
								System.out.println("Invalid syntax for logical operators");
								close();
							}
							getNextToken();
							skipSpace();
						}
						if(token == null || !token.equals("TERMINATOR")){
							System.out.println("Needs to end in !");
							close();
						}
						if(value){
							variables.put(var, "TAMA");
						}
						else{
							variables.put(var, "MALI");
						}
					}
					else
					{
						BaryaBall ball = new BaryaBall(var, lexemes.get(index-1));

						//check if number, string
						if(ball.type.equals("number")){
							variables.put(var, ball.getValue());
						}
						else if(ball.type.equals("string")){
							System.out.println("Strings should be placed inside a \"\"");
							close();
						}
						else if(ball.type.equals("error")){
							System.out.println("Wrong number format");
							close();
						}
					}
				}
			}
			else{
				System.out.println("Syntax should be: <var_name> AY <value>");
				close();
				String[] a = {"As"};
			}
		}
		else{
			if(arrays.get(var) != -1){
				System.out.println(var + " has already been used for an array");
				close();
			}
			System.out.println(var + " must be initialized.");
			close();
		}
	}

	//PRINT
	public static void D(){
		String value = "";
		if(token != null && token.equals("MO")){
			getNextToken();
			skipSpace();
			if(token != null && token.equals("BEYBEH")){
				getNextToken();
				skipSpace();
				if(token.equals("TERMINATOR")){
					System.out.println("Invalid syntax for ILABAS MO BEYBEH !");
					close();
				}
				var = lexemes.get(index-1);
				while(token != null && !token.equals("TERMINATOR")){
					if(token.equals("PATI")){
						getNextToken();
						skipSpace();
						if(token == null || token.equals("TERMINATOR")){
							System.out.println("1Invalid syntax for ILABAS MO BEYBEH");
							close();
						}
					}
					if(token.equals("DQUOTE")){
						getNextToken();
						while(token != null && !token.equals("DQUOTE")){
							value += lexemes.get(index-1);
							getNextToken();
							if(token != null && token.equals("DQUOTE") && value.charAt(value.length()-1) == '\\'){
								value = value.substring(0, value.length()-1) + "\"";
								getNextToken();
							}
						}
						System.out.print(value);
					}
					else if(token.equals("IDENT")){
						if(variables.get(var) != null){
							System.out.print(variables.get(var));
						}
					}
					getNextToken();
					skipSpace();
					var = lexemes.get(index-1);
				}
				if(token == null || !token.equals("TERMINATOR")){
					System.out.println("Missing ! at the end of line");
					close();
				}
			}
			else{
				System.out.println("Syntax error: ILABAS MO BEYBEH <value>");
				close();
			}
		}
		else{
			System.out.println("Syntax error: ILABAS MO BEYBEH <value>");
			close();
		}
	}

	//--------------------------------------------------
	public static void numStart(){
		//		float number = numA();
		if(token.equals("TERMINATOR")){
			System.out.println(number);
		}
	}

	//	public static float numA(){
	//		float value = numB();
	//		getNextToken();
	//		if(token == null){
	//			System.out.println("Invalid math expression");
	//			close();
	//		}
	//		while(token.equals("PLUS") || token.equals("MINUS")){
	//			if(token.equals("PLUS")){
	//				getNextToken();
	//				if(token.equals("MINUS")){
	//					getNextToken();
	//					BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
	//					if(ball.type.equals("number"))
	//						lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
	//					else{
	//						System.out.println("Wrong number format");
	//						close();
	//					}
	//				}
	//				value += numB();
	//			}
	//			else if(token.equals("MINUS")){
	//				getNextToken();
	//				if(token.equals("MINUS")){
	//					getNextToken();
	//					BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
	//					if(ball.type.equals("number"))
	//						lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
	//					else{
	//						System.out.println("Wrong number format");
	//						close();
	//					}
	//				}
	//				value -= numB();
	//			}
	//			getNextToken();
	//		}
	//		return value;
	//	}
	//
	//	public static float numB(){
	//		float value = numC();
	//		getNextToken();
	//		if(token == null){
	//			System.out.println("Invalid math expression");
	//			close();
	//		}
	//		while(token.equals("MULT") || token.equals("DIVIDE" ) || token.equals("MODULO")){
	//			if(token.equals("MULT")){
	//				getNextToken();
	//				if(token.equals("MINUS")){
	//					getNextToken();
	//					BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
	//					if(ball.type.equals("number"))
	//						lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
	//					else{
	//						System.out.println("Wrong number format");
	//						close();
	//					}
	//				}
	//				value *= numC();
	//			}
	//			else if(token.equals("DIVIDE")){
	//				getNextToken();
	//				if(token.equals("MINUS")){
	//					getNextToken();
	//					BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
	//					if(ball.type.equals("number"))
	//						lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
	//					else{
	//						System.out.println("Wrong number format");
	//						close();
	//					}
	//				}
	//				value /= numC();
	//			}else if(token.equals("MODULO")){
	//				getNextToken();
	//				System.out.println("here");
	//				if(token.equals("MINUS")){
	//					getNextToken();
	//					BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
	//					if(ball.type.equals("number"))
	//						lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
	//					else{
	//						System.out.println("Wrong number format");
	//						close();
	//					}
	//				}
	//				value %= numC();
	//			}
	//			getNextToken();
	//			System.out.println(token);
	//		}
	//		return value;
	//	}
	//	public static float numC(){
	//		float value = numD();
	//		getNextToken();
	//		if(token == null){
	//			System.out.println("Invalid math expression");
	//			close();
	//		}
	//		while(token.equals("EXP")){
	//			if(token.equals("EXP")){
	//				getNextToken();
	//				if(token.equals("MINUS")){
	//					getNextToken();
	//					BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
	//					if(ball.type.equals("number"))
	//						lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
	//					else{
	//						System.out.println("Wrong number format");
	//						close();
	//					}
	//				}
	//				value = Integer.parseInt(String.valueOf(equation))^Integer.parseInt(String.valueOf(numD()));
	//			}
	//			getNextToken();
	//		}
	//		return value;
	//	}
	//	public static float numD(){
	//		while(token.equals("PAREN")){
	//			getNextToken();
	//			equation = numA();
	//			if(token.equals("ENDPAREN")){
	//				getNextToken();
	//			}
	//			else{
	//				System.out.println("Need to close ()");
	//				close();
	//			}
	//
	//		}
	//		BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
	//		if(ball.type.equals("number")){
	//			return Float.parseFloat(lexemes.get(index-1));
	//		}
	//		else{
	//			System.out.println("Number error");
	//			close();
	//		}
	//		return -1;
	//	}

	//method checks if string is numeric
	public static boolean isNumeric(String str)
	{
		try  
		{  
			double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true; 
	}

	// method for checking validity of variable name
	// returns true
	public static Boolean checkVar(String var, Boolean array, int num){
		// store variable names into a String array
		String[] vars = var.split("(?<=[,])|(?=[,])| ");
		// iterate through each variable name
		for(int a = 0; a < vars.length; a++){
			String temp = vars[a];
			if(!temp.equals(",")){
				// the case where two commas appeared without a variable name in between them and it's neither the first nor the last item in the array [in which case it would be okay]
				if(temp.isEmpty() && a != 0 && a != (vars.length-1)){
					System.out.println("Hindi maaring null o walang pangalan ang variable.");
					close();
				}

				// the case where a variable name exists for checking
				if(!temp.isEmpty()){
					// the case where the starting character is not in the listed characters that are valid
					if(character.indexOf(String.valueOf(temp.charAt(0))) == -1){
						System.out.println("	Kailangang magsimula ang variable sa a-z.");
						close();
					}
					else{
						// iterate through each character in the variable name
						for(int b = 1; b < temp.length(); b++){
							// the case where a character in the variable name is not in the set of valid characters
							if(character2.indexOf(String.valueOf(temp.charAt(b))) == -1){
								// this is only reached if an invalid character is reached
								// the only invalid character allowed is a period and that's if it's the last character of the line -- signifying the end of a line in the "Ale, Bata" language
								System.out.println("	Maari lamang ang a-z, 0-9,- at _.");
								close();
							}
							// the case where all characters in the variable name are valid and we're iterating through the last character of said name
							else if(b == temp.length()-1){
								if(variables.get(temp) != null || arrays.get(temp) != null){
									System.out.println("	Mayroon nang variable na ang pangalan ay " + temp + ".");
									close();
								}
								else{
									if(array){
										createArray(temp, num);
									}
									else{
										variables.put(temp, "");
									}
									//																		System.out.println("	Gumawa ka ng variable na ang pangalan ay " + temp + ".");
								}
							}
						}
					}
				}
			}
		}
		if(var.charAt(0) == ',' || vars[vars.length-1].equals(",")){
			System.out.println("Invalid syntax , should start with variable name and end with !");
			close();
		}
		return true;
	}

	public static Boolean checkTrue(String value, String var){
		if(value != null){
			if(value.equals("TAMA"))
				return true;
			else if(value.equals("MALI"))
				return false;
			else{
				System.out.println(var + " is not a Boolean");
				close();
			}
		}
		else{
			if(variables.get(var).equals("TAMA"))
				return true;
			else if(variables.get(var).equals("MALI"))
				return false;
			else{
				System.out.println(var + " is not a Boolean");
				close();
			}
		}
		return true;
	}

	public static void createArray(String name, int num){
		for(int a = 1; a < num+1; a++){
			variables.put(name + "#" + a, "");
		}
		arrays.put(name, num);
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

	public static void close(){
		System.exit(0);

	}

	public static void skipSpace(){
		while(token != null && token.equals("SPACE")){
			getNextToken();
		}
	}
}
