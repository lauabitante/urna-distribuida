package urna;

public class Painel {
	public static void main(String[] args) {
	    Thread threadPainel = new Thread(new PainelThread());
	    threadPainel.start();
	}
}
