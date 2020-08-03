package MemoryGame;


import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Board extends JFrame {

	private static final long serialVersionUID = 1L;
	Menu panel = null;
	DataPlayer dataPlayer = new DataPlayer();
	JButton[] board_butons = null;
	String all_imgs[];
	private int[] correct_imgs = null;
	private int total_attempts = 0, attempt= 0, num_correct_imgs = 0, old= -1;
	Socket Cliente = new Socket();
	List<String> ImagensTabuleiro = new ArrayList<String>();

	public Board(DataPlayer _dataPlayer, Menu _p, Socket _Cliente, List<String> _ImagensTabuleiro)
			throws UnknownHostException, IOException {
		;

		panel = _p;
		dataPlayer = _dataPlayer;
		ImagensTabuleiro = _ImagensTabuleiro;
		GridLayout tabuleiro = new GridLayout(dataPlayer.getDifficulty(), dataPlayer.getDifficulty(), 20, 20);
		panel.setLayout(tabuleiro);
		panel.setSize(190 * dataPlayer.getDifficulty(), 190 * dataPlayer.getDifficulty());
		panel.addWindowListener(new Terminator());
		correct_imgs = new int[dataPlayer.getDifficulty() * dataPlayer.getDifficulty()];
		board_butons = new JButton[dataPlayer.getDifficulty() * dataPlayer.getDifficulty()];
		for (int k = 0; k < dataPlayer.getDifficulty() * dataPlayer.getDifficulty(); k++) {
			board_butons[k] = new JButton("");
			board_butons[k].setText("");
			panel.add(board_butons[k]);
			board_butons[k].addActionListener(new carregaBotao(k));
		}
		panel.setLayout(tabuleiro);
		panel.repaint();
		panel.validate(); 

	}


	class Terminator extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}

	class carregaBotao implements ActionListener {
		private int index;
		public carregaBotao(int imagem_tabuleiro) {
			this.index = imagem_tabuleiro;
		}

		public void actionPerformed(ActionEvent e) {
						
			if (num_correct_imgs == board_butons.length) {
				dataPlayer.setAttempts(total_attempts);
				try {
					Cliente = new Socket("localhost", 6666);
					ObjectOutputStream saida = new ObjectOutputStream(Cliente.getOutputStream());
					saida.writeObject("Game over");
					saida.writeObject(dataPlayer.getName());
					saida.writeObject(dataPlayer.getDifficulty());
					saida.writeObject(dataPlayer.getAttempts());				
					ObjectInputStream in = new ObjectInputStream(Cliente.getInputStream());
					int Melhorranking = (int) in.readObject();	
					JOptionPane.showMessageDialog(null,
							"Game over! \nTotal attempts: " + total_attempts + " \tBest score: " + Melhorranking + " \n To play again please exit and login again.", "Memory game", JOptionPane.INFORMATION_MESSAGE);
					saida.close();
					Cliente.close();
				} catch (  IOException | ClassNotFoundException e1) {
				 e1.printStackTrace();
				 }				
				return;
			}
			
		if(attempt == 0){
			for (int i = 0; i < num_correct_imgs && correct_imgs.length != 0; i++) {
				if (index == correct_imgs[i]) {
					JOptionPane.showMessageDialog(null, "Picture already chosen!", "Memory game", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			attempt = 1;
			total_attempts++;
			old = index;
			try {
				Image img = ImageIO.read(getClass().getResource("imagens//" + ImagensTabuleiro.get(index) + ".jpg"));
				board_butons[index].setIcon(new ImageIcon(img));
			} catch (IOException ex) {
			}
			panel.validate();
			return;
		}
		else
		{
			if (index == old) {
				JOptionPane.showMessageDialog(null, "Picture already chosen " + attempt, "Memory game", JOptionPane.INFORMATION_MESSAGE);
				return;
			}else {		
				for (int i = 0; i < num_correct_imgs && correct_imgs.length != 0; i++) {
					if (index == correct_imgs[i]) {
						JOptionPane.showMessageDialog(null, "Picture already chosen!", "Memory game", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				attempt = 0;
				try {
					Image img = ImageIO
							.read(getClass().getResource("imagens//" + ImagensTabuleiro.get(index) + ".jpg"));
					board_butons[index].setIcon(new ImageIcon(img));
					panel.validate();
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				try {
					Cliente = new Socket("localhost", 6666);
					ObjectOutputStream saida = new ObjectOutputStream(Cliente.getOutputStream());
					saida.writeObject("Validate");
					saida.writeObject(old);
					saida.writeObject(index);
					saida.writeObject(num_correct_imgs);
					saida.writeObject(ImagensTabuleiro);
					saida.flush();
					ObjectInputStream in = new ObjectInputStream(Cliente.getInputStream());
					String Validacao = (String) in.readObject();
					if (Validacao.equals("Right")) {
						JOptionPane.showMessageDialog(null, "Right!!  :)", "Memory game", JOptionPane.INFORMATION_MESSAGE);
						correct_imgs[num_correct_imgs] = index;
						correct_imgs[num_correct_imgs + 1] = old;
						num_correct_imgs = num_correct_imgs + 2;
						return;
					}

					if (Validacao.equals("Wrong")) {
						JOptionPane.showMessageDialog(null, "Wrong!!  :(", "Memory game", JOptionPane.INFORMATION_MESSAGE);
						board_butons[index].setIcon(null);
						board_butons[old].setIcon(null);
						panel.validate();
						return;
					}
					in.close();
					Cliente.close();
				} catch (ClassNotFoundException | IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}}
