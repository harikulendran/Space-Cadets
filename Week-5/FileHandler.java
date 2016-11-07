import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileHandler {
	public String[] load(String path, String deliminator) {
		String truePath;
		if (path.contains(":")) {
			truePath = path;
		} else {
			truePath = System.getProperty("user.dir") + "//" + path;
		}
		
		String unSplitOutput = "";

		try {
			String currentLine;
			BufferedReader br = new BufferedReader(new FileReader(truePath));
			while ((currentLine = br.readLine()) != null) {
				unSplitOutput += currentLine.replace("\t","").replaceAll("^\\s+","");
			}
		} catch (IOException e) {
			System.out.println("System could not find the specified file");
			System.exit(0);
		}
		//System.out.println(unSplitOutput);
		return unSplitOutput.split(deliminator);
	}

	public static void main (String[] args) {
		FileHandler fh = new FileHandler();
		fh.load("../Week 4/TestCode.txt",";");
	}
}
