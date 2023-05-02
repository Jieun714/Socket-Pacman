// JavaObjClient.java

// ObjecStream 사용하는 채팅 Client

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JavaGameClientMain extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	//private JPanel contentPane;
	private JTextField txtUserName;
	//private JTextField txtIpAddress;
	//private JTextField txtPortNumber;
	//private Image background; //시작 배경화면
	private ImageIcon icon;
	private ImageIcon btnIcon;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaGameClientMain frame = new JavaGameClientMain();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					//frame.setSize(300, 300);
					frame.setUndecorated(true); //title 지우기
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 *
	 * @throws IOException
	 */
	public JavaGameClientMain() {
		icon = new ImageIcon(Game.class.getResource("/main/main.png"));
		btnIcon = new ImageIcon(Game.class.getResource("/main/start.png"));

		setBounds(500, 250, 500, 300);
		JPanel contentPane = new JPanel(){
			public void paintComponent(Graphics g) {
				g.drawImage(icon.getImage(),0,0,null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		contentPane.setVisible(true);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//user name 입력하는 상자
		txtUserName = new JTextField();
		txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
		txtUserName.setBounds(210, 222, 116, 33);
		txtUserName.setBackground(Color.BLACK);
		txtUserName.setForeground(Color.WHITE);
		contentPane.add(txtUserName);
		txtUserName.setColumns(10);


		//JButton btnConnect = new JButton(new ImageIcon(JavaGameClientMain.class.getResource("/main/start.png")));
		//JButton btnConnect = new JButton("Connect");
		JButton btnConnect = new JButton(){
			public void paintComponent(Graphics g) {
				g.drawImage(btnIcon.getImage(),0,0,null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		btnConnect.setBounds(350, 222, 85, 35);
		btnConnect.setBackground(Color.BLACK);
		btnConnect.setBorderPainted(false);  // 버튼 테두리 설정해제
		btnConnect.setFocusPainted(false);

		contentPane.add(btnConnect); //contentPane에 버튼 부착

		Myaction action = new Myaction();
		btnConnect.addActionListener(action);
		txtUserName.addActionListener(action);
		//txtIpAddress.addActionListener(action);
		//txtPortNumber.addActionListener(action);

	}
	/*public void paint(Graphics g) {//그리는 함수
		g.drawImage(background, 0, 0, null);//background를 그려줌
	}*/

	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = txtUserName.getText().trim();
			//String ip_addr = txtIpAddress.getText().trim();
			//String port_no = txtPortNumber.getText().trim();
			Game game = new Game(username);

			setVisible(false);
		}
	}
}
