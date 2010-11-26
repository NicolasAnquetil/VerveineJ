package OMGL2;
import java.io.BufferedReader;
import java.io.IOException;


public class Fournisseur {

	private String num;
	private String nom;
	private String numCli;
	
	public Fournisseur(String num, String nom, String numCli) {
		this.setNum(num);
		this.setNom(nom);
		this.setNumCli(numCli);
	}

	public static Fournisseur lire(BufferedReader in) throws ReadException {
		String nom = "";
		String num = "";
		String numCli = "";
		char c = ' ';
		
		try {
			for (int i=0; (i<5) && in.ready(); i++) {
				c = (char)in.read();
				num += c;
			}
			while ( in.ready() && (c != '.')) {
				if (c != '.') {
					c = (char)in.read();
				}
				nom += c;
			}
			for (int i=0; (i<5) && in.ready(); i++) {
				c = (char)in.read();
				numCli += c;
			}
		} catch (IOException e) {
			throw new ReadException();
			return null;
		}
		
		return new Fournisseur(num, nom, numCli);
		
	}

	public String toString() {
		return "fournisseur: ["+getNum()+"] "+getNom()+" du client no. "+getNumCli();
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

	public void setNumCli(String numCli) {
		this.numCli = numCli;
	}

	public String getNumCli() {
		return numCli;
	}

	
	public boolean estFournisseur(Client cli) {
		return getNumCli().equals(cli.getNum());
	}

}
