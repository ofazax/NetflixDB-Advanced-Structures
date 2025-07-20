// Implementação do algoritmo de casamento de padrões Knuth-Morris-Pratt (KMP).

/*Escolhemos implementar o algoritmo de Knuth-Morris-Pratt (KMP) para o casamento de padrões devido à sua eficiência, 
especialmente em textos longos. Ele supera o algoritmo de força bruta ao evitar comparações desnecessárias, otimizando
o processo de busca através do pré-processamento do padrão e da utilização da 
matriz LPS (Longest Proper Prefix which is also Suffix). Essa otimização resulta em uma complexidade de 
tempo linear (O(n+m), onde n é o tamanho do texto e m é o tamanho do padrão), tornando-o uma escolha vantajosa 
para diversas aplicações.  
*/

class KMP {

    /**
     * Realiza a busca do padrão (pat) no texto (txt) usando o algoritmo KMP.
     *
     * @param pat O padrão a ser procurado.
     * @param txt O texto onde o padrão será buscado.
     * @return true se o padrão for encontrado no texto, false caso contrário.
     */
    public boolean KMPSearch(String pat, String txt) {
        int M = pat.length(); // Comprimento do padrão
        int N = txt.length(); // Comprimento do texto

        // Cria o array lps[] que armazenará os maiores valores de prefixo sufixo
        // para o padrão. Este array é usado para evitar comparações desnecessárias
        // quando ocorre uma incompatibilidade.
        int lps[] = new int[M];
        int j = 0; // Índice para o padrão (pat[])

        // Pré-processa o padrão (calcula o array lps[])
        computeLPSArray(pat, M, lps);

        int i = 0; // Índice para o texto (txt[])
        while (i < N) {
            if (pat.charAt(j) == txt.charAt(i)) {
                j++;
                i++;
            }

            // Se o padrão for completamente encontrado
            if (j == M) {
                // O padrão foi encontrado no índice (i - j).
                // Para esta aplicação, apenas precisamos saber se foi encontrado.
                return true; // Retorna true se o padrão for encontrado
            }

            // Incompatibilidade após j correspondências
            else if (i < N && pat.charAt(j) != txt.charAt(i)) {
                // Não corresponde aos caracteres lps[0..lps[j-1]],
                // eles corresponderão de qualquer forma.
                if (j != 0) {
                    j = lps[j - 1]; // Pula para a próxima correspondência possível no padrão
                } else {
                    i = i + 1; // Avança para o próximo caractere no texto
                }
            }
        }
        return false; // Retorna false se o padrão não for encontrado em todo o texto
    }

    /**
     * Preenche o array lps[] (longest prefix suffix) para o padrão dado.
     * lps[i] armazena o comprimento do maior prefixo que também é um sufixo
     * da subcadeia pat[0..i].
     *
     * @param pat O padrão.
     * @param M O comprimento do padrão.
     * @param lps O array para armazenar os valores lps.
     */
    void computeLPSArray(String pat, int M, int lps[]) {
        int len = 0; // Comprimento do maior prefixo sufixo anterior
        int i = 1;
        lps[0] = 0; // lps[0] é sempre 0

        // O loop calcula lps[i] para i = 1 a M-1
        while (i < M) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else { // (pat[i] != pat[len])
                // Isso é complicado. Considere o exemplo.
                // AAACAAAA e i = 7. A ideia é semelhante à etapa de busca.
                if (len != 0) {
                    len = lps[len - 1]; // Volta para o comprimento do lps anterior
                } else { // se (len == 0)
                    lps[i] = len; // Define lps[i] como 0
                    i++;
                }
            }
        }
    }
}
