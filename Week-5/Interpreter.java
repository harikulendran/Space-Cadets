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
		errorHandler = new ErrorHandler();
		fileHandler = new FileHandler();
		commands.put("incr",new Incr(this,errorHandler));
		commands.put("decr",new Decr(this,errorHandler));
		commands.put("while",new While(this,errorHandler));
		commands.put("end",new End(this,errorHandler));
		commands.put("clear",new Ignore(this,errorHandler));

	}

	public HashMap<String,Integer> getVariables() {
		return variables;
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
				System.out.println("Line " + (currentLine+1) + ": " + "X: " + variables.get("X"));
				System.out.println("Line " + (currentLine+1) + ": " + "Y: " + variables.get("Y"));
				System.out.println("Line " + (currentLine+1) + ": " + "W: " + variables.get("W"));
				System.out.println("Line " + (currentLine+1) + ": " + "Z: " + variables.get("Z"));
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

	public static void main(String[] args) {
		Interpreter i = new Interpreter();
		i.run();
	}
}
