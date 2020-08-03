package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

public class Server implements Runnable {

	public Socket treatment;
	List<String> All_Imgs = new ArrayList<String>();
	
	File currentDir = new File (".");
	String basePath = currentDir.getAbsolutePath();
	String pathLog = basePath + "/playersLogger.txt";
	String pathLogScore = basePath + "/scoreLogger.txt";
	static String date;
	
	public Server(Socket socket) {
		this.treatment = socket;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		int port = 6666;
		Calendar calendar = Calendar.getInstance(); // Returns instance with current date and time set
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		date = formatter.format(calendar.getTime());
		

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept(); // tratar da comunicacao com o cliente, assim que um pedido de
														// conexão chegar ao servidor e a conexao for aceita:

				System.out.println("New client connected " + socket.getInetAddress().getHostAddress() + " \n");
				Server treatment = new Server(socket);
				Thread t = new Thread(treatment);
				t.start();

			}

		} catch (IOException ex) {
			System.out.println(ex.getMessage());

		}

	}

	Protocol Comm = new Protocol();
	DataPlayer Player = new DataPlayer();
	int num_correct_imgs = 0;
	@Override
	public void run() {
		
		FileWriter myWriter;
		
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(treatment.getInputStream());
			
			String Msg = (String) in.readObject();
			System.out.println(Msg);
			if (Msg.equals("Start")) {
				
				Player=Comm.MsgStart(treatment, in);
				System.out.println(Player.getAll());
				
				try {
					myWriter = new FileWriter(pathLog, true); // true = since creation
					myWriter.write("[NewGame] " + Player.getName() + " started a new game at " + date + "\n");
					myWriter.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					StringWriter errors = new StringWriter();
                    e1.printStackTrace(new PrintWriter(errors));
				}
				
				return;
			}
			if (Msg.equals("Ranking")) {
				
				int Difficulty = 0;
				Comm.MsgRanking(treatment, Difficulty);
			}
			if (Msg.equals("Validate")) {

				int Pos1 = (int) in.readObject();
				System.out.println("Position1: " + Pos1);
				int Pos2 = (int) in.readObject();
				System.out.println("Position2: " + Pos2);
				num_correct_imgs = (int) in.readObject();
				System.out.println("num_correct_imgs: " + num_correct_imgs);
				All_Imgs = (List<String>)in.readObject();
				
				int num_correct_imgs_updated = Comm.MsgValidar(Pos1, Pos2, All_Imgs, num_correct_imgs);
				
				
				if(num_correct_imgs_updated != num_correct_imgs) {
					num_correct_imgs = num_correct_imgs_updated;
					System.out.println("Number of correct images: " + num_correct_imgs);
					ObjectOutputStream saida = new ObjectOutputStream(treatment.getOutputStream());
					saida.writeObject("Right");
				}
				else {
					System.out.println("Wrong");
					System.out.println("Number of correct images: " + num_correct_imgs);
					ObjectOutputStream saida = new ObjectOutputStream(treatment.getOutputStream());
					saida.writeObject("Wrong");
				}
				
	
			}
			if(Msg.equals("Game over")) {
				
				Player.setName((String) in.readObject());
				Player.setDifficulty((int) in.readObject());
				Player.setAttempts((int) in.readObject());
				
				List<DataPlayer> Players = new ArrayList<DataPlayer>();
				InputOutputData InputOutput = new InputOutputData();
				if (InputOutput.ReadData("Ficheiro") == null) {
					System.out.println("No players");	
					Players.add(Player);					
					InputOutput.WriteData(Players, "Ficheiro");
					StringBuilder SB = new StringBuilder();
					for (DataPlayer dataPlayer : Players) {
						SB.append(dataPlayer.getAttempts() + "\n");
					}
					return;
				}
				else {
					int attempts = Player.getAttempts();
					Players = InputOutput.ReadData("Ficheiro");
					Players.add(Player);
					InputOutput.WriteData(Players, "Ficheiro");

					for (DataPlayer Player2 : Players) {
						if(Player2.getName() != null) {
							if (Player2.getName().equals(Player.getName())) {
								if (Player2.getDifficulty() == Player.getDifficulty()) {
									if (Player2.getAttempts() < attempts) {
										attempts = Player2.getAttempts();
									}
								}
							}
						}
						

					}
					
					
					
					try {
						myWriter = new FileWriter(pathLogScore, true);
						myWriter.write("[GameOver] " + Player.getName() + " finished a new game at " + date + " with " + Player.getAttempts() + " attempts\n");
						myWriter.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						StringWriter errors = new StringWriter();
	                    e1.printStackTrace(new PrintWriter(errors));
					}
					
					
					
					ObjectOutputStream saida = new ObjectOutputStream(treatment.getOutputStream());
					saida.writeObject(attempts);

				
			}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	
	}
}
