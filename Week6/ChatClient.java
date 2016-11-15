import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient {
	private Socket socket;
	private String myName;

	public ChatClient() {
		System.out.println("Connecting to Chat Server...");
		createSocket("localhost",1025);
		getUsername();
		startReadWriteThreads();
	}

	private void createSocket(String host, int port) {
		try {
			socket = new Socket(host,port);
		} catch (Exception e) {
			System.out.println("Unnable to connect to server");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void getUsername() {
		Scanner sc = new Scanner(System.in);
		System.out.println("What is your name?");
		myName = sc.nextLine();
	}

	private void startReadWriteThreads() {
		Thread read = new Thread(new ServerReader(socket));
		Thread write = new Thread(new ServerWriter(socket,myName));
		read.start();
		write.start();
	}

	public static void main (String[] args) {
		ChatClient cc = new ChatClient();
	}
}

class ServerReader implements Runnable {
	Socket socket;

	public ServerReader(Socket _socket) {
		socket = _socket;
	}

	public void run() {
		DataInputStream serverStream = getDataInStream();
		printServerStream(serverStream);
	}

	private DataInputStream getDataInStream() {
		try {
			InputStream serverStream = socket.getInputStream();
			DataInputStream dataIn = new DataInputStream(serverStream);
			return dataIn;
		} catch (Exception e) {
			System.out.println("Unable to get Data Stream from server");
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}

	private void printServerStream(DataInputStream in) {
		while (true) {
		    getServerInput(in);
		}
	}

	private void getServerInput(DataInputStream in) {
		try {
			System.out.println(in.readUTF());
		} catch (Exception e) {
			System.out.println("Unable to read Data Stream from Server");
			e.printStackTrace();
			System.exit(-1);
		}
	}
}

class ServerWriter implements Runnable {
	Socket server;
	String myName;
	public ServerWriter(Socket _socket, String _myName) {
		server = _socket;
		myName = _myName;
	}

	public void run() {
		DataOutputStream dataOut = getDataOutStream();
		Scanner sc = new Scanner(System.in);
		sendMessageToServer(dataOut,sc);
	}

	private DataOutputStream getDataOutStream() {
		try {
			OutputStream clientStream = server.getOutputStream();
			DataOutputStream dataOut = new DataOutputStream(clientStream);
			return dataOut;
		} catch (Exception e) {
			System.out.println("Unable to open Data Stream to Server");
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}

	private void sendMessageToServer(DataOutputStream out, Scanner sc) {
		while(true) {
			String message = getUserInput(sc);
			writeToServer(out,message);
			checkQuit(message);
		}
	}

	private void writeToServer(DataOutputStream out, String message) {
		try {
			out.writeUTF(message);
		} catch (Exception e) {
			System.out.println("Unable to send message through Output Stream");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private String getUserInput(Scanner sc) {
		String input = sc.nextLine();
		return "["+myName+":] "+input;
	}

	private void checkQuit(String message) {
		if (message.equals("["+myName+":] QUIT")) {
			try {
				System.out.println("did work");
				server.close();
				System.exit(0);
			} catch (Exception e) {
				System.out.println("didntwork");
			}
		}
	}
}
