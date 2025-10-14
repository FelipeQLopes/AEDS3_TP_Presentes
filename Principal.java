import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        try {
            ArquivoUsuario arqUsuarios = new ArquivoUsuario();
            ArquivoLista arqListas = new ArquivoLista();


            Scanner console = new Scanner(System.in);
            int usuarioLogadoId = -1;

            int opcao;
            do {
                System.out.println("\n\nPresenteFácil 1.0\n");
                System.out.println("--------------\n");
                System.out.println("> Início\n");
                System.out.println("(1) Meus dados\n");
                System.out.println("(2) Minhas Listas\n");
                System.out.println("(3) Produtos\n");
                System.out.println("(4) Buscar Lista\n\n");
                System.out.println("(S) - Sair\n");

                System.out.print("\nOpção: ");
                try {
                    opcao = Integer.parseInt(console.nextLine());
                } catch (NumberFormatException e) {
                    opcao = -1;
                }

                switch (opcao) {
                    case 1:
                        (new MenuUsuario()).menu();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }

            } while (opcao != 0);

            console.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
