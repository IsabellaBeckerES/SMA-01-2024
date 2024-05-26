import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class App {

    public static void main(String[] args) throws Exception {

        SimuladorFilas simulador = new SimuladorFilas();
        ConfigSimulador configSimulador = lerArquivoYaml();
        simulador.iniciaSimulacao(configSimulador);
    }

    public static void escrever(String dados) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter("numeros.csv", true));
        bw.write(dados);
	    bw.newLine();
        bw.close();
    }

    public static ConfigSimulador lerArquivoYaml() throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File("configSimulador.yml"), ConfigSimulador.class);
    }
}