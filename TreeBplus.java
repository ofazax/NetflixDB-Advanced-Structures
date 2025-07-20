import java.io.RandomAccessFile;
import java.util.ArrayList;

//Escolhemos a estrutura árvore B+ pois ela é a melhor estrutura para buscar mais de 1 id

// Classe que representa um ponteiro para um endereço em arquivo com um ID associado 
class PonteiroArquivo{
    
    private int id;
    private long endereco;

    public PonteiroArquivo(int id, long endereco){
        
        this.id = id;
        this.endereco = endereco;
    }

    public int getID(){
        return id;
    }

    public void setEndereco(long endereco){
        this.endereco = endereco;
    }

    public long getEndereco(){
        return endereco;
    }

    /* 
      
     public String toString(){
        return "\n ID: s"+ id + "\n Endereço: " + endereco;
    }
    */

}

//Classe da arvore B+
public class TreeBplus {
    
    private int ordem; //Ordem da arvore
    private No raiz; //Iniciando um novo nó vazio

    public TreeBplus(int ordem) {
        this.ordem = ordem;
        this.raiz = new No(ordem); //Cria uma raiz inicial
    }

    public TreeBplus() {

    }

    public No getRaiz(){
        return raiz;
    }
    
    public int getOrdem(){
        return this.ordem;
    }

