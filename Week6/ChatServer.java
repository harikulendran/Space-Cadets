import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ChatServer {
	private ServerSocket serverSocket;

	private ArrayList<Socket> allSockets;

	public ChatServer(int port) {
		allSockets = new ArrayList<Socket>();
		createServerSocket(port);
	}

	public void run() {
		while(true) {
			try {
			Socket client = connectToClient();
			DataOutputStream dataOut = getDataOutStream(client);
			writeToClient(dataOut,"You have connected to the server! " + allSockets.size() + " people chatting!");
			createClientThread(client);
			} catch (IOException e) {
				//do nothing
			}
		}
	}
	
	private void createServerSocket(int port) {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(10000);
		} catch (Exception e) {
			System.out.println("Unable to create Server Socket");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private Socket connectToClient() throws IOException {
		Socket client = serverSocket.accept();
		allSockets.add(client);
		System.out.println(client.getRemoteSocketAddress() + " has connected");
		return client;	
	}

	private DataOutputStream getDataOutStream(Socket socket) {
		try {
			OutputStream socketStream = socket.getOutputStream();
			DataOutputStream out = new DataOutputStream(socketStream);
			return out;
		} catch (IOException e) {
			System.out.println("Unable to open Data Stream to Socket");
			return null;
		}
	}
	

	private void writeToClient(DataOutputStream out, String message) {
		try {
			out.writeUTF(message);
		} catch (Exception e) {
			System.out.println("Unable to send message through Output Stream");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void createClientThread(Socket client) {
		if (client != null) {
			new Thread(new ClientReader(client,allSockets)).start();
		}
	}

	public static void main (String[] args) {
		ChatServer cs = new ChatServer(1025);
		cs.run();
	}
}

class ClientReader implements Runnable {
	private Socket client;
	private ArrayList<Socket> allSockets;

	public ClientReader(Socket _socket, ArrayList<Socket> _allSockets) {
		client = _socket;
		allSockets = _allSockets;
	}
	
	public void run() {
		DataInputStream in = getDataInStream(client);
			while(true) {
				if(dataIncoming(in)) {
					String message;
					try {
						message = getMessage(in);
					} catch (IOException e) {
						break;
					}
					System.out.println(message);
					if (message.split(" ")[1].equals("QUIT") && message.split(" ").length==2) {
						allSockets.remove(client);
					}
					for (Socket s : allSockets) {
						if (s != client) {
							try {
								sendMessage(s,message);
							} catch (IOException e) {
								break;
							}
						}
					}
				}
			}
			allSockets.remove(client);
	}

	private Boolean dataIncoming(DataInputStream in) {
		try {
			if (in.available()!=0) {
				return true;
			} else {
				return false;
			}
		}catch (IOException e) {
			return false;
		}
	}
	
	private DataInputStream getDataInStream(Socket client) {
		try {
			InputStream serverStream = client.getInputStream();
			DataInputStream dataIn = new DataInputStream(serverStream);
			return dataIn;
		} catch (IOException e) {
			System.out.println("Unable to get Data Stream from server");
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}

	private String getMessage(DataInputStream in) throws IOException {
			return in.readUTF();
	}

	private void sendMessage(Socket socket, String message) throws IOException {
		new ClientSender(socket,message).run();
	}
}

class ClientSender {
	private Socket me;
	private String message;

	public ClientSender(Socket _socket, String _message) {
		me = _socket;
		message = _message;
	}

	public void run() throws IOException {
		DataOutputStream dataOut = getDataOutStream(me);
		writeToClient(dataOut,message);
	}

	private DataOutputStream getDataOutStream(Socket socket) throws IOException {
		OutputStream socketStream = socket.getOutputStream();
		DataOutputStream out = new DataOutputStream(socketStream);
		return out;
	}
	

	private void writeToClient(DataOutputStream out, String message) throws IOException {
		out.writeUTF(message);
	}
}
