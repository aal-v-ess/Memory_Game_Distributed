package Server;


import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class InputOutputData {

	InputOutputData(){
		
	}
	
	
	public boolean WriteData(List<DataPlayer> _DataPlayers, String _NomeFicheiro) throws FileNotFoundException, IOException {
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(_NomeFicheiro+".dat"));
		
		out.writeObject(_DataPlayers);
		out.close();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataPlayer> ReadData(String _NomeFicheiro) throws FileNotFoundException, IOException, ClassNotFoundException {
		List<DataPlayer> _DataPlayers = new ArrayList<DataPlayer>();
		FileInputStream fis = new FileInputStream(_NomeFicheiro+".dat");
		if(fis.getChannel().size()!=0) {
		    ObjectInputStream in = new ObjectInputStream(fis);
		    _DataPlayers=(List<DataPlayer>)in.readObject();
			boolean cont = true;
			try{
				
				
			   while(cont){
				   if(fis.available() != 0){
					   DataPlayer obj = new DataPlayer(); 
					   obj=(DataPlayer) in.readObject();    
				         _DataPlayers.add(obj);
				        }
				        else
				        	cont = false;
			   }        
			}catch(Exception e){
			   
			}
		    fis.close();
			return _DataPlayers;
		}
		fis.close();
		return null;
		
	}
}
