import java.io.*;
import java.util.*;

public class LZW {

    // Método para compressão LZW
    // O método compress recebe um array de bytes como entrada e retorna um array de bytes comprimido
    // O método utiliza um dicionário para armazenar sequências de bytes e seus códigos correspondentes
    // O dicionário é inicializado com os códigos ASCII de 0 a 255
    // O método percorre o array de bytes de entrada, construindo sequências e adicionando novos códigos ao dicionário
    // Quando uma sequência não é encontrada no dicionário, o código correspondente à sequência anterior é adicionado ao resultado
    // O método retorna o array de bytes comprimido
    // O método utiliza um ByteArrayOutputStream e um DataOutputStream para escrever os códigos comprimidos
    // O método utiliza um ArrayList para armazenar os códigos comprimidos
    // O método utiliza um Map para armazenar o dicionário, onde a chave é uma lista de bytes e o valor é o código correspondente
    // O método utiliza um loop para percorrer o array de bytes de entrada, construindo sequências e adicionando novos códigos ao dicionário

    public static byte[] compress(byte[] input) throws IOException {  // Método de compressão LZW
        Map<List<Byte>, Integer> dictionary = new HashMap<>(); // Dicionário para armazenar sequências de bytes e seus códigos correspondentes
        for (int i = 0; i < 256; i++) { // Inicializa o dicionário com os códigos ASCII de 0 a 255
            dictionary.put(Arrays.asList((byte) i), i); // Adiciona os códigos ASCII ao dicionário
        }

        List<Integer> result = new ArrayList<>(); // Lista para armazenar os códigos comprimidos
        List<Byte> w = new ArrayList<>(); // Sequência atual
        int dictSize = 256; // Tamanho do dicionário, começa em 256 porque os códigos ASCII já foram adicionados

        for (byte b : input) { // Percorre o array de bytes de entrada
            List<Byte> wc = new ArrayList<>(w); // Cria uma nova sequência com a sequência atual
            wc.add(b); // Adiciona o byte atual à nova sequência
            // Verifica se a nova sequência está no dicionário
            if (dictionary.containsKey(wc)) { //
                w = wc; // Atualiza a sequência atual para a nova sequência
            } else { 
                result.add(dictionary.get(w)); // Adiciona o código correspondente à sequência anterior ao resultado
                dictionary.put(wc, dictSize++); // Adiciona a nova sequência ao dicionário com um novo código
                w = new ArrayList<>(); // Reinicia a sequência atual
                w.add(b); // Adiciona o byte atual à nova sequência
            }
        }
        
        if (!w.isEmpty()) { // Verifica se a sequência atual não está vazia
            result.add(dictionary.get(w)); // Adiciona o código correspondente à sequência atual ao resultado
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Cria um ByteArrayOutputStream para armazenar os códigos comprimidos
        DataOutputStream dos = new DataOutputStream(baos); // Cria um DataOutputStream para escrever os códigos comprimidos
        for (int code : result) { // Percorre a lista de códigos comprimidos
            dos.writeInt(code); // Escreve o código no DataOutputStream
        }
        return baos.toByteArray(); // Retorna o array de bytes comprimido
    }

    // Método para descompressão LZW
    // O método decompress recebe um array de bytes comprimido como entrada e retorna um array de bytes descomprimido
    // O método utiliza um dicionário para armazenar sequências de bytes e seus códigos correspondentes
    // O dicionário é inicializado com os códigos ASCII de 0 a 255
    // O método percorre o array de bytes comprimido, reconstruindo as sequências e adicionando novos códigos ao dicionário
    // Quando uma sequência não é encontrada no dicionário, o código correspondente à sequência anterior é adicionado ao resultado
    // O método retorna o array de bytes descomprimido
    // O método utiliza um ByteArrayOutputStream e um DataInputStream para ler os códigos comprimidos
    // O método utiliza um ArrayList para armazenar os códigos comprimidos
    // O método utiliza um Map para armazenar o dicionário, onde a chave é uma lista de bytes e o valor é o código correspondente
    // O método utiliza um loop para percorrer o array de bytes comprimido, reconstruindo sequências e adicionando novos códigos ao dicionário
    public static byte[] decompress(byte[] compressed) throws IOException { 
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(compressed)); // Cria um DataInputStream para ler os códigos comprimidos
        List<Integer> compressedCodes = new ArrayList<>(); // Lista para armazenar os códigos comprimidos
        while (dis.available() > 0) { // Verifica se há mais dados para ler
            compressedCodes.add(dis.readInt()); // Lê o código comprimido e adiciona à lista
        }

        Map<Integer, List<Byte>> dictionary = new HashMap<>(); // Dicionário para armazenar sequências de bytes e seus códigos correspondentes
        for (int i = 0; i < 256; i++) { // Inicializa o dicionário com os códigos ASCII de 0 a 255
            dictionary.put(i, Arrays.asList((byte) i)); // Adiciona os códigos ASCII ao dicionário
        }
        
        int dictSize = 256; // Tamanho do dicionário, começa em 256 porque os códigos ASCII já foram adicionados
        if (compressedCodes.isEmpty()) {  // Verifica se o array comprimido está vazio
            throw new IOException("Arquivo LZW está vazio ou corrompido."); // Lança uma exceção se o array comprimido estiver vazio
        }

        // Verifica se o primeiro código é válido
        int firstCode = compressedCodes.remove(0); // Remove o primeiro código da lista
        List<Byte> w = new ArrayList<>(dictionary.get(firstCode)); // Cria uma nova sequência com o primeiro código
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Cria um ByteArrayOutputStream para armazenar os bytes descomprimidos
        for (byte b : w) { // Percorre a sequência e escreve os bytes no ByteArrayOutputStream
            baos.write(b); // Escreve o byte no ByteArrayOutputStream
        }

        for (int k : compressedCodes) { // Percorre os códigos comprimidos restantes
            List<Byte> entry; // Variável para armazenar a sequência correspondente ao código
            if (dictionary.containsKey(k)) { // Verifica se o código está no dicionário
                entry = dictionary.get(k); // Obtém a sequência correspondente ao código
            } else if (k == dictSize) { // Verifica se o código é igual ao tamanho do dicionário
                entry = new ArrayList<>(w); // Cria uma nova sequência com a sequência anterior
                entry.add(w.get(0)); // Adiciona o primeiro byte da sequência anterior à nova sequência
            } else { // Se o código não está no dicionário e não é igual ao tamanho do dicionário
                throw new IOException("Erro na descompressão LZW"); // Lança uma exceção
            }

            for (byte b : entry) { // Percorre a sequência e escreve os bytes no ByteArrayOutputStream
                baos.write(b); // Escreve o byte no ByteArrayOutputStream
            }

            // Adiciona a nova sequência ao dicionário
            List<Byte> newEntry = new ArrayList<>(w); // Cria uma nova sequência com a sequência anterior
            newEntry.add(entry.get(0)); //  Adiciona o primeiro byte da nova sequência à nova sequência
            dictionary.put(dictSize++, newEntry); // Adiciona a nova sequência ao dicionário com um novo código
            w = entry; // Atualiza a sequência anterior para a nova sequência
        }

        return baos.toByteArray(); // Retorna o array de bytes descomprimido
    }
}
