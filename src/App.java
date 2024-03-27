import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class App {
    static long a = 65;
    static long c = 88;
    static long M = 982397845;
    // semente
    static long semente = 7;

    static int servidores = 2;
    static int capacidade = 4;

    static long[] acumulador = new long[capacidade + 1];

    static ArrayList<String> fila = new ArrayList<String>();
    static ArrayList<Evento> escalonador = new ArrayList<Evento>();

    static long tempoGlobal;
    static int perda = 0;

    static double[] tempoChegadas= new double[2];
    static double[] tempoSaidas = new double[2];

    static int count = 100000;

    public static void main(String[] args) throws Exception {
        // for (int i = 0; i < 1000; i++) {
        //     semente = gerarNumPseudoaleatorio();         
        //     System.out.println(semente + ";" + normalizar());
        //     escrever(semente + ";" + normalizar());
        // }
        tempoChegadas[0] = 2.0;
        tempoChegadas[1] = 5.0;

        tempoSaidas[0] = 3.0;
        tempoSaidas[1] = 5.0;

        tempoGlobal = 0;

        Evento primeiro = new Evento(2, "C");
        escalonador.add(primeiro);
        
        while (count > 0) {
            Evento ev = getNextEvento();
            if (ev.tipo == "C") {
                chegada(ev);
            } else {
                saida(ev);
            }
            count--;
        }
    }

    public static class Evento {
        public long tempo;
        public String tipo;

        public Evento (long tempo, String tipo) {
            this.tempo = tempo;
            this.tipo = tipo;
        }
    }

    public static Evento getNextEvento () {
        Evento proximoEvento = escalonador.get(0);
        for (Evento ev : escalonador) {
            if (proximoEvento.tempo > ev.tempo) {
                proximoEvento = ev;
            }            
        }
        return proximoEvento;
    }

    public static void chegada(Evento ev) {
        acumulaTempo();
        if (fila.size() < capacidade) {
            fila.add("x");

            if (fila.size() <= servidores) {
                escalonador.add(calcularTempoAgendado("S"));
            }
        } else {
            perda ++;
            escalonador.add(calcularTempoAgendado("C"));
        }
    }

    public static void saida(Evento ev) {
        acumulaTempo();
        fila.remove(0);
        if (fila.size() >= servidores) {
            escalonador.add(calcularTempoAgendado("S"));
        }
    }

    public static void acumulaTempo () {
        long deltaT = System.currentTimeMillis() - tempoGlobal;
        tempoGlobal = deltaT + tempoGlobal;
        acumulador[fila.size()] = acumulador[fila.size()] + deltaT;
    }

    public static Evento calcularTempoAgendado(String tipoEvento) {
        gerarNumPseudoaleatorio();
        var numPseudoaleatorio = normalizar();
        long tempo;
        // Chegada
        if ("C".equals(tipoEvento)){
            tempo = (long) (tempoChegadas[0] + ((tempoChegadas[1] - tempoChegadas[0]) * numPseudoaleatorio));
        } else { // Saida
            tempo = (long) (tempoSaidas[0] + ((tempoSaidas[1] - tempoSaidas[0]) * numPseudoaleatorio));
        }
        Evento ev = new Evento(tempo, tipoEvento);

        return ev;
    }

    public static long gerarNumPseudoaleatorio() {
        semente = ((a * semente + c) % M);
        return semente;
    }

    public static double normalizar() {
        return (1.0 * semente)/M;
    }

    public static void escrever(String dados) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter("numeros.csv", true));
        bw.write(dados);
	    bw.newLine();
        bw.close();
    }
}
