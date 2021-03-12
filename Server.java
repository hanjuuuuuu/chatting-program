import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Server extends Thread implements ActionListener{
	Frame frame;
	Panel panel;
	TextArea ta;
	TextField tf;
	Button btnSend, btnEnd;
	ServerSocket s;
	Socket sk;
	DataOutputStream dos;
	DataInputStream dis;
	boolean stop;

	public Server(){
		
		launchFrame();
		service();
	}

	private void launchFrame(){		//프레임 생성
		frame  = new Frame("서버");
		ta = new TextArea();
		tf = new TextField(25);
		frame.setBackground(Color.lightGray);	//바탕 색 지정
		ta.setEditable(false);		//쓰기 금지

		frame.add(ta, BorderLayout.CENTER);
		tf.addActionListener(this);
		
		btnSend = new Button("전송");
		btnEnd = new Button("종료");
		panel = new Panel();
		panel.add(tf);
		panel.add(btnSend);
		panel.add(btnEnd);
		frame.add(panel, BorderLayout.SOUTH);		//버튼 위치 지정
		btnSend.addActionListener(this);
		btnEnd.addActionListener(this);
		
		frame.setBounds(20, 100, 430, 300);			//프레임 크기, 위치 지정
		frame.setVisible(true);
		tf.requestFocus();
	}

	private void service(){
		try{
			ta.append("준비중...\n");
			s= new ServerSocket(10204);		//서버 소켓 생성
			ta.append("클라이언트 접속  대기중...");
			sk = s.accept();			//서버 소켓 연결, 클라이언트 소켓  생성해 리턴
			ta.append("클라이언트가  접속하였습니다. : "+sk.getInetAddress()+'\n');

			//통신을 위해 스트림 생성
			dos= new DataOutputStream(sk.getOutputStream());
			dis= new DataInputStream(sk.getInputStream());
			 
			this.start();
	        dos.writeUTF("채팅 서버에 접속하였습니다.");
	        
		}catch(IOException io){
			io.printStackTrace();
		}
		
	}


	public static void main(String args[]){
		new Server();
	}


	public void actionPerformed(ActionEvent action){
		try{
			String msg = tf.getText();		//textfiled에 써있는 글씨 읽어오기
			ta.append(msg+'\n');
			if(btnEnd==action.getSource()){		// 종료버튼 눌러 창닫기
				stop=true;
				dos.close();
				sk.close();
				System.exit(0);

			}else{					//메시지 전송
				dos.writeUTF("서버 :"+msg);
				tf.setText("");
			}

		}catch(IOException io){
			ta.append(io.toString()+'\n');
		}
	}


	public void run(){
		try{
			while(!stop){
				ta.append(dis.readUTF()+'\n');		//채팅창에 출력
			}
	        dis.close();
	        sk.close();
		}catch(EOFException eof){
			ta.append("클라이언트로 부터 연결이 끊어졌습니다.\n ");
		}catch(IOException io){
			io.printStackTrace();
		}
	}
}


