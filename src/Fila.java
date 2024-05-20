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

    public boolean cabeMaisUmElemento() {
        return getStatus() < getCapacidade() || getCapacidade() == -1;
    }

    public boolean temServidoresOciosos() {
        System.out.println(idFila + ": " + getStatus() + " elementos, " + getServidores() + " servidores");
        return getStatus() < getServidores(); // trocar por <= funciona mas a lógica fica errada
    }

    public int push(String elemento) {
        elementos.add(elemento);
        return elementos.size();
    }

    public String pop() {
        return elementos.remove(0);
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

    public double taxaDeAtendimentoDoEstadoPorHora(int estado) {
        return Math.min(estado, capacidade) * taxaMediaDeAtendimentoDaFilaPorHora();
    }

    public double taxaMediaDeAtendimentoDaFila() {
        return capacidade / (tempoAtendimentoMax - tempoAtendimentoMin);
    }

    public double taxaMediaDeAtendimentoDaFilaPorHora() {
        return taxaMediaDeAtendimentoDaFila() * 60;
    }

    public double populacaoDoEstado(int estado, double tempoGlobal) {
        return probabilidadeDoEstado(estado, tempoGlobal) * estado;
    }

    public double populacao(double tempoGlobal) {
        double populacao = 0;
        for (int estado = 0; estado < getAcumulador().length; estado++)
            populacao += populacaoDoEstado(estado, tempoGlobal);
        return populacao;
    }

    public double vazaoDoEstadoPorHora(int estado, double tempoGlobal) {
        return probabilidadeDoEstado(estado, tempoGlobal) * taxaDeAtendimentoDoEstadoPorHora(estado);
    }

    public double vazaoPorHora(double tempoGlobal) {
        double vazao = 0;
        for (int estado = 0; estado < getAcumulador().length; estado++)
            vazao += vazaoDoEstadoPorHora(estado, tempoGlobal);
        return vazao;
    }

    public double utilizacaoDoEstado(int estado, double tempoGlobal) {
        return probabilidadeDoEstado(estado, tempoGlobal) * (Math.min(estado, capacidade) / (double) capacidade);
    }

    public double utilizacao(double tempoGlobal) {
        double utilizacao = 0;
        for (int estado = 0; estado < getAcumulador().length; estado++)
            utilizacao += utilizacaoDoEstado(estado, tempoGlobal);
        return utilizacao;
    }

    public double tempoDeRespostaEmHoras(double tempoGlobal) {
        return populacao(tempoGlobal) / vazaoPorHora(tempoGlobal);
    }

}