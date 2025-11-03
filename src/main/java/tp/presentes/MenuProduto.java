package tp.presentes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import tp.presentes.aed3.ArvoreBMais;


public class MenuProduto {

    int ID_GLOBAL;
    String NOME_GLOBAL;
    String CPF_GLOBAL;
    
    ArquivoProduto arqProdutos;
    ArvoreBMais<ParIntInt> relacaoProdutoLista;
    ArvoreBMais<ParIntInt> relacaoListaProduto;
    private static Scanner console = new Scanner(System.in);
    

    public MenuProduto(int ID_GLOBAL, String NOME_GLOBAL, String CPF_GLOBAL) throws Exception {

        this.ID_GLOBAL = ID_GLOBAL;
        this.NOME_GLOBAL = NOME_GLOBAL;
        this.CPF_GLOBAL = CPF_GLOBAL;
        this.arqProdutos = new ArquivoProduto();
        this.relacaoProdutoLista = new ArvoreBMais<>(ParIntInt.class.getConstructor(), 5, "./src/main/resources/dados/relacaoProdutoLista.db");
        this.relacaoListaProduto = new ArvoreBMais<>(ParIntInt.class.getConstructor(), 5, "./src/main/resources/dados/relacaoListaProduto.db");
        return;
    }

    //region menu
    public void menu() {

        int opcao;
        do {

            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println(">");
            System.out.println("\n1 - Buscar produtos por GTIN");
            System.out.println("2 - Listar todos os produtos");
            System.out.println("3 - Cadastrar um novo produto");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    buscarProdutoPorGtin();
                    break;                
                case 2:
                    //listarProdutosPaginado();
                    break;
                case 3:
                    cadastrarProduto();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
        
        return;
    }
    //endregion menu

    private void buscarProdutoPorGtin() {
    System.out.print("Digite o GTIN-13: ");
    String gtin = console.nextLine().trim();
    try {
        Produto p = arqProdutos.read(gtin);
        if (p != null) {
            System.out.println("\nProduto encontrado:\n" + p.toString());
            mostrarProdutoDetalhes(p);
        } else {
            System.out.println("Produto não encontrado.");
        }
    } catch (Exception e) {
        System.out.println("Erro ao buscar produto: " + e.getMessage());
    }
}


/*
private void listarProdutosPaginado() {
    
}
*/

private void cadastrarProduto() {
    try {
        System.out.print("Nome: ");
        String nome = console.nextLine().trim();
        System.out.print("Descrição: ");
        String descricao = console.nextLine().trim();
        System.out.print("GTIN-13 (13 dígitos): ");
        String gtin = console.nextLine().trim();
        if (!gtin.matches("\\d{13}")) {
            System.out.println("GTIN inválido. Deve ter 13 dígitos numéricos.");
            return;
        }
        // checar duplicidade
        Produto existente = arqProdutos.read(gtin);
        if (existente != null) {
            System.out.println("Já existe um produto com esse GTIN: " + existente.getNome());
            return;
        }
        Produto p = new Produto(nome, gtin, descricao);
        int id = arqProdutos.create(p);
        System.out.println("Produto cadastrado com ID: " + id);
    } catch (Exception e) {
        System.out.println("Erro ao cadastrar produto: " + e.getMessage());
    }
}

private void mostrarProdutoDetalhes(Produto p) {
    System.out.println("\n" + p.toString());
    // mostrar em quais listas aparece
    try {
        ParIntInt parPesquisa = new ParIntInt(p.getId(), -1);
        ArrayList<ParIntInt> rels = relacaoProdutoLista.read(parPesquisa);
        ArrayList<String> minhas = new ArrayList<>();
        int outras = 0;
        for (ParIntInt par : rels) {
            int idLista = par.getNum2(); // se o par trabalha (produto, idLista) ou vice-versa: ajustar conforme seu uso
            Lista l = new ArquivoLista().read(idLista);
            if (l.getIdUsuario() == ID_GLOBAL) minhas.add(l.getNome());
            else outras++;
        }
        if (!minhas.isEmpty()) {
            System.out.println("\nAparece nas minhas listas:");
            minhas.sort(String::compareToIgnoreCase);
            for (String s : minhas) System.out.println("- " + s);
        }
        System.out.println("\nAparece também em mais " + outras + " listas de outras pessoas.");
    } catch (Exception e) {
        // se erro, só ignora
    }

    System.out.println("\n(1) Alterar os dados do produto\n(2) Inativar o produto\n(R) Retornar");
    System.out.print("Opção: ");
    String op = console.nextLine().trim();
    if (op.equals("1")) {
        alterarProduto(p);
    } else if (op.equals("2")) {
        inativarProduto(p);
    }
}

private void alterarProduto(Produto p) {
    try {
        System.out.print("Novo nome (enter para manter): ");
        String nome = console.nextLine().trim();
        if (!nome.isEmpty()) p.setNome(nome);
        System.out.print("Nova descrição (enter para manter): ");
        String desc = console.nextLine().trim();
        if (!desc.isEmpty()) p.setDescricao(desc);
        boolean ok = arqProdutos.update(p);
        if (ok) System.out.println("Produto alterado com sucesso.");
        else System.out.println("Falha ao alterar produto.");
    } catch (Exception e) {
        System.out.println("Erro: " + e.getMessage());
    }
}

private void inativarProduto(Produto p) {
   
}


    public ArrayList<Produto> mostrarProdutos(Lista lista){

        ParIntInt parPesquisa = new ParIntInt(lista.getId(), -1);
        ArrayList<Produto> listaProdutosLista = new ArrayList<>();
        int contador = 1;
        try {
            ArrayList<ParIntInt> listaProdutos = relacaoListaProduto.read(parPesquisa);

            System.out.println("Produtos:\n");
            for (ParIntInt par : listaProdutos) {
                int idLista = par.getNum1();
                int idProduto = par.getNum2();
                if (idLista == lista.getId()) {
                    Produto produto = arqProdutos.read(idProduto);
                    listaProdutosLista.add(produto);
                    System.out.printf(" (%d) %s | %s | %s \n", contador, produto.getNome(), produto.getDescricao(), produto.getGtin13());
                    contador++;
                }

            }
        } catch (Exception e) {

        }
        return listaProdutosLista;

    }

}