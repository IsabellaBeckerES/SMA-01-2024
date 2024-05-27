# SMA-01-2024

## Parâmetros arquivo yml

**qtdNumPseudoAleatorios:** quantidade de números peseudoaleatórios que seráo utilizados na simulação.
**semente:** semnte que sera usada na geração de números peseudoaleatórios.

**chegada:** indica o tempo em que ocorrerá o primeiro evento.
**idFilaChegada:** ida da fila que iá reecber o priemiro evento.

**filas:** deverá conter o array com os parâmetros de cada fila.
- idFila: identificador da fila, deve ser um número.
- servidores: quantidade de servidores da fila.
- capacidade: valor máximo de itens que pode tem na fila.
- tempoChegadaMin: tempo minimo que pode ocorrer uma chegada na fila (caso a fila só receba de outras filas esse campo deverá ser preenchido com -1 assim como campo tempoChegadaMax).
- tempoChegadaMax: tempo máximo que pode ocorrer uma chegada na fila.
- tempoAtendimentoMin: tempo minimo de atendimento do servidor da fila.
- tempoAtendimentoMax: tempo máximo de atendimento do servidor da fila.
- probabilidadeSaida: probabiliadde de ocorrer uma saida da fila (caso a fila só ocorra passagens para outras filas esse campo deve ser preenchido com -1)

**conexoes:**
- idOrigem: id da fial de origem.
- idDestino: id fila de destino.
- probabilidade: probabiliade de ocorrer a passagem entre as filas origem para a destino.

No projeto há um arquivo yml pronto para uso e edição, o nome do arquivo é configSimulador.yml.
