// Compressor.java
import java.io.*;

public class Compressor {

    // Caminho do arquivo original
    private static final String ARQUIVO_ORIGINAL = "./Dados/ArquivoOriginal/netflix1.db";

    // Método para compressão de todos os algoritmos
    public static void compressAll() throws Exception { 
        File original = new File(ARQUIVO_ORIGINAL); // Caminho do arquivo original
        byte[] originalBytes = lerArquivo(original); // Lê o arquivo original

        // Huffman
        long inicio = System.currentTimeMillis(); // Início da compressão
        byte[] huffBytes = Huffman.compressBytes(originalBytes); // Compressão Huffman
        salvarArquivo("./Dados/Compressed/netflix1_HuffmanCompressao", huffBytes); // Salva o arquivo comprimido
        long tempoHuffman = System.currentTimeMillis() - inicio; // Tempo de compressão

        // LZW
        inicio = System.currentTimeMillis(); // Início da compressão
        byte[] lzwBytes = LZW.compress(originalBytes); // Compressão LZW
        salvarArquivo("./Dados/Compressed/netflix1_LZWCompressao", lzwBytes); // Salva o arquivo comprimido
        long tempoLZW = System.currentTimeMillis() - inicio; // Tempo de compressão

        // Comparações
        System.out.println("\n-- Comparação de Compressão --"); // Início da comparação
        compara(originalBytes.length, huffBytes.length, "Huffman", tempoHuffman, true); // Comparação Huffman
        compara(originalBytes.length, lzwBytes.length, "LZW", tempoLZW, true); // Comparação LZW
    }

    // Método para descompressão de todos os algoritmos
    public static void decompress(String algoritmo, String algoritmo2) throws Exception {
        String caminho = "./Dados/Compressed/netflix1_" + algoritmo + "Compressao"; // Caminho do arquivo comprimido
        String caminho2 = "./Dados/Compressed/netflix1_" + algoritmo2 + "Compressao"; // Caminho do segundo arquivo comprimido
        byte[] dadosComprimidos = lerArquivo(new File(caminho)); // Lê o arquivo comprimido
        byte[] dadosComprimidos2 = lerArquivo(new File(caminho2)); // Lê o segundo arquivo comprimido
        byte[] dadosDescomprimidos; // Dados descomprimidos
        byte[] dadosDescomprimidos2; // Dados descomprimidos do segundo arquivo

        String caminhoDescomprimido = "./Dados/Decompressed/netflix1_" + algoritmo + "Descompressao"; // Caminho do arquivo descomprimido
        String caminhoDescomprimido2 = "./Dados/Decompressed/netflix1_" + algoritmo2 + "Descompressao"; // Caminho do segundo arquivo descomprimido
        
        // Início da descompressão
        long inicioH = System.currentTimeMillis(); // Início da compressão
        long inicioL = System.currentTimeMillis(); // Início da compressão
        
        // Descompressão
        // Huffman
        dadosDescomprimidos = Huffman.decompressBytes(dadosComprimidos); // Descompressão Huffman
        salvarArquivo(caminhoDescomprimido, dadosDescomprimidos); // Salva o arquivo descomprimido
        
        // LZW
        dadosDescomprimidos2 = LZW.decompress(dadosComprimidos2); // Descompressão LZW
        salvarArquivo(caminhoDescomprimido2, dadosDescomprimidos2); // Salva o segundo arquivo descomprimido
        
        // Comparação
        System.out.println("\n-- Comparação de Descompressão --"); // Início da comparação
        compara(dadosComprimidos.length, dadosDescomprimidos.length, "Huffman", System.currentTimeMillis() - inicioH, false); // Comparação Huffman
        compara(dadosComprimidos2.length, dadosDescomprimidos2.length, "LZW", System.currentTimeMillis() - inicioL, false); // Comparação LZW
    }

    // Método principal
    private static byte[] lerArquivo(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f); // Cria um FileInputStream para ler o arquivo
        byte[] dados = new byte[(int) f.length()]; // Cria um array de bytes do tamanho do arquivo
        fis.read(dados); // Lê os dados do arquivo
        fis.close(); // Fecha o FileInputStream
        return dados;// Retorna os dados lidos
    }

    // Método para salvar o arquivo
    private static void salvarArquivo(String caminho, byte[] dados) throws IOException {
        FileOutputStream fos = new FileOutputStream(caminho); // Cria um FileOutputStream para salvar o arquivo
        fos.write(dados); // Escreve os dados no arquivo
        fos.close(); // Fecha o FileOutputStream
    }

    // Método para comparar os tamanhos dos arquivos
    private static void compara(long a, long b, String nome, long tempo, boolean isCompressao) {
        
        if(isCompressao) {
            System.out.println("\nComparando compressão...");
            double ganho = 100.0 * (a - b) / a; // Cálculo da economia
            System.out.printf("%s: Tamanho Original: %d bytes, Tamanho Comprimido = %d bytes, Tempo = %d ms, Economia = %.2f%%\n",
                nome, a, b, tempo, ganho); // Exibe os resultados
        } else {
            System.out.println("\nComparando descompressão...");
            double ganho = 100.0 * (a - b) / a; // Cálculo da economia
            System.out.printf("%s: Tamanho Comprimido: %d bytes, Tamanho Descomprimido = %d bytes, Tempo = %d ms, Economia = %.2f%%\n",
                nome, a, b, tempo, ganho); // Exibe os resultados
        }
        
    }
}