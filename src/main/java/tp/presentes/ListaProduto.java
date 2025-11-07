package tp.presentes;
import tp.presentes.aed3.*;
import java.io.*;

public class ListaProduto implements Registro {
    private int id;        
    private int idLista;  
    private int idProduto;
    private int quantidade;

    public ListaProduto(){
        
        this(-1,-1,-1,0);

    }

    public ListaProduto(int id, int idLista, int idProduto, int quantidade){

        this.id = id;
        this.idLista = idLista;
        this.idProduto = idProduto;
        this.quantidade = quantidade;

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIdLista() {
        return idLista;
    }
    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }
    public int getIdProduto() {
        return idProduto;
    }
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    // Serialização para bytes (salvar em arquivo)

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeInt(this.idLista);
        dos.writeInt(this.idProduto);
        dos.writeInt(this.quantidade);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.idLista = dis.readInt();
        this.idProduto = dis.readInt();
        this.quantidade = dis.readInt();

    }


}