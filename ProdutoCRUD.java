import java.util.ArrayList;
import java.util.List;

public class ProdutoCRUD {
    private List<Produto> produtos = new ArrayList<>();
    

    // Criando o produto
    public void adicionarProduto(String nome, String gtin13, String descricao) {
        if (gtin13.length() != 13 || !gtin13.matches("\\d{13}")) {
            System.out.println("GTIN-13 inválido!");
            return;
        }
        Produto p = new Produto(nome, gtin13, descricao);
        produtos.add(p);
        System.out.println("Produto adicionado com sucesso!");
    }

    // Listar os produtos PELO NOME!!
    public void listarProdutos() {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            for (Produto p : produtos) {
                System.out.println(p.getNome());
            }
        }
    }

    public void buscaGtin(String valor){
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
             for (Produto p : produtos) {
        if (p.getGtin13().equals(valor)) {
            System.out.println("Produto encontrado: " + p.getNome());

        }
        }
    }

}
    // Deletar
    public void removerProduto(String nome) {
        produtos.removeIf(p -> p.getNome().equals(nome));
        System.out.println("Produto removido (se existia).");
    }
}
 