public class GeradorNumPseudoaleatorio {
    double a = 65;
    double c = 88;
    double M = 982397845;
    // semente
    double semente = 7;

    public double gerarNumPseudoaleatorio() {
        semente = ((a * semente + c) % M);
        return normalizar();
    }

    public double normalizar() {
        return (1.0 * semente)/M;
    }
}
