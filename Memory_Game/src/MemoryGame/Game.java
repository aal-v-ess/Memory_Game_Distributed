package MemoryGame;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;

public class Game {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException{
		Game app = new Game();
		app.init();
	}

	public void init() {
		Menu initial_menu = new Menu(this);
		initial_menu.setVisible(true);;
	}
	
}

