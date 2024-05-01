public class Evento {
    private double tempo;
    private TipoEvento tipo;
    private int idFila; // origem
    private int idFilaDestino;

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

    public void setIdFila(int idFila) {
        this.idFila = idFila;
    }

    public int getIdFilaDestino() {
        return idFilaDestino;
    }

    public void setIdFilaDestino(int idFilaDestino) {
        this.idFilaDestino = idFilaDestino;
    }

    @Override
    public String toString() {
        return "[" + tipo + "] - tempo: " + tempo + " fila: " + idFila + " filaDestino: " + idFilaDestino  + "\n"; 
    }
}
