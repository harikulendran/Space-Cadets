import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BareBones {
	//Hold the variables used by the BareBones code
	private HashMap<String,Integer> Variables = new HashMap<String,Integer>();
	private HashMap<Integer,String[]> Whiles = new HashMap<Integer,String[]>();
	private String[] allCommands = {"clear","incr","decr","while","end"};
	private Boolean warning = false;
	private String warningList = "";
	private Boolean Error = false;
	private String errorList = "ERRORS:" + '\n';
	private String currentError;

	//Display Variables
	private Boolean printAllSteps = false;
	private Boolean safeExecute = false;
	private Boolean waitForInput = false;
	

	//Interpret, this will check and run the code if there are no errors
	private void Interpret(String[] in) {
		errorCheck(in);
		printVariables();
	}
	
	//CompileErrorCheck
	private void errorCheck(String[] code) {
		int errorCount = 0;
		for (int i=0; i<code.length; i++) {
			if(!validateLine(code[i])) {
				errorCount++;
				errorList += "Line " + (i+1) + ":" + currentError + '\n';
			}
		}
		if (errorCount > 0) {
			Error = true;
			System.out.println("There are " + errorCount + " error(s) in the code");
			System.out.println('\n' + errorList);
		} else {
			Error = false;
		}
		if (!Error) {
			if (!checkWhile(code)) {
				Error = true;
				System.out.println("While loop errors:" +'\n' + errorList);
			}
		}
		if (!Error) {
			if (!checkInfiniteLoop()) {
				Error = true;
				System.out.println(errorList);
			}
		}
		if (warning) {
			System.out.println("POSSIBLE ERRORS:" + '\n' + warningList);
		}
		if (!Error) {
			Execute(code);
		}
	}

	//Execute
	private void Execute(String[] inputEx) {
		inputEx = removeWhiles(inputEx);
		for (int i=0; i<inputEx.length; i++) {
			chooseMethod(inputEx[i],i);
		}
	}

	//BackBone methods
	private void BBclear(String var) {
		check(var);
		Variables.put(var,0);
	}

	private void BBincr(String var) {
		check(var);
		int temp = Variables.get(var);
		temp++;
		Variables.put(var,temp);
	}

	private Boolean BBdecr(String var) {
		check(var);
		if(Variables.get(var) > 0) {
			int temp = Variables.get(var);
			temp--;
			Variables.put(var,temp);
			return true;
		}
		return false;
	}

	private void BBwhile(int line) {
		String[] loopCode = Whiles.get(line);
		String parameter = splitCommand(splitCommand(loopCode[0])[1])[0];
		check(parameter);
		int noOfLoops = 0;
		while (Variables.get(parameter) > 0) {
			for (int i=1; i<loopCode.length; i++) {
				chooseMethod(loopCode[i],line+i);
			}
			noOfLoops++;
			if (noOfLoops > 100 && safeExecute) {
				System.out.println("100 loops exceeded, program exiting. Check loop or run out of safe mode");
				System.exit(0);
			}
		}
	}
	
	//Validation methods
	private void check(String var) {
		if (!Variables.containsKey(var)) {
			Variables.put(var,0);
		}
	}
	
	private void chooseMethod(String command, int Line) {
		if (printAllSteps) {
			System.out.println("Line " + Line + ": " + command);
			printVariables();
		}
		if (waitForInput) {pause();}

		String[] input = splitCommand(command);
		if (input[0].equals("clear")) { BBclear(input[1]);}
		else if (input[0].equals("incr")) {BBincr(input[1]);}
		else if (input[0].equals("decr")) {BBdecr(input[1]);}
		else if (input[0].equals("while")) {BBwhile(Line);}
	}

	//Goes through all the code and finds each while loop and puts them in a HashMap
	private Boolean checkWhile(String[] input) {
		Boolean errorOccurred = false;
		for (int i=0; i<input.length; i++) {
			Boolean functionalWhile = false;
			if (splitCommand(input[i])[0].equals("while")) {
				int endCheck = 0;
				for (int j=i+1; j<input.length; j++) {
					if (splitCommand(input[j])[0].equals("while")) {
						endCheck--;
					} else if (splitCommand(input[j])[0].equals("end")) {
						endCheck++;
						if (endCheck == 1) {
							functionalWhile = true;
							//I use i, not i+1, because I want the while present in the map so I can check the variable
							Whiles.put(i,Arrays.copyOfRange(input,i,j));
							j = input.length;
						}
					}
				}
				if (!functionalWhile) {
					errorList += "Line " + (i+1) + ":"
							+ '\n' + "\t" + input[i] + ";"
							+ '\n' + "\t\t" + "while statement has not been terminated" + '\n';
					errorOccurred = true;
				}	
			}
		}
		if (errorOccurred) {
			return false;
		}
		return true;
	}
	
	//This is not perfect as it will not catch infinite loops if there is a nested while loop incrementing the parameter
	//and not allowing it to reach 0, but it's still useful for making sure there are no other infinite loops
	private Boolean checkInfiniteLoop() {
		Boolean errorOccurred = false;
		if (!Whiles.isEmpty()) {
			for (Map.Entry<Integer,String[]> entry : Whiles.entrySet()) {
				String parameter = splitCommand(splitCommand(entry.getValue()[0])[1])[0];
				int internalWhileCount = 0;
				int dec = 0;
				for (String s : entry.getValue()) {
					String[] stwo = splitCommand(s);
					if ((stwo[0]+" "+stwo[1]).equals("decr "+parameter)){
						dec++;
					} else if ((stwo[0]+" "+stwo[1]).equals("incr "+parameter)) {
						dec--;
					} else if ((stwo[0]+" "+stwo[1]).contains("while")) {
						internalWhileCount++;
					}
				}
				if (dec < 1) {
					if (internalWhileCount<2) {
						errorList += "While loop:" + '\n' +
							"\t" + entry.getValue()[0] + '\n' +
							"\t\t" + "will execute indefinitely" + '\n';
						errorOccurred = true;
					} else {
						warning = true;
						warningList += "While loop:" + '\n' +
							"\t" + entry.getValue()[0] + '\n' +
							"\t\t" + "MAY execute indefinitely, consider executing with -s" + '\n';
					}
				}
			}
		}
		if (errorOccurred) {
			return false;
		}
		return true;
	}
	

	//These two methods below remove the loop code from all necessary code snippets so they are not run more than they are
	//supposed to be
	private String[] removeWhiles(String[] allcode) {
		for (Map.Entry<Integer,String[]> entry : Whiles.entrySet()) {
			for (int i=entry.getKey()+1; i<entry.getKey()+entry.getValue().length; i++) {
				allcode[i] = "IGNORE";
			}
			for (int i=1; i<entry.getValue().length; i++) {
				if (entry.getValue()[i].contains("while")) {
					String[] temp = entry.getValue();
					temp = removeWhiles(temp,entry.getKey()+i,i);
					entry.setValue(temp);
				}
			}
		}
		return allcode;
	}

	private String[] removeWhiles(String[] allcode, int Line, int myLine) {
		for (int i=myLine+1; i<Whiles.get(Line).length+myLine+1; i++) {
			allcode[i] = "IGNORE";
		}
		return allcode;
	}

	//Checks if the given line has any errors of syntax and existence
	private Boolean validateLine(String line) {
		currentError = '\n' + "\t" + line + ";" + '\n' + "\t"+"\t";
		
		//Check to see if any non alphanumeric characters are used
		//need this because I use a character for formatting later so if the code contained it
		//it would allow incorrect code to execute
		Matcher nonAlphNum = Pattern.compile("[^a-zA-Z0-9 ]").matcher(line);
		if (nonAlphNum.find()) {
			currentError += "Invalid characters found";
			return false;
		}

		String[] lineSplit = splitCommand(line);
		Boolean test = false;
		for(String s : allCommands) {
			if (lineSplit[0].equals(s)) {
				test = true;
			}
		}
		if (!test) {
			currentError += "Command not recognized";
			return false;
		}
		if (line.split(" ").length < 2 && !lineSplit[0].equals("end")) {
			currentError += "Command requires a parameter";
			return false;
		}
		if (lineSplit[0].equals("while")) {
			String[] whileSplit = splitCommand(lineSplit[1]);
			if (whileSplit[1].replaceAll("\\s+","|").equals("not|0|do")) {
				return true;
			} else {
				currentError += "while command takes one parameter and must end with 'not 0 do'";
				return false;
			}
		} else {
			String newLine = lineSplit[1].trim();
			String[] oSplit = newLine.split(" ");
			if(oSplit.length != 1) {
				currentError += "Wrong number of parameters";
				return false;
			} else {
				return true;
			}
		}
	}
	
	//Testing code, used to see what was going on in the program to find out what was going wrong
	/*
	private void print(String var) {
		System.out.println(Variables.get(var));
	}
	
	private void printAllVars() {
		for (Map.Entry<String,Integer> entry : Variables.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}

	private void printWhiles() {
		for (Map.Entry<Integer,String[]> entry : Whiles.entrySet()) {
			System.out.println("Line " + entry.getKey() +":");
			for (String s : entry.getValue()) {
				System.out.println(s);
			}
			System.out.println();
		}
	}
	*/
	
	//Puts the code input in a uniform fashion regardless of bad formatting
	private String[] splitCommand(String command) {
		String[] output = new String[2];
		command = command.replaceAll("^\\s+", "");
		Matcher front = Pattern.compile("(.*?) ").matcher(command);
		Matcher back = Pattern.compile(" (.*)").matcher(command);
		if (front.find()) {
			output[0] = front.group(1);
		} else {
			output[0] = command;
		}
		if(back.find()){
			output[1] = back.group(1);
		} else {
			output[1] = "ERROR";
		}
		return output;
	}
	
	private String[] loadCodeFromFile(String path) {
		String truePath;
		if (path.contains(":")) {
			truePath = path;
		} else {
			truePath = System.getProperty("user.dir") + "//" + path;
		}
		String fullCode = "";
		try {
			String curLine;
			BufferedReader br = new BufferedReader(new FileReader(truePath));
			while ((curLine = br.readLine()) != null) {
				fullCode += curLine.replace("\t","");
			}
		} catch (IOException e) {
			System.err.println("System could not find the file specified");
			System.exit(0);
		}
		String[] codeArray = fullCode.split(";");
		return codeArray;
	}

	private void printVariables() {
		System.out.println('\n' + "Current state of variables:");
		for (Map.Entry<String,Integer> entry : Variables.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}

	private Boolean checkArguments(String[] args) {
		if (args.length < 1) {
			System.out.println("Please specify a file to interpret, use -h for help");
			return false;
		} else if (args.length == 1) {
			if (args[0].equals("-h")) {
				//print help
				System.out.println("Commands:" + '\n' +
						"--------------------------------------------------------------" + '\n' +
						"  -h  :  prints this help window" + '\n' +
						"  -p  :  prints the state of all variables after each line of" + '\n' +
						"         code is executed" + '\n' +
						"  -s  :  code is executed in safe mode, in this mode a while" + '\n' +
						"         can only loop 100 times before an error is thrown" + '\n' +
						"  -w  :  waits for the users input before executing the next" + '\n' +
						"         line of code (automatically prints all lines)" + '\n' +
						"**The code file (either local path or absolute path) should be" + '\n' +
						"the last parameter passed to the interpreter**");
				return false;
			} else if (args[0].contains("-")) {
				System.out.println("Please specify a file to interpret, use -h for help");
				return false;
			}
		} else {
			if(args[args.length-1].contains("-")) {
				System.out.println("Please specify a file to interpret, make sure the file is entered last after all other commands");
				return false;
			} else {
				for (int i=0; i<args.length-1; i++) {
					//Very messy but couldn't use \b contraction as I needed - to be included in word boundaries so had to 
					//use the expanded form
					if (!args[i].matches("(?:(?<![\\w-])(?=[\\w-])|(?<=[\\w-])(?![\\w-]))(-p|-s|-w|-h)(?:(?<![\\w-])(?=[\\w-])|(?<=[\\w-])(?![\\w-]))")) {
						System.out.println("command '" + args[i] + "' not recognized");
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private void setArguments(String[] args) {
		if (Arrays.asList(args).contains("-p")) {printAllSteps = true;}
		if (Arrays.asList(args).contains("-s")) {safeExecute = true;}
		if (Arrays.asList(args).contains("-w")) {waitForInput = true; printAllSteps = true;}
	}

	private void pause() {
		System.out.println("Press Enter to continue...");
		try { System.in.read(); }
		catch (Exception e) {}
	}

	public static void main (String[] args) {
		BareBones bb = new BareBones();
		if (bb.checkArguments(args)) {
			bb.setArguments(args);
			bb.Interpret(bb.loadCodeFromFile(args[args.length-1]));
		}
	}
}
