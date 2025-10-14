import java.util.Scanner;

public class MenuProdutos {
    public static void main(String[] args) {
        ProdutoCRUD crud = new ProdutoCRUD();
        Scanner scanner = new Scanner(System.in);
        int opcao;
        //opções de cadastro do produto
        do {
            System.out.println("\n=== CRUD de Produtos ===");
            System.out.println("(1). Buscar produtos por GTIN");
            System.out.println("(2). Listar todos os produtos");
            System.out.println("(3). Cadastrar um novo produto");
            System.out.println("(R). Retornar ao menu anterior");
            System.out.print("Opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcao) {
                case 2 -> crud.listarProdutos();
                case 3 -> {
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("GTIN-13: ");
                    String gtin = scanner.nextLine().trim(); 
                    System.out.print("Descrição: ");
                    String desc = scanner.nextLine();
                    crud.adicionarProduto(nome, gtin, desc);
                }
                
                case 'R' -> {
                    
                }
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        scanner.close();
    }
}
