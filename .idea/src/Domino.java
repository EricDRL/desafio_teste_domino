import java.util.*;

/**
 * DOMINO (duplo-seis, 28 pecas) para 2 jogadores - console.
 * (Arquivo 100% ASCII: sem acentos e sem simbolos especiais.)
 *
 * Modos: [1] Voce x Computador   [2] Dois jogadores humanos (mesma tela, revezando)
 *
 * Regras:
 *  - Cada jogador recebe 7 pecas; as 14 restantes formam o monte.
 *  - Comeca quem tem a maior carroca (peca dupla); se ninguem tiver, a peca de maior soma.
 *    A peca inicial e jogada automaticamente.
 *  - Na sua vez, jogue uma peca cujo lado combine com uma das pontas da mesa.
 *  - Sem jogada? Voce compra do monte ate conseguir jogar. Monte vazio e sem jogada = passa a vez.
 *  - A rodada termina quando alguem bate (fica sem pecas) ou quando o jogo trava (os dois passam).
 *      * Bateu: ganha a soma dos pontos das pecas do adversario.
 *      * Travou: vence quem tiver menos pontos na mao e ganha a diferenca (empate = ninguem pontua).
 *  - A partida termina quando um jogador chega a META pontos.
 *
 * Como jogar: digite o numero da peca (ex: 3). Se ela encaixar nas duas pontas,
 *             digite tambem o lado: 3 e (esquerda) ou 3 d (direita). 'sair' encerra.
 *             Pecas marcadas com * sao jogaveis.
 *
 * Compilar: javac Domino.java
 * Executar: java Domino
 */
public class Domino {

    static final int META = 50;

    static final Scanner sc = new Scanner(System.in);
    static final Random rnd = new Random();

    static String[] nome = new String[2];
    static boolean[] humano = new boolean[2];
    static boolean doisHumanos;
    static int[] placar = new int[2];

    static List<List<int[]>> mao = new ArrayList<>();
    static List<int[]> monte = new ArrayList<>();
    static Deque<int[]> mesa = new ArrayDeque<>();

    static class Jogada {
        final int idx;
        final boolean esquerda;

        Jogada(int idx, boolean esquerda) {
            this.idx = idx;
            this.esquerda = esquerda;
        }
    }

    // ===================== Main =====================
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("      DOMINO - 2 JOGADORES");
        System.out.println("=====================================");
        System.out.println("Partida ate " + META + " pontos.");

