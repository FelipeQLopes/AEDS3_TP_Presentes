import java.time.LocalDate;

import aed3.Registro;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Lista implements Registro {

    public int id;
    public String nome;
    public String descricao;
    public String codigo;
    public LocalDate dataCriacao;
    public LocalDate dataLimite;

    public Lista() {
        this(-1, "", "", "", LocalDate.now(), LocalDate.now().plusDays(7));
    }
    public Lista(String n, String d, String c, LocalDate d1, LocalDate d2) {
        this(-1, n, d, c, d1, d2);
    }

    public Lista(int i, String n, String d, String c, LocalDate d1, LocalDate d2) {
        this.id = i;
        this.nome = n;
        this.descricao = d;
        this.codigo = c;
        this.dataCriacao = d1;
        this.dataLimite = d2;
    }

public int getId() { 
    return id; 
}
public void setId(int id) { 
    this.id = id; 
}

public String getNome() { 
    return nome; 
}
public void setNome(String nome) { 
    this.nome = nome; 
}

public String getDescricao() { 
    return descricao; 
}
public void setDescricao(String descricao) { 
    this.descricao = descricao; 
}

public String getCodigo() { 
    return codigo; 
}
public void setCodigo(String codigo) { 
    this.codigo = codigo; 
}

public LocalDate getDataCriacao() { 
    return dataCriacao; 
}
public void setDataCriacao(LocalDate dataCriacao) { 
    this.dataCriacao = dataCriacao; 
}

public LocalDate getDataLimite() { 
    return dataLimite; 
}
public void setDataLimite(LocalDate dataLimite) { 
    this.dataLimite = dataLimite; 
}

public String toString() {
    return "Lista{" +
            "id=" + id +
            ", nome='" + nome + '\'' +
            ", descricao='" + descricao + '\'' +
            ", codigo='" + codigo + '\'' +
            ", dataCriacao=" + dataCriacao +
            ", dataLimite=" + dataLimite +
            '}';
}


    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.descricao);
        dos.writeUTF(this.codigo);
        dos.writeInt((int) this.dataCriacao.toEpochDay());
        dos.writeInt((int) this.dataLimite.toEpochDay());
        return baos.toByteArray();
    }


    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF();
        this.codigo = dis.readUTF();
        this.dataCriacao = LocalDate.ofEpochDay(dis.readInt());
        this.dataLimite = LocalDate.ofEpochDay(dis.readInt());

    }

}