import java.io.BufferedWriter;
import java.io.FileWriter;

public class App {

    public static void main(String[] args) throws Exception {

        SimuladorFilas simlador = new SimuladorFilas();
        simlador.iniciaSimulacao();
    }

    public static void escrever(String dados) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter("numeros.csv", true));
        bw.write(dados);
	    bw.newLine();
        bw.close();
    }
}