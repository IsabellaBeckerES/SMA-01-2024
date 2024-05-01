import java.util.ArrayList;

public class SimuladorFilas {
    private double tempoGlobal;
    private GeradorNumPseudoaleatorio geradorNumPseudoaleatorio = new GeradorNumPseudoaleatorio();
    ArrayList<Evento> escalonador = new ArrayList<Evento>();
    ArrayList<Fila> filas = new ArrayList<>();

    // TODO - fazer isso de uma forma mais mutavel
    private double tempoPrimeiroEvento = 2;
    private int count = 100; // quantidade de num pseudoaleatorios

    public void iniciaSimulacao() {
        iniciaFilas();

        Evento primeiro = new Evento(tempoPrimeiroEvento, TipoEvento.CHEGADA, filas.size()-1);
        escalonador.add(primeiro);        
        
        System.out.println(escalonador.toString());
        while (count > 0) {
            Evento ev = getNextEvento();
            if (ev.getTipo() == TipoEvento.CHEGADA) {
                chegada(ev);
            } else if (ev.getTipo() == TipoEvento.SAIDA) {
                saida(ev);
            }
            count--;
            System.out.println(escalonador.toString());
        }
    }

    private void iniciaFilas() {
        Fila fila = new Fila(1, 4, filas.size(), 1, 2, 2, 3);
        filas.add(fila);
    }

    public void chegada(Evento ev) {
        acumulaTempo(ev);
        var filaEv = filas.get(ev.getIdFila());
        if (filaEv.getStatus() < filaEv.getCapacidade()) {
            // adiciona na fila
            filaEv.getElementos().add("x");
            // remove do escalonador pois ação foi executada
            escalonador.remove(ev);

            if (filaEv.getStatus() <= filaEv.getServidores()) {
                // agenda saida
                escalonador.add(calcularTempoAgendado(ev, TipoEvento.SAIDA));
            }
            escalonador.add(calcularTempoAgendado(ev, TipoEvento.CHEGADA));
        } else {
            // loss
            
        }
    }

    public void saida(Evento ev) {
        acumulaTempo(ev);
        var filaEv = filas.get(ev.getIdFila());
        filaEv.getElementos().remove(0);
        escalonador.remove(ev);
        if (filaEv.getStatus() >= filaEv.getServidores()) {
            escalonador.add(calcularTempoAgendado(ev, TipoEvento.SAIDA));
        }
    }

    public void acumulaTempo (Evento ev) {        
        double delta = ev.getTempo() - tempoGlobal;
        for (Fila fila : filas) {
            int status = fila.getStatus();
            fila.getAcumulador()[status] = fila.getAcumulador()[status] + delta;
        }
        tempoGlobal = ev.getTempo();
    }

    public Evento calcularTempoAgendado(Evento ev, TipoEvento tipoEvento) {
        var filaEv = filas.get(ev.getIdFila());
        var numPseudoaleatorio = geradorNumPseudoaleatorio.gerarNumPseudoaleatorio();
        double tempo;
        // Chegada
        if (tipoEvento == TipoEvento.CHEGADA){
            //System.out.println(filaEv.getTempoChegadaMin() + " * " +  filaEv.getTempoChegadaMax() + " * " + filaEv.getTempoChegadaMin() + " * " + numPseudoaleatorio);
            tempo = (double) (filaEv.getTempoChegadaMin() + ((filaEv.getTempoChegadaMax() - filaEv.getTempoChegadaMin()) * numPseudoaleatorio));
            //System.out.println(tempo);
        } else { // Saida
            //System.out.println(filaEv.getTempoAtendimentoMin() + " * " +  filaEv.getTempoAtendimentoMax() + " * " + filaEv.getTempoAtendimentoMin() + " * " + numPseudoaleatorio);
            tempo = (double) (filaEv.getTempoAtendimentoMin()+ ((filaEv.getTempoAtendimentoMax() - filaEv.getTempoAtendimentoMin()) * numPseudoaleatorio));
            //System.out.println(tempo);
        }
        tempo = tempoGlobal + tempo;
        Evento evento = new Evento(tempo, tipoEvento, ev.getIdFila());

        return evento;
    }

    public Evento getNextEvento () {
        Evento proximoEvento = escalonador.get(0);
        for (Evento ev : escalonador) {
            if (proximoEvento.getTempo() > ev.getTempo()) {
                proximoEvento = ev;
            }            
        }
        return proximoEvento;
    }
}
