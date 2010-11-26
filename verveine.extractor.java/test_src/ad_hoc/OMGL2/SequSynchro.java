package OMGL2;
import java.io.BufferedReader;


public class SequSynchro {

	public static void parcours(BufferedReader in_cli, BufferedReader in_four) {
		Client cli;
		Fournisseur four;
		
		cli = Client.lire(in_cli);
		four = Fournisseur.lire(in_four);
		while ( (cli != null) && (four != null) ) {
			if (four.estFournisseur(cli)) {
				System.out.println(cli.toString()+" a pour fournisseur: ["+four.getNum()+"] "+four.getNom());
				cli = Client.lire(in_cli);
				four = Fournisseur.lire(in_four);
			}
			else if (cli.getNum().compareTo(four.getNumCli()) < 0) {
				cli = Client.lire(in_cli);
			}
			else {
				four = Fournisseur.lire(in_four);
			}
		}
	}
	
}
