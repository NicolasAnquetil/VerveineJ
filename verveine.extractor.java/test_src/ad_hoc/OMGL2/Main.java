package OMGL2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;



public class Main {

	public static void main(String[] args) throws IOException {
		//mainFiles();
		mainStrings();
	}

	public static void mainFiles() throws IOException {
		File client = new File("client.dat") ;
	    File fourni =  new File("fournisseur.dat");

	    SequSynchro.parcours(new BufferedReader(new FileReader(client)), new BufferedReader(new FileReader(fourni)));
	}

	public static void mainStrings() throws IOException {
		String client = "00001Jean Valjean.00002Bernard Lermitte.";
	    String fourni =  "10001Thibeaudeau et frere.0000110002Chez Marius.00002";

	    SequSynchro.parcours(new BufferedReader(new StringReader(client)), new BufferedReader(new StringReader(fourni)));
	}
}
