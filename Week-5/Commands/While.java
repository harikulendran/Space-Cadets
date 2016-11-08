import java.util.HashMap;
import java.util.Map;

public class While extends Command {
	private HashMap<Integer,Integer> Whiles;
	
	public While(Interpreter _interpreter, ErrorHandler _errorHandler) {
		super(_interpreter,_errorHandler);
		Whiles = new HashMap<Integer,Integer>();
		argNumber = 4;
		arguments = "VAR|not|0|do";
	}

	protected void function(String var) {
		System.out.println(var.split(" ")[0]);
		checkVariable(var.split(" ")[0]);
		int currentLine = interpreter.getCurrentLine();
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
