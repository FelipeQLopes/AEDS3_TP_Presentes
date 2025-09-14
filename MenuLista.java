import aed3.ArvoreBMais;
import dados.usuarios.ParIntInt;

import java.time.LocalDate;
import java.util.Scanner;

public class MenuLista {

    private int ID_GLOBAL;
    private String NOME_GLOBAL;
    private String CPF_GLOBAL;

    private ArquivoLista arqListas;
    private ArvoreBMais<ParIntInt> relacaoUsuarioLista;
    private Scanner console;

    public MenuLista(int ID_GLOBAL, String NOME_GLOBAL, String CPF_GLOBAL) throws Exception {
        this.ID_GLOBAL = ID_GLOBAL;
        this.NOME_GLOBAL = NOME_GLOBAL;
        this.CPF_GLOBAL = CPF_GLOBAL;

        this.arqListas = new ArquivoLista();

        this.console = new Scanner(System.in);
    }

    public void menu() {
        int opcao;

        do {
            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Home > Listas");
            System.out.println("\n1 - Minhas Listas");
            System.out.println("2 - Buscar Listas");
            System.out.println("3 - Criar nova Lista");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    minhasListas();
                    break;
                case 2:
                    buscarListas();
                    break;
                case 3:
                    criarLista();
                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }

   public void minhasListas() {
  

}


    public void buscarListas() {
        System.out.print("Digite o código da lista para buscar: ");
        String codigo = console.nextLine();

        try {
            Lista lista = arqListas.read(codigo);
            if (lista != null) {
                System.out.println(lista);
            } else {
                System.out.println("Lista não encontrada.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar lista: " + e.getMessage());
        }
    }

    public void criarLista() {
        if (ID_GLOBAL == -1) {
            System.out.println("Você precisa estar logado para criar uma lista.");
            return;
        }

        try {
            System.out.print("Digite o nome da nova lista: ");
            String nomeLista = console.nextLine();

            System.out.print("Digite a descrição da lista: ");
            String descricaoLista = console.nextLine();

            System.out.print("Digite a data limite (YYYY-MM-DD): ");
            String dataLimiteStr = console.nextLine();
            LocalDate dataLimite = LocalDate.parse(dataLimiteStr);

            Lista novaLista = new Lista();
            novaLista.setNome(nomeLista);
            novaLista.setDescricao(descricaoLista);
            novaLista.setDataLimite(dataLimite);
            novaLista.setIdUsuario(ID_GLOBAL);

            int idLista = arqListas.create(novaLista);

            // Relacionar usuário com a lista criada na árvore
            relacaoUsuarioLista.create(new ParIntInt(ID_GLOBAL, idLista));

            System.out.println("Lista criada com sucesso! ID: " + idLista);

        } catch (Exception e) {
            System.out.println("Erro ao criar lista: " + e.getMessage());
        }
    }


    //Inserção manual de uma lista!!!
    /*
    public static void main(String[] args) {
        try {
            // Simula que um usuário com ID 1 está logado
            int usuarioLogadoId = 1;

            // Inicializa os arquivos
            ArquivoLista arqLista = new ArquivoLista();
            ArvoreBMais<ParIntInt> relacaoUsuarioLista = new ArvoreBMais<>(
                ParIntInt.class.getConstructor(),
                "dados/relacoesUsuarioLista.db"
            );

            // Cria nova lista manualmente
            Lista novaLista = new Lista("Compra", "Compra no marketing",
                                        LocalDate.of(2025, 12, 10));
            novaLista.setIdUsuario(usuarioLogadoId);  // Garanta que esse campo exista na sua classe Lista

            // Grava a lista
            int idLista = arqLista.create(novaLista);

            // Relaciona usuário com lista
            relacaoUsuarioLista.create(new ParIntInt(usuarioLogadoId, idLista));

            // Confirma no console
            System.out.println("Lista criada com sucesso!");
            System.out.println("ID da Lista: " + idLista);
            System.out.println("Relacionamento salvo na árvore B+");

        } catch (Exception e) {
            System.out.println("Erro ao criar lista:");
            e.printStackTrace();
        }
    } */
}
