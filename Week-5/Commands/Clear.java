public class Clear extends Command{
	public Clear(Interpreter _interpreter, ErrorHandler _errorHandler) {
		super(_interpreter, _errorHandler);
		argNumber = 1;
	}

	protected void function(String var) {
		//do nothing
	}
	protected Boolean validateMe(String[] code) {
		return true;
	}
}
