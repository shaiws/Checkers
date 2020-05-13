import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) throws IOException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		Object[] options = { "Local", "Online" };
		String optionChoose = null;
		while (optionChoose == null) {
			optionChoose = (String) JOptionPane.showInputDialog(null, "What kind of game would you like to play",
					"Choose Game", JOptionPane.PLAIN_MESSAGE, null, options, "Online");
		}
		if (optionChoose.equals("Local")) {
			LocalGame.setArgs(args);
			String player1Name = null, player2Name = null;
			JFrame localGame = new JFrame("Local Game");
			localGame.setBounds(10, 10, (int) width - 150, (int) height - 100);
			while (player1Name == null)
				player1Name = (String) JOptionPane.showInputDialog(null, "Please input your name", "Player 1 name",
						JOptionPane.PLAIN_MESSAGE, null, null, "Player 1");
			while (player2Name == null)
				player2Name = (String) JOptionPane.showInputDialog(null, "Please input your opponent name",
						"Player 2 name", JOptionPane.PLAIN_MESSAGE, null, null, "Player 2");
			LocalGame gui = new LocalGame(player1Name, player2Name);
			localGame.add(gui);
			localGame.setVisible(true);
			gui.setMainBoard(localGame);
		} else {
			System.out.println(optionChoose);
			String playerName = null;
			String serverAddress = null;

			while (playerName == null) {
				playerName = (String) JOptionPane.showInputDialog(null, "Please input your name", "Your name",
						JOptionPane.PLAIN_MESSAGE, null, null, "Player" + ((int) new Random().nextInt(100)));
			}
			while (serverAddress == null) {
				serverAddress = (String) JOptionPane.showInputDialog(null, "Please input host address", "Host address",
						JOptionPane.PLAIN_MESSAGE, null, null, "127.0.0.1");
			}

			JFrame onlineGame = new JFrame("Online@"+serverAddress);
			onlineGame.setBounds(10, 10, (int) width - 150, (int) height - 100);
			final OnlineGame gui = new OnlineGame(playerName, serverAddress);
			onlineGame.add(gui);
			onlineGame.setVisible(true);
			onlineGame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			onlineGame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					gui.close();
				}
			});
			gui.setMainBoard(onlineGame);
		}
	}
}
