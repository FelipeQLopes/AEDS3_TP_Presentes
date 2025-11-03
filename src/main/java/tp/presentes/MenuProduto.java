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
                    
                    break;                
                case 2:
                    
                    break;
                case 3:
                    
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