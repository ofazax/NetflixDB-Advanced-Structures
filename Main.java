import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        
        CRUD fh;
        TreeBplus tree;
        HashEstendido hash;
        
        fh = new CRUD();
        fh.importarCSVParaBinario();
        
        // Verifica se o arquivo da árvore B+ já existe
        if(!new File("./Dados/ArquivosID/idsArvore.db").exists()){
            System.out.println("Digite a ordem da Árvore B+: ");
            int ordem = Integer.parseInt(scan.nextLine());
            tree = fh.gerarArquivoIDsArvore("./Dados/ArquivosID/idsArvore.db", ordem);
            // Se o arquivo já existe, cria a árvore B+ a partir do arquivo existente
        } else {
            tree = new TreeBplus();
            tree.criarArvoreArquivo("./Dados/ArquivosID/idsArvore.db");
        }
        
        // Verifica se o arquivo do hash estendido já existe
        if (!new File("./Dados/ArquivosID/idsHash.db").exists()) {
            hash = fh.gerarArquivoIDsHash("./Dados/ArquivosID/idsHash.db"); // Gera o novo arquivo apenas com IDs hash
            // Se o arquivo já existe, cria o hash estendido a partir do arquivo existente
        }else{
            hash = new HashEstendido();
            hash.criarHashArquivo("./Dados/ArquivosID/idsHash.db");
        }
        
        Main main = new Main();
        main.menu(fh, tree, hash);
        scan.close();
        
    }

    public void menu(CRUD metodos, TreeBplus arvore, HashEstendido hash) throws IOException {

        
        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Criar registro");
            System.out.println("2. Buscar 1 registro");
            System.out.println("3. Buscar mais de 1 registro");
            System.out.println("4. Atualizar registro");
            System.out.println("5. Deletar registro");
            System.out.println("6. Realizar a compressão do arquivo");
            System.out.println("7. Descomprimir o arquivo");
            System.out.println("8. Pesquisar por palavra-chave com KMP");
            System.out.println("9. Realizar a cifra One Time Pad");
            System.out.println("10. Desfazer a cifra One Time Pad");
            System.out.println("11. Realizar a cifra AES");
            System.out.println("12. Desfazer a cifra AES");
            System.out.println("0. Sair");
            System.out.print("Digite sua escolha: ");
            int opcao = Integer.parseInt(scan.nextLine());

            switch (opcao) {
                // Criar um novo registro
                case 1:
                    System.out.println("\nDigite os atributos necessarios:");
                    System.out.println("\nTipo: ");
                    String tipo = scan.nextLine();
                    System.out.println("\nTitulo");
                    String titulo = scan.nextLine();
                    System.out.println("\nDiretor");
                    String diretor = scan.nextLine();
                    System.out.println("\nPaís");
                    String pais = scan.nextLine();
                    System.out.println("\nDuração");
                    String duracao = scan.nextLine();

                    System.out.println("\nData (mm/dd/aaaa): ");
                    String data = scan.nextLine();

                    System.out.println("\nDigite a quantidade de generos: ");
                    int qtd = Integer.parseInt(scan.nextLine());

                    System.out.println("\nDigite os generos: ");
                    ArrayList<String> genero = new ArrayList<String>();
                    for (int i = 0; i < qtd; i++) {
                        genero.add(scan.nextLine());
                    }

                    Netflix nf = new Netflix();
                    nf.setTipo(tipo);
                    nf.setTitulo(titulo);
                    nf.setDiretor(diretor);
                    nf.setPais(pais);
                    nf.setDuracao(Integer.parseInt(duracao));
                    nf.setData(data);
                    nf.setGenero(genero);

                    metodos.escreveNetflixBin(nf, arvore, hash);
                    break;

                case 2:
                    
                    // Buscando ID
                    System.out.println("\nDigite o numero do ID: ");
                    String id = scan.nextLine().replace("s", "0");
                    int idInt = Integer.parseInt(id);
                   
                    //Busca na arvore
                    PonteiroArquivo aux = arvore.buscaElemento(idInt);
                    long enderecoHash = hash.buscar(idInt);
                    
                    System.out.println("\nBusca na arvore:");
                    if(aux != null)
                        metodos.buscaEndereçoRegistro(aux.getEndereco());
                    else{
                        System.out.println("ID s" + idInt + " não encontrado na árvore.");
                        
                    }
                    
                    //Busca no hash
                    System.out.println("\nBusca no Hash Estendido:");
                    if (enderecoHash != -1) {
                        metodos.buscaEndereçoRegistro(enderecoHash);
                    } else {
                        System.out.println("ID s" + idInt + " não encontrado no hash.");
                    }

                    break;  
                           
                case 3:
                    
                    // Buscando IDs
                    System.out.println("\nDigite a quantidade de registros que deseja pesquisar: ");
                    int qtd2 = Integer.parseInt(scan.nextLine());
                    ArrayList<String> Ids = new ArrayList<String>();
                    System.out.println("\nDigite os IDs: ");

                    for (int i = 0; i < qtd2; i++) {
                        Ids.add(scan.nextLine());
                    }
                    
                    // Varios IDs arvore
                    // Busca na arvore
                    System.out.println("\nBusca na arvore:");
                    PonteiroArquivo[] elementos = arvore.buscaVariosElementos(Ids);
                    for (PonteiroArquivo no : elementos) {
                        if(no != null)
                            metodos.buscaEndereçoRegistro(no.getEndereco());
                        else{
                            System.out.println("Elemento não encontrado na arvore.");
                            
                        }
                    }
                    
                    // Varios IDs hash
                    // Busca no Hash Estendido
                    System.out.println("\nBusca no Hash Estendido:");
                    long[] enderecosHash = hash.buscaVariosElementos(Ids);
                    for (long endereco : enderecosHash) {
                        if (endereco != -1) {
                            metodos.buscaEndereçoRegistro(endereco);
                        } else {
                            System.out.println("Elemento não encontrado no hash.");
                            
                        }
                    }
                    break;
                    
                    case 4:
                    // Atualizando registro
                    System.out.println("\nDigite o numero do ID: ");
                    String id2 = scan.nextLine();
                    System.out.println("\nDigite os atributos para a atualização:");
                    System.out.println("\nTipo: ");
                    String tipo2 = scan.nextLine();
                    System.out.println("\nTitulo");
                    String titulo2 = scan.nextLine();
                    System.out.println("\nDiretor");
                    String diretor2 = scan.nextLine();
                    System.out.println("\nPaís");
                    String pais2 = scan.nextLine();
                    System.out.println("\nDuração");
                    String duracao2 = scan.nextLine();
                    System.out.println("\nData (mm/dd/aaaa): ");
                    String data2 = scan.nextLine();
                    System.out.println("\nDigite a quantidade de generos: ");
                    int qtd3 = Integer.parseInt(scan.nextLine());

                    System.out.println("\nDigite os generos: ");
                    ArrayList<String> genero2 = new ArrayList<String>();
                    for (int i = 0; i < qtd3; i++) {
                        genero2.add(scan.nextLine());
                    }

                    Netflix netflix = new Netflix();

                    netflix.setIdFilme(id2);
                    netflix.setTipo(tipo2);
                    netflix.setTitulo(titulo2);
                    netflix.setDiretor(diretor2);
                    netflix.setPais(pais2);
                    netflix.setDuracao(Integer.parseInt(duracao2));
                    netflix.setData(data2);
                    netflix.setGenero(genero2);

                    //Atualiza na arvore e no hash ao mesmo tempo
                    metodos.atualizar(netflix, arvore, hash);

                    break;

                case 5:
                    // Delete
                    System.out.println("\nDigite o ID do registro que deseja deletar: ");
                    String ID = scan.nextLine().replace("s", "0");

                    //Deleta na arvore e no hash ao mesmo tempo
                    metodos.deletar(ID, arvore, hash);
                    System.out.println("Registro excluido na arvore e no hash");
                    break;

                
                case 6:
                    //Compressao Huffman e LZW
                    System.out.println("\nCompactando com Huffman e LZW...\n");
                    try {
                        Compressor.compressAll();
                    } catch (Exception e) {
                        System.out.println("Erro ao compactar: " + e.getMessage());
                    }
                    break;

                case 7:
                    //Descompactacao Huffman e LZW
                    System.out.print("\n ---Descompactação (Huffman e LZW)---\n");
                    
                    try {
                        Compressor.decompress("Huffman", "LZW");
                    } catch (Exception e) {
                        System.out.println("Erro ao descomprimir: " + e.getMessage());
                    }
                    break;
                case 8:
                    // Pesquisa por palavra-chave com KMP
                    System.out.println("\nDigite a palavra-chave para pesquisa: ");
                    String palavraChave = scan.nextLine();
                    System.out.println("\nResultados da pesquisa:");
                    try {
                        metodos.pesquisarPalavraChave(palavraChave);
                    } catch (IOException e) {
                        System.out.println("Erro ao realizar a pesquisa: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case 9:
                    // Realizar a cifra One Time Pad
                    System.out.println("\nCifrando o arquivo netflix1.db usando One Time Pad...");
                    try {
                        // Crie o diretório 'CifradoOTP' se não existir
                        File cifradoDir = new File("./Dados/CifradoOTP/");
                        if (!cifradoDir.exists()) {
                            cifradoDir.mkdirs();
                        }
                        // Crie o diretório 'ChavesOTP' se não existir
                        File chavesDir = new File("./Dados/ChavesOTP/");
                        if (!chavesDir.exists()) {
                            chavesDir.mkdirs();
                        }
                        OneTimePad.encryptFile();
                    } catch (IOException e) {
                        System.out.println("Erro ao cifrar o arquivo: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case 10:
                    // Desfazer a cifra One Time Pad
                    System.out.println("\nDecifrando o arquivo cifrado com One Time Pad...");
                    try {
                        // Crie o diretório 'DecifradoOTP' se não existir
                        File decifradoDir = new File("./Dados/DecifradoOTP/");
                        if (!decifradoDir.exists()) {
                            decifradoDir.mkdirs();
                        }
                        OneTimePad.decryptFile();
                    } catch (IOException e) {
                        System.out.println("Erro ao decifrar o arquivo: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 11:
                    // Realizar a cifra AES
                    System.out.println("\nCifrando o arquivo netflix1.db usando AES...");
                    try {
                        AES.encryptFile(); // Chama o método de criptografia de arquivo AES
                    } catch (IOException e) {
                        System.out.println("Erro ao cifrar o arquivo com AES: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                    
                case 12:
                    // Desfazer a cifra AES
                    System.out.println("\nDecifrando o arquivo cifrado com AES...");
                    try {
                        AES.decryptFile(); // Chama o método de descriptografia de arquivo AES
                    } catch (IOException e) {
                        System.out.println("Erro ao decifrar o arquivo com AES: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 0:
                    // Fim
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

}