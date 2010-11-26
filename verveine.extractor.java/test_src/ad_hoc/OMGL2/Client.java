package OMGL2;
import java.io.BufferedReader;
import java.io.IOException;




public class Client {

	private String num;
	private String nom;
	
	public Client(String num, String nom) {
		this.setNum(num);
		this.setNom(nom);
	}

	public static Client lire(BufferedReader in) throws ReadException {
		String nom = "";
		String num = "";
		char c = ' ';
		
		try {
			for (int i=0; (i<5) && in.ready(); i++) {
				c = (char)in.read();
				num += c;
			}
			while ( in.ready() && (c != '.')) {
				c = (char)in.read();
				if (c != '.') {
					nom += c;
				}
			}
		} catch (IOException e) {
			throw new ReadException();
			return null;
		}
		
		return new Client(num, nom);
		
	}

	public String toString() {
		return "client: ["+getNum()+"] "+getNom();
	}
	
	public void setNum(String num) {
		this.num = num;
	}

	public String getNum() {
		return num;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNom() {
		return nom;
	}
}
