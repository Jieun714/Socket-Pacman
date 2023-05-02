import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Score {

	String path;
	File scoreboard;
	private ArrayList<Integer> scores;
	public int score1;
	public int score2;
	private int temp;
	public int total;
	public int rank;
	public int rank2;

	Score(String path){
		this.path=path;
		scores = new ArrayList<>();
		score1 = 0;
		score2 = 0;
		scoreboard = new File(path);
		try { // 이전 점수 모두 scores라는 ArrayList에 저장
			Scanner scan = new Scanner(scoreboard);
			while(scan.hasNextInt()) {
				scores.add(scan.nextInt());
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void insertScore() {
		scores.add(this.score1); // pacman이 새로 받은 점수를 추가하여 내림차순 정렬
		scores.add(this.score2);
		Collections.sort(scores);
		total = scores.size();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			PrintWriter pw = new PrintWriter(bw);
			for(int i=total-1;i>=0;i--) {
				temp=scores.get(i);
				pw.printf("%d\n", temp);

				if(score1==temp)
					rank=total-i;
				if(score2==temp)
					rank2=total-i;
			}
			pw.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void render(Graphics g) {
		if(Game.STATE==Game.GAME) {
			g.setColor(Color.BLACK);
			g.fillRect(30, 860, 150, 70);
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 60));
			g.drawString(String.valueOf(score1), 30, 930);
		}
	}

	public void render2(Graphics g) {
		if(Game.STATE==Game.GAME) {
			g.setColor(Color.BLACK);
			g.fillRect(30, 860, 150, 70);
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 60));
			g.drawString(String.valueOf(score2), 30, 930);
		}
	}

}
