import java.util.HashMap;
import java.util.Map;

public class While extends Command {
	private HashMap<Integer,Integer> Whiles;
	
	public While(Interpreter _interpreter, ErrorHandler _errorHandler) {
		super(_interpreter,_errorHandler);
		Whiles = new HashMap<Integer,Integer>();
	}

	//public HashMap<Integer,Integer> getWhiles() {
	//	return Whiles;
	//}

	protected void function(String var) {
		checkVariable(var);
		int currentLine = interpreter.getCurrentLine();
		/* Have the errorChecking class check all the whiles
		 * as before then create a HashMap of int int with 
		 * the start line as key and end line as value then
		 * have a metod that lets you give the start line 
		 * and get the end line and use this here to do 
		 * the while loop
		 */
		//get vars
		//get vars each time then check if condition is met
		//if not then continue code, if so then skip to past end line
		//rewrite end to not be ignpre but to send back to start of loop
		//System.out.println(variables.get(var.split(" ")[0]) + "   " + var.split(" ")[2]);
		if (variables.get(var.split(" ")[0]).equals(Integer.parseInt(var.split(" ")[2]))) {
			interpreter.setCurrentLine(Whiles.get(currentLine));
		}
	}

	protected Boolean validateMe(String[] code) {
		Boolean brokenWhile = false;
		for (int i=0; i<code.length; i++) {
			Boolean localFunction = false;
			if (errorHandler.splitCommand(code[i])[0].equals("while")) {
				int endCheck = 0;
				for (int j=i+1; j<code.length; j++) {
					if (errorHandler.splitCommand(code[j])[0].equals("while")) {
						endCheck--;
					} else if (errorHandler.splitCommand(code[j])[0].equals("end")) {
						endCheck++;
						if (endCheck == 1) {
							localFunction = true;
							Whiles.put(i,j);
							j = code.length;
						}
					}
				}
				if (!localFunction) {
					errorHandler.addError(i,"While statement has not been terminated");
					brokenWhile = true;
				}
			}
		}
		if (brokenWhile) {
			return false;
		}
		interpreter.setWhiles(Whiles);
		for (Integer i : Whiles.keySet()) {
			System.out.println(i + "  " + Whiles.get(i));
		}
		return true;
	}
}
