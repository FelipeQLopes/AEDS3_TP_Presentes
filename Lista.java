import java.time.LocalDate;

import aed3.Registro;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
//implements Registo
public class Lista {

    public int id;
    public String nome;
    public String descricao;
    public String codigo;
    public LocalDate dataCriacao;
    public LocalDate dataLimite;
    

    //N se torna necessário, n existe lista sem dados
    /*public Lista() {
        this(-1, "", "", LocalDate.now(), LocalDate.now().plusDays(7));
    }*/

    public Lista(String n, String d, LocalDate d2) {
        this(-1, n, d, d2);
       
    }

    public Lista(int i, String n, String d, LocalDate d2) {
        this.id = i;
        this.nome = n;
        this.descricao = d;
        this.dataCriacao = LocalDate.now();
        this.dataLimite = d2;
        this.codigo=NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                                           NanoIdUtils.DEFAULT_ALPHABET,
                                           10);
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

public static void main(String[] args){
    Lista l1 = new Lista ("compra", "lista de compras", LocalDate.of(2025, 12, 23) );
    System.out.println(l1);

}
}

/* 
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

}*/