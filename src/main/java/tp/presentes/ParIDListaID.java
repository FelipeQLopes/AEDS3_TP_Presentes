package tp.presentes;
import tp.presentes.aed3.*;
import java.io.*;

public class ParIDListaID implements RegistroHashExtensivel<ParIDListaID> {

    protected int idLista;
    protected int idProduto;
    protected int id; // id do registro ListaProduto no arquivo

    public ParIDListaID() {
        this(-1, -1, -1);
    }

    public ParIDListaID(int idLista, int idProduto, int id) {
        this.idLista = idLista;
        this.idProduto = idProduto;
        this.id = id;
    }

    /** 
     * Gera um hash único baseado em (idLista, idProduto)
     */
    public static int hash(int idLista, int idProduto) {
        int h = 7;
        h = 31 * h + Integer.hashCode(idLista);
        h = 31 * h + Integer.hashCode(idProduto);
        return Math.abs(h);
    }

    @Override
    public int hashCode() {
        return hash(idLista, idProduto);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParIDListaID)) return false;
        ParIDListaID other = (ParIDListaID) o;
        return this.idLista == other.idLista && this.idProduto == other.idProduto;
    }

    public int getIdLista() {
        return idLista;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public int getId() {
        return id;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idLista);
        dos.writeInt(idProduto);
        dos.writeInt(id);
        dos.flush();
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        idLista = dis.readInt();
        idProduto = dis.readInt();
        id = dis.readInt();
    }

    @Override
    public int size() {
        // três ints = 12 bytes
        return Integer.BYTES * 3;
    }
}