    public void criarArvoreArquivo(String pathFile){
        try (RandomAccessFile arq = new RandomAccessFile(pathFile, "r")){
            this.ordem = arq.readByte();
            this.raiz = new No(this.ordem);
            while (arq.getFilePointer() < arq.length()) {
                this.inserir(new PonteiroArquivo(arq.readInt(), arq.readLong()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Varredura em profundidade
    public void varreduraNiveis(){
        ArrayList<No> listaNos = new ArrayList<>();

        listaNos.add(this.raiz);

        while (!listaNos.isEmpty()) {
            No aux = listaNos.remove(0);
            for (PonteiroArquivo pa : aux.getElementos()) {
                System.out.print(pa+" ");
            }
            System.out.println();
            if(!aux.ehFolha()){
                for (No filho : aux.getFilhos()) {
                    listaNos.add(filho);
                }
                
            }
        }
    }

    //Metodo para inserir na arvore
    public void inserir(PonteiroArquivo elemento){

        No novoNo = inserirNo(raiz,elemento);

        //Se a raiz foi dividida, cria uma nova raiz
        if(novoNo != null){
            No nextage = new No(ordem); //Nova raiz
            nextage.addPonteiro(-1, raiz); //Ponteiro para a antiga raiz
            nextage.addPonteiro(0, novoNo); //Ponteiro para o novo nó
            nextage.inserirElemento(novoNo.getIndiceElemento(0)); //Insere o valor que divide os dois filhos
            
            if (!novoNo.ehFolha()) {
                novoNo.removeElemento(0); //Remove o valor que subiu
            }
            raiz = nextage; //atualiza a raiz
        }
    }

    //Inserção recursiva
    private No inserirNo(No no, PonteiroArquivo elemento){
        No resp = null;

        if(!no.ehFolha()){ //Se não for folha, desce a árvore
            resp = inserirNo(no.proximoNo(elemento.getID()), elemento);
        }else{
            no.inserirElemento(elemento); //Insere na folha
            if (!no.verificaOrdem()) {
                return no.separaRetornaFolha(); //Se for necessario divide a folha
            }
            return null;
        }

        //Se um nó filho foi divido
        if(resp != null){
            int aux = no.inserirElemento(resp.getIndiceElemento(0)); //Sobe valor
            if(!resp.ehFolha()){
                resp.removeElemento(0);
            }

            no.addPonteiro(aux, resp); //Adiciona um novo ponteiro

            if (!no.verificaOrdem()) {
                return no.separaRetorna(); //Divide no interno
            }
            return null;
        }
        return null;

    }

    //Método para remover um elemento do nó
    public PonteiroArquivo excluirElemento(int ID){

        //Excluindo um elemento
        No noAtual = raiz;

        while(!noAtual.ehFolha()){
            noAtual = noAtual.proximoNo(ID);
        }
        
        PonteiroArquivo resposta = noAtual.removerElemento(ID);

        //Recriando uma nova arvore
        noAtual = raiz;

        while(!noAtual.ehFolha()){
            noAtual = noAtual.noEsquerda();
        }


        TreeBplus aux = new TreeBplus(ordem);
        while(noAtual.getPonteiroLado() != null){
            for(int i = 0; i < noAtual.getElementos().size(); i++){
                aux.inserir(noAtual.getElementos().get(i));

            }
            noAtual = noAtual.getPonteiroLado();
        }

        for(int i = 0; i < noAtual.getElementos().size(); i++){
            aux.inserir(noAtual.getElementos().get(i));
        }

        raiz = aux.getRaiz();

        return resposta;
    }

    //Método para buscar um elemento
    public PonteiroArquivo buscaElemento(int elemento){

        No noAtual = raiz;

        while(!noAtual.ehFolha()){
            noAtual = noAtual.proximoNo(elemento);
        }

        for(int i = 0; i < noAtual.getElementos().size(); i++){
            if(elemento == noAtual.getElementos().get(i).getID()){
                //System.out.println("Elemento encontrado: " + noAtual.getElementos().get(i));
                //System.out.println("Endereco: " + noAtual.getElementos().get(i).getEndereco());
                return noAtual.getElementos().get(i);
               
            }
        }
        //System.out.println("\nElemento não encontrado\n");
        return null;
    }

    //Método para buscar um elemento
    public PonteiroArquivo quebragalho(int elemento){

        No noAtual = raiz;

        while(!noAtual.ehFolha()){
            noAtual = noAtual.proximoNo(elemento);
        }

        for(int i = 0; i < noAtual.getElementos().size(); i++){
            if(elemento == noAtual.getElementos().get(i).getID()){
                //System.out.println("Elemento encontrado: " + noAtual.getElementos().get(i) + "\n");
                //System.out.println("Endereco: " + noAtual.getElementos().get(i).getEndereco());
                return noAtual.getElementos().get(i);
               
            }
        }
        System.out.println("\nElemento não encontrado\n");
        return null;
    }

    //Método para buscar mais de um elemento
    public PonteiroArquivo[] buscaVariosElementos(ArrayList<String> elementos){

        PonteiroArquivo[] resp = new PonteiroArquivo[elementos.size()];
        ArrayList<Integer> ids = new ArrayList<>();

        for(String elemento : elementos){
            int num = Integer.parseInt(elemento.replace("s", "0"));
            ids.add(num);
        }
        
        for(int i = 0; i < elementos.size(); i++){
            resp[i] = this.buscaElemento(ids.get(i));
        }

        return resp;
    }

    //Metodo que inicia a impressão
    public void imprimeArvore(){

        this.imprimeArvore(raiz);

    }

    //Metodo auxiliar para imprimir os nós da árvore (de forma recursiva)
    private void imprimeArvore(No noAtual){

        //Percorre os ponteiros filhos
        for(int i = 0; i < noAtual.getFilhos().size(); i++){ 
            imprimeArvore(noAtual.getFilhos().get(i));
        }

        if(noAtual.ehFolha()){ //Imprime apenas as folhas (se quiser imprimir a arvore toda é so tirar o if)
            //Imprime os elementos do nó
            for (int i = 0; i < noAtual.getElementos().size(); i++) { 
                System.out.println(noAtual.getIndiceElemento(i));
            }
            System.out.println("\n");
        }

    }

}

class No{

    private int ordem;
    private ArrayList<PonteiroArquivo> elementosNo;
    private No ponteiroLado; //Ponteiro para o próximo nó (folhas)
    private ArrayList<No> listasPonteiros; //Lista de ponteiros para os filhos
    
    No(int ordem){
        this.ordem = ordem;
        elementosNo = new ArrayList<PonteiroArquivo>();
        ponteiroLado = null;
        listasPonteiros = new ArrayList<No>();//Estamos inserindo e verificando depois

    }

    public ArrayList<No> getFilhos(){
        return listasPonteiros; //Retorna ponteiros
    }

    public ArrayList<PonteiroArquivo> getElementos(){
        return elementosNo; //Retorna elemento do nó
    }

    //Pegando o indice do elemento
    public PonteiroArquivo getIndiceElemento(int i){
        return elementosNo.get(i);
    }

    public No getPonteiroLado(){
        return ponteiroLado; //Retorna o ponteiro que aponta para outra folha
    }



    //Verifica se tem a quatidade de elementos nó está respeitando a ordem
   public boolean verificaOrdem(){
        if(elementosNo.size() < ordem){
            return true;
        }
        return false;    
    }

    //Verifica se o nó é uma folha
    public boolean ehFolha(){

        for(int i = 0; i < listasPonteiros.size(); i++){
            if(listasPonteiros.get(i) != null){
                return false;
            }
        }
        return true;
    }

    public No noEsquerda(){
        return listasPonteiros.get(0);
    }

    //Separa o ultimo elemento do nó e retorna um novo nó
    public No separaRetornaFolha(){

        No novoNo = new No(ordem);

        //Move metade dos elementos para o novo nó
        for(int i = elementosNo.size() - 1; i >= ordem / 2; i--){
            //qtdElementos--;
            novoNo.inserirElemento(elementosNo.remove(i));//Separa os elementos
        }
        ponteiroLado = novoNo;//Interliga os nós folhas
        return novoNo;
        
    }

    //Remove elemento do nó
    public void removeElemento(int index){
        elementosNo.remove(index); 
    }

    //Método para dividir o nó interno
    public No separaRetorna(){

        No novoNo = new No(ordem);

        //Move metade dos elementos e ponteiros para o novo nó
        for(int i = elementosNo.size() - 1; i >= ordem / 2; i--){
            //qtdElementos--;
            novoNo.inserirElemento(elementosNo.remove(i));//Separa os elementos
            novoNo.addPonteiro(-1, listasPonteiros.remove(listasPonteiros.size()-1));
        }
        
        return novoNo;

    }

    //Adicionando o ponteiro para o nó
    public void addPonteiro(int indice, No no){
        listasPonteiros.add(indice + 1, no);
    }

    //Verifica onde o novo elemento vai ser inserido (em qual ponteiro)
    public No proximoNo(int elemento){
        //System.out.println(elementosNo);
        for(int i = 0; i < elementosNo.size(); i++){
            if(elemento < elementosNo.get(i).getID()){
                return listasPonteiros.get(i);
            }
        }
        return listasPonteiros.get(listasPonteiros.size() - 1);
    } 

    //Insere o elemento no nó
    public int inserirElemento(PonteiroArquivo elemento){

        for(int i = 0; i < elementosNo.size(); i++){
            if(elemento.getID() < elementosNo.get(i).getID()){
                elementosNo.add(i, elemento); //Insere ordenado
                return i;
            }
        }
        elementosNo.add(elementosNo.size(), elemento); //Insere no final
        return elementosNo.size() - 1;
    }

    //Remover elemento do nó
    public PonteiroArquivo removerElemento(int elemento){

        for(int i = 0; i < elementosNo.size(); i++){
            if(elemento == elementosNo.get(i).getID()){
                PonteiroArquivo resposta = elementosNo.get(i);
                elementosNo.remove(i);
                return resposta;
            }
        }
        return null;
    }


}
