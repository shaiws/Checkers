import java.awt.Color;

public class Pieces {
	protected  Color color;
	protected  int yPosition, xPosition;
	private  boolean crowned = false;

	public Pieces() {}
	public Pieces(Color color, int y, int x) {
		this.color = color;
		this.yPosition = y;
		this.xPosition = x;
	}

	public String getColor() {
		if (this.color.toString().equals(Color.black.toString()))
			return "black";
		return "white";
	}

	public String toStringColor() {
		if (this.color.toString().equals(Color.black.toString()))
			return " @ ";
		return "( )";
	}

	public String toFile(){
		return  this.yPosition+","+this.xPosition+","+this.getColor()+","+this.crowned;	}

	public String toString() {
		System.out.println(crowned);
		return "[" + this.yPosition + "," + this.xPosition + "]";
	}
	

	public void move(int x, int y) {
			this.yPosition = y;
			this.xPosition = x;
	}

	public boolean isCrowned() {
		return crowned;
	}

	public boolean setCrowned() {
		if (this.color.equals(Color.white)) {
			if (this.yPosition == 0)
				crowned = true;
		}
		if (this.color.equals(Color.black)) {
			if (this.yPosition == 7)
				crowned = true;
		}
		return crowned;
	}
}