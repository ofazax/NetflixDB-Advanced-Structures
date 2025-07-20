import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Huffman {

    // Classe do nó da árvore de Huffman
    static class Node implements Comparable<Node>, Serializable {
        char ch;    //Armazena o no
        int freq;   //Frequencia do caractere
        Node left, right;   //Filhos da esquerda e da direita

        // Construtores
        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
        // Verifica se o nó é uma folha
        public boolean isLeaf() {
            return (this.left == null && this.right == null);   //No folha nao tem filhos
        }
        // Compara a frequencia entre os nos
        public int compareTo(Node other) {
            return Integer.compare(this.freq, other.freq); //Compara frequencia entre os nos
        }
    }

    // Gera a árvore de Huffman
    public static Node construirArvore(Map<Character, Integer> frequencias) {
        PriorityQueue<Node> fila = new PriorityQueue<>(); //Fila de prioridade com base na frequencia
        // Adiciona cada caractere como nó
        for (Map.Entry<Character, Integer> entry : frequencias.entrySet()) {
            fila.add(new Node(entry.getKey(), entry.getValue()));
        }
        // Enquanto houver mais de um nó na fila, combine os dois nós com menor frequência
        while (fila.size() > 1) {
            Node esq = fila.poll(); //Menor frequencia
            Node dir = fila.poll(); //Segunda menor
            Node pai = new Node('\0', esq.freq + dir.freq, esq, dir);   //Novo no interno
            fila.add(pai);  // Adiciona o nó pai à fila
        }

        return fila.poll(); //Retorna a raiz
    }

    // Gera o dicionário de códigos de Huffman
    public static void gerarCodigos(Node no, String codigo, Map<Character, String> mapa) {
        if (no != null) { // Verifica se o nó não é nulo
            if (no.isLeaf()) { // Se for uma folha, associa o caractere ao código
                mapa.put(no.ch, codigo); //Associa o caractere ao código
            }
            gerarCodigos(no.left, codigo + '0', mapa); // Esquerda = 0
            gerarCodigos(no.right, codigo + '1', mapa); // Direita = 1
        }
    }

    // Codifica o texto original usando os códigos gerados
    public static String codificar(String texto, Map<Character, String> mapa) {
        StringBuilder sb = new StringBuilder(); // StringBuilder para construir a string codificada
        for (char ch : texto.toCharArray()) { // Itera sobre cada caractere do texto
            sb.append(mapa.get(ch)); // Substitui o caractere pelo código
        }
        return sb.toString(); // Retorna a string codificada
    }

    // Decodifica a string binária usando a árvore
    public static String decodificar(String binario, Node raiz) {
        StringBuilder sb = new StringBuilder(); // StringBuilder para construir a string decodificada
        Node atual = raiz; // Começa na raiz da árvore
        for (char bit : binario.toCharArray()) { // Itera sobre cada bit da string binária
            atual = (bit == '0') ? atual.left : atual.right; // Move para a esquerda ou direita na árvore
            if (atual.isLeaf()) { // Se for uma folha, adiciona o caractere à string decodificada
                sb.append(atual.ch); // Adiciona o caractere à string decodificada
                atual = raiz; // Volta para a raiz da árvore
            }
        }
        return sb.toString(); // Retorna a string decodificada
    }

    // Salva a compressão
    // Salva a compressão em arquivo
    public static void comprimir(String caminhoOrigem, String caminhoDestino) throws IOException { // Caminho de origem e destino
        FileInputStream fis = new FileInputStream(caminhoOrigem); // Abre arquivo original
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Cria um ByteArrayOutputStream para armazenar os dados
        byte[] buffer = new byte[1024]; // Buffer para leitura
        int len; // Variável para armazenar o comprimento lido
        while ((len = fis.read(buffer)) != -1) { // Lê os dados do arquivo
            baos.write(buffer, 0, len); // Escreve os dados no ByteArrayOutputStream
        }
        fis.close(); // Fecha o FileInputStream
        byte[] dados = baos.toByteArray(); // Converte os dados lidos em um array de bytes
        String texto = new String(dados, StandardCharsets.ISO_8859_1); // Converte os bytes para texto usando ISO-8859-1

        Map<Character, Integer> frequencias = new HashMap<>(); // Mapa para armazenar as frequências dos caracteres
        for (char ch : texto.toCharArray()) { // Itera sobre cada caractere do texto
            frequencias.put(ch, frequencias.getOrDefault(ch, 0) + 1); // Atualiza a frequência do caractere
        }

        Node raiz = construirArvore(frequencias); // Gera a árvore de Huffman
        Map<Character, String> mapa = new HashMap<>(); // Mapa para armazenar os códigos de Huffman
        gerarCodigos(raiz, "", mapa); // Gera os códigos de Huffman

        StringBuilder binario = new StringBuilder(); // StringBuilder para armazenar a string binária
        for (char c : texto.toCharArray()) { // Itera sobre cada caractere do texto
            binario.append(mapa.get(c)); // Substitui o caractere pelo código
        }

        BitSet bits = new BitSet(binario.length()); // Cria um BitSet para armazenar os bits
        for (int i = 0; i < binario.length(); i++) { // Itera sobre cada bit da string binária
            if (binario.charAt(i) == '1') bits.set(i); // Define o bit no BitSet
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoDestino))) { // Cria um ObjectOutputStream para salvar os dados
            oos.writeObject(raiz); // Salva a árvore serializada             
            oos.writeInt(binario.length()); // Salva o comprimento real dos bits
            oos.write(bits.toByteArray()); // Salva os bits compactados
        }
    }


    // Descomprime o arquivo e salva os dados descomprimidos
    // Descomprime o arquivo para texto
    public static void descomprimir(String caminhoComprimido, String caminhoSaida) throws IOException, ClassNotFoundException { // Caminho de origem e destino
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoComprimido))) { // Cria um ObjectInputStream para ler os dados
            Node raiz = (Node) ois.readObject(); // Lê a árvore
            String binario = (String) ois.readObject(); // Lê a string binária
            String texto = decodificar(binario, raiz); // Decodifica a string binária usando a árvore

            try (FileOutputStream fos = new FileOutputStream(caminhoSaida)) { // Cria um FileOutputStream para salvar os dados
                fos.write(texto.getBytes()); // Escreve os dados descomprimidos no arquivo de saída
            }
        }
    }

    // Compressão em memória
    public static byte[] compressBytes(byte[] dados) throws IOException {
        String texto = new String(dados, StandardCharsets.ISO_8859_1); // Converte os bytes para texto usando ISO-8859-1
        Map<Character, Integer> freq = new HashMap<>(); // Mapa para armazenar as frequências dos caracteres
        for (char c : texto.toCharArray()) { // Itera sobre cada caractere do texto
            freq.put(c, freq.getOrDefault(c, 0) + 1); // Atualiza a frequência do caractere
        }

        Node raiz = construirArvore(freq); // Gera a árvore de Huffman
        Map<Character, String> mapa = new HashMap<>(); // Mapa para armazenar os códigos de Huffman
        gerarCodigos(raiz, "", mapa); // Gera os códigos de Huffman

        // Codifica o texto
        StringBuilder binario = new StringBuilder(); // StringBuilder para armazenar a string binária
        for (char c : texto.toCharArray()) { // Itera sobre cada caractere do texto
            binario.append(mapa.get(c)); // Substitui o caractere pelo código
        }

        // Converte para bits reais
        BitSet bits = new BitSet(binario.length()); // Cria um BitSet para armazenar os bits
        for (int i = 0; i < binario.length(); i++) { // Itera sobre cada bit da string binária
            if (binario.charAt(i) == '1') bits.set(i); // Define o bit no BitSet
        }

        // Salva a árvore e bits compactados
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Cria um ByteArrayOutputStream para armazenar os dados
        ObjectOutputStream oos = new ObjectOutputStream(baos); // Cria um ObjectOutputStream para salvar os dados
        oos.writeObject(raiz); // Salva a árvore serializada
        oos.writeInt(binario.length()); // Salva o comprimento real dos bits
        oos.write(bits.toByteArray()); // Salva os bits compactados
        oos.close(); // Fecha o ObjectOutputStream

        return baos.toByteArray(); // Retorna os dados comprimidos
    }

    // Descomprime os dados para texto
    public static byte[] decompressBytes(byte[] dados) throws IOException, ClassNotFoundException { 
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dados)); // Cria um ObjectInputStream para ler os dados
        Node raiz = (Node) ois.readObject(); // Lê a árvore
        int bitLength = ois.readInt(); // Lê o comprimento dos bits
    
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = ois.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        byte[] bitBytes = baos.toByteArray(); // Lê os bytes dos bits
        BitSet bits = BitSet.valueOf(bitBytes); // Converte os bytes para um BitSet
    
        // Reconstrói string de bits
        StringBuilder bin = new StringBuilder(); // StringBuilder para armazenar a string binária
        for (int i = 0; i < bitLength; i++) { // Itera sobre cada bit
            bin.append(bits.get(i) ? '1' : '0'); // Adiciona '1' ou '0' à string binária
        }
    
        String texto = decodificar(bin.toString(), raiz); // Decodifica a string binária usando a árvore
        return texto.getBytes(StandardCharsets.ISO_8859_1); // Retorna os dados descomprimidos
    }
    

}
