package BotClient;

public class GameStartTest {
	public static void main( String[] args ) {
		
		BotClient botclient = new BotClient("18.150.7.174:6667","1221",false);
		
		while( !botclient.gameStarted() ) {
		}
		
		System.out.println("***GAME STARTED***");
		//for ( int i = 0; i < 100; i++ )
			System.out.println("MAP --> " + botclient.getMap());
		
		botclient.close();
	}
}

// {"token":"EViL","GAME":"start"}done\n