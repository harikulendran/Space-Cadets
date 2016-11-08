import java.util.HashMap;

public class Interpreter {
	private HashMap<String,Integer> variables;
	protected HashMap<String,Command> commands;
	protected HashMap<Integer,Integer> Whiles;
	private int currentLine = 0;
	private ErrorHandler errorHandler;
	private FileHandler fileHandler;
	private String[] code;

	public Interpreter() {
		variables = new HashMap<String,Integer>();
		commands = new HashMap<String,Command>();
		fileHandler = new FileHandler();
		commands.put("incr",new Incr(this,errorHandler));
		commands.put("decr",new Decr(this,errorHandler));
		commands.put("while",new While(this,errorHandler));
		commands.put("end",new End(this,errorHandler));
		commands.put("clear",new Clear(this,errorHandler));
		commands.put("ignore",new Ignore(this,errorHandler));
		errorHandler = new ErrorHandler(this);
		for(String s : commands.keySet()) {
			commands.get(s).setErrorHandler(errorHandler);
		}

	}

	public HashMap<String,Integer> getVariables() {
		return variables;
	}

	private void printVariables() {
		System.out.println('\n' + "line " + currentLine + ":");
		for (String s: variables.keySet()) {
			System.out.println(s + ": " + variables.get(s));
		}
	}

	public void run() {
		code = fileHandler.load("../Week 4/TestCode.txt",";");
		Boolean error = !errorHandler.checkFile(code);
		for (String s : commands.keySet()) {
			if(!commands.get(s).validateMe(code)) {
				error = false;
			}
		}
		if (!error) {
			errorHandler.printErrors();
		} else {
			for (currentLine = 0; currentLine<code.length; currentLine++) {
				String[] splitLine = errorHandler.splitCommand(code[currentLine]); 
				//ystem.out.println(splitLine[0]);
				commands.get(splitLine[0]).function(splitLine[1]);
				printVariables();
				try {System.in.read();}
				catch (Exception e) {}
			}
		}
	}

	public String[] getCode() {
		return code;
	}

	public int getCurrentLine() {
		return currentLine;
	}
	public void setCurrentLine(int line) {
		currentLine = line;
	}

	public void setWhiles(HashMap<Integer,Integer> _whiles) {
		Whiles = _whiles;
	}
	public HashMap<Integer,Integer> getWhiles() {
		return Whiles;
	}
	public HashMap<String,Command> getCommands() {
		return commands;
	}

	public static void main(String[] args) {
		Interpreter i = new Interpreter();
		i.run();
	}
}
