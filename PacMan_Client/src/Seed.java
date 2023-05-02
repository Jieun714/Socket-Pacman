import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Seed extends Rectangle{

	public Seed(int x, int y) {
		//setBounds(x+12, y+12, 8, 8); //setBounds(가로위치, 세로위치, 가로길이, 세로길이)
		setBounds(x+6, y+8, 4, 4);
	}

	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(x, y, width, height);
	}

}
