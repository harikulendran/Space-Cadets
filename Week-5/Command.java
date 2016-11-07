import java.util.HashMap;
import java.util.Map;

public abstract class Command {
	protected Interpreter interpreter;
	protected ErrorHandler errorHandler;
	protected HashMap<String,Integer> variables;

	public Command(Interpreter _interpreter, ErrorHandler _errorHandler) {
		interpreter = _interpreter;
		errorHandler = _errorHandler;
		variables = interpreter.getVariables();
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

	protected abstract void function(String var);
	protected abstract Boolean validateMe(String[] code);

	protected void run(String var) {
		checkVariable(var);
		function(var);
	}
}
