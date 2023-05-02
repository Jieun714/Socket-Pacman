import java.awt.image.BufferedImage;

public class Character { // Appearance에서 받아온 유령이미지와 팩맨 이미지를 저장
	public static BufferedImage[] pacman;
	public static BufferedImage[] pacman2;
	public static BufferedImage[] ghost;
	public static BufferedImage[] smartGhost;
	public Character(){
		pacman = new BufferedImage[4];
		pacman2 = new BufferedImage[4];
		ghost = new BufferedImage[4];
		smartGhost = new BufferedImage[8];

		// 인덱스값에 따라 이미지가 달라짐
		pacman[0]=Game.appearance.getAppearance(0, 0);
		pacman[1]=Game.appearance.getAppearance(16,0);
		pacman[2]=Game.appearance.getAppearance(32, 0);
		pacman[3]=Game.appearance.getAppearance(48, 0);

		pacman2[0]=Game.appearance.getAppearance(0,16);
		pacman2[1]=Game.appearance.getAppearance(16,16);
		pacman2[2]=Game.appearance.getAppearance(32,16);
		pacman2[3]=Game.appearance.getAppearance(48,16);

		ghost[0]=Game.appearance.getAppearance(0,32);
		ghost[1]=Game.appearance.getAppearance(16,32);
		ghost[2]=Game.appearance.getAppearance(32,32);
		ghost[3]=Game.appearance.getAppearance(48,32);

		smartGhost[0]=Game.appearance.getAppearance(0,48);
		smartGhost[1]=Game.appearance.getAppearance(16,48);
		smartGhost[2]=Game.appearance.getAppearance(32,48);
		smartGhost[3]=Game.appearance.getAppearance(48,48);
		smartGhost[4]=Game.appearance.getAppearance(0, 48);
		smartGhost[5]=Game.appearance.getAppearance(16, 64);
		smartGhost[6]=Game.appearance.getAppearance(32, 64);
		smartGhost[7]=Game.appearance.getAppearance(48, 64);

	}
}