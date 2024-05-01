public class Evento {
    private double tempo;
    private TipoEvento tipo;
    private int idFila;

    public Evento (double tempo, TipoEvento tipo, int idFila) {
        this.tempo = tempo;
        this.tipo = tipo;
        this.idFila = idFila;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public int getIdFila() {
        return this.idFila;
    }

    public int setIdFila() {
        return this.idFila;
    }

    @Override
    public String toString() {
        return "[" + tipo + "] - tempo: " + tempo + " fila: " + idFila + "\n"; 
    }
}
