import java.awt.Color;
import java.io.*;

public class Board {
	private static Pieces[][] table;
	private Color color = Color.black;
	private static boolean whiteTurn;
	private static boolean blackTurn;
	private String err;
	private int blck, whte, bPieces, wPieces;
	private BufferedReader restore = null;

	public Board() {
		int i, j;
		table = new Pieces[8][8];
		bPieces = wPieces = 12;
		for (i = 0; i < 8; i++) {
			switch (i) {
			case 1:
				for (j = 0; j < 8; j += 2) {
					table[i][j] = new Pieces(color, i, j);
				}
				break;
			case 3:
				continue;
			case 4:
				continue;
			case 5:
				color = Color.white;
				for (j = 0; j < 8; j += 2) {
					table[i][j] = new Pieces(color, i, j);
				}
				break;
			case 7:
				for (j = 0; j < 8; j += 2) {
					table[i][j] = new Pieces(color, i, j);
				}
				break;
			default:
				for (j = 1; j < 8; j += 2) {
					table[i][j] = new Pieces(color, i, j);
				}
				break;
			}
		}
		whiteTurn = true;
		blackTurn = false;
	}

	public Board(String string) {

		int i, j;
		table = new Pieces[8][8];
		bPieces = wPieces = 12;
		for (i = 0; i < 8; i++) {
			switch (i) {
			case 1:
				for (j = 0; j < 8; j += 2) {
					table[i][j] = new King(color, i, j);
				}
				break;
			case 3:
				continue;
			case 4:
				continue;
			case 5:
				color = Color.white;
				for (j = 0; j < 8; j += 2) {
					table[i][j] = new King(color, i, j);
				}
				break;
			case 7:
				for (j = 0; j < 8; j += 2) {
					table[i][j] = new King(color, i, j);
				}
				break;
			default:
				for (j = 1; j < 8; j += 2) {
					table[i][j] = new King(color, i, j);
				}
				break;
			}
		}
		whiteTurn = true;
		blackTurn = false;
	}

	public void restoreBoard(String file) throws IOException {
		String[] line;
		int x, y;
		Color c = null;
		String color, king = null;
		try {
			restore = new BufferedReader(new FileReader(new File(file)));
		} catch (IOException io) {
			setErr("There is no saved game!");
		}
		if (restore != null) {
			String s = restore.readLine();
			while (s != null) {
				if (s.equals("true")) {
					whiteTurn = true;
					blackTurn = false;
				} else if (s.equals("false")) {
					blackTurn = true;
					whiteTurn = false;
				} else {

					line = s.split(",");
					y = Integer.parseInt(line[0]);
					x = Integer.parseInt(line[1]);
					color = line[2];
					if (line.length == 4) {
						king = line[3];
						if (king.equals("true")) {
							if (color.equals("black"))
								c = Color.black;
							else
								c = Color.white;
							Board.table[y][x] = new King(c, y, x);
							s = restore.readLine();
							continue;
						}
					}

					if (color.equals("white")) {
						c = Color.white;
						Board.table[y][x] = new Pieces(c, y, x);
					} else if (color.equals("black")) {
						c = Color.black;
						Board.table[y][x] = new Pieces(c, y, x);
					} else
						Board.table[y][x] = null;
				}
				s = restore.readLine();
			}
		}
		System.out.println(toStringColor());
	}

