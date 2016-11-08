import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.HashMap;

public class ErrorHandler {
	private String Errors;
	private HashMap<String,String[]> commandInfo;
	private FileHandler file;
	private String[] inputCode;
	private Interpreter interpreter;

	public ErrorHandler(Interpreter _interpreter) {
		interpreter = _interpreter;
		Errors = "Errors were found in your code:";
		file = new FileHandler();
		commandInfo = new HashMap<String,String[]>();
		String[] fileList = file.getCommandList();
		//ONLY CHECK HAVA FILEs
		HashMap<String,Command> commands = interpreter.getCommands();
		for (String s : fileList) {
			String key = s.split("\\.")[0].toLowerCase();
			commandInfo.put(key,(commands.get(key).getCommandData()).split("\\|"));
		}
	}

	public void addError(int lineNo, String errorText) {
		String currentError = '\n' + "\t" + "line " + lineNo + ": " + inputCode[lineNo] + ";" + '\n' + "\t\t";
		Errors += currentError + errorText;
	}

	public String[] splitCommand(String command) {
		String[] output = new String[2];
		Matcher front = Pattern.compile("(.*?) ").matcher(command);
		Matcher back = Pattern.compile(" (.*)").matcher(command);

		if(front.find()) { output[0] = front.group(1); }
		else { output[0] = command; }
		if(back.find()) { output[1] = back.group(1); }
		else { output[1] = ""; }

		return output;
	}
	
	public void printErrors() {
		System.out.println(Errors);
	}

	public Boolean checkFile(String[] code) {
		inputCode = code;
		Boolean errorFound = false;
		for (int i=0; i<code.length; i++) {
			if (!validateLine(i+1,code[i])) {
				errorFound = true;
			}
		}
		return errorFound;
	}

	private Boolean validateLine(int lineNo, String line) {
		String currentError = '\n' + "\t" + "line " + lineNo + ": " + line + ";" + '\n' + "\t\t";
		
		//Check for non Alphanumeric characters
		Matcher nonAlphanumeric = Pattern.compile("[^a-zA-Z0-9 ]").matcher(line);
		if (nonAlphanumeric.find()) {
			currentError += "Invalid characters found";
			Errors += currentError;
			return false;
		}
		
		//Check if commands are valid
		String[] splitLine = splitCommand(line);
		Boolean commandExists = false;
		for(String s : commandInfo.keySet()) {
			if (splitLine[0].equals(s.split("\\|")[0])) { commandExists = true; }
		}
		if (!commandExists) {
			Errors += currentError + "Command not recognized";
			return false;
		}
		
		//Check if command has correct number of parameters
		String[] variableData = commandInfo.get(splitLine[0]);
		if (splitLine[1].split(" ").length != Integer.parseInt(variableData[0]) && !splitLine[1].equals("")) {
			Errors += currentError + "Wrong number of parameters, expected " + variableData[0];
			return false;
		}

		//Check if command has correct syntax
		if (Integer.parseInt(variableData[0]) > 1) {
			Boolean syntaxError = false;
			for (int i=0; i<Integer.parseInt(variableData[0]); i++) {
				if (!splitLine[1].split(" ")[i].equals(variableData[i+1]) && !variableData[i+1].equals("VAR")) {
					Errors += currentError + "Incorrect Syntax";
					syntaxError = true;
				}
			}
			if (syntaxError) {
				return false;
			}
		}

		return true;
	}

}
