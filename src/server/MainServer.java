package server;

public class MainServer {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Server server = new Server(Server.DEFAULT_SERVER_PORT, Server.DEFAULT_DATA_PORT);
	}
}
