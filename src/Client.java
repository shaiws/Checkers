import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client implements Runnable {
	public static boolean started = false;
	Socket socket;
	BufferedReader in;
	BufferedWriter out;
	String playerName;
	OnlineGame onlineGame;
	private boolean turn;

	public Client(String player, String host, OnlineGame onlineGame) {
		this.onlineGame = onlineGame;
		this.playerName = player;
		try {
			socket = new Socket(host, 1337);
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to server.");
			System.exit(1);
		}

	}

	public void start() {
		Thread logRead = new Thread(this, "client");
		logRead.start();
	}

	public void run() {
		try {
			System.out.println(onlineGame.client.getTurn());
			NetworkReadThread readThread = new NetworkReadThread(socket, onlineGame);
			Thread read = new Thread(readThread, "read");
			read.start();
			this.in = new BufferedReader(new InputStreamReader(System.in));
			String line = in.readLine();
			while (true) {
				while (started) {
					if (line != null) {
						sendMessageToServer(line);
						line = in.readLine();
					}
				}
				started = false;
			}
		} catch (IOException e) {
			System.err.println("Couldn't read or write");
			System.exit(1);
		}
	}

	public void sendMessageToServer(String msg) {
		try {
			out.write(msg + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public boolean getTurn() {
		return turn;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
