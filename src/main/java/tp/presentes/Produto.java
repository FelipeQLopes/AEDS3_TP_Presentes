package tp.presentes;

import tp.presentes.aed3.Registro;
import java.io.*;

public class Produto implements Registro {
    private int id;
    private String nome;
    private String gtin13;
    private String descricao;
    private boolean ativo;

    public Produto() {
        this.id = -1;
        this.nome = "";
        this.gtin13 = "";
        this.descricao = "";
        this.ativo = true;
    }

    public Produto(String nome, String gtin13, String descricao, boolean ativo) {
        this.id = -1;
        this.nome = nome;
        this.gtin13 = gtin13;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getGtin13() {
        return gtin13;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setGtin13(String gtin13) {
        this.gtin13 = gtin13;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "\nProduto: " + nome +
               "\nGTIN-13: " + gtin13 +
               "\nDescrição: " + descricao;
    }

    // Serialização binária (gravação no arquivo)
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeUTF(nome);
        dos.writeUTF(gtin13);
        dos.writeUTF(descricao);
        dos.writeBoolean(ativo);
        return baos.toByteArray();
    }

    // Desserialização binária (leitura do arquivo)
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        nome = dis.readUTF();
        gtin13 = dis.readUTF();
        descricao = dis.readUTF();
        ativo = dis.readBoolean();
    }
}
