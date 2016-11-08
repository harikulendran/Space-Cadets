import java.util.HashMap;
import java.util.Map;

public class Incr extends Command {
	public Incr (Interpreter _interpreter, ErrorHandler _errorHandler) {
		super(_interpreter,_errorHandler);
		argNumber = 1;
	}

	protected void function(String var) {
		checkVariable(var);
		variables.put(var,variables.get(var)+1);
	}
	protected Boolean validateMe(String[] code) {
		return true;
	}
}
