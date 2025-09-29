package tp.presentes;
import tp.presentes.aed3.*;
import java.time.LocalDate;
import java.io.*;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class Lista implements Registro {

    private int id;
    private String nome;
    private String descricao;
    private String codigo;
    private LocalDate dataCriacao;
    private LocalDate dataLimite;
    private int idUsuario; // vincula a lista a um usuário

    public Lista() {
        this(-1, "", "", LocalDate.now(), -1);
    }

    public Lista(int id, String nome, String descricao, LocalDate dataLimite, int idUsuario) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = LocalDate.now();
        this.dataLimite = dataLimite;
        this.idUsuario = idUsuario;
        this.codigo = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                                               NanoIdUtils.DEFAULT_ALPHABET,
                                               10);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDate getDataLimite() { return dataLimite; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    @Override
    public String toString() {
        return "Lista{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", codigo='" + codigo + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataLimite=" + dataLimite +
                ", idUsuario=" + idUsuario +
                '}';
    }

    // Serialização para bytes (salvar em arquivo)

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.descricao);
        dos.writeUTF(this.codigo);
        dos.writeInt((int) this.dataCriacao.toEpochDay());
        dos.writeInt((int) this.dataLimite.toEpochDay());
        dos.writeInt(this.idUsuario);

        return baos.toByteArray();
    }

    // Desserialização de bytes (ler arquivo)

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF();
        this.codigo = dis.readUTF();
        this.dataCriacao = LocalDate.ofEpochDay(dis.readInt());
        this.dataLimite = LocalDate.ofEpochDay(dis.readInt());
        this.idUsuario = dis.readInt();
    }
}
