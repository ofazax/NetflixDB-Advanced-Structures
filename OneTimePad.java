import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

/*Escolhemos a cifra One-Time Pad (OTP) por ser uma das mais seguras, teoricamente inquebrável, pois utiliza uma chave 
aleatória do mesmo tamanho do arquivo, garantindo que cada byte do arquivo original seja cifrado com um byte único da chave. 
A chave deve ser mantida em segredo e usada apenas uma vez para garantir a segurança. A cifra One-Time Pad é ideal para 
este exemplo, pois permite a cifragem e decifragem de arquivos de forma segura, desde que a chave seja gerada corretamente 
e mantida em segurança. 
*/

public class OneTimePad {

    private static final String ARQUIVO_ORIGINAL_DB = "./Dados/ArquivoOriginal/netflix1.db";
    private static final String ARQUIVO_CIFRADO_OTP = "./Dados/CifradoOTP/netflix1_OneTimePad.db_cifrado";
    private static final String ARQUIVO_CHAVE_OTP = "./Dados/ChavesOTP/netflix1_OneTimePad.key";
    private static final String ARQUIVO_DECIFRADO_OTP = "./Dados/DecifradoOTP/netflix1_OneTimePad.db_decifrado";

    public static void encryptFile() throws IOException {
        File originalFile = new File(ARQUIVO_ORIGINAL_DB);

        if (!originalFile.exists()) {
            System.out.println("Erro: O arquivo original '" + ARQUIVO_ORIGINAL_DB + "' não existe.");
            return;
        }

        byte[] originalBytes = Files.readAllBytes(originalFile.toPath());
        int fileSize = originalBytes.length;

        // 1. Gerar uma chave aleatória do mesmo tamanho do arquivo
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[fileSize];
        secureRandom.nextBytes(keyBytes); // Preenche o array com bytes aleatórios

        // 2. Cifrar os dados usando XOR
        byte[] encryptedBytes = new byte[fileSize];
        for (int i = 0; i < fileSize; i++) {
            encryptedBytes[i] = (byte) (originalBytes[i] ^ keyBytes[i]); // Operação XOR byte a byte
        }

        // 3. Salvar o arquivo cifrado
        Files.write(Paths.get(ARQUIVO_CIFRADO_OTP), encryptedBytes);
        System.out.println("Arquivo cifrado com sucesso: " + ARQUIVO_CIFRADO_OTP);

        // 4. Salvar a chave One-Time Pad (CRÍTICO: A chave deve ser mantida SEGREDA e usada APENAS UMA VEZ)
        // Crie o diretório se não existir
        File keyDir = new File("./Dados/ChavesOTP/");
        if (!keyDir.exists()) {
            keyDir.mkdirs();
        }
        Files.write(Paths.get(ARQUIVO_CHAVE_OTP), keyBytes);
        System.out.println("Chave One-Time Pad salva em: " + ARQUIVO_CHAVE_OTP);
        System.out.println("AVISO: Esta chave deve ser usada apenas uma vez para decifrar e mantida em segurança!");
    }

    public static void decryptFile() throws IOException {
        File encryptedFile = new File(ARQUIVO_CIFRADO_OTP);
        File keyFile = new File(ARQUIVO_CHAVE_OTP);

        if (!encryptedFile.exists()) {
            System.out.println("Erro: O arquivo cifrado '" + ARQUIVO_CIFRADO_OTP + "' não existe.");
            return;
        }
        if (!keyFile.exists()) {
            System.out.println("Erro: O arquivo de chave '" + ARQUIVO_CHAVE_OTP + "' não existe.");
            System.out.println("Não é possível decifrar sem a chave correta.");
            return;
        }

        byte[] encryptedBytes = Files.readAllBytes(encryptedFile.toPath());
        byte[] keyBytes = Files.readAllBytes(keyFile.toPath());

        if (encryptedBytes.length != keyBytes.length) {
            System.out.println("Erro: O tamanho do arquivo cifrado e o tamanho da chave não correspondem.");
            System.out.println("Possível corrupção de dados ou chave incorreta.");
            return;
        }

        // Decifrar os dados usando XOR (a mesma operação XOR é usada para cifrar e decifrar)
        byte[] decryptedBytes = new byte[encryptedBytes.length];
        for (int i = 0; i < encryptedBytes.length; i++) {
            decryptedBytes[i] = (byte) (encryptedBytes[i] ^ keyBytes[i]); // Operação XOR byte a byte
        }

        // Salvar o arquivo decifrado
        Files.write(Paths.get(ARQUIVO_DECIFRADO_OTP), decryptedBytes);
        System.out.println("Arquivo decifrado com sucesso: " + ARQUIVO_DECIFRADO_OTP);

        // Opcional: Remover a chave após o uso para manter a propriedade de One-Time Pad.
        // Cuidado: Se você precisar decifrar novamente, precisará da chave.
        // keyFile.delete();
        // System.out.println("Chave One-Time Pad removida para garantir a unicidade de uso.");
    }

}
