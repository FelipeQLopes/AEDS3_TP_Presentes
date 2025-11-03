package tp.presentes;

import tp.presentes.aed3.*;

import java.time.LocalDate;
import java.util.*;


public class MenuLista {

    private int ID_GLOBAL;
    private String NOME_GLOBAL;
    private String CPF_GLOBAL;

    private ArquivoLista arqListas;
    private MenuProduto menuProduto;
    private ArvoreBMais<ParIntInt> relacaoUsuarioLista;
    private ArvoreBMais<ParIntInt> relacaoListaProduto;
    private ArvoreBMais<ParIntInt> relacaoProdutoLista;
    private Scanner console;

    public MenuLista(int ID_GLOBAL, String NOME_GLOBAL, String CPF_GLOBAL) throws Exception {
        this.ID_GLOBAL = ID_GLOBAL;
        this.NOME_GLOBAL = NOME_GLOBAL;
        this.CPF_GLOBAL = CPF_GLOBAL;
        
        this.menuProduto = new MenuProduto(ID_GLOBAL, NOME_GLOBAL, CPF_GLOBAL);
        this.arqListas = new ArquivoLista();
        this.relacaoUsuarioLista = new ArvoreBMais<>(ParIntInt.class.getConstructor(), 5, "./src/main/resources/dados/relacaoUsuarioLista.db");
        this.relacaoListaProduto = new ArvoreBMais<>(ParIntInt.class.getConstructor(), 5, "./src/main/resources/dados/relacaoListaProduto.db");
        this.relacaoProdutoLista = new ArvoreBMais<>(ParIntInt.class.getConstructor(), 5, "./src/main/resources/dados/relacaoProdutoLista.db");

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
        
        String opcao;
        int opcaoNum;

        do {
            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Home > Listas > Minhas Listas");

            System.out.println();
            ArrayList<Lista> listas = mostrarListas();
            System.out.println();
            
            System.out.println("1 - Criar nova Lista");
            System.out.println("0 - Voltar");
            
            
            System.out.print("\nOpção: ");

            opcao = console.nextLine();
            opcaoNum = Integer.parseInt(opcao);
            
            if(opcaoNum <= 90 && opcaoNum >= 65){
                exibirLista(listas.get(opcaoNum-65));
            }else if(opcaoNum == 1){
                criarLista();
            }else if(opcaoNum == 0){
                System.out.println("Voltando...");
                break;
            }else{
                System.out.println("Opção inválida!");
                break;
            }

        } while (opcaoNum != 0);
    }

    public void alterarDados(){

        String codigo;

        if (ID_GLOBAL == -1) {
            System.out.println("Você precisa estar logado para criar uma lista.");
            return;
        }

        do {
            System.out.print("\nDigite o código da lista a ser alterada: ");
            codigo = console.nextLine();

            if(codigo.isEmpty())
                return; 

            // Validação do CPF (11 dígitos e composto apenas por números)
        } while (codigo.length() == 10);


        try {
            Lista lista = arqListas.read(codigo);
            if (lista != null) {
                System.out.println("Lista encontrada:");
                System.out.println(lista.toString()+"\n");  // Exibe os dados do usuario para confirmação
                System.out.print("Digite o nome da nova lista: ");
                String nomeLista = console.nextLine();
                if (!nomeLista.isEmpty()) {
                    lista.setNome(nomeLista);  // Atualiza o CPF se fornecido
                }

                System.out.print("Digite a descrição da lista: ");
                String descricaoLista = console.nextLine();
                if (!descricaoLista.isEmpty()) {
                    lista.setDescricao(descricaoLista);  // Atualiza o CPF se fornecido
                }

                System.out.print("Digite a data limite (YYYY-MM-DD): ");
                String dataLimiteStr = console.nextLine();
                LocalDate dataLimite = LocalDate.parse(dataLimiteStr);
                if (!dataLimiteStr.isEmpty()) {
                    lista.setDataLimite(dataLimite);  // Atualiza o CPF se fornecido
                }

                String r;

                do{
                    System.out.print("\nConfirma a alteração da Lista? (S/N) ");
                    r = console.nextLine();
                }while(r.length() == 0);
                char resp = r.charAt(0);

                if (resp == 'S' || resp == 's') {
                    // Salva as alterações no arquivo
                    boolean alterado = arqListas.update(lista);
                    if (alterado) {
                        System.out.println("Usuario alterado com sucesso.");
                    } else {
                        System.out.println("Erro ao alterar o usuario.");
                    }
                } else {
                    System.out.println("Alterações canceladas.");
                }

            }else{
                System.out.println("Lista não encontrada.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao criar lista: " + e.getMessage());
        }
    }

    public ArrayList<Lista> mostrarListas() {

        ParIntInt parPesquisa = new ParIntInt(ID_GLOBAL, -1);
        ArrayList<Lista> listaPresentesUsuario = new ArrayList<>();
        int contador = 1;
        try {
            ArrayList<ParIntInt> listaPresentes = relacaoUsuarioLista.read(parPesquisa);

            System.out.println("Suas listas:\n");
            for (ParIntInt par : listaPresentes) {
                int idUsuario = par.getNum1();
                int idListaPresentes = par.getNum2();
                if (idUsuario == ID_GLOBAL) {
                    Lista lista = arqListas.read(idListaPresentes);
                    listaPresentesUsuario.add(lista);
                    System.out.printf("  %c - %s | %s | %s \n", contador+64, lista.getNome(), lista.getDataLimite(), lista.getCodigo());
                    contador++;
                }

            }
        } catch (Exception e) {

        }
        return listaPresentesUsuario;
    }

    public void exibirLista(Lista lista){

        int opcao = -1;

        do {
            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Home > Listas > Minhas Listas > " + lista.getNome());
            
            System.out.println(lista.toString());
            
            System.out.println("\n1 - Gerenciar Produtos da Lista");
            System.out.println("2 - Alterar Dados da Lista");
            System.out.println("0 - Voltar");
            
            System.out.print("\nOpção: ");

            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch(opcao){
                case 1:
                    gerenciarProdutos();
                    break;
                case 2:
                    alterarDados();
                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        }while(opcao != 0);
        
    }

    public void gerenciarProdutos(Lista lista){
        int opcao = -1;

        do {
            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Home > Listas > Minhas Listas > " + lista.getNome() + " > Produtos");
            
            System.out.println(lista.toString());
            
            System.out.println("\n1 - Gerenciar Produtos da Lista");
            System.out.println("2 - Alterar Dados da Lista");
            System.out.println("0 - Voltar");
            
            System.out.print("\nOpção: ");

            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }
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
            System.out.println(idLista + " " + ID_GLOBAL);
            relacaoUsuarioLista.create(new ParIntInt(ID_GLOBAL, idLista));

            System.out.println("Lista criada com sucesso! ID: " + idLista);
            System.out.println(novaLista.getCodigo());

        } catch (Exception e) {
            System.out.println("Erro ao criar lista: " + e.getMessage());
            e.printStackTrace(); // Adicione isto para ver o erro real
        }
    }


}
