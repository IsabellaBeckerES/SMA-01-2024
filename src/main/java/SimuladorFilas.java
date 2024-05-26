import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;


public class SimuladorFilas {
    private double tempoGlobal;
    private GeradorNumPseudoaleatorio geradorNumPseudoaleatorio = new GeradorNumPseudoaleatorio();
    private ArrayList<Evento> escalonador = new ArrayList<Evento>();
    private ArrayList<Fila> filas = new ArrayList<>();
    private ArrayList<ConexaoEntreFilas> conexoes = new ArrayList<>();
    private Queue<Double> listNumPseudoaleatorios = new LinkedList<>();

    private double tempoPrimeiroEvento;
    private int count; // quantidade de num pseudoaleatorios

    public void iniciaSimulacao(ConfigSimulador configSimulador) {
        tempoPrimeiroEvento = configSimulador.getChegada();
        // seta quantidade de num peseudoaleatorios que serão usados
        count = configSimulador.getQtdNumPseudoAleatorios();
        // cria lista de num peseudoaleatorios
        geradorNumPseudoaleatorio.semente = configSimulador.getSemente();
        while (count > 0) {
            listNumPseudoaleatorios.add(geradorNumPseudoaleatorio.gerarNumPseudoaleatorio());
            count--;
        }

        iniciaFilas(configSimulador);

        // cria primeiro evento e adiciona no escalonador
        Evento primeiro = new Evento(tempoPrimeiroEvento, TipoEvento.CHEGADA, configSimulador.getIdFilaChegada());
        escalonador.add(primeiro);

        int countSimulacao = 0;

        //try {
        //simulação ocorre até finalizar os numeros pseudoaleatorios
            while (listNumPseudoaleatorios.size() > 0) {
                System.out.println("\nContador: " + countSimulacao);
                System.out.println(escalonador.toString());
                for (Fila fila: filas) {
                    System.out.println(fila.getIdFila() + ": " + fila.getElementos().toString());
                }
                countSimulacao++;

                Evento ev = getNextEvento();
                if (ev.getTipo() == TipoEvento.CHEGADA) {
                    chegada(ev);
                } else if (ev.getTipo() == TipoEvento.SAIDA) {
                    saida(ev);
                } else if (ev.getTipo() == TipoEvento.PASSAGEM) {
                    passagem(ev);
                }
            }

            printSimulacao();
        /*} catch (Exception ex) {
            System.out.println("Simulação finalizada devido a exceção: ");
            System.out.println(ex);

            printSimulacao();
        }*/
    }

    public void printSimulacao() {
        System.out.println("******************************************");
        System.out.println("RESULTADO SIMULACAO");
        System.out.println("Tempo Global: " + tempoGlobal + "\n");

        System.out.println("Escalonador: ");
        System.out.println(escalonador.toString());

        System.out.println();

        System.out.println("******************************************");

        imprimirEstatisticasDeCadaFila();
    }

    private void iniciaFilas(ConfigSimulador configSimulador) {

        for (ConfigFila novaFila : configSimulador.getFilas()) {
            Fila fila;

            if (novaFila.getTempoChegadaMin() == -1 || novaFila.getTempoChegadaMax() == -1  ){
                fila = new Fila(novaFila.getServidores(), novaFila.getCapacidade(), novaFila.getIdFila(), novaFila.getTempoAtendimentoMin(), novaFila.getTempoAtendimentoMax());
            } else {
                fila = new Fila(novaFila.getServidores(), novaFila.getCapacidade(), novaFila.getIdFila(), novaFila.getTempoChegadaMin(), novaFila.getTempoChegadaMax(), novaFila.getTempoAtendimentoMin(), novaFila.getTempoAtendimentoMax());
            }
            // se houver probabilidade de saida seta o campo
            if (novaFila.getProbabilidadeSaida() != -1) {
                fila.setProbabilidadeSaida(novaFila.getProbabilidadeSaida());
            }
            filas.add(fila);
        }

        for (ConexaoEntreFilas novaConexao : configSimulador.getConexoes()) {
            conexoes.add(novaConexao);
        }
    }

