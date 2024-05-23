public class ConexaoEntreFilas {
    private int idOrigem;
    private int idDestino;
    private double probabilidade;

    public ConexaoEntreFilas (int idOrigem, int idDestino, double probabilidade) {
        this.idOrigem = idOrigem;
        this.idDestino = idDestino;
        this.probabilidade = probabilidade;
    }

    public int getIdDestino() {
        return idDestino;
    }

    public int getIdOrigem() {
        return idOrigem;
    }

    public double getProbabilidade() {
        return probabilidade;
    }

}
