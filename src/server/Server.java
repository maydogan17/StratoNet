package server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket serverSocket;
	public static final int DEFAULT_SERVER_PORT = 4444;

	private ServerSocket dataSocket;
	public static final int DEFAULT_DATA_PORT = 5555;

	public Server(int port, int port2) {

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Oppened up a server socket on " + Inet4Address.getLocalHost());

			dataSocket = new ServerSocket(port2);
			System.out.println("Oppened up a data socket on " + Inet4Address.getLocalHost());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Server class.Constructor exception on oppening a server socket");
		}
		while (true) {
			ListenAndAccept();
		}
	}

	private void ListenAndAccept() {
		Socket welcSoc;
		try {
			welcSoc = serverSocket.accept();
			System.out.println(
					"A connection was established with a client on the address of " + welcSoc.getRemoteSocketAddress());
			ServerThread st = new ServerThread(welcSoc, this);
			st.clientList();
			st.start();

		}

		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Server Class.Connection establishment error inside listen and accept function");
		}
	}

	public void DataSocket(String username) {
		Socket datSoc;
		try {
			datSoc = dataSocket.accept();
			System.out.println(
					"A connection was established with a client on the address of " + datSoc.getRemoteSocketAddress());
			DataThread st = new DataThread(datSoc, username);
			st.start();

		}

		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Server Class.Connection establishment error inside listen and accept function");
		}
	}

}
