package urna;

public class Comissario {
    public static void main(String[] args) {
        Thread threadComissario = new Thread(new ComissarioThread());
        threadComissario.start();
    }
}
