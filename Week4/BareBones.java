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
	private String[] codeInput;
	

	//Interpret, this will check and run the code if there are no errors
	public void Interpret(String[] in) {
		errorCheck(in);
		printAllVars();
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
		while (Variables.get(parameter) > 1) {
			for (int i=1; i<loopCode.length; i++) {
				chooseMethod(loopCode[i],line+i);
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
							"\t\t" + "MAY execute indefinitely" + '\n';
					}
				}
			}
		}
		if (errorOccurred) {
			return false;
		}
		return true;
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
	
	//input / manipulation methods
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
	
	//Puts the code input in a uniform fashion regardless of bad syntax
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
			System.err.println(e);
		}
		String[] codeArray = fullCode.split(";");
		return codeArray;
	}

	public static void main (String[] args) {
		BareBones bb = new BareBones();
		//String[] testing = {"clear X","incr X","incr X","clear Y","incr Y","incr Y","incr Y","clear Z","while X not 0 do","clear W","while Y not 0 do","incr Z","incr W","decr Y","end","while W not 0 do","incr Y","decr W","end","decr X","end"};
		bb.Interpret(bb.loadCodeFromFile(args[0]));
	}
}
