package server;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import tcp.TCPPayload;

public class ServerThread extends Thread {
	protected ObjectInputStream is;
	protected ObjectOutputStream os;

	protected Socket s;
	protected Server server;

	private TCPPayload line = null;
	private String lines = new String();

	private HashMap<String, String> clients = new HashMap<String, String>();

	String username;

	public ServerThread(Socket s, Server server) {
		this.s = s;
		this.server = server;

	}

	public int giveMeHash() {
		String token = username.concat("56");
		int result = token.hashCode();
		return result;
	}

	public void clientList() {
		BufferedReader reader;
		int lineNum = 0;
		String dummy = "me";
		try {
			reader = new BufferedReader(new FileReader("clients.txt"));
			String line = reader.readLine();
			while (line != null) {
				if (lineNum % 2 == 0) {
					clients.put(line, "0");
					dummy = line;
				}
				if (lineNum % 2 == 1) {
					clients.put(dummy, line);
				}
				lineNum++;
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(clients.toString());
	}

	public void authentication(ObjectOutputStream os, ObjectInputStream is, TCPPayload line) {

		if (clients.containsKey(line.getApPayload())) {
			String id = line.getApPayload();
			username = id;
			String msg = "Please enter pass:";
			try {
				TCPPayload toSend = new TCPPayload(0, 1, msg.length(), msg);
				toSend.setIdToken(giveMeHash());
				os.writeObject(toSend);
				os.flush();
				line = (TCPPayload) is.readObject();
			} catch (IOException e) {

				e.printStackTrace();
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			}

			lines = "Client messaged : '" + line.getApPayload() + "' at  Thread ID: " + Thread.currentThread().getId();
			System.out.println("Client " + s.getRemoteSocketAddress() + " sent :  " + lines);

			authentication_Pass(os, is, line, id);

		} else {
			String msg5 = "Invalid username. All actions terminated.";
			try {
				os.writeObject(new TCPPayload(0, 2, msg5.length(), msg5));
				os.flush();
			} catch (IOException e) {

				e.printStackTrace();
			}
			Disconnect();

		}

	}

	public void authentication_Pass(ObjectOutputStream os, ObjectInputStream is, TCPPayload line, String id) {
		int count = 0;

		while (true) {

			if (line.getApPayload().equals(clients.get(id))) {
				String msg2 = "Pass correct. Please enter done to end auth process.";
				try {
					os.writeObject(new TCPPayload(0, 3, msg2.length(), msg2));
					os.flush();
				} catch (IOException e) {

					e.printStackTrace();
				}
				break;
			} else {
				count++;
				if (count != 3) {
					String msg3 = "Pass incorrect. Please try again! Reamining chances: " + (3 - count);
					try {
						os.writeObject(new TCPPayload(0, 1, msg3.length(), msg3));
						os.flush();
						line = (TCPPayload) is.readObject();
					} catch (IOException e) {

						e.printStackTrace();
					} catch (ClassNotFoundException e) {

						e.printStackTrace();
					}
					lines = "Client messaged : '" + line.getApPayload() + "' at  Thread ID: "
							+ Thread.currentThread().getId();
					System.out.println("Client " + s.getRemoteSocketAddress() + " sent :  " + lines);
				} else {
					String ms4 = "Pass incorrect. All actions terminated!";
					try {
						os.writeObject(new TCPPayload(0, 2, ms4.length(), ms4));
						os.flush();
					} catch (IOException e) {

						e.printStackTrace();
					}
					Disconnect();
					break;
				}

			}

		}
	}

	public void run() {
		try {
			os = new ObjectOutputStream(s.getOutputStream());
			is = new ObjectInputStream(s.getInputStream());

		} catch (IOException e) {
			System.err.println("Server Thread. Run. IO error in server thread");
		}

		try {
			line = (TCPPayload) is.readObject();
			while (line.getApPayload().compareTo("QUIT") != 0) {

				lines = "Client messaged : '" + line.getApPayload() + "' at  Thread ID: "
						+ Thread.currentThread().getId();

				System.out.println("Client " + s.getRemoteSocketAddress() + " sent :  " + lines);

				authentication(os, is, line);

				line = (TCPPayload) is.readObject();

			}
		} catch (SocketException e) {

		} catch (EOFException e) {

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Server Thread. Run. IO Error/ Client " + line + " terminated abruptly");
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.err.println("Server Thread. Run.Client " + line + " Closed");
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} finally {

			Disconnect();
			server.DataSocket(username);
		}
	}

	public void Disconnect() {
		try {
			System.out.println("Closing the connection");
			if (is != null) {
				is.close();
				System.err.println(" Socket Input Stream Closed");
			}

			if (os != null) {
				os.close();
				System.err.println("Socket Out Closed");
			}
			if (s != null) {
				s.close();
				System.err.println("Socket Closed");
			}
		} catch (IOException ie) {
			System.err.println("Socket Close Error");
		}
	}

}
