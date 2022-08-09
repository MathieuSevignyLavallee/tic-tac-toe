import java.io.*;
import java.net.*;
class Client {
	public static void main(String[] args) {

		Socket MyClient;
		BufferedInputStream input;
		BufferedOutputStream output;
		int[][] board = new int[9][9];
		int XO = 0;
	
		

		try {
			MyClient = new Socket("localhost", 8888);

			input = new BufferedInputStream(MyClient.getInputStream());
			output = new BufferedOutputStream(MyClient.getOutputStream());
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

			while (1 == 1) {
				char cmd = 0;

				cmd = (char) input.read();
				System.out.println(cmd);
				// Debut de la partie en joueur blanc
				if (cmd == '1') {
					XO = 4;
					byte[] aBuffer = new byte[1024];

					int size = input.available();
					// System.out.println("size " + size);
					input.read(aBuffer, 0, size);
					String s = new String(aBuffer).trim();
					System.out.println(s);
					String[] boardValues;
					boardValues = s.split(" ");
					int x = 0, y = 0;
					for (int i = 0; i < boardValues.length; i++) {
						board[x][y] = Integer.parseInt(boardValues[i]);
						x++;
						if (x == 9) {
							x = 0;
							y++;
						}
					}

					System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
					String move = null;
					// move = console.readLine();
					move = "E5";
					board[4][4] = XO;
					output.write(move.getBytes(), 0, move.length());
					output.flush();
				}
				// Debut de la partie en joueur Noir
				if (cmd == '2') {
					XO = 2;
					System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
					byte[] aBuffer = new byte[1024];

					int size = input.available();
					// System.out.println("size " + size);
					input.read(aBuffer, 0, size);
					String s = new String(aBuffer).trim();
					System.out.println(s);
					String[] boardValues;
					boardValues = s.split(" ");
					int x = 0, y = 0;
					for (int i = 0; i < boardValues.length; i++) {
						board[x][y] = Integer.parseInt(boardValues[i]);
						x++;
						if (x == 9) {
							x = 0;
							y++;
						}
					}
				}

				// Le serveur demande le prochain coup
				// Le message contient aussi le dernier coup joue.
				if (cmd == '3') {
					byte[] aBuffer = new byte[16];

					int size = input.available();
					System.out.println("size :" + size);
					input.read(aBuffer, 0, size);

					String s = new String(aBuffer);
					String[] serverResponse = s.split("");

					// enregister coup adversaire
					int xAdv = posX(serverResponse[1]);
					int yAdv = Integer.parseInt(serverResponse[2]) - 1;

					if (XO == 2) {
						board[xAdv][yAdv] = 4;
					}

					if (XO == 4) {
						board[xAdv][yAdv] = 2;
					}

					System.out.println(xAdv + "|" + yAdv);
					position pos = null;

					int jouerX = posX(serverResponse[1]);
					int jouerY = posY(serverResponse[2]);

					System.out.println("Coup Adversaire: " + serverResponse[1] + "|" + serverResponse[2]);
					System.out.println("Il veut nous faire jouer ici: " + jouerX + "|" + jouerY);

					pos = algo.trouverCoups(board, XO, jouerX, jouerY);
					System.out.println("On joue ici: " +( pos.getX())+ "|" + pos.getY());
					
					int x = pos.getX();
					int y = pos.getY();

					System.out.println("Dernier coup :" + s);
					System.out.print("Entrez votre coup : ");
					String move = null;
					move = intToChar(x) + Integer.toString(y + 1);
					board[x][y] = XO;
					System.out.println(move);
					System.out.println(".");
					// move = console.readLine();
					output.write(move.getBytes(), 0, move.length());
					output.flush();

				}
				// Le dernier coup est invalide
				if (cmd == '4') {
					System.out.println("Coup invalide, entrez un nouveau coup : ");
					String move = null;
					move = console.readLine();
					output.write(move.getBytes(), 0, move.length());
					output.flush();

				}
				// La partie est terminée
				if (cmd == '5') {
					byte[] aBuffer = new byte[16];
					int size = input.available();
					input.read(aBuffer, 0, size);
					String s = new String(aBuffer);
					System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
					String move = null;
					move = console.readLine();
					output.write(move.getBytes(), 0, move.length());
					output.flush();

				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static int posX(String str) {

		if (str.equals("A"))
			return 0;
		else if (str.equals("B"))
			return 1;
		else if (str.equals("C"))
			return 2;
		else if (str.equals("D"))
			return 3;
		else if (str.equals("E"))
			return 4;
		else if (str.equals("F"))
			return 5;
		else if (str.equals("G"))
			return 6;
		else if (str.equals("H"))
			return 7;
		else if (str.equals("I"))
			return 8;

		return -1;

	}

	public static int posY(String str) {

		return Integer.parseInt(str) -1;

	}

	public static int ouJouerY(String str) {

		if (str.equals("1") || str.equals("4") || str.equals("7"))
			return 1;
		else if (str.equals("2") || str.equals("5") || str.equals("8"))
			return 4;
		else if (str.equals("3") || str.equals("6") || str.equals("9"))
			return 7;
		return 0;
	}

	public static int ouJouerX(String str) {

		if (str.equals("A") || str.equals("D") || str.equals("G"))
			return 1;
		else if (str.equals("B") || str.equals("E") || str.equals("H"))
			return 4;
		else if (str.equals("C") || str.equals("F") || str.equals("I"))
			return 7;
		return 0;
	}

	public static String intToChar(int x) {

		String posX = "";

		if (x == 0)
			posX = "A";
		else if (x == 1)
			posX = "B";
		else if (x == 2)
			posX = "C";
		else if (x == 3)
			posX = "D";
		else if (x == 4)
			posX = "E";
		else if (x == 5)
			posX = "F";
		else if (x == 6)
			posX = "G";
		else if (x == 7)
			posX = "H";
		else if (x == 8)
			posX = "I";

		return posX;

	}
}