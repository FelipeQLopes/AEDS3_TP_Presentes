import java.util.Scanner;

public class MenuLista {

    int ID_GLOBAL;
    String NOME_GLOBAL;
    String CPF_GLOBAL;
    
    ArquivoLista arqListas;
    private static Scanner console = new Scanner(System.in);



    public MenuLista(int ID_GLOBAL, String NOME_GLOBAL, String CPF_GLOBAL){
        this.ID_GLOBAL = ID_GLOBAL;
        this.NOME_GLOBAL = NOME_GLOBAL;
        this.CPF_GLOBAL = CPF_GLOBAL;
    }

    public void menu(){

        int opcao;
        do {

            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Home > Listas");
            System.out.println("\n1 - Minhas Listas");
            System.out.println("2 - Buscar Listas");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    minhasListas();
                    break;                
                case 2:
                    buscarListas();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }

    public void minhasListas(){

    }

    public void buscarListas(){

    }
    
}
