import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class Netflix{
    
    private String idFilme;
    private int duracao;
    private String tipo;
    private String titulo;
    private String diretor;
    private String pais;
    private Date data;
    private ArrayList<String> genero;

    public Netflix(int duracao, String tipo, String titulo, String diretor, String pais, Date data, ArrayList<String> genero){
        this.duracao = duracao;
        this.tipo = tipo;
        this.titulo = titulo;
        this.diretor = diretor;
        this.pais = pais;
        this.data = data;
        this.genero = genero;

    }

    public Netflix(){
        this.idFilme = "";
        this.duracao = -1;
        this.tipo = "";
        this.titulo = "";
        this.diretor = "";
        this.pais = "";
        this.data = null;
        this.genero = new ArrayList<String>();
    }
    
    public String getIdFilme() {
        return idFilme;
    }
    public int getIntIdFilme(){

        String aux = this.idFilme.replace("s","0"); // Ignorando o s e passado para 0, que não vai atrapalhar na hora da musica

        return Integer.parseInt(aux);
    }
    public void setIdFilme(String idFilme) {
        this.idFilme = idFilme;
    }
    public int getDuracao() {
        return duracao;
    }
    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }
    public void setDuracao(String duracao) {
        String[] aux = duracao.split(" ");
        this.duracao = Integer.parseInt(aux[0]);
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getDiretor() {
        return diretor;
    }
    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }
    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }
    public String getData() {
        if (data == null) return "Data não definida";
        DateFormat d = new SimpleDateFormat("dd/MM/yyyy");
        return d.format(data);
    }
    
    public void setData(String data){
        DateFormat d = new SimpleDateFormat("MM/dd/yyyy");
        d.setLenient(false); // Garante que só aceita datas válidas

        try {
            this.data = d.parse(data);
        } catch (ParseException e) {
            
            e.printStackTrace();
        }
    }
    public void setData(long timestamp){

        this.data = new Date(timestamp);
        
    }

    public ArrayList<String> getGenero() {
        return genero;
    }
    public void setGenero(ArrayList<String> genero) {
        this.genero = genero;
    }

    public void setGenero(String generos){

        String[] generosRaw = generos.split(","); 
        
        if(generosRaw.length <= 1){
            this.genero.add(generos);
            return;
        }
        
        for(int i = 0; i < generosRaw.length; i++){
            if(i == 0){
                String aux = "";
                for(int j = 1; j < generosRaw[i].length(); j++){
                    aux += generosRaw[i].charAt(j);
                }
                this.genero.add(aux);
            } else if(i == generosRaw.length - 1){
                String aux = "";
                for(int j = 0; j < generosRaw[i].length() - 1; j++){
                    aux += generosRaw[i].charAt(j);
                }
                this.genero.add(aux);
            } else {
                this.genero.add(generosRaw[i]);
            }
        }
    }

    private String generoToString(){
        String aux = "";
        for (int i = 0; i < this.genero.size()-1; i++){
            aux += this.genero.get(i) + ",";
        }
        aux += this.genero.get(this.genero.size()-1);
        return aux;
    }
    
    public String toString(){
        return "\nID: " + idFilme + "| Tipo: " + tipo + " | Titulo: " + titulo + " | Diretor: " + diretor + " | País: " + pais + " | Duração: " + String.valueOf(this.duracao) + " min" + " | Data: " + this.getData() + " | Generos:" + this.generoToString();
    }

    //Escrever no arquivo
    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeUTF(idFilme);
        dos.writeUTF(tipo);
        dos.writeUTF(titulo);
        dos.writeUTF(diretor);
        dos.writeUTF(pais);        
        dos.writeInt(duracao);
        
        dos.writeLong(this.data.getTime());
        dos.writeShort(genero.size()); //INFORMA A QUANTIDADE DE GENEROS
        for(int i = 0; i < genero.size(); i++){
            dos.writeUTF(genero.get(i));
        }
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.idFilme = dis.readUTF();
        this.tipo = dis.readUTF();
        this.titulo = dis.readUTF();
        this.diretor = dis.readUTF();
        this.pais = dis.readUTF();
        this.duracao = dis.readInt();
        this.setData(dis.readLong()); 

        int tamVet = dis.readShort();
        for (int i = 0; i < tamVet; i++) {
            this.genero.add(dis.readUTF());
        }
        
    }

    
}
