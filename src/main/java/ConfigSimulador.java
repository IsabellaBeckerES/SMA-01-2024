import java.util.List;

public class ConfigSimulador {
    private int qtdNumPseudoAleatorios;
    private int semente;
    private double chegada;

    private List<ConfigFila> filas;

    private List<ConexaoEntreFilas> conexoes;

    public int getQtdNumPseudoAleatorios() {
        return qtdNumPseudoAleatorios;
    }

    public void setQtdNumPseudoAleatorios(int qtdNumPseudoAleatorios) {
        this.qtdNumPseudoAleatorios = qtdNumPseudoAleatorios;
    }

    public int getSemente() {
        return semente;
    }

    public void setSemente(int semente) {
        this.semente = semente;
    }

    public double getChegada() {
        return chegada;
    }

    public void setChegada(double chegada) {
        this.chegada = chegada;
    }

    public List<ConfigFila> getFilas() {
        return filas;
    }

    public void setFilas(List<ConfigFila> filas) {
        this.filas = filas;
    }

    public List<ConexaoEntreFilas> getConexoes() {
        return conexoes;
    }

    public void setConexoes(List<ConexaoEntreFilas> conexoes) {
        this.conexoes = conexoes;
    }
}
