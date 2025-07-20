import java.io.RandomAccessFile;
import java.util.*;

//A estrutura do Hash estendido foi a opcao escolhida pois é a mais dinamica e o otimizada com relacao as tabelas hash

// Classe que representa uma entrada no hash (par ID e endereço)
class EntradaHash {

    private int id; //ID do elemento
    private long endereco; //Endereco onde o elemento esta armazenado

    //Construtor que define o ID e o endereco
    public EntradaHash(int id, long endereco) {
        this.id = id;
        this.endereco = endereco;
    }

    public int getID() {
        return id;
    }

    public long getEndereco() {
        return endereco;
    }

    public void setEndereco(long endereco) {
        this.endereco = endereco;
    }
    /* 
      
     public String toString() {
        return "ID: s" + id + ", Endereço: " + endereco;
    }
    */
}

//Classe do Hash Estendido
public class HashEstendido {

    private final int TAM_BUCKET = 4; //Capacidade maxima de elementos por buckets
    private int profundidadeGlobal; //Profundidade global do diretorio
    private ArrayList<ArrayList<EntradaHash>> diretorio; //Diretorio contendo buckets


    //Construtor da tabela hash
    public HashEstendido() {
        
        this.profundidadeGlobal = 1; //Inicializa a profundidade global com 1
        this.diretorio = new ArrayList<>();

        //Cria 2 buckets iniciais
        for (int i = 0; i < Math.pow(2, profundidadeGlobal); i++) {
            diretorio.add(new ArrayList<>()); //Adiciona buckets vazios 
        }
    }

    //Modulo da profundidade global
    private int hash(int id) {
        return id % (int) Math.pow(2, profundidadeGlobal);
    }
    

    //Metodo para inserir um novo id na tabela hash
    public void inserir(int id, long endereco) {

        int pos = hash(id); //Calcula a posicao
        ArrayList<EntradaHash> bucket = diretorio.get(pos); //Obtem o bucket correspondente

        //Verifica se o elemento ja existe para atualizar 
        for (EntradaHash entrada : bucket) {
            if (entrada.getID() == id) {
                entrada.setEndereco(endereco); // Atualiza se já existe
                return;
            }
        }

        //Adiciona se ainda tiver espaco no bucket
        if (bucket.size() < TAM_BUCKET) {
            bucket.add(new EntradaHash(id, endereco));

        } else { //Se o bucket estiver cheia, expande a tabela e tenta inserir novamente
            expandir();
            inserir(id, endereco); // Tenta inserir novamente
        }
    }

    //Metodo que expande a profundidade global da tabela
    private void expandir() {
        profundidadeGlobal++; //Aumenta a profundidade global
        ArrayList<ArrayList<EntradaHash>> novoDiretorio = new ArrayList<>();
        int novoTamanho = (int) Math.pow(2, profundidadeGlobal); //Novo tamanho do diretorio

        //Cria novos buckets no novo diretorio
        for (int i = 0; i < novoTamanho; i++) {
            novoDiretorio.add(new ArrayList<>());
        }

        //Reorganiza todos os elementos dos buckets antigos no novo diretorio
        for (ArrayList<EntradaHash> bucket : diretorio) {

            for (EntradaHash entrada : bucket) {
                int novaPos = hash(entrada.getID()); //Recalcula a nova posicao com nova profundidade
                novoDiretorio.get(novaPos).add(entrada); //adiciona ao novo bucket
            }
        }

        //Substitui o diretorio antigo pelo novo
        this.diretorio = novoDiretorio;
    }

    //Metodo para buscar um endeco pelo ID
    public long buscar(int id) {
        int pos = hash(id); //Calcula a posicao hash
        ArrayList<EntradaHash> bucket = diretorio.get(pos); //Acessa o bucket correspondente

        //Busca dentro do bucket
        for (EntradaHash entrada : bucket) {
            if (entrada.getID() == id) {
                //Exibe os dados encontrados
                //System.out.println("ID: s" + entrada.getID());
                //System.out.println("Endereço: " + entrada.getEndereco());
                return entrada.getEndereco(); //retorna o endereco encontrado
            }
        }
        return -1; // Não encontrado
    }
    
    //Método para buscar mais de um elemento
    public long[] buscaVariosElementos(ArrayList<String> elementos){

        long[] enderecos = new long [elementos.size()]; //Array de IDs
        
        for(int i = 0; i < elementos.size(); i++){
            //Remove o caracter "s" do ID e converte para inteiro
            int id = Integer.parseInt(elementos.get(i).replace("s", "0"));
            enderecos[i] = this.buscar(id); //Busca o endereco correspondente
        }

        return enderecos; //Retorna os enderecos encontrados
    }

    //Metodo que atualiza o endereco de um ID
    public boolean atualizar(int id, long novoEndereco) {
        int pos = hash(id); //Calcula a posicao do hash
        ArrayList<EntradaHash> bucket = diretorio.get(pos); //Obtem o bucket correspondente

        //Procura o elemento no bucket
        for (EntradaHash entrada : bucket) {
            if (entrada.getID() == id) {
                entrada.setEndereco(novoEndereco); //Atualiza o endereco
                return true;
            }
        }
        return false;
    }

    //Metodo para remover um ID da tabela hash
    public boolean remover(int id) {
        int pos = hash(id); //Calcula a posicao hash
        ArrayList<EntradaHash> bucket = diretorio.get(pos); //Acessa o bucket

        //Procura pelo ID no bucket
        for (int i = 0; i < bucket.size(); i++) {
            if (bucket.get(i).getID() == id) {
                bucket.remove(i); //Remove o elemento
                return true;
            }
        }
        return false;
    }

    //Metodo para imprimir o conteudo da tabela hash
    public void imprimirHash() {
        System.out.println("\n--- Estrutura Hash ---");

        //Percorre por todos os buckets do diretorio
        for (int i = 0; i < diretorio.size(); i++) {
            System.out.print("[" + i + "] ");
            for (EntradaHash entrada : diretorio.get(i)) {
                System.out.print(entrada + " "); //Imprime cada entrada do bucket
            }
            System.out.println(); //Nova linha para o proximo bucket
        }
    }

    public void criarHashArquivo(String pathFile){
        try (RandomAccessFile arq = new RandomAccessFile(pathFile, "r")){
            while (arq.getFilePointer() < arq.length()) {
                this.inserir(arq.readInt(), arq.readLong());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
