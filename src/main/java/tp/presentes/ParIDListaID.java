package tp.presentes;
import tp.presentes.aed3.*;

import java.io.*;
import java.util.Objects;

public class ParIDListaID implements RegistroHashExtensivel<ParIDListaID> {

    protected int idLista;
    protected int id;

    public ParIDListaID() {
        this(-1, -1);
    }

    public ParIDListaID(int idLista, int id) {
        this.idLista = idLista;
        this.id = id;
    }

    /**
     * Hash baseado apenas em idLista — usado para agrupar entradas da mesma lista no bucket.
     */
    public static int hash(int idLista) {
        return Math.abs(Integer.hashCode(idLista));
    }

    /**
     * HashCode da instância: apenas idLista é usado para decidir bucket na HashExtensivel.
     */
    @Override
    public int hashCode() {
        return Math.abs(Integer.hashCode(idLista));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParIDListaID)) return false;
        ParIDListaID other = (ParIDListaID) o;
        return this.idLista == other.idLista && this.id == other.id;
    }

    public int getIdLista() {
        return idLista;
    }

    public int getId() {
        return id;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }

    public void setIdProduto(int id) {
        this.id = id;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idLista);
        dos.writeInt(id);
        dos.flush();
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idLista = dis.readInt();
        id = dis.readInt();
    }

    @Override
    public int size() {
        // dois ints = 4 + 4 = 8 bytes
        return Integer.BYTES * 2;
    }
}