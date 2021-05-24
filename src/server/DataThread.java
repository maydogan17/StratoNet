package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tcp.TCPPayload;

public class DataThread extends Thread {
	protected ObjectInputStream is;
	protected ObjectOutputStream os;
	protected Socket s;
	private TCPPayload line = null;
	private String lines = new String();
	String username;

	public DataThread(Socket s, String username) {
		this.s = s;
		this.username = username;
	}

	public int giveMeHash() {
		String token = username.concat("56");
		int result = token.hashCode();
		return result;
	}

	public void requests(ObjectOutputStream os, ObjectInputStream is, TCPPayload line) {
		if (line.getApPayload().equals("prev")) {
			if (!getAP().equals("NOT FOUND!")) {
				String dum = getAP();
				JSONParser parse = new JSONParser();
				try {
					JSONObject jobj = (JSONObject) parse.parse(dum);
					int jsonHash = jobj.hashCode();
					TCPPayload tosend = new TCPPayload(1, 0, jobj);
					tosend.setJsonHash(jsonHash);
					os.writeObject(tosend);
					os.flush();

				} catch (ParseException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			} else {
				String msg3 = "NOT FOUND!";
				try {
					os.writeObject(new TCPPayload(1, 2, msg3.length(), msg3));
					os.flush();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		} else if (line.getApPayload().equals("apod")) {
			String msg10 = "Please enter a date (1999-05-01) in order too see APOD.";
			try {
				os.writeObject(new TCPPayload(1, 1, msg10.length(), msg10));
				os.flush();
				line = (TCPPayload) is.readObject();
			} catch (IOException | ClassNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			lines = "Client messaged : '" + line.getApPayload() + "' at  Thread ID: " + Thread.currentThread().getId();
			System.out.println("Client " + s.getRemoteSocketAddress() + " sent :  " + lines);

			TCPPayload apd = new TCPPayload();
			BufferedImage image = null;
			byte[] data = null;
			try {
				image = getApod(line.getApPayload());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageIO.write(image, "jpg", bos);
				data = bos.toByteArray();

			} catch (MalformedURLException e2) {

				e2.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			apd = new TCPPayload(0, 0, data);
			apd.setImageHash(Arrays.hashCode(data));
			try {
				os.writeObject(apd);
				os.flush();
			} catch (IOException e) {

				e.printStackTrace();
			}

		} else {
			String msg12 = "Please enter a valid request (apod or prev)!";
			try {
				os.writeObject(new TCPPayload(1, 3, msg12.length(), msg12));
				os.flush();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	public void run() {
		try {
			os = new ObjectOutputStream(s.getOutputStream());
			is = new ObjectInputStream(s.getInputStream());

		} catch (IOException e) {
			System.err.println("Data Thread. Run. IO error in server thread");
		}

		try {
			line = (TCPPayload) is.readObject();
			while (line.getApPayload().compareTo("QUIT") != 0) {
				lines = "Client messaged : '" + line.getApPayload() + "' at  Thread ID: "
						+ Thread.currentThread().getId();

				System.out.println("Client " + s.getRemoteSocketAddress() + " sent :  " + lines);

				if (line.isToken()) {
					if (line.getIdToken() == giveMeHash()) {
						while (true) {
							requests(os, is, line);
							line = (TCPPayload) is.readObject();

							lines = "Client messaged : '" + line.getApPayload() + "' at  Thread ID: "
									+ Thread.currentThread().getId();

							System.out.println("Client " + s.getRemoteSocketAddress() + " sent :  " + lines);
						}
					} else {
						String ms666 = "Usarname token mismatched.";
						os.writeObject(new TCPPayload(1, 4, ms666.length(), ms666));
						os.flush();
					}

				} else {
					String ms66 = "Token not found. All actions terminated.";
					os.writeObject(new TCPPayload(1, 5, ms66.length(), ms66));
					os.flush();
				}

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

	public BufferedImage getApod(String date) throws MalformedURLException {
		try {
			String firsturl = "https://api.nasa.gov/planetary/apod?api_key=VbbiLZf6q2YzlQy3JD5AfEl0sEFzOmLEmG4TsQF2&date=";
			firsturl = firsturl.concat(date);
			URL url = new URL(firsturl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responsecode = conn.getResponseCode();
			if (responsecode != 200)
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			else {
				Scanner sc = new Scanner(url.openStream());
				String inline = "";
				while (sc.hasNext()) {
					inline += sc.nextLine();
				}
				sc.close();
				JSONParser parse = new JSONParser();
				JSONObject jobj = (JSONObject) parse.parse(inline);
				URL url2 = new URL(jobj.get("url").toString());
				BufferedImage image = ImageIO.read(url2);
				return image;
			}

		} catch (IOException | ParseException e) {

			e.printStackTrace();
		}
		return null;
	}

	public String getAP() {
		Random rand = new Random();
		try {
			URL url = new URL(
					"https://api.nasa.gov/insight_weather/?api_key=VbbiLZf6q2YzlQy3JD5AfEl0sEFzOmLEmG4TsQF2&feedtype=json&ver=1.0");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responsecode = conn.getResponseCode();
			if (responsecode != 200)
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			else {
				Scanner sc = new Scanner(url.openStream());
				String inline = "";
				while (sc.hasNext()) {
					inline += sc.nextLine();
				}
				sc.close();
				JSONParser parse = new JSONParser();
				JSONObject jobj = (JSONObject) parse.parse(inline);
				JSONArray jsonarr = (JSONArray) jobj.get("sol_keys");
				int randNum = rand.nextInt(jsonarr.size());
				String randStr = (String) jsonarr.get(randNum);
				JSONObject jobj1 = (JSONObject) jobj.get(randStr);
				String res = jobj1.get("PRE").toString();

				return res;
			}

		} catch (IOException | ParseException e) {

			e.printStackTrace();
		}
		return "NOT FOUND!";
	}
}
