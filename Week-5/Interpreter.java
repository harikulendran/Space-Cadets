import java.util.HashMap;
import java.lang.reflect.*;

public class Interpreter {
	private HashMap<String,Integer> variables;
	protected HashMap<String,Command> commands;
	protected HashMap<Integer,Integer> Whiles;
	private int currentLine = 0;
	private ErrorHandler errorHandler;
	private FileHandler fileHandler;
	private String[] code;
	private String path;

	public Interpreter(String _path) {
		path = _path;
		variables = new HashMap<String,Integer>();
		commands = new HashMap<String,Command>();
		fileHandler = new FileHandler();
		for (String s : fileHandler.getCommandList()) {
			String[] splitFile = s.split("\\.");
			if (splitFile[1].equals("java")) {
				commands.put(splitFile[0].toLowerCase(),getCommand(splitFile[0]));
			}
		}
		errorHandler = new ErrorHandler(this);
		for(String s : commands.keySet()) {
			commands.get(s).setErrorHandler(errorHandler);
		}

	}

	private Command getCommand(String name) {
		Command toSend = null;
		name = name.substring(0,1).toUpperCase() + name.substring(1);
		try {
			Class myClass = Class.forName(name);
			Constructor c = myClass.getConstructor(Interpreter.class,ErrorHandler.class);
			toSend = (Command)c.newInstance(this,errorHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toSend;
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
		code = fileHandler.load(path,";");
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
		if (args.length != 1) {
			System.out.println("Incorrect number of arguments, expects 1");
			System.exit(0);
		}
		Interpreter i = new Interpreter(args[0]);
		i.run();
	}
}