    public void chegada(Evento ev) {
        Fila filaEv = filas.get(ev.getIdFilaOrigem());

        acumulaTempo(ev);
        
        if (filaEv.cabeMaisUmElemento()) {
            System.out.println("Fila " + filaEv.getIdFila() + " cabe mais um elemento.");
            filaEv.push("x");

            if (filaEv.getStatus() <= filaEv.getServidores()) {
                int idDestino = getDestino(filaEv.getIdFila());
                if (idDestino >= 0) {
                    Evento aux = new Evento(0, TipoEvento.PASSAGEM, ev.getIdFilaOrigem());
                    aux.setIdFilaDestino(idDestino);
                    agendarEvento(aux);
                } else {
                    Evento aux = new Evento(0, TipoEvento.SAIDA, ev.getIdFilaOrigem());
                    aux.setIdFilaDestino(-1);
                    agendarEvento(aux);
                }
            }

        } else {
            filaEv.addPerda();
        }

        Evento aux = new Evento(0, TipoEvento.CHEGADA, ev.getIdFilaOrigem());
        aux.setIdFilaDestino(ev.getIdFilaOrigem());
        agendarEvento(aux);
    }

    private void saida(Evento ev) {
        Fila filaEv = filas.get(ev.getIdFilaOrigem());

        acumulaTempo(ev);
        filaEv.pop();

        if (filaEv.getStatus() >= filaEv.getServidores()) {
            int idDestino = getDestino(filaEv.getIdFila());
            if (idDestino >= 0) {
                Evento aux = new Evento(0, TipoEvento.PASSAGEM, ev.getIdFilaOrigem());
                aux.setIdFilaDestino(idDestino);
                agendarEvento(aux);
            } else {
                Evento aux = new Evento(0, TipoEvento.SAIDA, ev.getIdFilaOrigem());
                aux.setIdFilaDestino(-1);
                agendarEvento(aux);
            }
        }
    }

    private void agendarEvento(Evento ev) {
        escalonador.add(calcularTempoAgendado(ev));
    }

    private void passagem(Evento ev) {
        Fila filaEvDestino = filas.get(ev.getIdFilaDestino());
        Fila filaEvOrigem = filas.get(ev.getIdFilaOrigem());

        acumulaTempo(ev);

        // trata saida da fila origem
        filaEvOrigem.getElementos().remove(0);
        if (filaEvOrigem.getStatus() >= filaEvOrigem.getServidores()) {
            int idDestino = getDestino(filaEvOrigem.getIdFila());
            
            if (idDestino >= 0) {
                Evento aux = new Evento(0, TipoEvento.PASSAGEM, ev.getIdFilaOrigem());
                aux.setIdFilaDestino(idDestino);
                agendarEvento(aux);
            } else {
                Evento aux = new Evento(0, TipoEvento.SAIDA, ev.getIdFilaOrigem());
                aux.setIdFilaDestino(-1);
                agendarEvento(aux);
            }
        }

        // trata entrada da fila destino
        if (filaEvDestino.cabeMaisUmElemento()) {

            filaEvDestino.push("x");

            if (filaEvDestino.getStatus() <= filaEvDestino.getServidores()) {
                // verifica se deve agendar uma passagem ou saida
                int idDestino = getDestino(filaEvDestino.getIdFila());
                
                if (idDestino >= 0) {
                    Evento aux = new Evento(0, TipoEvento.PASSAGEM, ev.getIdFilaDestino());
                    aux.setIdFilaDestino(idDestino);
                    agendarEvento(aux);
                } else {
                    Evento aux = new Evento(0, TipoEvento.SAIDA, ev.getIdFilaDestino());
                    aux.setIdFilaDestino(-1);
                    agendarEvento(aux);
                }
            }
        } else {
            filaEvDestino.addPerda();
        }
    }

