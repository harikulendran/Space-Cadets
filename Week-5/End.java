import java.util.HashMap;
import java.util.Map;

public class End extends Command {
	public End(Interpreter _interpreter, ErrorHandler _errorHandler) {
		super(_interpreter,_errorHandler);
	}

	protected Boolean validateMe(String[] code) {
		return true;
	}

	protected void function(String var) {
		HashMap<Integer,Integer> whiles = interpreter.getWhiles();
		int targetNo = -1;
		for (Map.Entry<Integer,Integer> entry : whiles.entrySet()) {
			if (entry.getValue().equals(interpreter.getCurrentLine())) {
				targetNo = entry.getKey();
			}
		}
		interpreter.setCurrentLine(targetNo-1);
	}
}
