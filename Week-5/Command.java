import java.util.HashMap;
import java.util.Map;

public abstract class Command {
	protected Interpreter interpreter;
	protected ErrorHandler errorHandler;
	protected HashMap<String,Integer> variables;
	protected int argNumber;
	protected String arguments;

	public Command(Interpreter _interpreter, ErrorHandler _errorHandler) {
		interpreter = _interpreter;
		errorHandler = _errorHandler;
		variables = interpreter.getVariables();
		argNumber = 0;
		arguments = "";
	}

	protected void checkVariable(String var) {
		Boolean found = false;
		for (Map.Entry<String,Integer> entry : variables.entrySet()) {
			if (entry.getKey().equals(var)) {
				found = true;
			}
		}
		if (!found) {
			variables.put(var,0);
		}
	}

	protected String getCommandData() {
		return Integer.toString(argNumber) + "|" + arguments;
	}

	protected abstract void function(String var);
	protected abstract Boolean validateMe(String[] code);
	
	public void setErrorHandler(ErrorHandler eh) {
		errorHandler = eh;
	}

	protected void run(String var) {
		checkVariable(var);
		function(var);
	}
}
