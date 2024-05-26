public class ConfigFila {
    private int servidores;
    private int capacidade;
    private int idFila;
    private double tempoChegadaMin;
    private double tempoChegadaMax;
    private double tempoAtendimentoMin;
    private double tempoAtendimentoMax;

    private double probabilidadeSaida;

    // Atributos que não irão variar após a inicialização
    public int getServidores() {
        return servidores;
    }

    public int getCapacidade() {
        return capacidade;
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
}