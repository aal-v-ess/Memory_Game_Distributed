package Server;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;



public class Protocol {
	
	public static final int NBEST_GAMES_SHOW=10;
	
	private int tentativa,total_tentativas,antiga;
	int[] imagens_correctas;
	List<String> TodasImagens = new ArrayList<String>();
	
	public Protocol() {
		 
		 total_tentativas=0;
		 antiga=-1;
	}
		/**
	 * Metodo para quando recebe uma mensagem comecar o jogo
	 * 
	 * @param Cliente
	 */
	public DataPlayer MsgStart(Socket Cliente, ObjectInputStream entrada) {
		try {
			DataPlayer Player = new DataPlayer();

			Player.setName((String) entrada.readObject());
			Player.setDifficulty((int) entrada.readObject());
			Player.setAttempts(0);
			imagens_correctas = new int[Player.getDifficulty()*Player.getDifficulty()];
			System.out.println("mensagem recebida: " + Player.getAll());

			
			String todas_imagens[];
			todas_imagens = new String[8];
			todas_imagens[0] = "morango";
			todas_imagens[1] = "laranja";
			todas_imagens[2] = "pessego";
			todas_imagens[3] = "pera";
			todas_imagens[4] = "banana";
			todas_imagens[5] = "limao";
			todas_imagens[6] = "maracuja";
			todas_imagens[7] = "maça";

			String imagens_possiveis[], imagem_tabuleiro[];
			imagem_tabuleiro = new String[Player.getDifficulty() * Player.getDifficulty()];
			imagens_possiveis = new String[Player.getDifficulty() * Player.getDifficulty()];

			int j = 0;
			int mudarImagem = 0;
			for (int i = 0; i < Player.getDifficulty() * Player.getDifficulty(); i++) {
				mudarImagem++;
				imagens_possiveis[i] = todas_imagens[j];
				if (mudarImagem == 2) {
					mudarImagem = 0;
					j++;
				}
			}

			Random r = new Random();
			for (int i = 0; i < Player.getDifficulty() * Player.getDifficulty(); i++) {
				// a funcao nextint conta a partir do 0(inclusive) ate n(nao incluido). e.g. n=8
				// => intervalo possivel [0,1,2,3,4,5,6,7]
				int resultado = r.nextInt(Player.getDifficulty() * Player.getDifficulty() - i);// escolhe o numero de
																									// o
																									// numero de
																									// iomagens
																									// possives
				// tendo em conta as imagens que ja escolheu (o "i"
				// representa numero de imagens que ja foram
				// escolhidas)
				imagem_tabuleiro[i] = imagens_possiveis[resultado];
				// colocar imagem selecionada no fim array imagens_posseivis
				if (resultado != imagens_possiveis.length - 1) {// so entra aqui no caso de a imagem escolhida nao
																// estiver
																// na ultima posicao do array
					for (int k = resultado; k < Player.getDifficulty() * Player.getDifficulty() - 1; k++) {
						if (resultado != imagens_possiveis.length - 1) {
							String aux = imagens_possiveis[k + 1];
							imagens_possiveis[k + 1] = imagens_possiveis[k];
							imagens_possiveis[k] = aux;
						}
					}
				}
			}
			
			
			for (int i = 0; i < imagem_tabuleiro.length; i++) {
				System.out.println("Add " + imagem_tabuleiro[i]);
				TodasImagens.add(imagem_tabuleiro[i]);
			}
			ObjectOutputStream saida = new ObjectOutputStream(Cliente.getOutputStream());
			saida.writeObject(TodasImagens);
			System.out.println("Protocol "+ Player.getAll());
			return Player;
		} catch (IOException | ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
		return null;

	}

	/**
	 * quando recebe a mensagem Ranking, Vai ler o ficheiro e envia a lista de dados
	 * no historio
	 * 
	 * @param Cliente
	 */
	public void MsgRanking(Socket Cliente, int Dificuldade) {

		List<DataPlayer> Players= new ArrayList<DataPlayer>();
		InputOutputData DataIO= new InputOutputData();

		
		try {
							
				// To re-write the Player data it will be necessary get all the players from the file(to a list) and add in that list the new player(s)
				Players=  DataIO.ReadData("Ficheiro");

				if(Players.size()==0) {
					JOptionPane.showMessageDialog(null, "Sem jogadores");
					return;
				}

				
				
				// Sort by the top scores
				Collections.sort(Players, new SortScore());		

				List<DataPlayer> listOrdered = new ArrayList<DataPlayer>();
				int i=0;
				List<String> bestPlayers = new ArrayList<String>();

				for (DataPlayer resultado : Players) {
					// avoid various scores from the same player
					if(bestPlayers.contains(resultado.getName()))
						continue;

					bestPlayers.add(resultado.getName());

					listOrdered.add(resultado);
					i++;
					if(i == Protocol.NBEST_GAMES_SHOW) // saves the 10 best scores
						break;
				}
				
				
				
				
				StringBuilder SB= new StringBuilder();
				for (DataPlayer dataPlayer : Players) {
					SB.append("Name: \t" + dataPlayer.getName());
					SB.append("\t\t\t Difficulty: \t" + dataPlayer.getDifficulty() );
					SB.append("\t\t\t Attempts: \t" + dataPlayer.getAttempts() + "\n");
				}
				System.out.println(SB.toString());
				ObjectOutputStream saida = new ObjectOutputStream(Cliente.getOutputStream());
				saida.writeObject(SB);
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	}

	/**
	 * Metodo para validar as jogadas.
	 * Este metodo recebe as duas posicoes
	 * Verifica se sao iguais e se nao acabou o jogo.
	 * 
	 * @param Cliente
	 * @param Posicao
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public int  MsgValidar( int Posicao1, int Posicao2, List<String> TodasImagens, int num_de_img_correctas)
			throws FileNotFoundException, IOException {

		
		// Caso ja tenha acabado o jogo
		 
		
		System.out.println("1 "+TodasImagens.get(Posicao1)+"2 "+TodasImagens.get(Posicao2)+"\n");
		if (num_de_img_correctas == TodasImagens.size()) {

			return num_de_img_correctas;
		}
		// Caso nao tenha acabado o jogo
		// Verifica se as duas imagen sao iguais 
		else {
			
			// verifica se sao iguais
			if (TodasImagens.get(Posicao1).equals(TodasImagens.get(Posicao2))) {
				// sao iguais
				System.out.println("ACERTOU NAS IMAGENS\n");
				// adiciona as duas imag _img_correctas]=index;
//				imagens_correctas[num_de_img_correctas] = Posicao1;
//				imagens_correctas[num_de_img_correctas + 1] = Posicao2;
				// aumenta o numero de imgens correctas (2)
				num_de_img_correctas = num_de_img_correctas + 2;	
				return num_de_img_correctas;
			} else {
				// nao sao iguais
				return num_de_img_correctas;
			}
			
		}
	}

	
}
