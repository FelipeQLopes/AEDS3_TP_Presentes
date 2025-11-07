package tp.presentes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;


import tp.presentes.aed3.ArvoreBMais;


public class MenuProduto {

    int ID_GLOBAL;
    String NOME_GLOBAL;
    String CPF_GLOBAL;
    
    ArquivoProduto arqProdutos;
    ArquivoListaProduto arqListaProduto;
    ArvoreBMais<ParIntInt> relacaoProdutoLista;
    ArvoreBMais<ParIntInt> relacaoListaProduto;
    private static Scanner console = new Scanner(System.in);
    

    public MenuProduto(int ID_GLOBAL, String NOME_GLOBAL, String CPF_GLOBAL) throws Exception {

        this.ID_GLOBAL = ID_GLOBAL;
        this.NOME_GLOBAL = NOME_GLOBAL;
        this.CPF_GLOBAL = CPF_GLOBAL;
        this.arqProdutos = new ArquivoProduto();
        this.arqListaProduto = new ArquivoListaProduto();
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
                    buscarProdutoPorGtin(1, null);
                    break;                
                case 2:
                    listarProdutosPaginado(1, null);
                    break;
                case 3:
                    cadastrarProduto();
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

    public void buscarProdutoPorGtin(int modo, Lista lista) {
        System.out.print("Digite o GTIN-13: ");
        String gtin = console.nextLine().trim();
        try {
            Produto p = arqProdutos.read(gtin);
            if (p != null) {
                if(modo == 1){
                    System.out.println("\nProduto encontrado:\n" + p.toString());
                    mostrarProdutoDetalhes(p);
                }else if(modo == 2){
                    System.out.println("Você quer adicionar o produto: " + p.getNome() + "a sua lista?");
                    System.out.println("S/N");
                    String opt = console.nextLine().trim();
                    if (opt.equalsIgnoreCase("S")){
                        adicionarProdutoNaLista(lista, p, 1);
                    }
                }
            } else {
                System.out.println("Produto não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        }
    }


    public void listarProdutosPaginado(int modo, Lista lista) {
        try {
            ArrayList<Produto> todos = arqProdutos.listAll(); 
            todos.sort((a,b) -> a.getNome().compareToIgnoreCase(b.getNome()));
            int pageSize = 10;
            int total = todos.size();
            int totalPages = Math.max(1, (total + pageSize - 1) / pageSize);
            int page = 1;
            while (true) {
                int start = (page - 1) * pageSize;
                int end = Math.min(start + pageSize, total);
                System.out.printf("\nPresenteFácil 1.0\n-----------------\n> Início > Produtos > Listagem\n\nPágina %d de %d\n\n", page, totalPages);
                for (int i = start; i < end; i++) {
                    Produto p = todos.get(i);
                    System.out.printf("(%d) %s\n", (i - start) + 1, p.getNome());
                }
                System.out.println("\n(A) Página anterior  (P) Próxima página  (R) Retornar");
                System.out.print("Opção: ");
                String opt = console.nextLine().trim();
                if (opt.equalsIgnoreCase("A") && page > 1) page--;
                else if (opt.equalsIgnoreCase("P") && page < totalPages) page++;
                else if (opt.equalsIgnoreCase("R")) break;
                else {
                    // escolher produto por número da página
                    try {
                        int escolha = Integer.parseInt(opt);
                        if (escolha >= 1 && escolha <= (end - start)) {
                            Produto escolhido = todos.get(start + escolha - 1);
                            if(modo == 1){
                                mostrarProdutoDetalhes(escolhido);
                            }else if(modo == 2){
                                System.out.println("Você quer adicionar o produto: " + escolhido.getNome() + "a sua lista?");
                                System.out.println("S/N");
                                opt = console.nextLine().trim();
                                if (opt.equalsIgnoreCase("S")){
                                    adicionarProdutoNaLista(lista, escolhido, 1);
                                }
                            }
                        } else {
                            System.out.println("Opção inválida.");
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Opção inválida.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
    }

    public void adicionarProdutoNaLista(Lista lista, Produto produto, int quantidade) {
        try {
            // 1) procurar associações existentes para essa lista (relacaoListaProduto armazena: (idLista, idListaProduto))
            ArrayList<ParIntInt> rels = relacaoListaProduto.read(new ParIntInt(lista.getId(), -1));
            ListaProduto existente = null;
            int idListaProdutoExistente = -1;

            if (rels != null) {
                for (ParIntInt p : rels) {
                    int idListaFromRel = p.getNum1();
                    int idLp = p.getNum2(); // id do registro ListaProduto
                    if (idListaFromRel != lista.getId()) continue;
                    ListaProduto lp = arqListaProduto.read(idLp);
                    if (lp != null && lp.getIdProduto() == produto.getId()) {
                        existente = lp;
                        idListaProdutoExistente = idLp;
                        break;
                    }
                }
            }

            // 2) se existe, só aumenta a quantidade e faz update
            if (existente != null) {
                existente.setQuantidade(existente.getQuantidade() + quantidade);
                boolean ok = arqListaProduto.update(existente);
                if (ok) System.out.println("Quantidade atualizada: " + existente.getQuantidade());
                else System.out.println("Falha ao atualizar quantidade.");
                return;
            }

            // 3) se não existe, cria ListaProduto e cria as relações
            ListaProduto novo = new ListaProduto();
            novo.setIdLista(lista.getId());
            novo.setIdProduto(produto.getId());
            novo.setQuantidade(quantidade);

            int idNovo = arqListaProduto.create(novo); // retorna id do registro criado

            // cria relações que apontam para esse registro de associação (idNovo)
            relacaoListaProduto.create(new ParIntInt(lista.getId(), idNovo));
            relacaoProdutoLista.create(new ParIntInt(produto.getId(), idNovo));

            System.out.println("Produto adicionado à lista. quantidade = " + quantidade);
        } catch (Exception e) {
            System.out.println("Erro ao adicionar produto à lista: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public ArrayList<ListaProduto> listarProdutosDaLista(Lista lista) {
    ArrayList<ListaProduto> exibidos = new ArrayList<>();

    try {
        ArrayList<ListaProduto> listaProdutos = arqListaProduto.readAll();

        if (listaProdutos == null || listaProdutos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado nesta lista.");
            return exibidos;
        }

        System.out.println("\nSeus Produtos:\n");

        Map<String, Integer> quantidades = new LinkedHashMap<>();
        Map<String, ListaProduto> referencia = new LinkedHashMap<>();
        Map<String, Produto> produtosMap = new LinkedHashMap<>();

        for (ListaProduto lp : listaProdutos) {
            if (lp != null && lp.getIdLista() == lista.getId()) {
                Produto p = arqProdutos.read(lp.getIdProduto());
                if (p == null) continue;

                String gtin = p.getGtin13();
                quantidades.put(gtin, quantidades.getOrDefault(gtin, 0) + lp.getQuantidade());
                referencia.putIfAbsent(gtin, lp);
                produtosMap.putIfAbsent(gtin, p);
            }
        }

        char letra = 'A';
        for (String gtin : quantidades.keySet()) {
            Produto p = produtosMap.get(gtin);
            ListaProduto lpRef = referencia.get(gtin);
            int qtd = quantidades.get(gtin);

            System.out.printf("(%c) - %s | x%d\n", letra, p.getNome(), qtd);
            exibidos.add(lpRef);
            letra++;
        }

    } catch (Exception e) {
        System.out.println("Erro ao listar produtos da lista: " + e.getMessage());
    }

    return exibidos;
}




public void exibirProdutoDaLista(ListaProduto lp) {
    try {
        Produto produto = arqProdutos.read(lp.getIdProduto());
        System.out.println("\n\nPresenteFácil 1.0");
        System.out.println("-----------------");
        System.out.println("> Início > Minhas listas > " + lp.getIdLista() + " > Produtos > " + produto.getNome() + "\n");

        System.out.println("NOME.......: " + produto.getNome());
        System.out.println("GTIN-13....: " + produto.getGtin13());
        System.out.println("DESCRIÇÃO..: " + produto.getDescricao());
        System.out.println("QUANTIDADE.: " + lp.getQuantidade());
        System.out.println("OBSERVAÇÕES: " + lp.getObservacoes() + "\n");

        System.out.println("(1) Alterar a quantidade");
        System.out.println("(2) Alterar as observações");
        System.out.println("(3) Remover o produto desta lista");
        System.out.println("(R) Retornar ao menu anterior\n");

        System.out.print("Opção: ");
        String opcao = console.nextLine().trim().toUpperCase();

        switch (opcao.charAt(0)) {
            case '1':
                System.out.print("Nova quantidade: ");
                int novaQtd = Integer.parseInt(console.nextLine());
                lp.setQuantidade(novaQtd);
                arqListaProduto.update(lp);
                break;

            case '2':
                System.out.print("Nova observação: ");
                String obs = console.nextLine();
                lp.setObservacoes(obs);
                arqListaProduto.update(lp);
                break;

            case '3':
                arqListaProduto.delete(lp.getIdLista(), lp.getIdProduto());
                System.out.println("Produto removido da lista.");
                return;

            case 'R':
                return;

            default:
                System.out.println("Opção inválida!");
        }

    } catch (Exception e) {
        System.out.println("Erro ao manipular produto da lista: " + e.getMessage());
    }
}






    private void cadastrarProduto() {
        try {
            System.out.print("Nome: ");
            String nome = console.nextLine().trim();
            System.out.print("Descrição: ");
            String descricao = console.nextLine().trim();
            System.out.print("GTIN-13 (13 dígitos): ");
            String gtin = console.nextLine().trim();
            if (!gtin.matches("\\d{13}")) {
                System.out.println("GTIN inválido. Deve ter 13 dígitos numéricos.");
                return;
            }
            // checar duplicidade
            Produto existente = arqProdutos.read(gtin);
            if (existente != null) {
                System.out.println("Já existe um produto com esse GTIN: " + existente.getNome());
                return;
            }
            Produto p = new Produto(nome, gtin, descricao);
            int id = arqProdutos.create(p);
            System.out.println("Produto cadastrado com ID: " + id);
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    private void mostrarProdutoDetalhes(Produto p) {
        System.out.println("\n" + p.toString());
        // mostrar em quais listas aparece
        try {
            ParIntInt parPesquisa = new ParIntInt(p.getId(), -1);
            ArrayList<ParIntInt> rels = relacaoProdutoLista.read(parPesquisa);
            ArrayList<String> minhas = new ArrayList<>();
            int outras = 0;
            for (ParIntInt par : rels) {
                int idLista = par.getNum2(); // se o par trabalha (produto, idLista) ou vice-versa: ajustar conforme seu uso
                Lista l = new ArquivoLista().read(idLista);
                if (l.getIdUsuario() == ID_GLOBAL) minhas.add(l.getNome());
                else outras++;
            }
            if (!minhas.isEmpty()) {
                System.out.println("\nAparece nas minhas listas:");
                minhas.sort(String::compareToIgnoreCase);
                for (String s : minhas) System.out.println("- " + s);
            }
            System.out.println("\nAparece também em mais " + outras + " listas de outras pessoas.");
        } catch (Exception e) {
            // se erro, só ignora
        }

        System.out.println("\n(1) Alterar os dados do produto\n(2) Inativar o produto\n(R) Retornar");
        System.out.print("Opção: ");
        String op = console.nextLine().trim();
        if (op.equals("1")) {
            alterarProduto(p);
        } else if (op.equals("2")) {
            inativarProduto(p);
        }
    }

    private void alterarProduto(Produto p) {
        try {
            System.out.print("Novo nome (enter para manter): ");
            String nome = console.nextLine().trim();
            if (!nome.isEmpty()) p.setNome(nome);
            System.out.print("Nova descrição (enter para manter): ");
            String desc = console.nextLine().trim();
            if (!desc.isEmpty()) p.setDescricao(desc);
            boolean ok = arqProdutos.update(p);
            if (ok) System.out.println("Produto alterado com sucesso.");
            else System.out.println("Falha ao alterar produto.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void inativarProduto(Produto p) {
        try {
            p.setAtivo(false);
            if (arqProdutos.update(p)) {
                System.out.println("Produto inativado com sucesso.");
            } else {
                System.out.println("Falha ao inativar produto.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao inativar produto: " + e.getMessage());
        }
    }

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