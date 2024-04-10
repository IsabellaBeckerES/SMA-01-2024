import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class App {
    static double semente = 7;
    static double a = 65;
    static double c = 88;
    static double M = 982397845;

    static ArrayList<Evento> escalonador = new ArrayList<Evento>();

    static double tempoGlobal;
    static int count = 100000;

    static Fila fila1;
    static Fila fila2;

    public static void main(String[] args) throws Exception {
        /*for (int i = 0; i < 1000; i++) {
            semente = gerarNumPseudoaleatorio();         
            System.out.println(semente + ";" + normalizar());
                escrever(semente + ";" + normalizar());
        }*/

        tempoGlobal = 0;

        fila1 = new Fila(2, 3, 2, 5, 3, 5);
        fila2 = new Fila(1, 5, -1, -1, 2, 3);

        Evento primeiro = new Evento(1.5, "C");
        escalonador.add(primeiro);
        
        while (count > 0) {
            Evento ev = getNextEvento();
            if (ev.tipo.equals("C")) {
                chegada(ev);
            } else if (ev.tipo.equals("P")) {
                passagem(ev);
            } else if (ev.tipo.equals("S")) {
                saida(ev);
            }
            count--;
        }

        System.out.println("Tempo global: " + tempoGlobal);
        System.out.println("Total de perdas: " + fila1.quantidadeDePerdas() + fila2.quantidadeDePerdas());
    }

    public static class Evento {
        public double tempo;
        public String tipo;

        public Evento (double tempo, String tipo) {
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
        boolean entrouComSucesso = fila1.entrada("x");
        if (entrouComSucesso) {
            if (fila1.status() <= fila1.quantidadeDeServidores())
                escalonador.add(calcularTempoAgendado("P"));
        } else {
            escalonador.add(calcularTempoAgendado("C"));
        }
    }

    public static void passagem(Evento ev) {
        acumulaTempo();
        String elemento = fila1.saida();
        fila2.entrada(elemento);
    }

    public static void saida(Evento ev) {
        acumulaTempo();
        fila2.saida();
        if (fila2.status() >= fila2.quantidadeDeServidores())
            escalonador.add(calcularTempoAgendado("S"));
    }

    public static void acumulaTempo () {
        double deltaT = System.currentTimeMillis() - tempoGlobal;
        tempoGlobal = deltaT + tempoGlobal;
        
        fila1.acumularTempo(deltaT);
        fila2.acumularTempo(deltaT);
    }

    public static Evento calcularTempoAgendado(String tipoEvento) {
        gerarNumPseudoaleatorio();
        var numPseudoaleatorio = normalizar();
        double tempo = 0;
        
        switch (tipoEvento) {
            case "C":   // Chegada
                tempo = (double) (fila1.tempoMinimoDeChegada + ((fila1.tempoMaximoDeChegada - fila1.tempoMinimoDeChegada) * numPseudoaleatorio));
                break;
            case "P":   // Passagem
                tempo = (double) (fila1.tempoMinimoDeAtendimento + ((fila1.tempoMaximoDeAtendimento - fila1.tempoMinimoDeAtendimento) * numPseudoaleatorio));
                break;
            case "S":   // Saída
                tempo = (double) (fila2.tempoMinimoDeAtendimento + ((fila2.tempoMaximoDeAtendimento - fila2.tempoMinimoDeAtendimento) * numPseudoaleatorio));
                break;
        }
        
        Evento ev = new Evento(tempo, tipoEvento);

        return ev;
    }

    public static double gerarNumPseudoaleatorio() {
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
