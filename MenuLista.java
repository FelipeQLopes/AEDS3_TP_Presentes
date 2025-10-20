import aed3.ArvoreBMais;
import dados.usuarios.ParIntInt;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuLista {

    private int ID_GLOBAL;
    private String NOME_GLOBAL;
    private String CPF_GLOBAL;

    private ArquivoLista arqListas; 
    private ArvoreBMais<ParIntInt> relacaoUsuarioLista;
    private Scanner console;

    private Map<Integer, List<Integer>> cacheRelacao = new HashMap<>();

    // Gerador de IDs automático
    private AtomicInteger nextId = new AtomicInteger(1);

    public MenuLista(int ID_GLOBAL, String NOME_GLOBAL, String CPF_GLOBAL) throws Exception {
        this.ID_GLOBAL = ID_GLOBAL;
        this.NOME_GLOBAL = NOME_GLOBAL;
        this.CPF_GLOBAL = CPF_GLOBAL;

        this.relacaoUsuarioLista = new ArvoreBMais<>(
            ParIntInt.class.getConstructor(),
            "dados/relacoesUsuarioLista.db"
        );

        this.console = new Scanner(System.in);

    }

    public void menu() {
        String opcao;

        do {
            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Início > Listas\n");
            System.out.println("(1) - Minhas Listas");
            System.out.println("(2) - Buscar Lista");
            System.out.println("(3) - Criar nova Lista");
            System.out.println("(R) - Retornar ao menu anterior");

            System.out.print("\nOpção: ");
            try {
                opcao = console.nextLine().trim().toUpperCase();
                
            } catch (NumberFormatException e) {
                opcao = "-1";
            }

            switch (opcao) {
                case "1":
                    minhasListas(); 
                    break;
                case "2":
                    buscarListas(); 
                    break;
                case "3":
                    criarLista();
                    break;
                case "R":
                    Principal.main(new String[0]);
                    System.out.println("Voltando...\n");
                    break;

                default:
                    System.out.println("Opção inválida!\n");
                    break;
            }

        } while (opcao != "0");
    }

    public void minhasListas() {
        
        List<Integer> ids = cacheRelacao.getOrDefault(ID_GLOBAL, Collections.emptyList());

        if (ids.isEmpty()) {
            System.out.println("Nenhuma lista encontrada para o usuário " + ID_GLOBAL + ".");
            return;
        }

        System.out.println("\nListas do usuário (ID usuário = " + ID_GLOBAL + "):");

        if (arqListas != null) {
            for (Integer idLista : ids) {
                try {
                  
                    Lista lista = null;

                    try {
                        lista = arqListas.read(idLista); 
                    } catch (NoSuchMethodError | AbstractMethodError ex) {
                        lista = null;
                    } catch (Exception ex) {
                        lista = null;
                    }

                    if (lista != null) {
                        System.out.println(lista);
                    } else {
                        System.out.println("ID lista: " + idLista );
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao recuperar lista ID " + idLista + ": " + e.getMessage());
                }
            }
        } else {
            for (Integer idLista : ids) {
                System.out.println("ID lista: " + idLista );
            }
        }
    }

   

    public void buscarListas() {
       //falta implementar
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

            // Gera ID automático
            int idLista = nextId.getAndIncrement();

            if (arqListas != null) {
                try {
                    Lista novaLista = new Lista();
                    novaLista.setId(idLista);
                    novaLista.setNome(nomeLista);
                    novaLista.setDescricao(descricaoLista);
                    novaLista.setDataLimite(dataLimite);
                    novaLista.setIdUsuario(ID_GLOBAL);

                    arqListas.create(novaLista);
                } catch (Exception e) {
                    System.out.println("Aviso: Não foi possível gravar no ArquivoLista: " + e.getMessage());
                }
            }

            // Inserção da nova lusta na árvore B+
            try {
                relacaoUsuarioLista.create(new ParIntInt(ID_GLOBAL, idLista));
            } catch (Exception e) {
                System.out.println("Erro ao inserir relacionamento na árvore B+: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            cacheRelacao.computeIfAbsent(ID_GLOBAL, k -> new ArrayList<>()).add(idLista);

            System.out.println("Lista criada com sucesso! ID automático: " + idLista);

        } catch (Exception e) {
            System.out.println("Erro ao criar lista: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inserção manual de uma lista!!!
    public static void main(String[] args) {

        try {
            // Simula um usuário apenas para fins de testes
            int idUsuario = 1;
            String nomeUsuario = "Usuário Teste";
            String cpfUsuario = "000.000.000-00";

           
            MenuLista menu = new MenuLista(idUsuario, nomeUsuario, cpfUsuario);
            menu.menu();

        } catch (Exception e) {
            System.out.println("Erro ao iniciar o menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
