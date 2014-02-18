public class BaryaBall {

	String name, type, value;

	//Constructor
	public BaryaBall(){
		name = null;
		type = null;
		value = null;
	}
	
	//Constructor with arguments
	public BaryaBall(String n, String v){
		boolean digitLahat = false;

		//validity of variable name should've been checked already
		name = n;

		//iterate through the String in v to check if they're all digits
		for(int i = 0; i < v.length(); i++){
			if(v.charAt(i) == '-' && i == 0)
				i = 1;
			if(!Character.isDigit(v.charAt(i)))
				break;
			else if((i+1) == v.length())
				digitLahat = true;
		}

		//by this time, validity of value should no longer be needed
		if((v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) && !digitLahat) 
			type = "boolean";
		else if(digitLahat) 
			type = "number";
		else 
			type = "string";
		value = v;
	}
	
	public String getName(){
		return name;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getType(){
		return type;
	}

}