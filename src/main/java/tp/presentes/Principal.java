package tp.presentes;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


public class Principal {

    public static void main(String[] args) {
        try {
            ArquivoUsuario arqUsuarios = new ArquivoUsuario();
            ArquivoLista arqListas = new ArquivoLista();
            Files.createDirectories(Paths.get("./src/main/resources/dados/listaInvertida"));

            Scanner console = new Scanner(System.in);
            int usuarioLogadoId = -1;

            int opcao;
            do {
                System.out.println("\n\nAEDsIII");
                System.out.println("-------");
                System.out.println("> Início");
                System.out.println("\n1 - PresenteFácil 1.0");
                System.out.println("0 - Sair");

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
