package BotClient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ViewTest {
	public static void main( String[] args ) {
		
		// {"EViL":"admin", "1221":"jason", "0MtNBP10MG": "team1", "5byQh8Ly59": "team2", "b3MpHHs4J1":"team3", "a12L7plGB8":"team4", "H4Lx8c0mOw":"team5", "qXp0r37r9o":"team6", "mT82Qi240y":"team7", "YNxFh4tnOY":"team8", "14GHK83Vk6":"team9", "4LdcL0DQw4":"team10", "a0qzL4T9fq":"team11", "r5Z8L619ho":"team12", "i5d76YlHmB":"team13", "y8NKe1Oz54":"team14", "zP2Ki3R96P":"team15", "Nj5fd3q7pe":"team16", "fBETY83RSp":"team17"}
		
		BotClient botclient = new BotClient("18.150.7.174:6667","zP2Ki3R96P",false);
		
		botclient.send("a", "State", "Ball Search");
		botclient.send("b", "Gyro", "4.14159");
		
		// Smiley
		BufferedImage image = new BufferedImage(320,240,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();
		int WD = 320/8;
		for ( int i = 0; i < 8; i++ ) {
			Color col = Color.getHSBColor(i/8.0f,1.0f,1.0f);
			g.setColor(col);
			g.fillRect(i*WD,0,WD,240);
		}
		
		botclient.sendImage(image);
		
		botclient.close();
	}
}