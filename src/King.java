import java.awt.Color;

public class King extends Pieces {
	private boolean crowned = true;

	public King(Color color, int y, int x) {
		super(color, y, x);

	}

	@Override
	public boolean isCrowned() {
		System.out.println("KING isCrowned");
		return crowned;
	}

	public void move(int xPosition, int yPosition) {
		System.out.println("king move");
		this.yPosition = yPosition;
		this.xPosition = xPosition;
	}

	public String toString() {
		System.out.println(crowned);
		return "KING " + this.color.toString();
	}

	public String toStringColor() {
		if (this.color.toString().equals(Color.black.toString()))
			return "@ KING";
		return "( ) KING";
	}

	public String toFile() {
		return this.yPosition + "," + this.xPosition + "," + this.getColor() + "," + this.crowned;
	}

	public boolean setCrowned() {
		return false;
	}

}
