import java.util.ArrayList;

public class SimuladorFilas {
    private double tempoGlobal;
    private GeradorNumPseudoaleatorio geradorNumPseudoaleatorio = new GeradorNumPseudoaleatorio();
    private ArrayList<Evento> escalonador = new ArrayList<Evento>();
    private ArrayList<Fila> filas = new ArrayList<>();
    private ArrayList<ConexaoEntreFilas> conexoes = new ArrayList<>();

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
        // TODO - capacidade infinita
        Fila fila1 = new Fila(1, filas.size(), 2, 4, 1, 2);
        filas.add(fila1);

        Fila fila2 = new Fila(2, 5, filas.size(), 4, 8);
        fila2.setProbabilidadeSaida(0.2);
        filas.add(fila2);

        Fila fila3 = new Fila(2, 10, filas.size(), 5, 15);
        fila3.setProbabilidadeSaida(0.3);
        filas.add(fila3);        

        ConexaoEntreFilas conexao12 = new ConexaoEntreFilas(fila1.getIdFila(), fila2.getIdFila(), 0.8);
        conexoes.add(conexao12);
        ConexaoEntreFilas conexao13 = new ConexaoEntreFilas(fila1.getIdFila(), fila3.getIdFila(), 0.2);
        conexoes.add(conexao13);
        ConexaoEntreFilas conexao21 = new ConexaoEntreFilas(fila2.getIdFila(), fila1.getIdFila(), 0.3);
        conexoes.add(conexao21);
        ConexaoEntreFilas conexao22 = new ConexaoEntreFilas(fila2.getIdFila(), fila2.getIdFila(), 0.5);
        conexoes.add(conexao22);
        ConexaoEntreFilas conexao33 = new ConexaoEntreFilas(fila2.getIdFila(), fila1.getIdFila(), 0.7);
        conexoes.add(conexao33);
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
                // getConexao();
                // if ()
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
        double tempo = 0.0;
        // Chegada
        if (tipoEvento == TipoEvento.CHEGADA) {
            //System.out.println(filaEv.getTempoChegadaMin() + " * " +  filaEv.getTempoChegadaMax() + " * " + filaEv.getTempoChegadaMin() + " * " + numPseudoaleatorio);
            tempo = (double) (filaEv.getTempoChegadaMin() + ((filaEv.getTempoChegadaMax() - filaEv.getTempoChegadaMin()) * numPseudoaleatorio));
            //System.out.println(tempo);
        } else if (tipoEvento == TipoEvento.SAIDA) { // Saida
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

    private ConexaoEntreFilas getConexao (int id) {
        ArrayList<ConexaoEntreFilas> listAux = new ArrayList<>();
        // busca a conexao com base na probabilidade
        return null;
    }
}
