package client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

import tcp.TCPPayload;

public class Client {
	public static final String DEFAULT_SERVER_ADDRESS = "localhost";
	public static final int DEFAULT_SERVER_PORT = 4444;
	public static final int DEFAULT_DATA_PORT = 5555;
	private Socket s;
	private Socket dataS;
	protected ObjectInputStream is;
	protected ObjectOutputStream os;

	protected ObjectInputStream dis;
	protected ObjectOutputStream dos;

	protected String serverAddress;
	protected int serverPort;
	protected int dataPort;

	int myHash = 0;


	public Client(String address, int port, int data) {
		serverAddress = address;
		serverPort = port;
		dataPort = data;
	}

	/**
	 * Establishes a socket connection to the server that is identified by the
	 * serverAddress and the serverPort
	 */
	public void Connect() {
		try {
			s = new Socket(serverAddress, serverPort);

			is = new ObjectInputStream(s.getInputStream());
			os = new ObjectOutputStream(s.getOutputStream());

			System.out.println("Successfully connected to " + serverAddress + " on port " + serverPort);
		} catch (IOException e) {

			System.err.println("Error: no server has been found on " + serverAddress + "/" + serverPort);
		}
	}

	public void ConnectToData() {
		try {
			dataS = new Socket(serverAddress, dataPort);

			dis = new ObjectInputStream(dataS.getInputStream());
			dos = new ObjectOutputStream(dataS.getOutputStream());

			System.out.println("Successfully connected to " + serverAddress + " on port " + dataPort);
		} catch (

		IOException e) {

			System.err.println("Error: no server has been found on " + serverAddress + "/" + dataPort);
		}
	}


	public TCPPayload SendForAnswer(String message) {
		TCPPayload response = null;
		try {
			/*
			 * Sends the message to the server 
			 */
			TCPPayload toSend = new TCPPayload(0, 0, message.length(), message);
			os.writeObject(toSend);
			os.flush();
			/*
			 * Reads a line from the server
			 */
			response = (TCPPayload) is.readObject();
			if (response.getIdToken() != 0) {
				myHash = response.getIdToken();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ConnectionToServer. SendForAnswer. Socket read Error");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	public TCPPayload SendForAnswerData(String message) {
		TCPPayload response = null;
		try {
			/*
			 * Sends the message to the server
			 */
			TCPPayload toSend = new TCPPayload(0, 0, message.length(), message);
			toSend.setToken(true);
			toSend.setIdToken(myHash);
			dos.writeObject(toSend);
			dos.flush();
			/*
			 * Reads a line from the server
			 */
			response = (TCPPayload) dis.readObject();

			if (response.getJsonHash() != 0) {
				JSONObject myjson = response.getJobj();
				int jsonhash = myjson.hashCode();
				if(jsonhash != response.getJsonHash()) {
					System.out.println("JSON disrupted along the way.");
				}
			}
			if(response.getImageHash() != 0) {
				byte[] myByteArr = response.getByteImg();
				int byteHash = Arrays.hashCode(myByteArr);
				
				if(byteHash != response.getImageHash()) {
					System.out.println("Image disrupted along the way!");
				}
				else {
			        InputStream is = new ByteArrayInputStream(myByteArr);
			        BufferedImage newBi = ImageIO.read(is);
			        ImageIcon result = new ImageIcon(newBi);
			        TCPPayload newResponse = new TCPPayload(0, 0, result);
			        response = newResponse;
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ConnectionToServer. SendForAnswer. Socket read Error");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	public void Disconnect() {
		try {
			is.close();
			os.close();
			// br.close();
			s.close();
			System.out.println("ConnectionToServer. SendForAnswer. Connection Closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void DisconnectData() {
		try {
			dis.close();
			dos.close();
			// br.close();
			dataS.close();
			System.out.println("ConnectionToServer. SendForAnswerData. Connection Closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