	public void toFile(String file) {
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (table[i][j] != null) {
						writer.println(table[i][j].toFile());
					} else {
						writer.println(i + "," + j + "," + "null");
					}
				}
			}
			writer.println(whiteTurn);
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println(e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public String toStringColor() {
		String s = "";
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (table[i][j] != null)
					s += "[" + table[i][j].toStringColor() + "]";
				else
					s += "[   ]";
			}
			s += "\n";
		}
		return s;
	}

	public boolean move(int fromI, int fromJ, int toJ, int toI) {
		if (table[fromI][fromJ] != null && table[toI][toJ] == null && !table[fromI][fromJ].isCrowned()) {
			if (table[fromI][fromJ].getColor().equals("white") && whiteTurn) {
				if (fromI - 1 == toI) {
					if (fromJ + 1 == toJ || fromJ - 1 == toJ) {
						doTurn(fromI, fromJ, toJ, toI, Color.white);
						SwitchTurns();
						return true;
					} else {
						setErr("illegal move");
						return false;
					}
				} else if (fromI - 2 == toI) {
					try {
						if (fromJ + 2 == toJ && table[fromI - 1][fromJ + 1].getColor().equals("black")) {
							doTurn(fromI, fromJ, toJ, toI, Color.white);
							table[fromI - 1][fromJ + 1] = null;
							SwitchTurns();
							return true;
						} else if (fromJ - 2 == toJ && table[fromI - 1][fromJ - 1].getColor().equals("black")) {
							doTurn(fromI, fromJ, toJ, toI, Color.white);
							table[fromI - 1][fromJ - 1] = null;
							SwitchTurns();
							return true;
						}
					} catch (Exception e) {
						setErr("illegal move");
						return false;
					}
				} else {
					setErr("illegal move");
					return false;
				}
			} else if (table[fromI][fromJ].getColor().equals("black") && blackTurn) {
				if (fromI + 1 == toI) {
					if (fromJ + 1 == toJ || fromJ - 1 == toJ) {
						doTurn(fromI, fromJ, toJ, toI, Color.black);
						SwitchTurns();
						return true;
					} else {
						setErr("illegal move");
						return false;
					}
				} else if (fromI + 2 == toI) {
					try {
						if (fromJ + 2 == toJ && table[fromI + 1][fromJ + 1].getColor().equals("white")) {
							doTurn(fromI, fromJ, toJ, toI, Color.black);
							table[fromI + 1][fromJ + 1] = null;
							SwitchTurns();
							return true;
						} else if (fromJ - 2 == toJ && table[fromI + 1][fromJ - 1].getColor().equals("white")) {
							doTurn(fromI, fromJ, toJ, toI, Color.black);
							table[fromI + 1][fromJ - 1] = null;
							SwitchTurns();
							return true;
						}
					} catch (Exception e) {
						setErr("illegal move");
						return false;
					}
				} else {
					setErr("illegal move");
					return false;
				}
			} else {
				setErr("you can only move just your own pieces");
				return false;
			}
		} else if (table[fromI][fromJ] != null && table[toI][toJ] == null && table[fromI][fromJ].isCrowned()) {
			if (table[fromI][fromJ].getColor().equals("white") && whiteTurn) {
				if (Math.abs(toI - fromI) == Math.abs(toJ - fromJ)) {
					System.out.println("1����");
					if (doKingTurn(fromI, fromJ, toJ, toI)) {
						System.out.println("����3");
						SwitchTurns();
						return true;
					}
				} else {
					setErr("illegal move");
					return false;
				}
			} else if (table[fromI][fromJ].getColor().equals("black") && blackTurn) {
				if (Math.abs(toI - fromI) == Math.abs(toJ - fromJ)) {
					System.out.println("����2");
					if (doKingTurn(fromI, fromJ, toJ, toI)) {
						System.out.println("����4");
						SwitchTurns();
						return true;
					}
				} else {
					setErr("illegal move");
					return false;
				}
			} else {
				setErr("you can only move just your own pieces");
				return false;
			}
		} else {
			setErr("illegal move");
			return false;
		}

		System.out.println(this.toStringColor());
		return false;

	}

	private boolean doKingTurn(int fromI, int fromJ, int toJ, int toI) {
		int whiteCounter = 0, blackCounter = 0, hfresh, hibur;
		boolean sw = false;
		try {
			if (fromI > toI) {// moving diagonal up
				if (fromJ > toJ) {
					// diagonal left
					hfresh = Math.abs(fromI - fromJ);
					for (int i = fromI - 1; i > toI; i--) {
						System.out.println("[" + i + "]" + "[" + hfresh + "]");
						if (table[i][i + hfresh] != null && table[i][i + hfresh].getColor().equals("black")) {
							blackCounter++;
						} else if (table[i][i + hfresh] != null && table[i][i + hfresh].getColor().equals("white")) {
							whiteCounter++;
						}
					}
					System.out.println(whiteCounter);
					System.out.println(blackCounter);
					if (table[fromI][fromJ].getColor().equals("white")) {
						if (blackCounter <= 1 && whiteCounter == 0) {
							sw = true;
							if (table[toI + 1][toJ + 1].getColor().equals("black"))
								table[toI + 1][toJ + 1] = null;

						}
					} else if (table[fromI][fromJ].getColor().equals("black")) {
						if (whiteCounter <= 1 && blackCounter == 0) {
							sw = true;
							if (table[toI + 1][toJ + 1].getColor().equals("white"))
								table[toI + 1][toJ + 1] = null;

						}
					}
				} else {// diagonal right
					hibur = fromI + fromJ;
					for (int i = fromI - 1; i > toI; i--) {
						if (table[i][hibur - i] != null && table[i][hibur - i].getColor().equals("black")) {
							blackCounter++;
						} else if (table[i][hibur - i] != null && table[i][hibur - i].getColor().equals("white")) {
							whiteCounter++;
						}
					}
					System.out.println(whiteCounter);
					System.out.println(blackCounter);
					if (table[fromI][fromJ].getColor().equals("white")) {
						if (blackCounter <= 1 && whiteCounter == 0) {
							sw = true;
							if (table[toI + 1][toJ - 1].getColor().equals("black"))
								table[toI + 1][toJ - 1] = null;

						}
					} else if (table[fromI][fromJ].getColor().equals("black")) {
						if (whiteCounter <= 1 && blackCounter == 0) {
							sw = true;
							if (table[toI + 1][toJ - 1].getColor().equals("white"))
								table[toI + 1][toJ - 1] = null;

						}
					}
				}
			} else {// moving diagonal down
				if (fromJ > toJ) {// diagonal left
					hibur = fromI + fromJ;
					for (int i = fromI + 1; i < toI; i++) {
						if (table[i][hibur - i] != null && table[i][hibur - i].getColor().equals("black")) {
							blackCounter++;
						} else if (table[i][hibur - i] != null && table[i][hibur - i].getColor().equals("white")) {
							whiteCounter++;
						}
					}
					System.out.println(whiteCounter);
					System.out.println(blackCounter);
					if (table[fromI][fromJ].getColor().equals("white")) {
						if (blackCounter <= 1 && whiteCounter == 0) {
							sw = true;
							if (table[toI - 1][toJ + 1].getColor().equals("black"))
								table[toI - 1][toJ + 1] = null;

						}
					} else if (table[fromI][fromJ].getColor().equals("black")) {
						if (whiteCounter <= 1 && blackCounter == 0) {
							sw = true;
							if (table[toI - 1][toJ + 1].getColor().equals("white"))
								table[toI - 1][toJ + 1] = null;

						}
					}

				} else {// diagonal right
					hfresh = Math.abs(fromI - fromJ);
					for (int i = fromI + 1; i < toI; i++) {
						if (table[i][i + hfresh] != null && table[i][i + hfresh].getColor().equals("black")) {
							blackCounter++;
						} else if (table[i][i + hfresh] != null && table[i][i + hfresh].getColor().equals("white")) {
							whiteCounter++;
						}
					}
					System.out.println(whiteCounter);
					System.out.println(blackCounter);
					if (table[fromI][fromJ].getColor().equals("white")) {
						if (blackCounter <= 1 && whiteCounter == 0) {
							sw = true;
							if (table[toI - 1][toJ - 1].getColor().equals("black"))
								table[toI - 1][toJ - 1] = null;

						}
					} else if (table[fromI][fromJ].getColor().equals("black")) {
						if (whiteCounter <= 1 && blackCounter == 0) {
							sw = true;
							if (table[toI - 1][toJ - 1].getColor().equals("white"))
								table[toI - 1][toJ - 1] = null;

						}
					}
				}
			}
		} catch (Exception e) {
		}
		if (sw) {
			table[fromI][fromJ].move(toJ, toI);
			table[toI][toJ] = table[fromI][fromJ];
			table[fromI][fromJ] = null;
		}
		System.out.println(toStringColor());
		return sw;
	}

	public void doTurn(int fromI, int fromJ, int toJ, int toI, Color color) {
		this.color = color;
		table[fromI][fromJ].move(toJ, toI);
		table[toI][toJ] = table[fromI][fromJ];
		table[fromI][fromJ] = null;
		if (table[toI][toJ].setCrowned()) {
			table[toI][toJ] = new King(color, toI, toJ);
			System.out.println(table[toI][toJ].toString());
		}
		System.out.println(toStringColor());
	}

	public boolean isEmpty(int fromI, int fromJ) {
		if (table[fromI][fromJ] == null)
			return true;
		return false;
	}

	public void SwitchTurns() {
		blackTurn = !blackTurn;
		whiteTurn = !whiteTurn;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public String getErr() {
		if (err != null)
			return err;
		return " ";
	}

	public boolean getTurn() {
		return whiteTurn;
	}

	public Pieces[][] getPieces() {
		return table;
	}

	public int getbPieces() {
		return bPieces;
	}

	public int getwPieces() {
		return wPieces;
	}

	public String checkBoard() {
		whte = blck = 0;
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table.length; j++) {
				if (table[i][j] != null)
					if (table[i][j].getColor().equals("white"))
						whte++;
					else
						blck++;
			}
		}
		bPieces = blck;
		wPieces = whte;
		if (whte == 0)
			return "black";
		else if (blck == 0)
			return "white";
		return " ";

	}

	public void move(String serverString) {
		String[] line;
		int x, y, fromI, fromJ;

		if (serverString.equals("true")) {
			whiteTurn = true;
			blackTurn = false;
		} else if (serverString.equals("false")) {
			blackTurn = true;
			whiteTurn = false;
		} else if (serverString.length() <= 15 && !serverString.equals("0") && !serverString.equals("1")) {
			try {
				line = serverString.split(",");
				y = Math.abs(7 - Integer.parseInt(line[0]));
				x = Math.abs(7 - Integer.parseInt(line[1]));
				fromI = Math.abs(7 - Integer.parseInt(line[2]));
				fromJ = Math.abs(7 - Integer.parseInt(line[3]));
				move(fromI, fromJ, x, y);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}