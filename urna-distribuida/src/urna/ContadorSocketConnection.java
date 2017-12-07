package urna;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

class ContadorSocketConnection extends Thread {
	//Input e output do Contador.
    InputStream input;
    PrintWriter output;
    Socket socket;
    
    // Input do comissario.
    DataInputStream comissarioInput;
    Socket comissario;
    
    Socket painel;
    DataOutputStream painelOutput;
    
    ArrayList<String> votos = new ArrayList<String>(); //Array pra guardar os votos no formato  "titulo|voto"
    ArrayList<String> candidatos = new ArrayList<String>(); // Array de candidatos
    
    // Cria numeros de candidatos fake.
    private void carregaCandidatos() {
    	candidatos.add("1");
    	candidatos.add("2");
    	candidatos.add("3");
    	candidatos.add("4");
    	candidatos.add("5");
    }
    
    // Construção da classe com o socket
    // Aqui tambem é feita a conexão do Contador com o comissário.
    public ContadorSocketConnection(Socket socket) {
        super("Contador");
        this.socket = socket;
        this.carregaCandidatos();
        try {
            input = socket.getInputStream();
            output = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
            
        	comissario = new Socket("localhost", 8002);
        	comissarioInput = new DataInputStream(comissario.getInputStream());
            System.out.println("Contador: Conectado ao Comissario");
            
        	painel = new Socket("localhost", 8005);
        	painelOutput = new DataOutputStream(painel.getOutputStream());
            System.out.println("Contador: Conectado ao Painel");
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                do {
                	byte array[] = new byte[1024];
                    input.read(array);
                    String mensagemRecebida = new String(array).trim();
                    computarVoto(mensagemRecebida);
                    output.flush();
                } while (input.available() != 0);
            }
        } catch (IOException e) {
        	e.printStackTrace();
    	}
    }

    // Computa se o voto é valido e guarda no array de votos.
	private void computarVoto(String mensagemRecebida) throws IOException {
		String titulo = mensagemRecebida.split("\\|")[0];
		String voto = mensagemRecebida.split("\\|")[1];
		String mensagemParaEnviar = new String("NO".getBytes(), Charset.forName("UTF-8"));
		System.out.println("Contador apurando voto:" + mensagemRecebida);
		String votoParaSalvar = titulo+"|"+voto;
		if (candidatos.contains(voto)) {
			 votos.add(votoParaSalvar);  //Guarda o voto no array de votos.
			 mensagemParaEnviar = new String("OK".getBytes(), Charset.forName("UTF-8"));
			 painelOutput.writeUTF(mensagemRecebida);
		}                    
		output.write(mensagemParaEnviar);
	}
}