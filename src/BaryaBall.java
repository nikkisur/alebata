public class BaryaBall {

	String name, type, value;
	int period;
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
			if(v.charAt(i) == '.')
				period++;
			if(!Character.isDigit(v.charAt(i)) || (v.charAt(i) != '(' && period > 1))
				break;
			else if((i+1) == v.length())
				digitLahat = true;
		}

		//by this time, validity of value should no longer be needed
		if((v.equals("TAMA") || v.equals("FALSE")) && !digitLahat) 
			type = "boolean";
		else if(digitLahat) 
			type = "number";
		else if(period > 1){
			type = "error";
		}
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