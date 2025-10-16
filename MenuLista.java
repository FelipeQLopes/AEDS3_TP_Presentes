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
    this.relacaoUsuarioLista = new ArvoreBMais<>(
        ParIntInt.class.getConstructor(),
        "dados/relacoesUsuarioLista.db"
    );

    this.console = new Scanner(System.in);
}


    
    public void menu() {
        int opcao;

        do {
            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Início > Listas\n");
            System.out.println("(1) - Minhas Listas");
            System.out.println("(2) - Buscar Listas");
            System.out.println("(3) - Criar nova Lista\n");
            System.out.println("(R) - Retornar ao menu anterior");

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
                    System.out.println("Voltando...\n");
                    break;
                default:
                    System.out.println("Opção inválida!\n");
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

        // Verificações antes de persistir
        if (arqListas == null) {
            throw new IllegalStateException("arqListas não inicializado");
        }
        if (relacaoUsuarioLista == null) {
            throw new IllegalStateException("relacaoUsuarioLista não inicializado");
        }

        int idLista = arqListas.create(novaLista);

        if (idLista < 0) {
            System.out.println("arqListas.create retornou id inválido: " + idLista);
        } else {
            try {
                relacaoUsuarioLista.create(new ParIntInt(ID_GLOBAL, idLista));
            } catch (Exception e) {
                System.out.println("Erro ao relacionar usuário-lista:");
                e.printStackTrace();
            }
            System.out.println("Lista criada com sucesso! ID: " + idLista);
        }

    } catch (Exception e) {
        System.out.println("Erro ao criar lista: " + e.getClass().getName() + " - " + e.getMessage());
        e.printStackTrace(); // mostra linha exata do erro
    }
}



    //Inserção manual de uma lista!!!
    
    public static void main(String[] args) {
        
    try {
        // Simula um usuário logado (pode mudar depois se quiser)
        int idUsuario = 1;
        String nomeUsuario = "Usuário Teste";
        String cpfUsuario = "000.000.000-00";

        // Instancia a classe e chama o menu
        MenuLista menu = new MenuLista(idUsuario, nomeUsuario, cpfUsuario);
        menu.menu();

    } catch (Exception e) {
        System.out.println("Erro ao iniciar o menu: " + e.getMessage());
        e.printStackTrace();
    }
}

        /*
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
            e.printStackTrace();*/

          
    } 

