package MemoryGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.*;


public class Menu extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	boolean comecou = false;
	JButton[] botoes_taubeiro = null;
	private JMenuItem menu_GAME_Start;
	private JMenuItem menu_GAME_Exit;
	private JMenuItem menu_DIFFICULTY_easy;
	private JMenuItem menu_DIFFICULTY_hard;
	private JMenuItem menu_INFO_help;
	private JMenuItem menu_INFO_about;
	private JMenuItem menu_GAME_Login;
	private JMenuItem menu_GAME_Ranking;
	private JMenuItem menu_GAME_Disconnect;
	private int DIFFICULTY = 2;
	private JMenu menu_GAME;
	private Socket client;
	private boolean login = false;
	
	Game game_obj;

	private String playerName;

	public Menu(Game j) {
		game_obj = j;
		
		setTitle("Memory game");
		setLayout(new FlowLayout());
		setSize(400,400);
		addWindowListener( new Terminator());
		
		menu_GAME = new JMenu("Game");
		menu_GAME_Start = new JMenuItem("Start");
		menu_GAME_Start.addActionListener(this);
		menu_GAME.add(menu_GAME_Start);	
		
		menu_GAME_Login = new JMenuItem("Login");
		menu_GAME_Login.addActionListener(this);
		menu_GAME.add(menu_GAME_Login);

		menu_GAME_Ranking = new JMenuItem("Ranking");
		menu_GAME_Ranking.addActionListener(this);
		menu_GAME.add(menu_GAME_Ranking);
		
		menu_GAME_Disconnect = new JMenuItem("Disconnect");
		menu_GAME_Disconnect.addActionListener(this);
		menu_GAME.add(menu_GAME_Disconnect);
		
		menu_GAME_Exit= new JMenuItem("Exit");
		menu_GAME_Exit.addActionListener(this);
		menu_GAME.add(menu_GAME_Exit);
		
		JMenu menu_DIFICULDADE = new JMenu("Difficulty");
		menu_DIFFICULTY_easy = new JMenuItem("Easy");
		menu_DIFFICULTY_easy.addActionListener(this);
		
		menu_DIFFICULTY_hard= new JMenuItem("Hard");
		menu_DIFFICULTY_hard.addActionListener(this);
		
		menu_DIFICULDADE.add(menu_DIFFICULTY_easy);
		menu_DIFICULDADE.add(menu_DIFFICULTY_hard);
			
		JMenu menu_INFORMACAO= new JMenu("Information");
		menu_INFO_help = new JMenuItem("Help");
		menu_INFO_help.addActionListener(this);
				
		menu_INFO_about = new JMenuItem("About");
		menu_INFO_about.addActionListener(this);
		
		menu_INFORMACAO.add(menu_INFO_help);
		menu_INFORMACAO.add(menu_INFO_help);	
		
		JMenuBar menuBar= new JMenuBar();
		
		menuBar.add(menu_GAME);
		menuBar.add(menu_DIFICULDADE);
		menuBar.add(menu_INFORMACAO);
		setJMenuBar(menuBar);
		setBackground(Color.WHITE);

	}
	
	class Terminator extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == menu_GAME_Start) {
			if(comecou){
				JOptionPane.showMessageDialog(null, "Já tem um jogo a decorrer");
			}
			else {
				
				if(login) {
					comecou=true;	
					DataPlayer Player= new DataPlayer(playerName,DIFFICULTY);
					try {
						client = new Socket("localhost",6666);
						ObjectOutputStream saida = new ObjectOutputStream(client.getOutputStream());		
						saida.writeObject("Start");
						saida.writeObject(Player.getName());
						saida.writeObject(Player.getDifficulty());
						ObjectInputStream in = new ObjectInputStream(client.getInputStream());
						List<String> TodasImagens = (List<String>)in.readObject();
						if(TodasImagens.size() == Player.getDifficulty()*Player.getDifficulty()) {
							new Board(Player, this, client, TodasImagens);
						}
					} catch (IOException | ClassNotFoundException  e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
						comecou = false;
					}
				}else {
					JOptionPane.showMessageDialog(null, "Please login first.", "Memory game", JOptionPane.INFORMATION_MESSAGE);
				}
				
							
			}			
		}
	
		if(e.getSource() == menu_GAME_Exit) {
			
			if (client!=null) {
				try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			dispose();
			game_obj.init();
			comecou = false;
			System.exit(0);	
		}
			
		if(e.getSource() == menu_GAME_Disconnect) {		
			if(comecou) {
				JOptionPane.showMessageDialog(null, "You are now disconnected from the server.", "Memory game", JOptionPane.INFORMATION_MESSAGE);
				if (client!=null) {
					try {
						client.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				dispose();
				game_obj.init();
				comecou = false;
			}else {
				JOptionPane.showMessageDialog(null, "To disconnect you must first be logged in to the server.", "Memory game", JOptionPane.INFORMATION_MESSAGE);
			}		
		}
		
		if(e.getSource() == menu_DIFFICULTY_easy) {
			if(comecou) {
				JOptionPane.showMessageDialog(null, "To change difficulty, please leave the current session and login again.", "Memory game", JOptionPane.INFORMATION_MESSAGE);
			}else {
				DIFFICULTY=2;		
				JOptionPane.showMessageDialog(null, DIFFICULTY + "x" + DIFFICULTY );	
			}		
		}
		if(e.getSource() == menu_DIFFICULTY_hard) {
			if(comecou) {
				JOptionPane.showMessageDialog(null, "To change difficulty, please leave the current session and login again.", "Memory game", JOptionPane.INFORMATION_MESSAGE);
			}else {
				DIFFICULTY=4;
				JOptionPane.showMessageDialog(null, DIFFICULTY + "x" + DIFFICULTY);
			}			
		}

		if(e.getSource() == menu_INFO_help) {
			JOptionPane.showMessageDialog(null, "The goal is to find a matching pair with the least number of attempts.", "Memory game", JOptionPane.INFORMATION_MESSAGE);
		}
		
		if(e.getSource() == menu_INFO_about) {
			JOptionPane.showMessageDialog(null, "This game was developed as hobbie :)", "Memory game", JOptionPane.INFORMATION_MESSAGE);
		}

		if(e.getSource() == menu_GAME_Ranking) {	
			if(comecou == true) {
				try {
					client = new Socket("localhost",6666);
					ObjectOutputStream saida = new ObjectOutputStream(client.getOutputStream());
					saida.writeObject("Ranking");		
					ObjectInputStream in = new ObjectInputStream(client.getInputStream());
					StringBuilder SB = (StringBuilder)in.readObject();
					JOptionPane.showMessageDialog(null, SB.toString());
					client.close();		
				} catch (IOException | ClassNotFoundException  e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}else {
				JOptionPane.showMessageDialog(null, "Must be logged in to view ranking.", "Memory game", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		if(e.getSource() == menu_GAME_Login) {
			 playerName = JOptionPane.showInputDialog(null, "Insert the player name: ", "Memory game", JOptionPane.INFORMATION_MESSAGE);
			 while(playerName.isBlank()) {
				playerName = JOptionPane.showInputDialog(null, "Please insert a valid name:", "Memory game", JOptionPane.INFORMATION_MESSAGE);
			}			 
			 setTitle("Memory game: \t" + playerName);
			 login = true;
			 return;
		}

	}
}

