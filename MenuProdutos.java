import java.util.Scanner;

public class MenuProdutos {
    public static void main(String[] args) {
        ProdutoCRUD crud = new ProdutoCRUD();
        Scanner scanner = new Scanner(System.in);
        String opcao;

        do {
            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Início > Produtos\n");

            System.out.println("(1). Buscar produtos por GTIN");
            System.out.println("(2). Listar todos os produtos");
            System.out.println("(3). Cadastrar um novo produto");
            System.out.println("(R). Retornar ao menu anterior");
            System.out.print("Opção: ");

            opcao = scanner.nextLine().trim().toUpperCase();

            switch (opcao) {
                case "1":
                System.out.println("Digite o código Gtin do produto:");
                String valor = scanner.nextLine();
                crud.buscaGtin(valor);
                    break;
                case "2":
                    crud.listarProdutos();
                    break;

                case "3":
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("GTIN-13: ");
                    String gtin = scanner.nextLine().trim();
                    System.out.print("Descrição: ");
                    String desc = scanner.nextLine();
                    crud.adicionarProduto(nome, gtin, desc);
                    break;

                case "R":
                    System.out.println("Voltando...\n");
                    Principal.main(new String[0]);
                    break;

                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (!opcao.equals("R"));

        scanner.close();
    }
}
