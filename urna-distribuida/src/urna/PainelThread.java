package urna;

//import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JPanel;

public class PainelThread implements Runnable {
	
	private ServerSocket server;

	@Override
    public void run() {
        try {
            server = new ServerSocket(8005);
            System.out.println("Painel: Aguardando Contador");
            while (true) {
                new PainelSocketConnection(server.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class PainelSocketConnection extends Thread {

	ArrayList<String> votos = new ArrayList<String>(); //Array pra guardar os votos no formato  "titulo|voto"
	DataInputStream input;
    PrintWriter output;
    Socket socket;
    
    JFrame mainFrame = new JFrame("Painel de apuração de votos");
	JLabel votosCandidato1 = new JLabel("0", JLabel.RIGHT); 
	JLabel votosCandidato2 = new JLabel("0", JLabel.RIGHT); 
	JLabel votosCandidato3 = new JLabel("0", JLabel.RIGHT);
	
	boolean showPainel = false;
	
	public PainelSocketConnection(Socket socket) {
        super("Painel");
        showPainel();
        this.socket = socket;
        try {
        	input = new DataInputStream(socket.getInputStream());
            output = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void atualizaPainel() {
		int cand1 = 0;
		int cand2 = 0;
		int cand3 = 0;
		for (int i=0; i<votos.size(); i++) {
			String voto = votos.get(i).split("\\|")[1];
			if (voto.equals("1")) {
				cand1++;
			} else if (voto.equals("2")) {
				cand2++;
			} else if (voto.equals("3")) {
				cand3++;
			}
		}
		votosCandidato1.setText(Integer.valueOf(cand1).toString());
		votosCandidato2.setText(Integer.valueOf(cand2).toString());
		votosCandidato3.setText(Integer.valueOf(cand3).toString());
	}
	
	public void showPainel() {
		if (showPainel == false) {
			showPainel = true;
			mainFrame.setSize(240,120);
			mainFrame.setLayout(new GridLayout(5, 5));

			JLabel candidato1 = new JLabel("Candidato 1", JLabel.LEFT);
			JLabel candidato2 = new JLabel("Candidato 2",JLabel.LEFT);
			JLabel candidato3 = new JLabel("Candidato 3", JLabel.LEFT);

			mainFrame.add(candidato1);
			mainFrame.add(votosCandidato1);
			mainFrame.add(candidato2);
			mainFrame.add(votosCandidato2);
			mainFrame.add(candidato3);
			mainFrame.add(votosCandidato3);

			mainFrame.setVisible(true); 
		}
	}
	
    @Override
    public void run() {
        try {
            while (true) {
                do {
                	byte array[] = new byte[1024];
                    input.read(array);
                    String mensagem = new String(array).trim();
                    votos.add(mensagem);
                    System.out.println("PAINEL:" + mensagem);
                    atualizaPainel();
                    output.flush();
                } while (input.available() != 0);
            }
        } catch (IOException e) {
        	e.printStackTrace();
    	}
    }
	
}
