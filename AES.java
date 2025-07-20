import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.io.UnsupportedEncodingException;

/*Escolhemos a cifra de bloco AES por que esta cifra é amplamente utilizada e considerada muito segura para criptografia 
de dados. Ela opera em blocos de 128 bits e suporta chaves de 128, 192 ou 256 bits. Neste exemplo, utilizaremos uma chave 
de 128 bits (16 bytes). A cifra AES é eficiente e rápida, tornando-a adequada para criptografar arquivos de tamanho 
considerável, como o banco de dados Netflix. 
*/

public class AES {

    private static final String ARQUIVO_ORIGINAL_DB = "./Dados/ArquivoOriginal/netflix1.db";
    private static final String ARQUIVO_CIFRADO_AES = "./Dados/CifradoAES/netflix1_AES.db_cifrado";
    private static final String ARQUIVO_DECIFRADO_AES = "./Dados/DecifradoAES/netflix1_AES.db_decifrado";

    // Chave fixa
    private static final String AES_KEY_STRING = "MySuperSecretKey"; // 16 bytes (128 bits) para AES-128
    private static SecretKeySpec aesKey;

    static {
        try {
            // Garante que a chave tenha 16 bytes (128 bits) para AES-128
            byte[] keyBytes = AES_KEY_STRING.getBytes("UTF-8");
            if (keyBytes.length != 16) {
                // Ajusta o tamanho da chave se não for 16 bytes.
                // É mais seguro garantir que a string original já tenha 16 caracteres/bytes.
                byte[] paddedKeyBytes = new byte[16];
                System.arraycopy(keyBytes, 0, paddedKeyBytes, 0, Math.min(keyBytes.length, 16));
                aesKey = new SecretKeySpec(paddedKeyBytes, "AES");
                System.out.println("Aviso: A chave AES_KEY_STRING foi ajustada para 16 bytes.");
            } else {
                aesKey = new SecretKeySpec(keyBytes, "AES");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.err.println("Erro ao inicializar a chave AES.");
        }
    }
    
    public static void encryptFile() throws IOException {
        File originalFile = new File(ARQUIVO_ORIGINAL_DB);

        if (!originalFile.exists()) {
            System.out.println("Erro: O arquivo original '" + ARQUIVO_ORIGINAL_DB + "' não existe. Por favor, importe o CSV primeiro.");
            return;
        }

        // Crie o diretório 'CifradoAES' se não existir
        File cifradoDir = new File("./Dados/CifradoAES/");
        if (!cifradoDir.exists()) {
            cifradoDir.mkdirs();
        }

        byte[] originalBytes = Files.readAllBytes(originalFile.toPath());

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[cipher.getBlockSize()]; // AES tem 16 bytes de tamanho de bloco para IV
            random.nextBytes(iv); // Gera um IV aleatório para esta operação
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(originalBytes);

            // Concatena o IV com os dados criptografados para armazenamento no arquivo de saída
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(iv); // Escreve o IV primeiro
            outputStream.write(encryptedBytes); // Em seguida, os dados criptografados

            Files.write(Paths.get(ARQUIVO_CIFRADO_AES), outputStream.toByteArray());
            System.out.println("Arquivo cifrado com sucesso (AES): " + ARQUIVO_CIFRADO_AES);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            System.err.println("Erro durante a criptografia AES do arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void decryptFile() throws IOException {
        File encryptedFile = new File(ARQUIVO_CIFRADO_AES);

        if (!encryptedFile.exists()) {
            System.out.println("Erro: O arquivo cifrado (AES) '" + ARQUIVO_CIFRADO_AES + "' não existe. Cifre-o primeiro.");
            return;
        }

        // Crie o diretório 'DecifradoAES' se não existir
        File decifradoDir = new File("./Dados/DecifradoAES/");
        if (!decifradoDir.exists()) {
            decifradoDir.mkdirs();
        }

        byte[] encryptedDataWithIV = Files.readAllBytes(encryptedFile.toPath());

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            int ivSize = cipher.getBlockSize(); // Tamanho do IV (16 bytes para AES)

            if (encryptedDataWithIV.length < ivSize) {
                System.out.println("Erro: O arquivo cifrado é muito curto para conter o IV.");
                return;
            }

            byte[] iv = new byte[ivSize];
            System.arraycopy(encryptedDataWithIV, 0, iv, 0, ivSize); // Extrai o IV
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            byte[] cipherText = new byte[encryptedDataWithIV.length - ivSize];
            System.arraycopy(encryptedDataWithIV, ivSize, cipherText, 0, encryptedDataWithIV.length - ivSize); // Extrai os dados cifrados

            cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(cipherText);

            Files.write(Paths.get(ARQUIVO_DECIFRADO_AES), decryptedBytes);
            System.out.println("Arquivo decifrado com sucesso (AES): " + ARQUIVO_DECIFRADO_AES);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            System.err.println("Erro durante a descriptografia AES do arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
