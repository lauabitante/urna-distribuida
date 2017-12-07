package urna;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

class ComissarioSocketConnection extends Thread {
	DataInputStream input;
	DataOutputStream output;
    Socket socket;
    String sessaoEZona = "1|123"; // Numero da zona e sessao.
    ArrayList<String> titulos = new ArrayList<String>();

    // Carrega titulos disponiveis para a sessão / zona
    private void carregaTitulos() {
    	titulos.add("00000");
    	titulos.add("11111");
    	titulos.add("22222");
    	titulos.add("33333");
    }
    
    // Construção da classe com o socket
    public ComissarioSocketConnection(Socket socket) {
        super("Comissario");
        this.carregaTitulos();
        this.socket = socket;
        try {
        	input = new DataInputStream(socket.getInputStream());
        	output = new DataOutputStream(socket.getOutputStream());
            System.out.println("Comissario: Distribuidor Conectado!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                do {
                	// Leitura e tratamento das mensagens recebidas
                	byte array[] = new byte[1024]; // Cria um array para guardar os bytes do input.
                    input.read(array); // Carrega os bytes do input para o array.
                    String mensagemRecebida = new String(array).trim(); // Transforma o array de bytes em string.
                    if (mensagemRecebida.equals("SESSAO")) { // Caso o pedido seja pra carregar os dados da sessao
                    	sessao(mensagemRecebida);
                    } else {
                    	validaTitulo(mensagemRecebida);
                    	output.flush(); // Limpa o output.
                    }
                } while (input.available() != 0);
            }
        } catch (IOException e) {
        	e.printStackTrace();
    	}
    }

    // Verifica se o titulo que veio no comando, existe no array de titulos.
    // Caso exista, retorna OK para o cliente, caso contrário, retorna NO.
	private void validaTitulo(String mensagemRecebida) throws IOException {
		String comando = mensagemRecebida.split("\\|")[0];
		String titulo = mensagemRecebida.split("\\|")[1];
		if (comando.equals("VALIDAR")) {
			String mensagemParaEnviar = new String("NO".getBytes(), Charset.forName("UTF-8"));
			 if(titulos.contains(titulo)) { 
		         mensagemParaEnviar = new String("OK".getBytes(), Charset.forName("UTF-8"));
			 }
			 output.writeUTF(mensagemParaEnviar);
		}
	}
    
	// Carrega a sessão e zona.
    private void sessao(String mensagem) throws IOException {
    	System.out.println("Comissario - Mensagem recebida :" + mensagem);
        String dadosDaSessao = new String(sessaoEZona.getBytes(), Charset.forName("UTF-8"));
        output.writeUTF(dadosDaSessao);
        output.flush(); // Limpa o output.
    }
}