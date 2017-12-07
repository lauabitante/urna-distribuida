package urna;

public class Distribuidor {
    public static void main(String[] args) {
    	DistribuidorThread distThread = new DistribuidorThread();
        Thread threadDist = new Thread(distThread);
        threadDist.start();
    }
}