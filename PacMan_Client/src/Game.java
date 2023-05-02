import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Game extends Canvas implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;

	// private JPanel contentPane;
	private JTextField txtInput;
	public String UserName;
	public JButton btnSend;
	public JButton btnExit;
	private ImageIcon icon;
	public JScrollPane scrollPane;
	public JPanel panel;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓

	private JLabel lblUserName;
	// public JTextArea textArea;
	JTextPane textArea;

	public ObjectInputStream ois;
	public ObjectOutputStream oos;

	private boolean isRunning = false;

	private String ip_addr = "127.0.0.1";
	private int port = 30000;

	public static final int WIDTH = 700; // = 1340;
	public static final int HEIGHT = 460; // = 960;
	public static final String TITLE = "Pacman";

	private Thread thread;

	Game game;
	public static Pacman pacman;
	public static Pacman2 pacman2;
	public static Map map;
	public static Appearance appearance;
	public static Score score1; // pacman 1 score
	public static Score score2; // pacman 2 score

	public String filename;

	// 모드
	public static final int START = 0;
	public static final int GAME = 1;
	public static boolean FINISH = false;
	public static boolean WIN = false;

	public static int STATE = -1;

	public boolean isSpace = false;

	int menu_width = 600; // 게임 시작 초기 화면 width
	int menu_height = 400; // 게임 시작 초기 화면 height
	int xx = Game.WIDTH / 2 - menu_width / 2;
	int yy = Game.HEIGHT / 2 - menu_height / 2;

	private int time = 0;
	private int targetFrames = 35;
	private boolean showText = true;


	/**
	 * @return
	 ******************************************************************************************************/

	public Game(ObjectInputStream ois, ObjectOutputStream oos) {
		Dimension dimension = new Dimension(Game.WIDTH, Game.HEIGHT);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);

		this.ois = ois; // objectInputStream을 Game(username)에서 가져옴
		this.oos = oos;
		addKeyListener(this);

		// 게임 시작을 보냄
		//STATE = START;
		sendStateStart(STATE);


		WIN = false;
		FINISH = false;

		score1 = new Score("res\\scoreboard\\scoreboard.txt");
		score2 = new Score("res\\scoreboard\\scoreboard.txt");
		pacman = new Pacman(Game.WIDTH / 2, Game.HEIGHT / 2); // 모니터 정 중앙에 배치
		pacman2 = new Pacman2(Game.WIDTH / 2 + 10, Game.HEIGHT / 2 + 10); // Player1 팩맨 옆에 배치
		map = new Map("/map/map.png");
		appearance = new Appearance("/appearance/appearance.png");

		new Character();
	}

	/********************************************************************************************************/

	public synchronized void start() {
		if (isRunning)
			return;
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	/********************************************************************************************************/

	public synchronized void stop() {
		if (!isRunning)
			return;
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/********************************************************************************************************/


	private void render() { // tick에서 변화시킨 클래스를 실제로 보여줌
		BufferStrategy bs = getBufferStrategy(); // 화면이 찢어지거나 깜빡거리는 현상을 방지하기 위함

		if (bs == null) {
			createBufferStrategy(3); // 버퍼를 3개 활용 -> 그림을 그리는 것 보다 화면에 표출하는 것이 빠른 현상을 방지함
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

		if (STATE == GAME) { // 게임 화면

			//sendStateGame(STATE);
			pacman.render(g);
			pacman2.render(g);
			map.render(g);
			score1.render(g); // pacman1 score
			score2.render2(g); // pacman2 score

			//sendSocre();

			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
			g.drawString("PacMan1  SCORE : " + score1.score1, 32, 24);
			g.drawString("PacMan2  SCORE : " + score2.score2, 450, 24);

		}

		else if (STATE == START) { // 다시 시작해야 하거나 시작하는 상황
			g.setColor(new Color(0, 0, 150));
			g.fillRect(xx, yy, menu_width, menu_height);

			g.setColor(Color.white);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 23));

			if (showText) { // 시작화면
				if (FINISH) { // 게임이 끝났을 때
					if (score1.score1 > score2.score2) { // Player1 이 이겼을 때
						g.drawString("!!! Yellow PacMan WIN !!!", xx + 150, yy + 170);
						g.drawString("\nPress space to restart the game!!", xx + 122, yy + 250);
					} else if (score1.score1 < score2.score2) { // Player2 이 이겼을 때
						g.drawString("!!! Red PacMan WIN !!!", xx + 150, yy + 170);
						g.drawString("\nPress space to restart the game!!", xx + 122, yy + 250);
					}
					if (score1.score1 == score2.score2) { // 비겼을 때
						g.drawString("!!! Score SAME !!!", xx + 200, yy + 170);
						g.drawString("\nPress space to restart the game!!", xx + 122, yy + 250);
					}
				} else if (WIN) { // 모든 쿠키를 먹었을 떄
					g.drawString("!!! Eat All Cookies !!!", xx + 215, yy + 130);
					g.drawString("Press space to start the game!!", xx + 125, yy + 180);
				} else // 기본 화면
					g.drawString("Press space to start the game!!", xx + 125, yy + 200);
			}
		}

		g.dispose(); // 화면에 있던 그래픽을 새걸로 바꾸기 위해 예전 그래픽 지움
		bs.show();
	}

	/********************************************************************************************************/

	private void tick() { // 거의 모든 클래스에 있는 tick 함수는 각 클래스의 상태를 변화시킴
		if (STATE == GAME) {
			pacman.tick();
			pacman2.tick();
			map.tick();
			//sendStateGame(STATE);
		} else if (STATE == START) {
			//sendStateStart(STATE);
			time++;
			if (time == targetFrames) {
				time = 0;
				if (showText) {
					ChatMsg cm = new ChatMsg(UserName, "703","showTextF");
					//showText = false;
					//cm.showText = false;
					showText = false;
					cm.showText = showText;
					SendObject(cm);
				} else {
					ChatMsg cm = new ChatMsg(UserName, "704","showTextT");
					//showText = true;
					//cm.showText = true;
					showText = true;
					cm.showText = showText;
					SendObject(cm);
				}
				sendStateStart(STATE);
			} // 시작하거나 죽었을 때 문구를 깜빡거리도록 함

			if (isSpace) { // 엔터를 눌렀으면 게임 새로 시작함
				sendIsSpaceF();//false로 변경
				pacman = new Pacman(Game.WIDTH / 2, Game.HEIGHT / 2);
				pacman2 = new Pacman2(Game.WIDTH / 2 + 10, Game.HEIGHT / 2 + 10);
				map = new Map("/map/map.png");
				appearance = new Appearance("/appearance/appearance.png");

				sendStateGame(STATE);
				//STATE = GAME;
			}
		}
	}

	/********************************************************************************************************/

	public Game(String username) {
		icon = new ImageIcon(Game.class.getResource("/main/gameMain.png"));

		JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("Pacman Game");// 이름 지정
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null); // 추가
		frame.setVisible(true); // 추가

		frame.setBounds(10, 100, 890, 495); // setBounds(100, 100, 839, 630); ->(100, 100, 1100, 520);
		JPanel contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(icon.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		contentPane.setVisible(true);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(18, 50, 182, 290); // setBounds(12, 10, 352, 361);
		scrollPane.setOpaque(false);
		contentPane.add(scrollPane);

		// 채팅이 나오는 곳
		textArea = new JTextPane();
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 10));
		textArea.setOpaque(false);
		scrollPane.setViewportView(textArea);

		// 메시지 입력하는 곳
		txtInput = new JTextField();
		txtInput.setBounds(18, 350, 150, 36); // (74, 381, 209, 40);
		txtInput.setOpaque(false);
		contentPane.add(txtInput);
		txtInput.setColumns(10);
		txtInput.setForeground(Color.WHITE);

		// send 버튼
		btnSend = new JButton(new ImageIcon(JavaGameClientMain.class.getResource("/main/send.png")));
		// btnSend = new JButton("Se");
		btnSend.setBounds(170, 350, 30, 36);
		btnSend.setBackground(Color.BLACK);
		btnSend.setBorderPainted(false); // 버튼 테두리 설정해제
		btnSend.setFocusPainted(false);
		btnSend.setOpaque(false);
		contentPane.add(btnSend); // contentPane에 버튼 부착

		// 해당 유저 name
		lblUserName = new JLabel("Name"); // dynamic으로 해당 user의 이름을 출력
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setForeground(Color.WHITE);
		;

		// lblUserName.setForeground(Color.WHITE);
		lblUserName.setFont(new Font("굴림", Font.BOLD, 12));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(25, 10, 120, 38); // (12, 430, 62, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		AppendText("User " + username + " connecting ");// + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username + "의 채팅창");

		btnExit = new JButton(new ImageIcon(JavaGameClientMain.class.getResource("/main/exit.png"))); // 종료
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendObject(msg);
				System.exit(0);
			}
		});
		btnExit.setBounds(160, 12, 30, 31); // (295, 430, 69, 40);
		btnExit.setBackground(Color.BLACK);
		btnExit.setBorderPainted(false); // 버튼 테두리 설정해제
		btnExit.setFocusPainted(false);
		btnExit.setOpaque(false);
		contentPane.add(btnExit);


		/*************************** server와 연결하는 함수들  ***********************************/

		try {

			socket = new Socket(ip_addr, port);

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();

			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();

			run();

		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			AppendText("connect error");
		}

		game = new Game(ois, oos);
		panel = new JPanel();
		panel.add(game);
		panel.setVisible(true);
		panel.setBounds(205, 0, 680, 460);// 700, 460
		panel.setOpaque(false);
		contentPane.add(panel);

		game.start();
	}


	/************************ 스레드 실행 함수 / fps관리 *********************/

	public void run() {

		requestFocus();
		int fps = 0; // fps가 항상 100이 되도록 즉 1/100초에 한번씩 화면에 새로운 그래픽을띄우도록 함
		double timer = System.currentTimeMillis();
		long lastTime = System.nanoTime();
		double targetTick = 100.0;
		double delta = 0;
		double ns = 1000000000 / targetTick;

		while (isRunning) {
			long now = System.nanoTime();
			delta = delta + ((now - lastTime) / ns);
			lastTime = now;

			while (delta >= 1) { // render를 tick보다 더 많이 부르지 않으면 synchronized가 되지 않음
				tick();
				render();
				fps++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) { // 1초보다 크면
				fps = 0;
				timer = timer + 1000;
			}
		}
		stop();
	}

	/**************************** ListenNetwork ********************************/
	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {

						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s] %s", cm.UserName, cm.data);
					} else
						continue;
					switch (cm.code) {
						case "200": // chat message
							if (cm.UserName.equals(UserName))
								AppendTextR(msg);
							else
								AppendText(msg);
							break;
						case "501":
							cm.isSpace = isSpace;
							isSpace = true;
							break;
						case "502":
							cm.isSpace = isSpace;
							isSpace = false;
							break;
						case "601": // KeyPressed 수신
							game.DoKeyPressed(cm.keyCode);
							break;
						case "602": // KeyReleased 수신
							game.DoKeyReleased(cm.keyCode);
							break;
						case "701": //STATE = START
							cm.state = STATE;
							STATE = START;
							break;
						case "702":  //STATE = GAME
							cm.state = STATE;
							STATE = GAME;
							break;
						case "703":
							showText = false;
							cm.showText = showText;
							break;
						case "704":
							showText = true;
							cm.showText = showText;
							break;
						case "800":  //점수의 결과를 받아옴
							cm.score1 = score1.score1;
							cm.score2 = score2.score2;
							break;
						case "901":
							map.Ghosts.add(new Ghost(cm.ghost_x, cm.ghost_y));

							break;
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			}
		}
	}

	/********************** 서버로 보내는 상태 값들 ***************************/

	public void sendStateStart(int STATE) {
		ChatMsg cm = new ChatMsg(UserName, "701","Start");
		STATE = START;
		cm.state = STATE;
		SendObject(cm);
	}


	public void sendStateGame(int STATE) {
		ChatMsg cm = new ChatMsg(UserName, "702","Game");
		STATE = GAME;
		cm.state = STATE;
		SendObject(cm);
	}

	public void sendIsSpaceT() {
		ChatMsg cm = new ChatMsg(UserName, "501","isSpace");
		isSpace = true;
		cm.isSpace = isSpace;
		SendObject(cm);
	}


	public void sendIsSpaceF() {
		ChatMsg cm = new ChatMsg(UserName, "502","isSpace");
		isSpace = false;
		cm.isSpace = isSpace;
		SendObject(cm);
	}

	/********************** 서버로 보내는 score 값들 ***************************/

	/*public void sendSocre() {
		ChatMsg cm = new ChatMsg(UserName, "800","scoreSend");
		cm.score1 = score.score;
		cm.score2 = score2.score2;
		cm.result = scoreResult;
		SendObject(cm);
	}*/


	/***************************** 키보드 입력 관리 ***************************/


	public void keyPressed(KeyEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "601", "keyPressed");
		cm.keyCode = e.getKeyCode();
		SendObject(cm);
	}

	public void keyReleased(KeyEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "602", "keyReleased");
		cm.keyCode = e.getKeyCode();
		SendObject(cm);
	}

	public void DoKeyPressed(int keyCode) {
		if (STATE == GAME) {
			// p1
			if (keyCode == KeyEvent.VK_LEFT) {
				pacman.left = true;
			} else if (keyCode == KeyEvent.VK_UP) {
				pacman.up = true;
			} else if (keyCode == KeyEvent.VK_RIGHT) {
				pacman.right = true;
			} else if (keyCode == KeyEvent.VK_DOWN) {
				pacman.down = true;
			}
			// P2
			else if (keyCode == KeyEvent.VK_A) {
				pacman2.left = true;
			} else if (keyCode == KeyEvent.VK_W) {
				pacman2.up = true;
			} else if (keyCode == KeyEvent.VK_D) {
				pacman2.right = true;
			} else if (keyCode == KeyEvent.VK_S) {
				pacman2.down = true;
			} else if(keyCode == KeyEvent.VK_R) {  //강제로 restart

				sendStateStart(STATE);
			}

			//sendStateGame(STATE);
		} else if (STATE == START) {
			if (keyCode == KeyEvent.VK_SPACE) {
				sendIsSpaceT();
				//isSpace = true;
			}
		}
	}

	/*public void DoKeyPressed2(int keyCode) {
		if (STATE == GAME) {
			// p1
			if (keyCode == KeyEvent.VK_LEFT) {
				pacman.left = true;
			} else if (keyCode == KeyEvent.VK_UP) {
				pacman.up = true;
			} else if (keyCode == KeyEvent.VK_RIGHT) {
				pacman.right = true;
			} else if (keyCode == KeyEvent.VK_DOWN) {
				pacman.down = true;
			}
		} else if (STATE == START) {
			if (keyCode == KeyEvent.VK_SPACE) {
				isSpace = true;
			}
		}
	}*/

	public void DoKeyReleased(int keyCode) {
		// p1
		if (keyCode == KeyEvent.VK_LEFT) {
			pacman.left = false;
		} else if (keyCode == KeyEvent.VK_UP) {
			pacman.up = false;
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			pacman.right = false;
		} else if (keyCode == KeyEvent.VK_DOWN) {
			pacman.down = false;
		}
		// P2
		else if (keyCode == KeyEvent.VK_A) {
			pacman2.left = false;
		} else if (keyCode == KeyEvent.VK_W) {
			pacman2.up = false;
		} else if (keyCode == KeyEvent.VK_D) {
			pacman2.right = false;
		} else if (keyCode == KeyEvent.VK_S) {
			pacman2.down = false;
		}
	}

	/*public void DoKeyReleased2(int keyCode) {
		// p1
		if (keyCode == KeyEvent.VK_LEFT) {
			pacman.left = false;
		} else if (keyCode == KeyEvent.VK_UP) {
			pacman.up = false;
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			pacman.right = false;
		} else if (keyCode == KeyEvent.VK_DOWN) {
			pacman.down = false;
		}
	}*/

	public void keyTyped(KeyEvent e) {

	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	// 화면에 좌측 출력
	public void AppendText(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	// 화면 우측에 출력
	public void AppendTextR(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", right);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server에게 network으로 전송
	public synchronized void SendMessage(String msg) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public synchronized void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error");
		}
	}

	/****************** Map class 이곳에서 지도와 쿠키, 고스트가 띄어진다 *********************************/
	class Map {
		public int width; // 40
		public int height; // 25
		public String GhostName;

		public Tile[][] tiles;

		public List<Seed> seeds;
		public List<Ghost> Ghosts;
		public List<SmartGhost> SmartGhosts;

		private int location = 30;

		public Map(String path) { // map(png)이 저장되어 있는 경로를 생성자로 받음

			try {
				seeds = new ArrayList<>();
				Ghosts = new ArrayList<>();
				SmartGhosts = new ArrayList<>();

				BufferedImage map = ImageIO.read(getClass().getResource(path));
				this.width = map.getWidth();
				this.height = map.getHeight();

				int[] pixels = new int[width * height]; // 포토샵으로 그려놓은 맵
				// pixels 배열에 map.png의 픽셀의 색을 일렬화(세로방향으로) 해서 저장함
				map.getRGB(0, 0, width, height, pixels, 0, width);

				tiles = new Tile[width][height]; // GUI에 구현될 맵

				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int val = pixels[i + (j * width)];
						if (val == 0xFF000A7C) { // 벽
							tiles[i][j] = new Tile(i * 16 + location, j * 16 + location);
						} else if (val == 0xFFFFD800) {// 팩맨
							Game.pacman.x = i * 16 + location;
							Game.pacman.y = j * 16 + location;
							Game.pacman2.x = i * 16 + location;
							Game.pacman2.y = j * 16 + location;
						} else if (val == 0xFFFF0000) {// 일반 고스트
							ChatMsg cm = new ChatMsg(UserName, "901", "GhostMove");
							cm.ghost_x = i * 16 + location;
							cm.ghost_y = j * 16 + location;
							SendObject(cm);
							//Ghosts.add(new Ghost(i*16+location, j*16+location));
						} else if (val == 0xFF00FFFF) {// 스마트 고스트
							//ChatMsg cm = new ChatMsg(UserName,"902","SmartGhostMove");
							//cm.ghost_x = i*16+location;
							//cm.ghost_y = j*16+location;
							//SendObject(cm);
							//SmartGhosts.add(new SmartGhost(i*16+location, j*16+location));
						} else {// 씨앗
							seeds.add(new Seed(i * 16 + location, j * 16 + location));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void tick() { // map에서 유령들의 상태를 바꾸고 랜더링 시킴
			for (int i = 0; i < Ghosts.size(); i++) {
				Ghosts.get(i).tick();
			}
			for (int i = 0; i < SmartGhosts.size(); i++) {
				SmartGhosts.get(i).tick();
			}
		}

		public void render(Graphics g) { // seeds와 tile은 상태가 바뀔 필요가 없으므로 tick함수가 존재하지 않음
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (tiles[i][j] != null)
						tiles[i][j].render(g);
				}
			}

			for (int i = 0; i < seeds.size(); i++) {
				seeds.get(i).render(g);
			}
			for (int i = 0; i < Ghosts.size(); i++) {
				Ghosts.get(i).render(g);
			}
			for (int i = 0; i < SmartGhosts.size(); i++) {
				SmartGhosts.get(i).render(g);
			}
		}
	}
}