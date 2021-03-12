import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;


public class Client extends Thread implements ActionListener{
	Frame frame;
	Panel panel;
	TextArea ta;
	TextField tf, tf2;
	Dialog dialog;
	Label label;
	Button btnSend, btnEnd;
	Socket sk;
	DataOutputStream dos;
	DataInputStream dis;
	boolean stop;
	String host;
	
	public Client(){
		launchFrame();
	}


	private void launchFrame(){  		//프레임 생성
		frame  = new Frame("클라이언트"); 
		ta = new TextArea();
		tf = new TextField(25);
		frame.setBackground(Color.lightGray);	//배경 색 지정
		ta.setEditable(false);	//쓰기 금지

		frame.add(ta, BorderLayout.CENTER);
		tf.addActionListener(this);
		
		btnSend = new Button("전송");
		btnEnd = new Button("종료");
		panel = new Panel();
		panel.add(tf);
		panel.add(btnSend);
		panel.add(btnEnd);
		frame.add(panel, BorderLayout.SOUTH);	//버튼 위치 지정
		btnSend.addActionListener(this);
		btnEnd.addActionListener(this);
		
		frame.setBounds(450, 100, 430, 300);	//프레임 크기, 위치 지정
		frame.setVisible(true);
		tf.requestFocus();

		dialog = new Dialog(frame, "서버 ip", true);	//다이얼로그창 생성
		label = new Label("접속할 서버 IP를 입력하세요.");
		tf2 = new TextField(15);
		dialog.add(label, BorderLayout.NORTH);
		dialog.add(tf2, BorderLayout.CENTER);
		tf2.addActionListener(this);

		dialog.pack();   //다이얼로그 창 크기 정해줌
		dialog.show();

		service(host);		//ip 주소 받아오기

		tf2.requestFocus();

	}
	
	private void service(String host){
		try{
			 sk = new Socket(host, 10204);  //받아온 ip주소와 포트로 소켓 생성
			 dis = new DataInputStream(sk.getInputStream());
			 dos = new DataOutputStream(sk.getOutputStream());
			 ta.append("접속완료..\n");	

			 this.start();

		}catch(IOException io){
			io.printStackTrace();
		}

	}



  public static void main(String args[])throws IOException {
		new Client();
	}


  public void actionPerformed(ActionEvent action){
		if(tf==action.getSource()){ 	
			try{
				String msg = tf.getText();	//textfiled에 써있는 글씨 읽어오기
				ta.append(msg+'\n');
				dos.writeUTF("클라이언트 :"+msg);
				tf.setText("");
				
			}catch(IOException io){
				ta.append(io.toString()+'\n');
			}

		}else if(btnSend==action.getSource()) {		//전송 버튼 눌러 메시지 전송
			try{
				String msg = tf.getText();
				ta.append(msg+'\n');
				dos.writeUTF("클라이언트 :"+msg);
				tf.setText("");
				
			}catch(IOException io) {
				ta.append(io.toString()+'\n');
			}
		}else if(btnEnd == action.getSource()) {	//종료 버튼 눌러 창닫기
			try{
				stop=true;
				dos.close();
				sk.close();
				System.exit(0);
				
			}catch(IOException io) {
				ta.append(io.toString()+'\n');
			}
		}else{						
			host=tf2.getText().trim(); //ip 주소 받아오기
			if(host.equals(""))
				host="127.0.0.1";
			dialog.dispose();	//다이얼로그 창 닫기
			}
	}

	

	public void run(){
		try{
			while(!stop){
				ta.append(dis.readUTF()+'\n');  //채팅창에 출력
			}
	        dis.close();
	        sk.close();
		}catch(EOFException eof){
			ta.append("서버로 부터 연결이 끊어졌습니다.");
		}catch(IOException io){
			io.printStackTrace();
		}

	}

}
