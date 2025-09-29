package tp.presentes;
import tp.presentes.aed3.*;

import java.io.*;

public class ParCodigoID implements RegistroHashExtensivel<ParCodigoID> {

    protected static final int TAM_CODIGO = 32; // tamanho fixo em caracteres
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
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Escreve o código com tamanho fixo
        String codigoAjustado = String.format("%-" + TAM_CODIGO + "s", codigo == null ? "" : codigo);
        dos.writeChars(codigoAjustado); // 2 bytes por char

        dos.writeInt(id);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        // Lê o código com tamanho fixo
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TAM_CODIGO; i++) {
            sb.append(dis.readChar());
        }
        codigo = sb.toString().trim();

        id = dis.readInt();
    }

    @Override
    public int size() {
        return TAM_CODIGO * 2 + 4; // 2 bytes por char + 4 bytes do int
    }
}