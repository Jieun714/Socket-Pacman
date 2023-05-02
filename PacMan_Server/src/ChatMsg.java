
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code;  // 100:로그인, 400:로그아웃, 200:채팅메시지, 500: Mouse Event, 601,602: KeyEvent
	public String UserName;
	public String GhostName;
	public String data;
	public String result;
	public ImageIcon img;
	public MouseEvent mouse_e;
	public int keyCode;
	public int length;
	public int state;  //게임 상태를 확인하기 위해서 사용한다.
	public int ghost_x;
	public int ghost_y;
	public int smartghost_x;
	public int smartghost_y;
	public int seeds_x;
	public int seeds_y;
	public String score;
	public int score1;
	public int score2;

	public int num =0;
	public boolean showText;
	public boolean win;
	public boolean finish;
	public boolean isSpace;

	public ChatMsg(String UserName, String code, String msg) {
		this.code = code;
		this.UserName = UserName;
		this.data = msg;
	}
}