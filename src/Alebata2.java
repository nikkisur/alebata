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
	private static Scanner scan, scanLine;
	private static File lookUp;
	private static HashMap<String, String> reservedWords = new HashMap<String, String>();
	private static ArrayList<String> tokens = new ArrayList<String>();
	private static ArrayList<String> lexemes = new ArrayList<String>();

	private static int index = 0;
	private static String token;
	private static float numValue;
	private static String operation = "";
	private static String equation = "";

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
		A();
	}

	public static void action(){
		while(scan.hasNext()){
			String line = scan.nextLine();
			String[] words = line.split("(?<=[!\"()*/%^+-])|(?=[!\"()*/%^+-])| ");
			String value = "";

			if(line.contains("\"")){
				for(int a = 0; a < words.length; a++){
					String word = words[a];
					if(reservedWords.get(word) != null){
						if(word.equals("\"")){
							tokens.add(reservedWords.get(word));
							lexemes.add(word);
							a++;
							word = words[a];
							if(line.indexOf("\"")+1 < line.lastIndexOf("\"")){
								value = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
								if(value.contains("\\\"")){
									value = value.substring(0, value.indexOf("\\"))+ value.substring(value.indexOf("\\") + 1,value.length());
								}
								tokens.add("IDENT");
								lexemes.add(value);
							}
							else{
								System.out.println("Need to close \" \" ");
								close();
							}
							while(!word.equals("\"")){
								if(word.equals("\\")){
									a++;
									word = words[a];
									if(word.equals("\"")){
										a++;
									}
								}
								a++;
								if(a < words.length){
									word = words[a];
								}
								else{
									System.out.println("Invalid string value");
									close();
								}
							}

						}
						tokens.add(reservedWords.get(word));
						lexemes.add(word);
						//						System.out.println(word + "			"+ reservedWords.get(word));
					}
					else{
						if(!word.equals("")){
							tokens.add("IDENT");
							lexemes.add(word);
							//							System.out.println(word + "			IDENT");
						}
					}
				}
			}
			else{
				for(String word: words){
					if(reservedWords.get(word) != null){
						tokens.add(reservedWords.get(word));
						lexemes.add(word);
						//					System.out.println(word + "			"+ reservedWords.get(word));
					}
					else{
						if(!word.equals("")){
							tokens.add("IDENT");
							lexemes.add(word);
							//						System.out.println(word + "			IDENT");
						}
					}
				}
			}
		}
	}

	public static void getNextToken(){
		if(index == tokens.size())
			token = null;
		else
			token = tokens.get(index++);
	}

	//A -> GAWAB 
	public static void A(){
		while(token != null){
			if(token.equals("TYPE1")){
				getNextToken();
				B();
			}
			else if(token.equals("IDENT")){
				getNextToken();
				C();
			}
			else if(token.equals("PRINT1")){
				getNextToken();
				D();

			}
			getNextToken();
		}
	}

	//B -> NG
	public static void B(){
		if(token != null){
			if(token.equals("TYPE2")){
				getNextToken();
				while(token!= null && token.equals("IDENT")){
					checkVar(lexemes.get(index-1));
					getNextToken();
				}

				if(token == null || !token.equals("TERMINATOR")){
					System.out.println("Missing ! at the end of line");
					close();
				}
			}
			else{
				System.out.println("Syntax should be: GAWA NG <var_name>!");
				close();
			}
		}
		else{
			System.out.println("Syntax should be: GAWA NG <var_name>!");
			close();
		}
	}

	public static void C(){
		String var = lexemes.get(index-2);
		if(variables.get(lexemes.get(index-2)) != null){
			if(token.equals("EQUALS")){
				getNextToken();
				if(token.equals("DQUOTE")){
					getNextToken();
					String value = "";
					while(!token.equals("DQUOTE")){
						value += lexemes.get(index-1);
						if(token != null && token.equals("SLASH")){
							getNextToken();
							if(token!= null && token.equals("DQUOTE")){
								value += " \"";
								getNextToken();
							}

						}
						if(token == null){
							System.out.println("Missing \" at the end of line");
							close();
						}
						else{
							value += " ";
						}
						getNextToken();
					}
					variables.put(var, value);
				}
				else{

					BaryaBall ball = new BaryaBall(var, lexemes.get(index-1));
					if(ball.type.equals("number")){
						numValue = 0;
						numStart();
					}
					else if(ball.type.equals("string")){
						System.out.println("Strings should be placed inside a \"\"");
						close();
					}
					else if(ball.type.equals("error")){
						System.out.println("Wrong number format");
						close();
					}
					else{
						variables.put(var, lexemes.get(index-1));
					}
				}
			}
			else{
				System.out.println("Syntax should be: <var_name> AY <value>");
				close();
			}
		}
		else{
			System.out.println(var + " must be initialized.");
			close();
		}
	}

	public static void D(){
		String value = "";
		if(token != null && token.equals("PRINT2")){
			getNextToken();
			if(token != null && token.equals("PRINT3")){
				getNextToken();
				while(token != null && !token.equals("TERMINATOR")){
					if(token.equals("DQUOTE")){
						getNextToken();
						while(!token.equals("DQUOTE")){
							value += lexemes.get(index-1);
							if(token.equals("SLASH")){
								getNextToken();
								if(token!= null && token.equals("DQUOTE")){
									value += " \"";
									getNextToken();
								}
							}
							getNextToken();
						}
						System.out.println(value);
					}
					else if(token.equals("IDENT")){
						if(variables.containsKey(lexemes.get(index-1))){
							System.out.println(variables.get(lexemes.get(index-1)));
						}
					}
					getNextToken();
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
		numA();
		if(token.equals("TERMINATOR")){
			System.out.println(equation);
		}
	}

	public static void numA(){
		numB();
		numC();
	}

	public static void numB(){
		numD();
		numE();
	}

	public static void numC(){
		if(token.equals("PLUS")){
			getNextToken();
			if(token.equals("MINUS")){
				getNextToken();
				BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
				if(ball.type.equals("number"))
					lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
				else{
					System.out.println("Wrong number format");
					close();
				}
			}
			operation = "+";
			equation += operation;

			numB();
			numC();
		}
		else if(token.equals("MINUS")){
			getNextToken();
			if(token.equals("MINUS")){
				getNextToken();
				BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
				if(ball.type.equals("number"))
					lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
				else{
					System.out.println("Wrong number format");
					close();
				}
			}
			operation = "-";
			equation += operation;
			numB();
			numC();
		}
	}

	public static void numD(){
		numF();
		numG();
	}

	public static void numE(){
		if(token.equals("MULT")){
			getNextToken();
			if(token.equals("MINUS")){
				getNextToken();
				BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
				if(ball.type.equals("number"))
					lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
				else{
					System.out.println("Wrong number format");
					close();
				}
			}
			operation = "*";
			equation += operation;
			numD();
			numE();
		}
		else if(token.equals("DIVIDE")){
			getNextToken();
			if(token.equals("MINUS")){
				getNextToken();
				BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
				if(ball.type.equals("number"))
					lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
				else{
					System.out.println("Wrong number format");
					close();
				}
			}
			operation = "/";
			equation += operation;
			numD();
			numE();
		}else if(token.equals("MODULO")){
			getNextToken();
			if(token.equals("MINUS")){
				getNextToken();
				BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
				if(ball.type.equals("number"))
					lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
				else{
					System.out.println("Wrong number format");
					close();
				}
			}
			operation = "%";
			equation += operation;
			numD();
			numE();
		}
	}

	public static void numF(){
		numH();
	}

	public static void numG(){
		if(token.equals("EXP")){
			getNextToken();
			if(token.equals("MINUS")){
				getNextToken();
				BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
				if(ball.type.equals("number"))
					lexemes.set(index-1, String.valueOf(Float.parseFloat(lexemes.get(index-1))*-1));
				else{
					System.out.println("Wrong number format");
					close();
				}
			}
			operation = "%";
			equation += operation;
			numF();
			numG();
		}
	}

	public static void numH(){
		if(token.equals("PAREN")){
			equation += "(";
			getNextToken();
			numA();
			if(token.equals("ENDPAREN")){
				equation += ")";
				getNextToken();
			}
			else{
				System.out.println("Need to close ()");
				close();
			}

		}
		else{
			BaryaBall ball = new BaryaBall("", lexemes.get(index-1));
			if(ball.type.equals("number")){
				equation += lexemes.get(index-1);
				getNextToken();
			}
			else{
				System.out.println("Number error");
				close();
			}
		}
	}

	// method for checking validity of variable name
	// returns true
	public static Boolean checkVar(String var){
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
								if(variables.get(temp) != null){
									//									System.out.println("	Mayroon nang variable na ang pangalan ay " + temp + ".");
									close();
								}
								else{
									variables.put(temp, "");
									//									System.out.println("	Gumawa ka ng variable na ang pangalan ay " + temp + ".");
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
}
