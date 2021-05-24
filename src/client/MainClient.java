package client;

import java.util.Scanner;

public class MainClient {
	@SuppressWarnings("resource")
	public static void main(String args[]) {
		Client connectionToServer = new Client(Client.DEFAULT_SERVER_ADDRESS,
				Client.DEFAULT_SERVER_PORT, Client.DEFAULT_DATA_PORT);
		connectionToServer.Connect();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to the StratoNet Server.\n"
				+ "Please enter a username.\n"
				+ "If you did not enter a valid username you will be kicked off from the server.\n"
				+ "Also if you enter your password wrong 3 times in a row you will be kicked off.");
		String message = scanner.nextLine();
		while (message.compareTo("done") != 0) {
			System.out.println("Response from server: " + connectionToServer.SendForAnswer(message).toString());
			message = scanner.nextLine();
		}
		connectionToServer.Disconnect();
		
		connectionToServer.ConnectToData();
        System.out.println("Please enter apod for APOD or prev for AP!");
        String message2 = scanner.nextLine();
        while (!message2.equals("QUIT"))
        {
            System.out.println("Response from server: " + connectionToServer.SendForAnswerData(message2).toString());
            message2 = scanner.nextLine();
        }
        connectionToServer.DisconnectData();
	}
}
