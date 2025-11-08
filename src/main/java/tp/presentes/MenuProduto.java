package tp.presentes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.Normalizer;


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
    ListaInvertida indiceInvertido;
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "o", "a", "os", "as", "um", "uma", "uns", "umas",
            "de", "do", "da", "dos", "das", "em", "no", "na", "nos", "nas",
            "por", "pelo", "pela", "pelos", "pelas", "para", "com", "sem", "sob",
            "sobre", "entre", "até", "após", "contra", "ante", "via",
            "eu", "tu", "ele", "ela", "eles", "elas", "nós", "vós",
            "me", "te", "se", "lhe", "nos", "vos",
            "meu", "minha", "meus", "minhas", "teu", "tua", "teus", "tuas",
            "nosso", "nossa", "nossos", "nossas", "vosso", "vossa", "vossos", "vossas",
            "este", "esta", "estes", "estas", "esse", "essa", "esses", "essas",
            "aquele", "aquela", "aqueles", "aquelas", "isso", "isto", "aquilo",
            "quem", "qual", "quais", "cujo", "cuja", "cujos", "cujas", "onde",
            "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez",
            "primeiro", "segundo", "terceiro", "quarto", "quinto", "sexto", "sétimo", "oitavo", "nono", "décimo"));
    

    public MenuProduto(int ID_GLOBAL, String NOME_GLOBAL, String CPF_GLOBAL) throws Exception {

        this.ID_GLOBAL = ID_GLOBAL;
        this.NOME_GLOBAL = NOME_GLOBAL;
        this.CPF_GLOBAL = CPF_GLOBAL;
        this.arqProdutos = new ArquivoProduto();
        this.arqListaProduto = new ArquivoListaProduto();
        this.relacaoProdutoLista = new ArvoreBMais<>(ParIntInt.class.getConstructor(), 5, "./src/main/resources/dados/relacaoProdutoLista.db");
        this.relacaoListaProduto = new ArvoreBMais<>(ParIntInt.class.getConstructor(), 5, "./src/main/resources/dados/relacaoListaProduto.db");
        this.indiceInvertido = new ListaInvertida(5, "./src/main/resources/dados/listaInvertida/dicionario.db", "./src/main/resources/dados/listaInvertida/bloco.db");
        return;
    }

    //region menu
    public void menu() {
        
        int opcao;
        do {

            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println(">");
            System.out.println("\n1 - Buscar produtos pelo Nome");
            System.out.println("2 - Buscar produtos por GTIN");
            System.out.println("3 - Listar todos os produtos");
            System.out.println("4 - Cadastrar um novo produto");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    listarProdutosPorNome(1, null);
                    break;                
                case 2:
                    buscarProdutoPorGtin(1, null);
                    break;                
                case 3:
                    listarProdutosPaginado(1, null);
                    break;
                case 4:
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
                    if(p.isAtivo()){
                        System.out.println("Você quer adicionar o produto: " + p.getNome() + " a sua lista?");
                        System.out.println("S/N");
                        String opt = console.nextLine().trim();
                        if (opt.equalsIgnoreCase("S")){
                            adicionarProdutoNaLista(lista, p, 1);
                        }
                    }else{                    
                        System.out.println("Esse produto foi inativado");
                    }
                }
            } else {
                System.out.println("Produto não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        }
    }
    
    public ArrayList<Produto> buscarProdutoPorNome(String nome) throws Exception {

        if (nome == null || nome.trim().isEmpty()) return new ArrayList<>();

        nome = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
        String[] termosBuscaRaw = nome.split("\\s+");

        // filtra stop-words e termos vazios
        ArrayList<String> termosBusca = new ArrayList<>();
        for (String t : termosBuscaRaw) {
            t = t.trim();
            if (t.isEmpty()) continue;
            if (STOP_WORDS.contains(t)) continue;
            termosBusca.add(t);
        }

        if (termosBusca.isEmpty()) return new ArrayList<>();

        ArrayList<Produto> all = arqProdutos.listAll();
        int total = all == null ? 0 : all.size();
        if (total == 0) return new ArrayList<>();

        HashMap<Integer, Float> mapaRelevancia = new HashMap<>();

        for (String termo : termosBusca) {
            ElementoLista[] lista = indiceInvertido.read(termo);
            if (lista == null || lista.length == 0) continue;
            float idfPalavra = (float) (Math.log((float) total / (float) lista.length) + 1);

            for (ElementoLista el : lista) {
                float tfidf = el.getFrequencia() * idfPalavra;
                mapaRelevancia.put(el.getId(), mapaRelevancia.getOrDefault(el.getId(), 0f) + tfidf);
            }
        }

        ArrayList<Integer> ids = new ArrayList<>(mapaRelevancia.keySet());
        ids.sort((id1, id2) -> Float.compare(mapaRelevancia.get(id2), mapaRelevancia.get(id1)));

        ArrayList<Produto> produtosOrdenados = new ArrayList<>();
        for (int id : ids) {
            Produto p = arqProdutos.read(id);
            if (p != null) produtosOrdenados.add(p);
        }

        return produtosOrdenados;
    }

    /**
     * Lista produtos por nome (interativo). modo: 1 = ver detalhes, 2 = adicionar a lista passada.
     */
    public void listarProdutoPorNome(int modo, Lista lista) {
        try {
            System.out.print("Digite o nome ou termos para buscar: ");
            String consulta = console.nextLine().trim();
            ArrayList<Produto> resultados = buscarProdutoPorNome(consulta);

            if (resultados.isEmpty()) {
                System.out.println("Nenhum produto encontrado para: " + consulta);
                return;
            }

            int pageSize = 10;
            int total = resultados.size();
            int totalPages = Math.max(1, (total + pageSize - 1) / pageSize);
            int page = 1;

            while (true) {
                int start = (page - 1) * pageSize;
                int end = Math.min(start + pageSize, total);
                System.out.printf("\nResultados da busca (%d itens) - Página %d/%d\n\n", total, page, totalPages);
                for (int i = start; i < end; i++) {
                    Produto p = resultados.get(i);
                    System.out.printf("(%d) %s%s\n", (i - start) + 1, p.getNome(), p.isAtivo() ? "" : " (Inativo)");
                }

                System.out.println("\n(A) Página anterior  (P) Próxima página  (R) Retornar");
                System.out.print("Opção ou número: ");
                String opt = console.nextLine().trim();

                if (opt.equalsIgnoreCase("A") && page > 1) { page--; continue; }
                if (opt.equalsIgnoreCase("P") && page < totalPages) { page++; continue; }
                if (opt.equalsIgnoreCase("R")) break;

                try {
                    int escolha = Integer.parseInt(opt);
                    if (escolha >= 1 && escolha <= (end - start)) {
                        Produto escolhido = resultados.get(start + escolha - 1);
                        if (modo == 1) mostrarProdutoDetalhes(escolhido);
                        else if (modo == 2) {
                            if (!escolhido.isAtivo()) {
                                System.out.println("Produto inativo. Não é possível adicionar.");
                            } else {
                                adicionarProdutoNaLista(lista, escolhido, 1);
                            }
                        }
                    } else {
                        System.out.println("Número inválido.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Opção inválida.");
                }
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar/listar produtos por nome: " + e.getMessage());
        }
    }

    public void listarProdutosPorNome(int modo, Lista lista) {
        while (true) {
            System.out.print("Digite o nome (ou R para retornar): ");
            String termo = console.nextLine().trim();
            if (termo.equalsIgnoreCase("R") || termo.isEmpty()) return;

            try {
                ArrayList<Produto> resultados = buscarProdutoPorNome(termo);
                if (resultados == null || resultados.isEmpty()) {
                    System.out.println("Nenhum produto encontrado para: " + termo);
                    continue;
                }

                // mostra resultados
                for (int i = 0; i < resultados.size(); i++) {
                    Produto p = resultados.get(i);
                    String ativo = p.isAtivo() ? "" : " (Inativo)";
                    System.out.printf("%d) %s%s — GTIN: %s\n", i + 1, p.getNome(), ativo, p.getGtin13());
                }

                System.out.println("\nDigite o número do produto para ver opções, (N) nova busca ou (R) retornar");
                System.out.print("Opção: ");
                String opt = console.nextLine().trim();
                if (opt.equalsIgnoreCase("R")) return;
                if (opt.equalsIgnoreCase("N")) continue;

                try {
                    int escolha = Integer.parseInt(opt);
                    if (escolha < 1 || escolha > resultados.size()) {
                        System.out.println("Escolha inválida.");
                        continue;
                    }
                    Produto escolhido = resultados.get(escolha - 1);
                    if (modo == 1) {
                        mostrarProdutoDetalhes(escolhido);
                    } else if (modo == 2) {
                        if (lista == null) {
                            System.out.println("Lista não informada.");
                        } else {
                            if(escolhido.isAtivo()){
                                System.out.println("Você quer adicionar o produto: " + escolhido.getNome() + " a sua lista?");
                                System.out.println("S/N");
                                opt = console.nextLine().trim();
                                if (opt.equalsIgnoreCase("S")){
                                    adicionarProdutoNaLista(lista, escolhido, 1);
                                }
                            }else{                    
                                System.out.println("Esse produto foi inativado");
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Entrada inválida.");
                }

            } catch (Exception e) {
                System.out.println("Erro na busca: " + e.getMessage());
            }
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
                    System.out.println("(" + ((i - start) + 1) + ") " + p.getNome() + (p.isAtivo() ? "" : " (Inativo)"));
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
                                if(escolhido.isAtivo()){
                                    System.out.println("Você quer adicionar o produto: " + escolhido.getNome() + " a sua lista?");
                                    System.out.println("S/N");
                                    opt = console.nextLine().trim();
                                    if (opt.equalsIgnoreCase("S")){
                                        adicionarProdutoNaLista(lista, escolhido, 1);
                                    }
                                }else{                    
                                    System.out.println("Esse produto foi inativado");
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
            ArrayList<ListaProduto> todos = arqListaProduto.readAll();
            ListaProduto existente = null;

            // verifica se já existe o mesmo produto nessa lista
            for (ListaProduto lp : todos) {
                if (lp.getIdLista() == lista.getId() && lp.getIdProduto() == produto.getId()) {
                    existente = lp;
                    break;
                }
            }

            if (existente != null) {
                // se já existe, apenas soma a quantidade
                existente.setQuantidade(existente.getQuantidade() + quantidade);
                arqListaProduto.update(existente);
                System.out.println("Quantidade atualizada: " + existente.getQuantidade());
                return;
            }

            // caso contrário, cria um novo registro
            ListaProduto novo = new ListaProduto();
            novo.setIdLista(lista.getId());
            novo.setIdProduto(produto.getId());
            novo.setQuantidade(quantidade);
            novo.setObservacoes("");

            int idNovo = arqListaProduto.create(novo);

            // adiciona o relacionamento nas árvores de índice
            relacaoListaProduto.create(new ParIntInt(lista.getId(), produto.getId()));
            relacaoProdutoLista.create(new ParIntInt(produto.getId(), lista.getId()));

            System.out.println("Produto adicionado à lista. ID=" + idNovo);

        } catch (Exception e) {
            System.out.println("Erro ao adicionar produto à lista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<ListaProduto> listarProdutosDaLista(Lista lista) {
        ArrayList<ListaProduto> exibidos = new ArrayList<>();

        try {
            ArrayList<ListaProduto> todos = arqListaProduto.readAll();

            System.out.println("Seus Produtos:\n");

            for (ListaProduto lp : todos) {
                if (lp.getIdLista() == lista.getId()) {
                    Produto p = arqProdutos.read(lp.getIdProduto());
                    if (p != null && p.isAtivo()) {
                        exibidos.add(lp);
                    }
                }
            }

            if (exibidos.isEmpty()) {
                System.out.println("  (nenhum produto encontrado)");
            } else {
                exibidos.sort((a, b) -> {
                    try {
                        Produto p1 = arqProdutos.read(a.getIdProduto());
                        Produto p2 = arqProdutos.read(b.getIdProduto());
                        if (p1 == null || p2 == null) return 0;
                        return p1.getNome().compareToIgnoreCase(p2.getNome());
                    } catch (Exception e) {
                        return 0;
                    }
                });

                int i = 0;
                for (ListaProduto lp : exibidos) {
                    Produto prod = arqProdutos.read(lp.getIdProduto());
                    String nome = (prod != null) ? prod.getNome() : "(produto não encontrado)";
                    System.out.printf("(%c) - %s | x%d%n", (char) ('A' + i), nome, lp.getQuantidade());
                    i++;
                }
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

            switch (opcao.isEmpty() ? ' ' : opcao.charAt(0)) {
                case '1':
                    System.out.print("Nova quantidade: ");
                    int novaQtd;
                    try {
                        novaQtd = Integer.parseInt(console.nextLine().trim());
                    } catch (NumberFormatException ex) {
                        System.out.println("Quantidade inválida.");
                        break;
                    }
                    lp.setQuantidade(novaQtd);
                    if (arqListaProduto.update(lp)) {
                        System.out.println("Quantidade atualizada com sucesso.");
                    } else {
                        System.out.println("Falha ao atualizar quantidade.");
                    }
                    break;

                case '2':
                    System.out.print("Nova observação: ");
                    String obs = console.nextLine();
                    lp.setObservacoes(obs);
                    if (arqListaProduto.update(lp)) {
                        System.out.println("Observação atualizada com sucesso.");
                    } else {
                        System.out.println("Falha ao atualizar observação.");
                    }
                    break;

                case '3':
                    // supondo que delete(id) exista — se não existir, use sua versão delete(idLista,idProduto)
                    if (arqListaProduto.delete(lp.getId())) {
                        System.out.println("Produto removido da lista.");
                    } else {
                        // fallback se você só tiver delete(idLista,idProduto)
                        try {
                            if (arqListaProduto.delete(lp.getIdLista(), lp.getIdProduto())) {
                                System.out.println("Produto removido da lista.");
                            } else {
                                System.out.println("Falha ao remover o produto da lista.");
                            }
                        } catch (Exception ignored) {
                            System.out.println("Falha ao remover o produto da lista.");
                        }
                    }
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
            Produto p = new Produto(nome, gtin, descricao, true);
            int id = arqProdutos.create(p);
            p.setId(id);
            String nomeNormalizado = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                    .toLowerCase();

            String[] palavras = nomeNormalizado.split("\\s+");
            List<String> palavrasFiltradas = new ArrayList<>();
            Map<String, Integer> contagem = new HashMap<>();

            for (String palavra : palavras) {

                if (!STOP_WORDS.contains(palavra) && !palavra.isBlank()) {
                    palavrasFiltradas.add(palavra);
                    contagem.put(palavra, contagem.getOrDefault(palavra, 0) + 1);
                }
            }

            int totalPalavras = palavrasFiltradas.size();

            for (String palavra : contagem.keySet()) {

                int ocorrencias = contagem.get(palavra);
                float frequencia = (float) ocorrencias / totalPalavras;
                indiceInvertido.create(palavra, new ElementoLista(id, frequencia));
            }
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

        System.out.println("\n(1) Alterar os dados do produto\n(2) " + (p.isAtivo()? "Inativar" : "Ativar") + " o produto\n(R) Retornar");
        System.out.print("Opção: ");
        String op = console.nextLine().trim();
        if (op.equals("1")) {
            alterarProduto(p);
        } else if (op.equals("2")) {
            ativarInativarProduto(p, !p.isAtivo());
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

    private void ativarInativarProduto(Produto p, boolean estado) {
        try {
            p.setAtivo(estado);
            if (arqProdutos.update(p)) {
                System.out.println("Produto " + (estado ? "" : "in") +"ativado com sucesso.");
            } else {
                System.out.println("Falha ao " + (estado ? "" : "in") +"ativar produto.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao " + (estado ? "" : "in") +"ativar produto: " + e.getMessage());
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