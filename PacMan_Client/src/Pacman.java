import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Pacman extends Rectangle { // 사이즈나 포지션 관리를 쉽게 하기 위해 extends Rectangle

	public boolean right;
	public boolean left;
	public boolean up;
	public boolean down;
	private int speed = 3;
	private int imageIndex = 0;
	private Clip clip;  //eat
	private Clip clip2; //death

	Score curScore;

	public Pacman(int x, int y) {
		curScore = Game.score1;
		curScore.score1 = 0;
		// setBounds(x, y, 30, 30);
		setBounds(x, y, 14, 14);
		gameSound("sound/eat.wav");
		gameSound2("sound/death.wav");
	}

	private boolean canMove(int next_x, int next_y) { // Ghost와 같은 함수 벽이 있으면 움직이지 못함

		Rectangle bounds = new Rectangle(next_x, next_y, width, height);

		for (int i = 0; i < Game.map.tiles.length; i++) {
			for (int j = 0; j < Game.map.tiles[0].length; j++) {
				if (Game.map.tiles[i][j] != null) {
					if (bounds.intersects(Game.map.tiles[i][j])) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private void gameSound(String pathName) {
		try {
			clip = AudioSystem.getClip(); // 비어있는 오디오 클립 만들기
			File audioFile = new File(pathName);
			AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
			clip.open(ais);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void gameSound2(String pathName) {
		try {
			clip2 = AudioSystem.getClip();
			File audioFile = new File(pathName);
			AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
			clip2.open(ais);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tick() { // 각 방향으로 움직일 때마다 이미지를 바꿈
		if (right && canMove(x + speed, y)) {
			x = x + speed;
			imageIndex = 0;
		}
		if (left && canMove(x - speed, y)) {
			x = x - speed;
			imageIndex = 1;
		}
		if (up && canMove(x, y - speed)) {
			y = y - speed;
			imageIndex = 2;
		}
		if (down && canMove(x, y + speed)) {
			y = y + speed;
			imageIndex = 3;
		}

		for (int i = 0; i < Game.map.seeds.size(); i++) { // 팩맨과 seed가 겹치면 seed는 사라짐
			// failSound();
			if (this.intersects(Game.map.seeds.get(i))) {
				curScore.score1 += 10;
				Game.map.seeds.remove(i);
				clip.setFramePosition(0);
				clip.start();
				break;
			}
		}

		if (Game.map.seeds.size() == 0) {
			// win
			// seeds를 다 먹음
			Game.STATE = Game.START;
			return;
		}

		for (int i = 0; i < Game.map.Ghosts.size(); i++) {
			// lose
			// 일반 유령에게 잡힘
			Ghost temp = Game.map.Ghosts.get(i);
			if (temp.intersects(this)) {
				Game.STATE = Game.START;
				Game.FINISH = true;
				curScore.insertScore();
				clip2.setFramePosition(0);
				clip2.start();
				return;
			}
		}
		for (int i = 0; i < Game.map.SmartGhosts.size(); i++) {
			// lose
			// 똑똑한 유령에게 잡힘
			SmartGhost temp = Game.map.SmartGhosts.get(i);
			if (temp.intersects(this)) {
				Game.STATE = Game.START;
				Game.FINISH = true;
				curScore.insertScore();
				clip2.setFramePosition(0);
				clip2.start();
				return;
			}
		}
	}

	public void render(Graphics g) {
		g.drawImage(Character.pacman[imageIndex], x, y, width, height, null);
	}
}
