import java.time.LocalDate;

import aed3.Registro;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cliente implements Registro {

    public int id;
    public String nome;
    public String cpf;
    public String email;
    public String perguntaSecreta;
    public String respostaSecreta;
    public String hashSenha;
    public LocalDate nascimento;
    public int idCategoria;

    public Cliente() {
        this(-1, "", "", "", "", "", "", LocalDate.now());
    }
    public Cliente(String n, String c, String e, String p, String r, String s, LocalDate d) {
        this(-1, n, c, e, p, r, s, d);
    }

    public Cliente(int i, String n, String c, String e, String p, String r, String s, LocalDate d) {
        this.id = i;
        this.nome = n;
        this.cpf = c;
        this.email = e;
        this.perguntaSecreta = p;
        this.respostaSecreta = r;
        this.hashSenha = gerarHash(s);
        this.nascimento = d;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String toString() {
        return "\nID........: " + this.id +
               "\nNome......: " + this.nome +
               "\nCPF.......: " + this.cpf +
               "\nEmail.....: " + this.email +
               "\nNascimento: " + this.nascimento;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.write(this.cpf.getBytes());
        dos.writeUTF(this.email);
        dos.writeUTF(this.perguntaSecreta);
        dos.writeUTF(this.respostaSecreta);
        dos.writeUTF(this.hashSenha);
        dos.writeInt((int) this.nascimento.toEpochDay());
        return baos.toByteArray();
    }


    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        byte[] cpf = new byte[11];
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        dis.read(cpf);
        this.cpf = new String(cpf);
        this.email = dis.readUTF();
        this.perguntaSecreta = dis.readUTF();
        this.respostaSecreta = dis.readUTF();
        this.hashSenha = dis.readUTF();
        this.nascimento = LocalDate.ofEpochDay(dis.readInt());
    }

    public static String gerarHash(String senha) {
        try {
            // Escolhendo o algoritmo (pode ser "SHA-256", "SHA-512", etc.)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Transforma a senha em bytes e calcula o hash
            byte[] hashBytes = md.digest(senha.getBytes());

            // Converte o hash em hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString(); // Retorna o hash em formato de string hex
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
}