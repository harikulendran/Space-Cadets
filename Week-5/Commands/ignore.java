public class Ignore extends Command{
	public Ignore(Interpreter _interpreter, ErrorHandler _errorHandler) {
		super(_interpreter, _errorHandler);
	}

	protected void function(String var) {
		//do nothing
	}
	protected Boolean validateMe(String[] code) {
		return true;
	}
}
