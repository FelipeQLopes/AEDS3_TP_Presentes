import aed3.RegistroHashExtensivel;

public class ParCodigoID implements RegistroHashExtensivel<ParCodigoID> {

    protected String codigo;
    protected int id;

    public static int hash(String codigo) {
    return Math.abs(codigo.hashCode());
}


    public ParCodigoID() {
        this("", -1);
    }

    public ParCodigoID(String codigo, int id) {
        this.codigo = codigo;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public int hashCode() {
        return Math.abs(codigo.hashCode());
    }

    @Override
    public String toString() {
        return this.codigo + " -> " + this.id;
    }

    @Override
    public byte[] toByteArray() throws java.io.IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
        dos.writeUTF(codigo);
        dos.writeInt(id);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws java.io.IOException {
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(ba);
        java.io.DataInputStream dis = new java.io.DataInputStream(bais);
        codigo = dis.readUTF();
        id = dis.readInt();
    }

    @Override
    public int size() {
        return 2 + codigo.length() * 2 + 4;
    }
}