        boolean continuar = true;
        while (continuar) {
            escolherModo();
            placar[0] = 0;
            placar[1] = 0;

            while (placar[0] < META && placar[1] < META) {
                jogarRodada();
                System.out.println("\n>>> PLACAR: " + nome[0] + " " + placar[0] + " x " + placar[1] + " " + nome[1]);
                if (placar[0] >= META || placar[1] >= META) break;
                System.out.print("\n[ENTER] para a proxima rodada...");
                ler();
            }

            System.out.println("\n=====================================");
            System.out.println("  " + (placar[0] >= META ? nome[0] : nome[1]).toUpperCase() + " VENCEU A PARTIDA!");
            System.out.println("  Placar final: " + nome[0] + " " + placar[0] + " x " + placar[1] + " " + nome[1]);
            System.out.println("=====================================");
            System.out.print("\nJogar novamente? (s/n): ");
            continuar = ler().startsWith("s");
        }
        System.out.println("\nAte a proxima!");
    }

    static void escolherModo() {
        System.out.println("\nModo de jogo:");
        System.out.println(" [1] Voce x Computador");
        System.out.println(" [2] Dois jogadores humanos (mesma tela)");
        while (true) {
            System.out.print("Opcao: ");
            String s = ler();
            if (s.equals("1")) {
                doisHumanos = false;
                nome[0] = "Voce";
                nome[1] = "Computador";
                humano[0] = true;
                humano[1] = false;
                return;
            }
            if (s.equals("2")) {
                doisHumanos = true;
                nome[0] = "Jogador 1";
                nome[1] = "Jogador 2";
                humano[0] = true;
                humano[1] = true;
                return;
            }
            System.out.println("Opcao invalida.");
        }
    }

    // ===================== Rodada =====================
    static void jogarRodada() {
        // Cria e embaralha as 28 pecas
        List<int[]> todas = new ArrayList<>();
        for (int a = 0; a <= 6; a++)
            for (int b = a; b <= 6; b++) todas.add(new int[]{a, b});
        Collections.shuffle(todas, rnd);

        mao.clear();
        mao.add(new ArrayList<>(todas.subList(0, 7)));
        mao.add(new ArrayList<>(todas.subList(7, 14)));
        monte = new ArrayList<>(todas.subList(14, 28));
        mesa.clear();

        System.out.println("\n================ NOVA RODADA ================");

        // Quem comeca: maior carroca; sem carroca, maior soma
        int inicio = 0, idxInicio = -1, melhor = -1;
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < mao.get(j).size(); i++) {
                int[] p = mao.get(j).get(i);
                int valor = (p[0] == p[1]) ? 100 + p[0] : p[0] + p[1];
                if (valor > melhor) {
                    melhor = valor;
                    inicio = j;
                    idxInicio = i;
                }
            }
        }
        int[] primeira = mao.get(inicio).remove(idxInicio);
        mesa.addLast(primeira);
        System.out.println(nome[inicio] + " comeca jogando " + texto(primeira) + ".");

        int turno = 1 - inicio;
        int passes = 0;
        int vencedor = -1;
        boolean bloqueio = false;

        while (true) {
            boolean jogou = vez(turno);
            if (jogou) {
                passes = 0;
                if (mao.get(turno).isEmpty()) {
                    vencedor = turno;
                    break;
                }
            } else {
                passes++;
                if (passes >= 2) {
                    bloqueio = true;
                    break;
                }
            }
            turno = 1 - turno;
        }

        // Fim da rodada
        System.out.println("\n---------------- FIM DA RODADA ----------------");
        desenharMesa();
        System.out.println(nome[0] + " ficou com: " + maoTexto(mao.get(0)) + " (" + pontos(mao.get(0)) + " pontos)");
        System.out.println(nome[1] + " ficou com: " + maoTexto(mao.get(1)) + " (" + pontos(mao.get(1)) + " pontos)");

        if (!bloqueio) {
            int pts = pontos(mao.get(1 - vencedor));
            placar[vencedor] += pts;
            System.out.println(nome[vencedor] + " BATEU! Ganha " + pts + " ponto(s).");
        } else {
            int p0 = pontos(mao.get(0)), p1 = pontos(mao.get(1));
            System.out.println("JOGO TRAVADO!");
            if (p0 == p1) {
                System.out.println("Empate nos pontos: ninguem pontua.");
            } else {
                int v = p0 < p1 ? 0 : 1;
                int pts = Math.abs(p0 - p1);
                placar[v] += pts;
                System.out.println(nome[v] + " tem menos pontos na mao e ganha " + pts + " ponto(s).");
            }
        }
    }

    // ===================== Vez de um jogador =====================
    /** Retorna true se jogou uma peca; false se passou. */
    static boolean vez(int j) {
        List<int[]> m = mao.get(j);

        if (doisHumanos) {
            System.out.println("\n=== Vez de " + nome[j] + " ===");
            System.out.print("(o outro jogador nao deve olhar) [ENTER] quando estiver pronto...");
            ler();
        }

        // Compra ate conseguir jogar
        List<Jogada> js = possiveis(m);
        while (js.isEmpty() && !monte.isEmpty()) {
            int[] p = monte.remove(monte.size() - 1);
            m.add(p);
            if (humano[j]) System.out.println(nome[j] + " nao tinha jogada e comprou " + texto(p) + ".");
            else System.out.println(nome[j] + " nao tinha jogada e comprou uma peca.");
            js = possiveis(m);
        }
        if (js.isEmpty()) {
            System.out.println(nome[j] + " PASSOU a vez (sem jogada e monte vazio).");
            if (doisHumanos) esconder();
            return false;
        }

        Jogada escolhida;
        if (humano[j]) {
            mostrarEstado(j, js);
            escolhida = escolherHumano(j, js);
        } else {
            escolhida = escolherIA(m, js);
        }

        int[] p = m.remove(escolhida.idx);
        aplicar(p, escolhida.esquerda);
        System.out.println(nome[j] + " jogou " + texto(p) + " na " + (escolhida.esquerda ? "esquerda" : "direita") + ".");

        if (doisHumanos) esconder();
        return true;
    }

    static void esconder() {
        System.out.print("[ENTER] para encerrar o turno...");
        ler();
        for (int i = 0; i < 40; i++) System.out.println();
    }

    static Jogada escolherHumano(int j, List<Jogada> js) {
        List<int[]> m = mao.get(j);
        while (true) {
            System.out.print("Escolha a peca [1-" + m.size() + "] (ex: 3, 3 e, 3 d) ou 'sair': ");
            String s = ler();
            if (s.equals("sair")) {
                System.out.println("\nAte a proxima!");
                System.exit(0);
            }
            String[] t = s.split("\\s+");
            int idx;
            try {
                idx = Integer.parseInt(t[0]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida.");
                continue;
            }
            if (idx < 0 || idx >= m.size() || !podeLado(js, idx, true) && !podeLado(js, idx, false)) {
                System.out.println("Essa peca nao encaixa em nenhuma ponta.");
                continue;
            }

            boolean esq = podeLado(js, idx, true);
            boolean dir = podeLado(js, idx, false);
            Boolean lado = null;
            if (t.length >= 2) {
                if (t[1].startsWith("e")) lado = true;
                else if (t[1].startsWith("d")) lado = false;
                else {
                    System.out.println("Lado invalido. Use 'e' ou 'd'.");
                    continue;
                }
            } else if (esq && dir && esquerdaPonta() != direitaPonta()) {
                while (lado == null) {
                    System.out.print("Em qual lado? [e]squerda ou [d]ireita: ");
                    String l = ler();
                    if (l.startsWith("e")) lado = true;
                    else if (l.startsWith("d")) lado = false;
                }
            } else {
                lado = esq; // so cabe em um lado (ou as pontas sao iguais)
            }

            if (!podeLado(js, idx, lado)) {
                System.out.println("Essa peca nao encaixa desse lado.");
                continue;
            }
            return new Jogada(idx, lado);
        }
    }

    /** IA: prefere pecas pesadas e jogadas que mantenham opcoes futuras. */
    static Jogada escolherIA(List<int[]> m, List<Jogada> js) {
        Jogada melhor = null;
        double melhorValor = -1e9;
        for (Jogada j : js) {
            int[] p = m.get(j.idx);
            int novaEsq = esquerdaPonta(), novaDir = direitaPonta();
            if (j.esquerda) novaEsq = (p[0] == esquerdaPonta()) ? p[1] : p[0];
            else novaDir = (p[0] == direitaPonta()) ? p[1] : p[0];

            int futuras = 0;
            for (int i = 0; i < m.size(); i++) {
                if (i == j.idx) continue;
                int[] q = m.get(i);
                if (q[0] == novaEsq || q[1] == novaEsq || q[0] == novaDir || q[1] == novaDir) futuras++;
            }
            double valor = p[0] + p[1] + (p[0] == p[1] ? 2 : 0) + 1.5 * futuras + rnd.nextDouble();
            if (valor > melhorValor) {
                melhorValor = valor;
                melhor = j;
            }
        }
        return melhor;
    }

    // ===================== Mesa e jogadas =====================
    static int esquerdaPonta() {
        return mesa.getFirst()[0];
    }

    static int direitaPonta() {
        return mesa.getLast()[1];
    }

    static List<Jogada> possiveis(List<int[]> m) {
        List<Jogada> js = new ArrayList<>();
        int e = esquerdaPonta(), d = direitaPonta();
        for (int i = 0; i < m.size(); i++) {
            int[] p = m.get(i);
            if (p[0] == e || p[1] == e) js.add(new Jogada(i, true));
            if (p[0] == d || p[1] == d) js.add(new Jogada(i, false));
        }
        return js;
    }

    static boolean podeLado(List<Jogada> js, int idx, boolean esquerda) {
        for (Jogada j : js) if (j.idx == idx && j.esquerda == esquerda) return true;
        return false;
    }

    static void aplicar(int[] p, boolean esquerda) {
        if (esquerda) {
            if (p[1] == esquerdaPonta()) mesa.addFirst(new int[]{p[0], p[1]});
            else mesa.addFirst(new int[]{p[1], p[0]});
        } else {
            if (p[0] == direitaPonta()) mesa.addLast(new int[]{p[0], p[1]});
            else mesa.addLast(new int[]{p[1], p[0]});
        }
    }

    static int pontos(List<int[]> m) {
        int s = 0;
        for (int[] p : m) s += p[0] + p[1];
        return s;
    }

    // ===================== Tela =====================
    static String texto(int[] p) {
        return "[" + p[0] + "|" + p[1] + "]";
    }

    static String maoTexto(List<int[]> m) {
        if (m.isEmpty()) return "(nenhuma peca)";
        StringBuilder sb = new StringBuilder();
        for (int[] p : m) sb.append(texto(p)).append(' ');
        return sb.toString().trim();
    }

    static void desenharMesa() {
        System.out.println("Mesa:");
        int n = 0;
        StringBuilder sb = new StringBuilder();
        for (int[] p : mesa) {
            sb.append(texto(p));
            n++;
            if (n % 10 == 0) {
                System.out.println("  " + sb);
                sb.setLength(0);
            }
        }
        if (sb.length() > 0) System.out.println("  " + sb);
        System.out.println("Pontas: esquerda = " + esquerdaPonta() + " | direita = " + direitaPonta());
    }

    static void mostrarEstado(int j, List<Jogada> js) {
        int outro = 1 - j;
        System.out.println("\n--------------------------------------------");
        System.out.println("Placar: " + nome[0] + " " + placar[0] + " x " + placar[1] + " " + nome[1]
                + " (meta " + META + ")");
        System.out.println("Monte: " + monte.size() + " peca(s) | " + nome[outro] + ": " + mao.get(outro).size() + " peca(s)");
        desenharMesa();

        List<int[]> m = mao.get(j);
        StringBuilder sb = new StringBuilder("Sua mao (" + nome[j] + "): ");
        for (int i = 0; i < m.size(); i++) {
            boolean joga = podeLado(js, i, true) || podeLado(js, i, false);
            sb.append(i + 1).append(") ").append(texto(m.get(i))).append(joga ? "*" : "").append("   ");
        }
        System.out.println(sb);
        System.out.println("(* = jogavel)");
    }

    static String ler() {
        if (!sc.hasNextLine()) {
            System.out.println("\nEntrada encerrada.");
            System.exit(0);
        }
        return sc.nextLine().trim().toLowerCase();
    }
}