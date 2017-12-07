package urna;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

class DistribuidorSocketConnection extends Thread {
	// Input e Output do Distribuidor
    InputStream input;
    PrintWriter output;
    Socket socket;
    
    // Input e Output do Comissario
    DataInputStream comissarioInput;
    DataOutputStream comissarioOutput;
    Socket comissario;

    public DistribuidorSocketConnection(Socket socket) {
        super("Distribuidor");
        this.socket = socket;
        try {
            input = socket.getInputStream();
            output = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
            
        	comissario = new Socket("localhost", 8001);
            System.out.println("Distribuidor: Conectado ao Comissario");
            
            comissarioInput = new DataInputStream(comissario.getInputStream());
            comissarioOutput = new DataOutputStream(comissario.getOutputStream());
            
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
                    String mensagem = new String(array).trim();
                    if (mensagem.equals("CONECTAR")) {
                    	conectar();
                    } else {
                    	validaTitulo(mensagem);
                    }  
                    output.flush();
                } while (input.available() != 0);
            }
        } catch (IOException e) {
        	e.printStackTrace();
    	}
    }

    // Valida o título de eleitor com o Comissário.
	private void validaTitulo(String mensagem) throws UnknownHostException, IOException {
		String[] dados = mensagem.split("\\|"); // Cria uma array a partir da string "VALIDA|titulo" usando o pipe de separador
		String comando = dados[0]; //Pega a mensagem VALIDAR
		String dado = dados[1]; // Pega o dado que é o titulo do eleitor
		if (comando.equals("VALIDAR")) {
			String validacaoString = new String("NO".getBytes(), Charset.forName("UTF-8"));
		    if(validar(dado).equals("OK")) {
		    	validacaoString = new String("OK".getBytes(), Charset.forName("UTF-8"));
		    }
		    output.write(validacaoString);
		}
	}

    // Conecta no cliente, retornando o numero da sessao e a zona.
	private void conectar() throws UnknownHostException, IOException {
		String sendString = new String("AGUARDE".getBytes(), Charset.forName("UTF-8"));
		output.write(sendString);
		output.flush();
		String sessao = this.getSession();
		output.write(sessao);
	}
    
    // Pede a sessão e a zona para o Comissario
    public String getSession() throws UnknownHostException, IOException {        
        System.out.println("Distribuidor: Requisitando sessão");
        comissarioOutput.writeUTF("SESSAO");
        String sessao = comissarioInput.readUTF();
        System.out.println("Distribuidor: Zona E sessão recebidas: "+sessao);
        return sessao;
    }
    
    // Valida o titulo de eleitor com o Comissário.
    public String validar(String titulo) throws UnknownHostException, IOException  {
        System.out.println("Distribuidor: Requisitando verificação de titulo: " + titulo);
        comissarioOutput.writeUTF("VALIDAR|"+titulo);
        String retorno =  comissarioInput.readUTF();
        System.out.println("Distribuidor: Titulo Validado: "+retorno);
        return retorno;
    }
}
