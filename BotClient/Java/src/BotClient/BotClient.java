package BotClient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;

public class BotClient {
	
	private String host = "";
	private int port = 0;
	private String token = "";
	
	final private boolean debug; 
	private Socket socket = null;
	private Receiver rcv = null;
	private PrintWriter out = null;
	private WritebackHandler handler = new WritebackHandler();
	
	volatile private boolean ping_sent = false;
	volatile private long ping_start = 0;
	volatile private long latency = -1;
	
	volatile private String map = null;
	volatile private boolean gameStarted = false;
	volatile private boolean waitingForGameStarted = false;
	
	public BotClient( String host_and_port, String token, boolean debug ) {
		this.debug = debug;
		String[] part = host_and_port.split(":");
		this.host = part[0];
		this.port = Integer.parseInt(part[1]);
		this.token = token;
		
		try {
			socket = new Socket(this.host, this.port);
			out = new PrintWriter(socket.getOutputStream(), true);
			rcv = new Receiver(handler,socket);
			rcv.start();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void send( String field, String title, String value ) {
		send("{\"token\":\""+token+"\"," + 
			 "\""+field+"\":" +
			 "[\""+title+"\",\""+value+"\"]}done");
	}
	
	public void sendRaw( String raw ) {
		send("{\"token\":\""+token+"\"," + raw + "}done");
	}
	
	public void sendImage( BufferedImage img ) {
		try { 
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			OutputStream b64 = new Base64.OutputStream(os);
			ImageIO.write(img, "PNG", b64);
			String result = os.toString("UTF-8");
			send("{\"token\":\""+token+"\",\"IMAGE_DATA\":\""+result+"\"}done");
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private void send( String msg ) {
		try {
	        if ( debug ) 
	        	System.out.println("[SEND] " + msg);
	        out.print(msg+"\n");
	        out.flush();
		} catch ( Exception e ) {
		}
	}
	
	public void pingGame() {
		try {
	        if ( debug ) 
	        	System.out.println("[SEND] GAME PING");
	        out.print("game\n");
	        out.flush();
		} catch ( Exception e ) {
		}
	}
	
	public void ping() {
		while ( ping_sent ) {
			try { Thread.sleep(10); } catch ( Exception e ) {}
		}
		ping_sent = true;
		ping_start = System.currentTimeMillis();
		send("ping");
	}
	
	public long getLatency() {
		return latency;
	}
	
	public String getMap() {
		map = null;
		send("MAP","junk","junk");
		int i = 0;
		while (map==null && i < 500){
			try {
				Thread.sleep(20);
			}
			catch (Exception e){
			}
			i++;
		}
		
		if (map == null) {
    		// Timeout -- crash the system with an error
    		System.err.println("ERROR: MAP NOT RECEIVED FROM BOT_CLIENT. REDEEM THIS LOG MESSAGE FOR A FREE REMATCH. RESTRICTIONS MAY APPLY, ASK YOUR LOCAL MASLAB DEALER FOR MORE INFORMATION.");
    		System.exit(-1);
		}
		
		return map;
	}
	
	public boolean gameStarted() {
		this.waitingForGameStarted = true;
		this.pingGame();
		int i = 0;
		while ( this.waitingForGameStarted && i < 500 ) {
			try {
				Thread.sleep(20);
			} catch ( Exception e ) {}
			i++;
		}
		
		return gameStarted;
	}
	
	public void close() {
		try {
			out.close();
			socket.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public class WritebackHandler {
		public void pong() {
			long ping_end = System.currentTimeMillis();
			if ( debug )
				System.out.println("[CALLBACK] BotClient ping received : " + (ping_end-ping_start)+"ms");
			latency = (ping_end-ping_start);
			ping_sent = false;
		}
		
		public void connected() {
			if ( debug )
				System.out.println("[CALLBACK] BotClient connected successfully!");
		}
		
		public void map( String m ) {
			if ( debug )
				System.out.println("[CALLBACK] MAP : " + m);
			map = m;
		}
		
		public void game( String state ) {
			if ( debug )
				System.out.println("[CALLBACK] GAME : " + state);
			if ( state.toLowerCase().equals("start") ) {
				gameStarted = true;
			} else {
				gameStarted = false;
			}
			waitingForGameStarted = false;
		}
	}
}
