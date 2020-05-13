import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class OnlineGame extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private int move;
	private static JFrame mainBoard;
	private JPanel board, data, clear;
	private JLabel title, moves, pieces;
	private JButton buttons[][];
	private final int SIZE = 8;
	private Board bd;
	private int toJ, toI, fromI, fromJ;
	private JButton frst = null, sec;
	private static JButton clearChoice, saveBoard, restoreBoard, newGame, undo, exit;
	private ImageIcon whiteImg, blackImg, whiteCrown, blackCrown;
	private Dimension screenSize;
	private double width, height;
	@SuppressWarnings("unused")
	private String player, opponent, serverAddress;
	public Client client;

	public OnlineGame(String player, String serverAddress) throws IOException {
		super();
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = screenSize.getWidth();
		this.height = screenSize.getHeight();
		this.move = 0;
		this.player = player;
		this.setLayout(null);
		this.board = new JPanel();
		this.board.setLayout(new GridLayout(SIZE, SIZE));
		this.board.setBounds(10, 10, (int) width - 600, (int) height - 200);
		this.buttons = new JButton[8][8];
		this.bd = new Board();
		this.whiteImg = new ImageIcon(ImageIO.read(OnlineGame.class.getResourceAsStream("res/white.png")));
		this.blackImg = new ImageIcon(ImageIO.read(OnlineGame.class.getResourceAsStream("res/black.png")));
		this.whiteCrown = new ImageIcon(ImageIO.read(OnlineGame.class.getResourceAsStream("res/whiteCrown.png")));
		this.blackCrown = new ImageIcon(ImageIO.read(OnlineGame.class.getResourceAsStream("res/blackCrown.png")));

		JButton temp;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				temp = new JButton();
				temp.setBackground(new Color((0xE6C45D)));
				temp.addActionListener(this);
				temp.setActionCommand(i + "," + j);
				if ((i + j) % 2 != 0) {
					temp.setBackground(new Color(0x585858));
					if (i > 4)
						temp.setIcon(this.whiteImg);
					if (i < 3)
						temp.setIcon(this.blackImg);
				} else
					temp.setEnabled(false);
				this.buttons[i][j] = temp;
				this.board.add(buttons[i][j]);
			}
		}

		this.add(board);
		if (this.bd.getTurn())
			this.title = new JLabel(this.player + "'s turn");
		else
			this.title = new JLabel(opponent + "'s turn");
		this.moves = new JLabel("Moves: " + Integer.toString(this.move));
		this.pieces = new JLabel("Black:" + this.bd.getbPieces() + "\n White:" + this.bd.getwPieces());
		this.data = new JPanel();
		this.data.setLayout(new GridLayout(5, 1));
		this.data.setBounds((int) this.width - 550, 10, 350, 100);
		this.data.setBackground(Color.white);
		this.data.setBorder(new LineBorder(Color.black, 10));
		this.data.add(this.title, BorderLayout.NORTH);
		this.data.add(this.moves, BorderLayout.PAGE_END);
		this.data.add(this.pieces, BorderLayout.PAGE_END);

		clearChoice = new JButton("Clear Choice");
		clearChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (frst != null) {
					frst.setBackground(new Color(0x585858));
					frst.setEnabled(true);
					frst = null;
				}
			}
		});

		saveBoard = new JButton("Save Game");
		saveBoard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bd.toFile("save.txt");
			}
		});

		restoreBoard = new JButton("Restore Game");
		restoreBoard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					bd.restoreBoard("save.txt");
					repaintBoard();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		undo = new JButton("Undo");
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					bd.restoreBoard("undo.txt");
					repaintBoard();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});

		newGame = new JButton("New Game");
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bd = new Board();

				repaintBoard();
			}
		});
		exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int dialogResult = JOptionPane.showConfirmDialog(mainBoard, "Are you sure? \nCurrent game will lost!",
						"EXIT", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (dialogResult == JOptionPane.YES_OPTION) {
					close();
					mainBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					mainBoard.dispose();
				}
			}
		});

		this.clear = new JPanel();
		this.clear.setLayout(new GridLayout(3, 3));
		this.clear.setBounds((int) this.width - 550, 130, 350, 100);
		this.clear.add(clearChoice);
		/**
		 * TODO: this.clear.add(saveBoard); this.clear.add(restoreBoard);
		 * this.clear.add(newGame); this.clear.add(undo);
		 */
		this.clear.add(exit);

		this.add(data);
		this.add(clear);

		client = new Client(player, serverAddress, this);
		client.start();
	}

	public void close() {
		client.close();
		mainBoard.dispose();
		System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd, winner;
		if (this.frst == null) {
			cmd = event.getActionCommand();
			System.out.println(cmd);
			this.fromI = cmd.charAt(0) - 48;
			this.fromJ = cmd.charAt(2) - 48;
			if (!bd.isEmpty(this.fromI, this.fromJ)) {
				this.frst = (JButton) event.getSource();
				if ((this.frst.getIcon().equals(whiteImg) && bd.getTurn())) {
					this.frst.setBackground(Color.green);
					this.frst.setEnabled(false);
				} else if ((this.frst.getIcon().equals(whiteCrown) && bd.getTurn())) {
					this.frst.setBackground(Color.green);
					this.frst.setEnabled(false);
				} else
					frst = null;
			}
		} else {
			bd.toFile("Undo.txt");
			this.sec = (JButton) event.getSource();
			cmd = event.getActionCommand();
			this.toJ = cmd.charAt(2) - 48;
			this.toI = cmd.charAt(0) - 48;
			this.sec.setBackground(Color.green);
			if (this.bd.move(this.fromI, this.fromJ, this.toJ, this.toI)) {
				client.sendMessageToServer(toI + "," + toJ + "," + fromI + "," + fromJ);
				client.setTurn(!client.getTurn());
				if (this.bd.getTurn())
					this.title.setText(this.player + "'s turn");
				else
					this.title.setText(this.opponent + "'s turn");
				this.sec.setIcon(this.frst.getIcon());
				this.frst.setIcon(null);
				this.move++;
				this.moves.setText("Moves: " + Integer.toString(move));
				if (this.fromI + 2 == this.toI) {
					if (this.fromJ - 2 == this.toJ) {
						for (int i = 0; i < SIZE; i++) {
							for (int j = 0; j < SIZE; j++) {
								if (this.buttons[i][j].getActionCommand()
										.equals((this.fromI + 1) + "," + (this.fromJ - 1))) {
									this.buttons[i][j].setIcon(null);
								}
							}
						}
						System.out.println("down left");
					} else if (this.fromJ + 2 == this.toJ) {
						for (int i = 0; i < SIZE; i++) {
							for (int j = 0; j < SIZE; j++) {
								if (this.buttons[i][j].getActionCommand()
										.equals((this.fromI + 1) + "," + (this.fromJ + 1))) {
									this.buttons[i][j].setIcon(null);
								}
							}
						}
						System.out.println("down right");
					}
				} else if (this.fromI - 2 == this.toI) {
					if (this.fromJ - 2 == this.toJ) {
						for (int i = 0; i < SIZE; i++) {
							for (int j = 0; j < SIZE; j++) {
								if (this.buttons[i][j].getActionCommand()
										.equals((this.fromI - 1) + "," + (this.fromJ - 1))) {
									this.buttons[i][j].setIcon(null);
								}
							}
						}
						System.out.println("up left");
					} else if (this.fromJ + 2 == this.toJ) {
						for (int i = 0; i < SIZE; i++) {
							for (int j = 0; j < SIZE; j++) {
								if (this.buttons[i][j].getActionCommand()
										.equals((this.fromI - 1) + "," + (this.fromJ + 1))) {
									this.buttons[i][j].setIcon(null);
								}
							}
						}
						System.out.println("up right");
					}
				}
				// else if(bd.getPieces()[fromI][fromJ].isCrowned()){
				else {
					if (this.fromI > this.toI) {
						if (this.fromJ > this.toJ) {
							for (int i = 0; i < SIZE; i++) {
								for (int j = 0; j < SIZE; j++) {
									if (this.buttons[i][j].getActionCommand()
											.equals((this.toI + 1) + "," + (this.toJ + 1))) {
										this.buttons[i][j].setIcon(null);
									}
								}
							}
						} else {
							for (int i = 0; i < SIZE; i++) {
								for (int j = 0; j < SIZE; j++) {
									if (this.buttons[i][j].getActionCommand()
											.equals((this.toI + 1) + "," + (this.toJ - 1))) {
										this.buttons[i][j].setIcon(null);
									}
								}
							}
						}
					} else {
						if (this.fromJ > this.toJ) {
							for (int i = 0; i < SIZE; i++) {
								for (int j = 0; j < SIZE; j++) {
									if (this.buttons[i][j].getActionCommand()
											.equals((this.toI - 1) + "," + (this.toJ + 1))) {
										this.buttons[i][j].setIcon(null);
									}
								}
							}
						} else {
							for (int i = 0; i < SIZE; i++) {
								for (int j = 0; j < SIZE; j++) {
									if (this.buttons[i][j].getActionCommand()
											.equals((this.toI - 1) + "," + (this.toJ - 1))) {
										this.buttons[i][j].setIcon(null);
									}
								}
							}
						}

					}
				}
				setKingIcon(toI, toJ);
			} else {
				this.title.setText("Error: " + this.bd.getErr().toUpperCase());
			}
			this.frst.setEnabled(true);
			this.frst.setBackground(new Color(0x585858));
			this.sec.setBackground(new Color(0x585858));
			this.frst = null;
			this.sec = null;
			winner = this.bd.checkBoard();
			this.pieces.setText("Black:" + this.bd.getbPieces() + "\n White:" + bd.getwPieces());
			if (!winner.equals(" ")) {
				// close
				mainBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainBoard.dispose();

			}
		}
	}

	// TODO:
	/*
	 * public boolean ifNoPossibleMoves(boolean whiteTurn){ if(whiteTurn){ for (int
	 * i = 0; i < SIZE; i++) { for (int j = 0; j < SIZE; j++) { return true; } } }
	 * return true;
	 * 
	 * }
	 */
	public void repaintBoard() {
		this.pieces.setText("Black:" + this.bd.getbPieces() + "\n White:" + bd.getwPieces());
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (bd.getPieces()[i][j] != null)
					if (bd.getPieces()[i][j].isCrowned())
						setKingIcon(i, j);
					else
						setIcon(i, j);
				else
					buttons[i][j].setIcon(null);

			}
		}
		repaint();
	}

	public void setIcon(int toI, int toJ) {
		if (bd.getPieces()[toI][toJ].getColor().equals("white"))
			this.buttons[toI][toJ].setIcon(this.whiteImg);
		else if (bd.getPieces()[toI][toJ].getColor().equals("black"))
			this.buttons[toI][toJ].setIcon(this.blackImg);
	}

	public void setKingIcon(int toI, int toJ) {
		System.out.println(bd.getPieces()[toI][toJ].toString());
		if (this.buttons[toI][toJ].getIcon() != null && toI == 0 && this.buttons[toI][toJ].getIcon().equals(whiteImg))
			this.buttons[toI][toJ].setIcon(this.whiteCrown);
		else if (this.buttons[toI][toJ].getIcon() != null && toI == 7
				&& /* this.buttons[toI][toJ].getIcon().equals(black) */ this.bd.getTurn())
			this.buttons[toI][toJ].setIcon(this.blackCrown);
		else if (bd.getPieces()[toI][toJ].isCrowned()) {
			if (this.bd.getPieces()[toI][toJ].getColor().equals("white"))
				this.buttons[toI][toJ].setIcon(this.whiteCrown);
			else if (this.bd.getPieces()[toI][toJ].getColor().equals("black"))
				this.buttons[toI][toJ].setIcon(this.blackCrown);
		}
	}

	public void setMainBoard(JFrame mainBoard) {
		OnlineGame.mainBoard = mainBoard;
	}

	public void setOpponentName(String opponent) {
		this.opponent = opponent;
	}

	public String getOpponentName() {
		return this.opponent;
	}

	public void move(String serverString) {
		bd.move(serverString);
		repaintBoard();
	}

	public Board getBoard() {
		return this.bd;
	}
}