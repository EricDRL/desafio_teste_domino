# Casos de teste - Domino (2 jogadores, duplo-seis)

Regras de referencia: 28 pecas, 7 por jogador, monte com 14, compra automatica, meta de 50 pontos.

| # | Caso | Dado | Entao |
|---|---|---|---|
| CT01 | Conjunto de pecas | Inicio de uma rodada | Existem exatamente 28 pecas unicas [a\|b], com 0 <= a <= b <= 6, sem repeticao |
| CT02 | Distribuicao inicial | Inicio de uma rodada com 2 jogadores | Cada jogador tem 7 pecas, o monte tem 14 e nenhuma peca aparece em dois lugares |
| CT03 | Quem comeca (maior carroca) | J1 tem [3\|3] e [5\|5]; J2 tem [4\|4] e [6\|1] | J1 comeca com [5\|5], jogada automaticamente |
| CT04 | Quem comeca (sem carroca) | Ninguem tem carroca; a maior soma e [6\|5], com J2 | J2 comeca com [6\|5] e o turno passa ao J1 |
| CT05 | Encaixe valido | Mesa [2\|5]; mao com [5\|1], [3\|4] e [2\|2] | [5\|1] e [2\|2] sao jogaveis; [3\|4] nao e |
| CT06 | Orientacao da peca | Mesa [2\|5]; jogador joga [1\|5] na direita | A mesa vira [2\|5][5\|1]; ponta direita = 1 e esquerda = 2 |
| CT07 | Peca serve nas duas pontas | Pontas 2 (esquerda) e 5 (direita); jogador tem [2\|5] | O jogo pede o lado. Na esquerda, a ponta esquerda vira 5. Na direita, a ponta direita vira 2 |
| CT08 | Jogada invalida | Pontas 2 e 5; jogador tenta [3\|4] | O jogo recusa com mensagem de erro. A mao e a mesa nao mudam e o turno continua do mesmo jogador |
| CT09 | Compra automatica | Sem peca jogavel; monte com 3 pecas | Compra uma a uma ate poder jogar. Se a 2a peca serve, o monte fica com 1. Nao compra se ja tem jogada |
| CT10 | Passar a vez | Sem jogada e monte vazio | Passa a vez automaticamente e o turno vai para o adversario |
| CT11 | Batida | J1 joga a ultima peca; J2 fica com [6\|4] e [1\|0] | J1 bate e ganha 11 pontos |
| CT12 | Jogo travado | Monte vazio e dois passes seguidos; J1 com 8 pontos na mao, J2 com 14 | J1 ganha a diferenca, 6 pontos. Com pontos iguais, ninguem pontua |
| CT13 | Fim da partida | Placar J1 = 45, J2 = 20; J1 bate somando 7 | J1 chega a 52 (meta 50) e vence. O jogo oferece "jogar novamente?" |
