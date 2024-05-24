import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class App {

    public static void main(String[] args) throws Exception {
        lerArquivoYaml();
        SimuladorFilas simlador = new SimuladorFilas();
        simlador.iniciaSimulacao();
    }

    public static void escrever(String dados) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter("numeros.csv", true));
        bw.write(dados);
	    bw.newLine();
        bw.close();
    }

    public static void lerArquivoYaml() throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        ConfigSimulador configSimulador = mapper.readValue(new File("configSimulador.yaml"), ConfigSimulador.class);

        //TODO setar campos em suas respectivas classes que serão usadas na simulação
    }
}