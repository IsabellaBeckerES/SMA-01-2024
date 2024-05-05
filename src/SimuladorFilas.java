import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class SimuladorFilas {
    private double tempoGlobal;
    private GeradorNumPseudoaleatorio geradorNumPseudoaleatorio = new GeradorNumPseudoaleatorio();
    private ArrayList<Evento> escalonador = new ArrayList<Evento>();
    private ArrayList<Fila> filas = new ArrayList<>();
    private ArrayList<ConexaoEntreFilas> conexoes = new ArrayList<>();
    private Queue<Double> listNumPseudoaleatorios = new LinkedList<>();

    private double tempoPrimeiroEvento = 2;
    private int count = 100000; // quantidade de num pseudoaleatorios

    public void iniciaSimulacao() {
        while (count > listNumPseudoaleatorios.size()) {
            listNumPseudoaleatorios.add(geradorNumPseudoaleatorio.gerarNumPseudoaleatorio());
            count--;
        }
        
        iniciaFilas();

        Evento primeiro = new Evento(tempoPrimeiroEvento, TipoEvento.CHEGADA, 0);
        escalonador.add(primeiro);
        
        int countSimulacao = 0;

        try {
            while (listNumPseudoaleatorios.size() > 0) {
                System.out.println("Contador: " + countSimulacao);
                System.out.println( escalonador.toString());
                countSimulacao++;

                if(countSimulacao == 28) {
                    System.out.println("PARA AQUI");
                }

                Evento ev = getNextEvento();
                // remove do escalonador pois ação foi executada
                escalonador.remove(ev);
                if (ev.getTipo() == TipoEvento.CHEGADA) {
                    chegada(ev);
                } else if (ev.getTipo() == TipoEvento.SAIDA) {
                    saida(ev);
                } else if (ev.getTipo() == TipoEvento.PASSAGEM) {
                    passagem(ev);
                }
            }
        } catch (Exception ex) {
            System.out.println("Simulação finalizada devido a exceção: ");
            System.out.println(ex);

            printSimulacao();
        }
    }

    public void printSimulacao() {
        System.out.println("******************************************");
        System.out.println("RESULTADO SIMULACAO");
        System.out.println("Tempo Global: " + tempoGlobal + "\n");

        System.out.println("Escalonador: ");
        System.out.println(escalonador.toString());

        for(var fila: filas){
            System.out.println("Fila: " + fila.getIdFila());
            System.out.println();
        }
        System.out.println();

        System.out.println("******************************************");


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
        ConexaoEntreFilas conexao23 = new ConexaoEntreFilas(fila2.getIdFila(), fila3.getIdFila(), 0.5);
        conexoes.add(conexao23);
        ConexaoEntreFilas conexao32 = new ConexaoEntreFilas(fila3.getIdFila(), fila2.getIdFila(), 0.7);
        conexoes.add(conexao32);

        // conexaoes de saida
        // -1 indica saida
        ConexaoEntreFilas conexaoSaida2 = new ConexaoEntreFilas(fila2.getIdFila(), -1, 0.2);
        conexoes.add(conexaoSaida2);
        ConexaoEntreFilas conexaoSaida3 = new ConexaoEntreFilas(fila3.getIdFila(), -1, 0.3);
        conexoes.add(conexaoSaida3);
    }

    public void chegada(Evento ev) {
        acumulaTempo(ev);
        var filaEv = filas.get(ev.getIdFila());
        // -1 significa que a fila tem capacidade infinita
        if (filaEv.getStatus() < filaEv.getCapacidade() || filaEv.getCapacidade() == -1 ) {
            // adiciona na fila
            filaEv.getElementos().add("x");

            if (filaEv.getStatus() <= filaEv.getServidores()) {
                // agenda saida ou passagem
                var idDestino = getDestino(filaEv.getIdFila());
                if (idDestino >= 0) {
                    // seta fila de destino para quando realizar a passagem
                    ev.setIdFilaDestino(idDestino);
                    // agenda passagem
                    escalonador.add(calcularTempoAgendado(ev, TipoEvento.PASSAGEM));
                } else {
                    escalonador.add(calcularTempoAgendado(ev, TipoEvento.SAIDA));
                }
            }
            escalonador.add(calcularTempoAgendado(ev, TipoEvento.CHEGADA));
        } else {
            filaEv.addPerda();
        }
    }

    private void saida(Evento ev) {
        acumulaTempo(ev);
        var filaEv = filas.get(ev.getIdFila());
        filaEv.getElementos().remove(0);
        if (filaEv.getStatus() <= filaEv.getServidores() && !filaEv.getElementos().isEmpty()) {
            var idDestino = getDestino(filaEv.getIdFila());
            if (idDestino >= 0) {
                // seta fila de destino para quando realizar a passagem
                ev.setIdFilaDestino(idDestino);
                ev.setIdFila(filaEv.getIdFila());
                // agenda passagem
                escalonador.add(calcularTempoAgendado(ev, TipoEvento.PASSAGEM));
            } else {
                ev.setIdFila(filaEv.getIdFila());
                escalonador.add(calcularTempoAgendado(ev, TipoEvento.SAIDA));
            }
        }
    }

    private void passagem(Evento ev) {
        acumulaTempo(ev);
        var filaEvDestino = filas.get(ev.getIdFilaDestino());
        var filaEvOrigem = filas.get(ev.getIdFila());

        // trata saida da fila origem
        filaEvOrigem.getElementos().remove(0);
        if (filaEvOrigem.getStatus() >= filaEvOrigem.getServidores() && !filaEvOrigem.getElementos().isEmpty()) {
            var idDestino = getDestino(filaEvOrigem.getIdFila());
            if (idDestino >= 0) {
                // seta fila de destino para quando realizar a passagem
                ev.setIdFilaDestino(idDestino);
                ev.setIdFila(filaEvOrigem.getIdFila());
                // agenda passagem
                escalonador.add(calcularTempoAgendado(ev, TipoEvento.PASSAGEM));
            } else {
                ev.setIdFila(filaEvOrigem.getIdFila());
                escalonador.add(calcularTempoAgendado(ev, TipoEvento.SAIDA));
            }
        }

        // trata entrada da fila destino
        if (filaEvDestino.getStatus() < filaEvDestino.getCapacidade() || filaEvDestino.getCapacidade() == -1 ) {

            filaEvDestino.getElementos().add("x");

            if (filaEvDestino.getStatus() <= filaEvDestino.getServidores()) {
                // verifica se deve agendar uma passagem ou saida
                var idDestino = getDestino(filaEvDestino.getIdFila());
                if (idDestino >= 0) {
                    // seta fila de destino para quando realizar a passagem
                    ev.setIdFilaDestino(idDestino);
                    ev.setIdFila(filaEvDestino.getIdFila());
                    // agenda passagem
                    escalonador.add(calcularTempoAgendado(ev, TipoEvento.PASSAGEM));
                } else {
                    ev.setIdFila(filaEvDestino.getIdFila());
                    escalonador.add(calcularTempoAgendado(ev, TipoEvento.SAIDA));
                }
            }
        } else {
            filaEvDestino.addPerda();
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
        var numPseudoaleatorio = listNumPseudoaleatorios.poll();
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
        } else if (tipoEvento == TipoEvento.PASSAGEM) { // Saida
            //System.out.println(filaEv.getTempoAtendimentoMin() + " * " +  filaEv.getTempoAtendimentoMax() + " * " + filaEv.getTempoAtendimentoMin() + " * " + numPseudoaleatorio);
            tempo = (double) (filaEv.getTempoAtendimentoMin()+ ((filaEv.getTempoAtendimentoMax() - filaEv.getTempoAtendimentoMin()) * numPseudoaleatorio));
            //System.out.println(tempo);
        }
        tempo = tempoGlobal + tempo;
        Evento evento = new Evento(tempo, tipoEvento, ev.getIdFila());
        if (TipoEvento.PASSAGEM.equals(tipoEvento)) {
            evento.setIdFilaDestino(ev.getIdFilaDestino());
        } else if (TipoEvento.SAIDA.equals(tipoEvento)) {
            // saida não tem fila de destino
            evento.setIdFilaDestino(-1);
        }

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

    // retorna id da fila destino
    private int getDestino(int id) {
        ArrayList<ConexaoEntreFilas> listAux = new ArrayList<>();        
        int filaDestinoSorteada = - 1;
        double rand = listNumPseudoaleatorios.poll();
        double sum = 0.0;

        // filtra pela fila de origem
        for (var conexao : conexoes) {
            if (conexao.getIdOrigem() == id) {
                listAux.add(conexao);
            }
        }
        // ordena pela menor probabilidade
        Collections.sort(listAux, Comparator.comparing(ConexaoEntreFilas::getProbabilidade));

        for (var itemList : listAux) {
            sum += itemList.getProbabilidade();
            if (rand < sum) {
                filaDestinoSorteada = itemList.getIdDestino();
                return filaDestinoSorteada;
            }
        }

        return -1;
    }

    public void imprimirEstatisticasDeCadaFila() {
        for (Fila fila: filas) {
            System.out.println("FILA " + fila.getIdFila());
            System.out.println("\t" + "Probabilidades");
            for (int estado = 0; estado < fila.getAcumulador().length; estado++)
                System.out.println("\t-" + "Estado " + estado + ": " + fila.probabilidadeDoEstado(estado, tempoGlobal));
            System.out.println("\t" + "População: " + fila.populacao(tempoGlobal));
            System.out.println("\t" + "Vazão: " + fila.vazao(tempoGlobal));
            System.out.println("\t" + "Utilização: " + fila.utilizacao());
            System.out.println("\t" + "Tempo de resposta: " + fila.tempoDeResposta(tempoGlobal));
        }
    }

}
