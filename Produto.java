import java.util.Scanner;

public class Produto {
    private int id;
    private String gtin13;
    private String nome;
    private String descricao;

    public Produto(String nome, String gtin13, String descricao) {
        this.gtin13 = gtin13;
        this.nome = nome;
        this.descricao = descricao;
    }
    public int getId() {return id;}
    public String getGtin13() { return gtin13; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }

    public void setGtin13(String gtin13) { this.gtin13 = gtin13; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return "NOME: " + nome + '\n' +
        "GTIN-13: " + gtin13 + '\n' +
        "DESCRIÇÃO: " + descricao + '\n';
    }
/* 
    public static void main (String[] args){
 Scanner scan = new Scanner(System.in);

    String nome, g13, descricao1;
    nome = scan.nextLine();
    g13 = scan.nextLine();
    descricao1 = scan.nextLine();

    Produto p = new Produto(nome, g13, descricao1);
    System.out.println(p.toString());
}
 */
    
}


