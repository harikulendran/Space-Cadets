import java.io.*;
import java.net.*;
import java.util.regex.*;

public class GetUser {
	private String getInput(String request) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		System.out.println(request);
		while (true) {
			try {
				input = br.readLine();
				Pattern an = Pattern.compile("[^a-zA-Z0-9]");
				if (an.matcher(input).find()) {
					System.out.println("Input contains invalid characters, please try again");
				} else {
					br.close();
					break;
				}
			} catch (IOException e) {
				System.err.println("Error: " + e);
				input = null;
				System.out.println("Please try again");
			}
		}
		return input;
	}
	
	private String[] getData(URL link) {
		String inputLine = null;
		String[] data = new String[4];
		Boolean Found = false;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(link.openStream()));
			while ((inputLine = br.readLine()) != null) {
				if (inputLine.contains("BreadcrumbList")) {
					Found = true;
					break;
				}
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error: " + e);
			return data;
		}
		if (!Found) {
			System.out.println("Error: Data not found");
			return data;
		} else {
			Pattern[] patterns = new Pattern[4];
			patterns[0] = Pattern.compile("property=\"name\">(.*?)<");
			patterns[1] = Pattern.compile("property='telephone'>(.*?)<");
			patterns[2] = Pattern.compile("property=\"url\" href=\"(.*?)\">Personal homepage");
			patterns[3] = Pattern.compile("property='email'>(.*?)<");
			for (int x=0; x<4; x++) {
				Matcher match = patterns[x].matcher(inputLine);
				if (match.find()) {
					data[x] = match.group(1);
				} else {
					data[x] = "Unknown";
				}
			}
			return data;
		}
	}

	public static void main (String[] args) {
		GetUser gu = new GetUser();
		String username = gu.getInput("Please enter an email id");
		URL link = null;
		try {
			link = new URL("http://www.ecs.soton.ac.uk/people/" + username);
		} catch (MalformedURLException e) {
			System.err.println(e);
		}
		String[] scrapedData = gu.getData(link);
		if (scrapedData[0].contains("Unknown") || scrapedData[0] == null) {
			System.out.println("User not found or may be set to private");
		} else {
			System.out.println(scrapedData[0] + '\n' +
					"---------------------" + '\n' +
					"Tel: " + scrapedData[1] + '\n' +
					"email: " + scrapedData[3] + '\n' +
					"webpage: " + scrapedData[2]);
		}
	}
}
