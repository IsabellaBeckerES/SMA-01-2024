import java.util.ArrayList;

public class Fila {

    ArrayList<String> elementos;

    int quantidadeDeServidores;
    int capacidade;
    int clientesPerdidos;

    int tempoMinimoDeChegada;   // -1 significa que a fila não recebe chegadas
    int tempoMaximoDeChegada;   // -1 significa que a fila não recebe chegadas
    int tempoMinimoDeAtendimento;
    int tempoMaximoDeAtendimento;

    long[] acumulador;

    public Fila(int servidores, int capacidade, int tempoMinimoDeChegada, int tempoMaximoDeChegada, int tempoMinimoDeAtendimento, int tempoMaximoDeAtendimento) {
        elementos = new ArrayList<>();
        
        clientesPerdidos = 0;

        this.quantidadeDeServidores = servidores;
        this.capacidade = capacidade;

        this.tempoMinimoDeChegada = tempoMinimoDeChegada;
        this.tempoMaximoDeChegada = tempoMaximoDeChegada;
        this.tempoMinimoDeAtendimento = tempoMinimoDeAtendimento;
        this.tempoMaximoDeAtendimento = tempoMaximoDeAtendimento;

        acumulador = new long[this.capacidade + 1];
    }

    public int capacidade() {
        return this.capacidade;
    }

    public int quantidadeDeServidores() {
        return this.quantidadeDeServidores;
    }

    public int status() {
        return this.elementos.size();
    }

    public int perda() {
        clientesPerdidos++;
        return clientesPerdidos;
    }

    public boolean entrada(String elemento) {
        if (status() < capacidade) {
            elementos.add(elemento);
            return true;
        } else {
            perda();
            return false;
        }
    }

    public String saida() {
        return elementos.remove(0);
    }

    public void acumularTempo(long tempoAAcumular) {
        this.acumulador[this.status()] += tempoAAcumular;
    }

}