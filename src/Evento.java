public class Evento {
    private double tempo;
    private TipoEvento tipo;
    private int idFilaOrigem; // origem
    private int idFilaDestino;

    public Evento (double tempo, TipoEvento tipo, int idFilaOrigem) {
        this.tempo = tempo;
        this.tipo = tipo;
        this.idFilaOrigem = idFilaOrigem;
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

    public int getIdFilaOrigem() {
        return this.idFilaOrigem;
    }

    public void setIdFilaOrigem(int idFilaOrigem) {
        this.idFilaOrigem = idFilaOrigem;
    }

    public int getIdFilaDestino() {
        return idFilaDestino;
    }

    public void setIdFilaDestino(int idFilaDestino) {
        this.idFilaDestino = idFilaDestino;
    }

    @Override
    public String toString() {
        return "[" + tipo + "] - tempo: " + tempo + " fila: " + idFilaOrigem + " filaDestino: " + idFilaDestino  + "\n"; 
    }
}
