package Server;

import java.io.Serializable;

public class DataPlayer implements Serializable{

	private static final long serialVersionUID = -8959750213583668358L;
	private String Name;
	private int Difficulty;
	private int Attempts;
	

	public DataPlayer(){
		
	}

	public DataPlayer(String _Name,int  _Difficulty){
		Name = _Name;
		Difficulty = _Difficulty;
		Attempts = 0;
	}
	public int getAttempts() {
		return Attempts;
	}
	public void setAttempts(int attempts) {
		Attempts = attempts;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getDifficulty() {
		return Difficulty;
	}
	public void setDifficulty(int difficulty) {
		Difficulty = difficulty;
	}

	public String getAll() {
		StringBuilder SB= new StringBuilder();
		SB.append(Name + "\t + \t" + Difficulty );
		return SB.toString();
	}
}