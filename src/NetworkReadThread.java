import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class NetworkReadThread implements Runnable {
	private Socket socket;
	private OnlineGame onlineGame;

	public NetworkReadThread(Socket socket, OnlineGame onlineGame) {
		this.socket = socket;
		this.onlineGame = onlineGame;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String serverString;
			while ((serverString = in.readLine()) != null) {
				System.out.println("got: " + serverString);
				if (serverString.equals("0")) {
					onlineGame.client.setTurn(true);
				} else if (serverString.equals("1")) {
					onlineGame.client.setTurn(false);
					onlineGame.getBoard().SwitchTurns();
				} else if (serverString.startsWith("What is your name?")) {
					onlineGame.client.sendMessageToServer(onlineGame.client.getPlayerName());
					System.out.println("Start");
					Client.started = true;
				} else if (serverString.startsWith("Your opponent name is")) {
					onlineGame.setOpponentName(serverString.split("Your opponent name is ")[1]);
				} else if (serverString.startsWith("Hi")) {
					System.out.println(serverString);
				} else {
					System.out.println(serverString);
					onlineGame.move(serverString);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Disconnected from server");
		System.exit(0);
	}
}
