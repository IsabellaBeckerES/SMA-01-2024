import java.util.ArrayList;
import java.util.HashMap;

public class Fila {

    private ArrayList<String> elementos;

    private int servidores;
    private int capacidade;
    private int idFila;

    private double tempoChegadaMin;
    private double tempoChegadaMax;
    private double tempoAtendimentoMin;
    private double tempoAtendimentoMax;

    private double probabilidadeSaida;

    private double[] acumulador;

    private int perdas;

    public Fila(int servidores, int capacidade, int idFila, double tempoChegadaMin, double tempoChegadaMax,
        double tempoAtendimentoMin, double tempoAtendimentoMax) {
        elementos = new ArrayList<>();
        this.idFila = idFila;
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.tempoChegadaMin= tempoChegadaMin;
        this.tempoChegadaMax= tempoChegadaMax;
        this.tempoAtendimentoMin = tempoAtendimentoMin;
        this.tempoAtendimentoMax = tempoAtendimentoMax;
        acumulador = new double[this.capacidade + 1];
        perdas = 0;
    }

    // fila com capacidade infinita
    public Fila(int servidores, int idFila, double tempoChegadaMin, double tempoChegadaMax,
        double tempoAtendimentoMin, double tempoAtendimentoMax) {
        elementos = new ArrayList<>();
        this.idFila = idFila;
        this.servidores = servidores;
        this.tempoChegadaMin= tempoChegadaMin;
        this.tempoChegadaMax= tempoChegadaMax;
        this.tempoAtendimentoMin = tempoAtendimentoMin;
        this.tempoAtendimentoMax = tempoAtendimentoMax;
        capacidade = -1;
        // TODO - implementar um método para aumentar capacidade desse array caso a fila tenha capacidade infinita.
        acumulador = new double[100];
        perdas = 0;
    }

    // fila que recebe passagem de outras filas, ou seja não são a primeira a receber
    public Fila(int servidores, int capacidade, int idFila, double tempoAtendimentoMin, double tempoAtendimentoMax) {
        elementos = new ArrayList<>();
        this.idFila = idFila;
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.tempoAtendimentoMin = tempoAtendimentoMin;
        this.tempoAtendimentoMax = tempoAtendimentoMax;
        acumulador = new double[this.capacidade + 1];
        perdas = 0;
    }

    // Atributos que irão variar ao decorrer da simulação
    public ArrayList<String> getElementos() {
        return elementos;
    }

    public double[] getAcumulador() {
        return acumulador;
    }

    // Atributos que não irão variar após a inicialização
    public int getServidores() {
        return servidores;
    }

    public void setServidores(int servidores) {
        this.servidores = servidores;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public int getStatus() {
        return elementos.size();
    }
    
    public int getIdFila() {
        return idFila;
    }

    public double getTempoAtendimentoMin() {
        return tempoAtendimentoMin;
    }

    public double getTempoAtendimentoMax() {
        return tempoAtendimentoMax;
    }

    public double getTempoChegadaMin() {
        return tempoChegadaMin;
    }

    public double getTempoChegadaMax() {
        return tempoChegadaMax;
    }

    public double getProbabilidadeSaida() {
        return probabilidadeSaida;
    }

    public void setProbabilidadeSaida(double probabilidadeSaida) {
        this.probabilidadeSaida = probabilidadeSaida;
    }

    public void addPerda() {
        perdas++;
    }

    public int getPerdas() {
        return perdas;
    }

    public HashMap<Integer, Double> probabilidadesDeCadaEstado(double tempoGlobal) {
        HashMap<Integer, Double> probabilidades = new HashMap<>();
        for (int estado = 0; estado < getAcumulador().length; estado++)
            probabilidades.put(estado, probabilidadeDoEstado(estado, tempoGlobal));
        return probabilidades;
    }

    public double probabilidadeDoEstado(int estado, double tempoGlobal) {
        return getAcumulador()[estado] / tempoGlobal;
    }

    public double taxaDeAtendimentoDoEstado(int estado) {
        return Math.min(estado, capacidade) * taxaDeAtendimentoDaFila();
    }

    public double taxaDeAtendimentoDaFila() {
        return 0; // ???
    }

    public double populacao(double tempoGlobal) {
        double populacao = 0;
        HashMap<Integer, Double> probabilidades = probabilidadesDeCadaEstado(tempoGlobal);
        for (int estado = 0; estado < getAcumulador().length; estado++)
            populacao += probabilidades.get(estado) * estado;
        return populacao;
    }

    public double vazao(double tempoGlobal) {
        double vazao = 0;
        for (int estado = 0; estado < getAcumulador().length; estado++)
            vazao += probabilidadeDoEstado(estado, tempoGlobal) * taxaDeAtendimentoDoEstado(estado);
        return vazao;
    }

    public double utilizacao() {
        double utilizacao = 0;
        for (int estado = 0; estado < getAcumulador().length; estado++)
            utilizacao += probabilidadeDoEstado(estado, utilizacao) * (Math.min(estado, capacidade) / capacidade);
        return utilizacao;
    }

    public double tempoDeResposta(double tempoGlobal) {
        return populacao(tempoGlobal) / vazao(tempoGlobal);
    }

}