    public void acumulaTempo(Evento ev) {        
        double delta = ev.getTempo() - tempoGlobal;
        for (Fila fila: filas) {
            int status = fila.getStatus();
            fila.getAcumulador()[status] = fila.getAcumulador()[status] + delta;
        }
        tempoGlobal = ev.getTempo();
    }

    public Evento calcularTempoAgendado(Evento ev) {
        Fila filaEv = filas.get(ev.getIdFilaOrigem());
        Double numPseudoaleatorio = geradorNumPseudoaleatorio.gerarNumPseudoaleatorio();
        double tempo = 0.0;
        
        switch (ev.getTipo()) {
            case CHEGADA:
                tempo = (double) (filaEv.getTempoChegadaMin() + ((filaEv.getTempoChegadaMax() - filaEv.getTempoChegadaMin()) * numPseudoaleatorio));
                break;
            case PASSAGEM:
                tempo = (double) (filaEv.getTempoAtendimentoMin() + ((filaEv.getTempoAtendimentoMax() - filaEv.getTempoAtendimentoMin()) * numPseudoaleatorio));
                break;
            case SAIDA:
                tempo = (double) (filaEv.getTempoAtendimentoMin() + ((filaEv.getTempoAtendimentoMax() - filaEv.getTempoAtendimentoMin()) * numPseudoaleatorio));
                break;
            default:
                break;
        }

        ev.setTempo(tempoGlobal + tempo);
        //Evento evento = new Evento(tempo, ev.getTipo(), ev.getIdFilaOrigem());
        /*if (TipoEvento.PASSAGEM.equals(tipoEvento)) {
            evento.setIdFilaDestino(ev.getIdFilaDestino());
        } else if (TipoEvento.SAIDA.equals(tipoEvento)) {
            // saida não tem fila de destino
            evento.setIdFilaDestino(-1);
        }*/

        return ev;
    }

    public Evento getNextEvento () {
       Evento proximoEvento = escalonador.get(0);
        int indiceDoProximoEvento = 0;
        for (int indice = 0; indice < escalonador.size(); indice++)
            if (proximoEvento.getTempo() > escalonador.get(indice).getTempo()) {
                proximoEvento = escalonador.get(indice);
                indiceDoProximoEvento = indice;
            }
        escalonador.remove(indiceDoProximoEvento);
        return proximoEvento;
    }

    // retorna id da fila destino
    private int getDestino(int id) {
        ArrayList<ConexaoEntreFilas> listAux = new ArrayList<>();        
        int filaDestinoSorteada = -1;
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
            System.out.println(rand + ", " + sum);
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
            System.out.println("\t" + "Dados por estado");
            for (int estado = 0; estado < fila.getAcumulador().length; estado++) {
                System.out.println("\t- " + "Estado " + estado);
                System.out.println("\t- - " + "Probabilidade: " + (fila.probabilidadeDoEstado(estado, tempoGlobal) * 100) + "%");
                System.out.println("\t- - " + "População: " + fila.populacaoDoEstado(estado, tempoGlobal) + " clientes");
                System.out.println("\t- - " + "Vazão: " + fila.vazaoDoEstadoPorHora(estado, tempoGlobal) + " clientes por hora");
                System.out.println("\t- - " + "Utilização: " + (fila.utilizacaoDoEstado(estado, tempoGlobal) * 100) + "%");
            }
            System.out.println("\t" + "Totais");
            System.out.println("\t- " + "População: " + fila.populacao(tempoGlobal) + " clientes");
            System.out.println("\t- " + "Vazão: " + fila.vazaoPorHora(tempoGlobal) + " clientes por hora");
            System.out.println("\t- " + "Utilização: " + (fila.utilizacao(tempoGlobal) * 100) + "%");
            System.out.println("\t- " + "Tempo de resposta: " + fila.tempoDeRespostaEmHoras(tempoGlobal) + " hora(s)");
        }
    }

}
