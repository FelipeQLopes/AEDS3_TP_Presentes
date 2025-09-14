import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class MenuUsuario {

    int ID_GLOBAL;
    String NOME_GLOBAL;
    String CPF_GLOBAL;
    
    ArquivoUsuario arqUsuarios;
    private static Scanner console = new Scanner(System.in);
    

    public MenuUsuario() throws Exception {
        arqUsuarios = new ArquivoUsuario();
    }

    //region menu
    public void menu() {

        int opcao;
        do {

            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println(">");
            System.out.println("\n1 - Login");
            System.out.println("2 - Novo Usuário");
            System.out.println("3 - Recuperação de Senha");
            System.out.println("0 - Sair");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    System.out.println("\n\nPresenteFácil 1.0");
                    System.out.println("-----------------");
                    System.out.println("> Login\n");
                    login();
                    break;                
                case 2:
                    System.out.println("\n\nPresenteFácil 1.0");
                    System.out.println("-----------------");
                    System.out.println("> Inclusão de Usuário\n");
                    incluirUsuario();
                    break;
                case 3:
                    System.out.println("\n\nPresenteFácil 1.0");
                    System.out.println("-----------------");
                    System.out.println("> Recuperação de Senha\n");
                    recuperarUsuario();
                    break;
                case 10:
                    buscarUsuario();
                    break;
                case 30:
                    System.out.println("\n\nPresenteFácil 1.0");
                    System.out.println("-----------------");
                    System.out.println("> Alterar Usuário\n");
                    alterarUsuario();
                    break;
                case 40:
                    System.out.println("\n\nPresenteFácil 1.0");
                    System.out.println("-----------------");
                    System.out.println("> Excluir Usuário\n");
                    excluirUsuario();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }
    //endregion menu

    //region login
    public void login(){
        String cpf;
        String senha;
        String hashSenha;
        boolean cpfValido = false;

        do {
            System.out.println("Aviso: Deixe o valor vazio para cancelar");
            System.out.print("\nDigite seu CPF: ");
            cpf = console.nextLine();  // Lê o CPF digitado pelo usuário

            if(cpf.isEmpty())
                return; 

            // Validação do CPF (11 dígitos e composto apenas por números)
            if (cpf.length() == 11 && cpf.matches("\\d{11}")) {
                cpfValido = true;  // CPF válido
            } else {
                System.out.println("CPF inválido. O CPF deve conter exatamente 11 dígitos numéricos, sem pontos e traços.");
            }
        } while (!cpfValido);

        System.out.print("\nDigite a sua senha: ");
        senha = console.nextLine();
        hashSenha = Usuario.gerarHash(senha);

        try {
            Usuario usuario = arqUsuarios.read(cpf);  // Chama o método de leitura da classe Arquivo
            if (usuario != null) {
                if(usuario.hashSenha.equals(hashSenha)){
                    ID_GLOBAL = usuario.getId();
                    NOME_GLOBAL = usuario.nome;
                    CPF_GLOBAL = usuario.cpf;
                    menuLogado();
                }else{
                    System.out.println("Erro no Sistema. O CPF ou a Senha estão incorretos");
                }
            } else {
                System.out.println("Erro no Sistema. O CPF ou a Senha estão incorretos");
            }
        } catch(Exception e) {
            System.out.println("Erro no Sistema. O CPF ou a Senha estão incorretos");
            e.printStackTrace();
        }

    }
    //endregion login

    //region menuLogado
    public void menuLogado(){

        int opcao;
        do {

            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Home");
            System.out.println("\n1 - Listas");
            System.out.println("2 - Produtos");
            System.out.println("3 - Meus Dados");
            System.out.println("0 - Logout");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    //(new MenuLista(ID_GLOBAL, NOME_GLOBAL, CPF_GLOBAL)).menu();
                    break;                
                case 2:
                    //(new MenuProdutos(ID_GLOBAL, NOME_GLOBAL).menu())
                    break;
                case 3:
                    meusDados();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }
    //endregion menuLogado

    //region buscarUsuario
    public void buscarUsuario() {
        System.out.println("\nBusca de usuario");
        String cpf;
        boolean cpfValido = false;

        do {
            System.out.print("\nCPF (11 dígitos): ");
            cpf = console.nextLine();  // Lê o CPF digitado pelo usuário

            if(cpf.isEmpty())
                return; 

            // Validação do CPF (11 dígitos e composto apenas por números)
            if (cpf.length() == 11 && cpf.matches("\\d{11}")) {
                cpfValido = true;  // CPF válido
            } else {
                System.out.println("CPF inválido. O CPF deve conter exatamente 11 dígitos numéricos, sem pontos e traços.");
            }
        } while (!cpfValido);

        try {
            Usuario usuario = arqUsuarios.read(cpf);  // Chama o método de leitura da classe Arquivo
            if (usuario != null) {
                mostraUsuario(usuario);  // Exibe os detalhes do usuario encontrado
            } else {
                System.out.println("Usuario não encontrado.");
            }
        } catch(Exception e) {
            System.out.println("Erro do sistema. Não foi possível buscar o usuario!");
            e.printStackTrace();
        }
    } 
    //endregion buscarUsuario

    //region incluirUsuario
    public void incluirUsuario() {
        String nome = "";
        String cpf = "";
        String email = "";
        String perguntaSecreta = "";
        String respostaSecreta = "";
        String hashSenha = "";
        LocalDate nascimento = null;
        boolean dadosCorretos = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        do {
            System.out.print("\nNome (min. de 4 letras ou vazio para cancelar): ");
            nome = console.nextLine();
            if(nome.length()==0)
                return;
            if(nome.length()<4)
                System.err.println("O nome do usuario deve ter no mínimo 4 caracteres.");
        } while(nome.length()<4);

        do {
            System.out.print("CPF (11 dígitos sem pontos ou traço): ");
            cpf = console.nextLine();
            if(cpf.length()!=11 && cpf.length()!=0)
                System.err.println("O CPF deve ter exatamente 11 dígitos.");
        } while(cpf.length()!=11 && cpf.length()!=0);

        do {
            System.out.print("Email: ");
            email = console.nextLine();
            if(!(email.contains("@") && email.contains(".") && email.length() > 10))
                System.err.println("O Email deve ser um email válido");
        } while(!(email.contains("@") && email.contains(".") && email.length() > 10));

        do {
            System.out.print("Senha: ");
            hashSenha = console.nextLine();
            if(hashSenha.length() < 6)
                System.err.println("A senha tem que ter no mínimo 6 dígitos");
        } while(hashSenha.length() < 6);
        
        do {
            System.out.print("Pergunta Secreta: ");
            perguntaSecreta = console.nextLine();
            if(perguntaSecreta.length()<10 || !(perguntaSecreta.contains("?")))
                System.err.println("Escreva uma pergunta váilida com ? e mais de 10 caracteres.");
        } while(perguntaSecreta.length()<10 || !(perguntaSecreta.contains("?")));
        
        do {
            System.out.print("Resposta Secreta: ");
            respostaSecreta = console.nextLine();
            if(respostaSecreta.length()<10)
                System.err.println("Escreva uma pergunta váilida com ? e mais de 10 caracteres.");
        } while(respostaSecreta.length()<10);

        do {
            System.out.print("Data de nascimento (DD/MM/AAAA): ");
            String dataStr = console.nextLine();
            dadosCorretos = false;
            try {
                nascimento = LocalDate.parse(dataStr, formatter);
                dadosCorretos = true;
            } catch (Exception e) {
                System.err.println("Data inválida! Use o formato DD/MM/AAAA.");
            }
        } while(!dadosCorretos);

        String r;
        do{
            System.out.print("\nConfirma a inclusão do Usuário? (S/N) ");
            r = console.nextLine();
        }while(r.length() == 0);
        char resp = r.charAt(0);
        
        if(resp=='S' || resp=='s') {
            try {
                Usuario c = new Usuario(nome, cpf, email, perguntaSecreta, respostaSecreta, hashSenha, nascimento);
                arqUsuarios.create(c);
                System.out.println("Usuario incluído com sucesso.");
            } catch(Exception e) {
                System.out.println("Erro do sistema. Não foi possível incluir o usuario!");
            }
        }
    }
    //endregion incluirUsuario

    //region alterarUsuario
    public void alterarUsuario() {
        String cpf;
        boolean cpfValido = false;

        do {
            System.out.print("\nCPF (11 dígitos): ");
            cpf = console.nextLine();  // Lê o CPF digitado pelo usuário

            if(cpf.isEmpty())
                return; 

            // Validação do CPF (11 dígitos e composto apenas por números)
            if (cpf.length() == 11 && cpf.matches("\\d{11}")) {
                cpfValido = true;  // CPF válido
            } else {
                System.out.println("CPF inválido. O CPF deve conter exatamente 11 dígitos numéricos, sem pontos e traços.");
            }
        } while (!cpfValido);


        try {
            // Tenta ler o usuario com o ID fornecido
            Usuario usuario = arqUsuarios.read(cpf);
            if (usuario != null) {
                System.out.println("Usuario encontrado:");
                mostraUsuario(usuario);  // Exibe os dados do usuario para confirmação

                // Alteração de nome
                System.out.print("\nNovo nome (deixe em branco para manter o anterior): ");
                String novoNome = console.nextLine();
                if (!novoNome.isEmpty()) {
                    usuario.nome = novoNome;  // Atualiza o nome se fornecido
                }

                // Alteração de CPF
                System.out.print("Novo CPF (deixe em branco para manter o anterior): ");
                String novoCpf = console.nextLine();
                if (!novoCpf.isEmpty()) {
                    usuario.cpf = novoCpf;  // Atualiza o CPF se fornecido
                }
                
                // Alteração de Email
                System.out.print("Novo Email (deixe em branco para manter o anterior): ");
                String novoEmail = console.nextLine();
                if (!novoCpf.isEmpty()) {
                    usuario.email = novoEmail;  // Atualiza o Email se fornecido
                }
                
                // Alteração de Senha
                System.out.print("Nova Senha (deixe em branco para manter o anterior): ");
                String hashSenha = console.nextLine();
                if (!hashSenha.isEmpty()) {
                    usuario.hashSenha = Usuario.gerarHash(hashSenha);  // Atualiza a Senha
                }
                
                // Alteração de Pergunta Secreta
                System.out.print("Nova Pergunta Secreta (deixe em branco para manter o anterior): ");
                String novaPerguntaSecreta = console.nextLine();
                if (!novaPerguntaSecreta.isEmpty()) {
                    usuario.perguntaSecreta = novaPerguntaSecreta; // Atualiza a Pergunta Secreta se fornecido
                }
                
                // Alteração de Resposta Secreta
                System.out.print("Nova Resposta Secreta (deixe em branco para manter o anterior): ");
                String novaRespostaSecreta = console.nextLine();
                if (!novaRespostaSecreta.isEmpty()) {
                    usuario.respostaSecreta = novaRespostaSecreta;  // Atualiza a Resposta Secreta se fornecido
                }
                

                // Alteração de data de nascimento
                System.out.print("Nova data de nascimento (DD/MM/AAAA) (deixe em branco para manter a anterior): ");
                String novaDataStr = console.nextLine();
                if (!novaDataStr.isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        usuario.nascimento = LocalDate.parse(novaDataStr, formatter);  // Atualiza a data de nascimento se fornecida
                    } catch (Exception e) {
                        System.err.println("Data inválida. Valor mantido.");
                    }
                }

                String r;
                do{
                    System.out.print("\nConfirma a alteração do Usuário? (S/N) ");
                    r = console.nextLine();
                }while(r.length() == 0);
                char resp = r.charAt(0);

                if (resp == 'S' || resp == 's') {
                    // Salva as alterações no arquivo
                    boolean alterado = arqUsuarios.update(usuario);
                    if (alterado) {
                        System.out.println("Usuario alterado com sucesso.");
                    } else {
                        System.out.println("Erro ao alterar o usuario.");
                    }
                } else {
                    System.out.println("Alterações canceladas.");
                }
            } else {
                System.out.println("Usuario não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível alterar o usuario!");
            e.printStackTrace();
        }
        
    }

    public void alterarUsuario(String CPF_GLOBAL){

        try {
            // Tenta ler o usuario com o ID fornecido
            Usuario usuario = arqUsuarios.read(CPF_GLOBAL);
            if (usuario != null) {

                // Alteração de nome
                System.out.print("\nNovo nome (deixe em branco para manter o anterior): ");
                String novoNome = console.nextLine();
                if (!novoNome.isEmpty()) {
                    usuario.nome = novoNome;  // Atualiza o nome se fornecido
                }

                // Alteração de CPF
                System.out.print("Novo CPF (deixe em branco para manter o anterior): ");
                String novoCpf = console.nextLine();
                if (!novoCpf.isEmpty()) {
                    usuario.cpf = novoCpf;  // Atualiza o CPF se fornecido
                }
                
                // Alteração de Email
                System.out.print("Novo Email (deixe em branco para manter o anterior): ");
                String novoEmail = console.nextLine();
                if (!novoCpf.isEmpty()) {
                    usuario.email = novoEmail;  // Atualiza o Email se fornecido
                }
                
                // Alteração de Pergunta Secreta
                System.out.print("Nova Pergunta Secreta (deixe em branco para manter o anterior): ");
                String novaPerguntaSecreta = console.nextLine();
                if (!novaPerguntaSecreta.isEmpty()) {
                    usuario.perguntaSecreta = novaPerguntaSecreta; // Atualiza a Pergunta Secreta se fornecido
                }
                
                // Alteração de Resposta Secreta
                System.out.print("Nova Resposta Secreta (deixe em branco para manter o anterior): ");
                String novaRespostaSecreta = console.nextLine();
                if (!novaRespostaSecreta.isEmpty()) {
                    usuario.respostaSecreta = novaRespostaSecreta;  // Atualiza a Resposta Secreta se fornecido
                }
                
                // Alteração de Senha
                System.out.print("Nova Resposta Secreta (deixe em branco para manter o anterior): ");
                String hashSenha = console.nextLine();
                if (!hashSenha.isEmpty()) {
                    usuario.hashSenha = Usuario.gerarHash(hashSenha);  // Atualiza a Senha
                }

                // Alteração de data de nascimento
                System.out.print("Nova data de nascimento (DD/MM/AAAA) (deixe em branco para manter a anterior): ");
                String novaDataStr = console.nextLine();
                if (!novaDataStr.isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        usuario.nascimento = LocalDate.parse(novaDataStr, formatter);  // Atualiza a data de nascimento se fornecida
                    } catch (Exception e) {
                        System.err.println("Data inválida. Valor mantido.");
                    }
                }

                String r;
                do{
                    System.out.print("\nConfirma a Alteração do Usuário? (S/N) ");
                    r = console.nextLine();
                }while(r.length() == 0);
                char resp = r.charAt(0);

                if (resp == 'S' || resp == 's') {
                    // Salva as alterações no arquivo
                    boolean alterado = arqUsuarios.update(usuario);
                    if (alterado) {
                        System.out.println("Usuario alterado com sucesso.");
                    } else {
                        System.out.println("Erro ao alterar o usuario.");
                    }
                } else {
                    System.out.println("Alterações canceladas.");
                }
            } else {
                System.out.println("Usuario não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível alterar o usuario!");
            e.printStackTrace();
        }
        
    }
    //endregion alterarUsuario

    //region excluirUsuario
    public void excluirUsuario() {
        System.out.println("\nExclusão de usuario");
        String cpf;
        boolean cpfValido = false;

        do {
            System.out.print("\nCPF (11 dígitos): ");
            cpf = console.nextLine();  // Lê o CPF digitado pelo usuário

            if(cpf.isEmpty())
                return; 

            // Validação do CPF (11 dígitos e composto apenas por números)
            if (cpf.length() == 11 && cpf.matches("\\d{11}")) {
                cpfValido = true;  // CPF válido
            } else {
                System.out.println("CPF inválido. O CPF deve conter exatamente 11 dígitos numéricos, sem pontos e traços.");
            }
        } while (!cpfValido);

        try {
            // Tenta ler o usuario com o ID fornecido
            Usuario usuario = arqUsuarios.read(cpf);
            if (usuario != null) {
                System.out.println("Usuario encontrado:");
                mostraUsuario(usuario);  // Exibe os dados do usuario para confirmação

                String r;
                do{
                    System.out.print("\nConfirma a Exclusão do Usuário? (S/N) ");
                    r = console.nextLine();
                }while(r.length() == 0);
                char resp = r.charAt(0);

                if (resp == 'S' || resp == 's') {
                    boolean excluido = arqUsuarios.delete(cpf);  // Chama o método de exclusão no arquivo
                    if (excluido) {
                        System.out.println("Usuario excluído com sucesso.");
                    } else {
                        System.out.println("Erro ao excluir o usuario.");
                    }
                } else {
                    System.out.println("Exclusão cancelada.");
                }
            } else {
                System.out.println("Usuario não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível excluir o usuario!");
            e.printStackTrace();
        }
    }

    public void excluirUsuario(String CPF_GLOBAL) {
        try {
            // Tenta ler o usuario com o ID fornecido
            Usuario usuario = arqUsuarios.read(CPF_GLOBAL);
            if (usuario != null) {

                String r;
                do{
                    System.out.print("\nConfirma a Exclusão do Usuário? (S/N) ");
                    r = console.nextLine();
                }while(r.length() == 0);
                char resp = r.charAt(0);

                if (resp == 'S' || resp == 's') {
                    boolean excluido = arqUsuarios.delete(CPF_GLOBAL);  // Chama o método de exclusão no arquivo
                    if (excluido) {
                        System.out.println("Usuario excluído com sucesso.");
                    } else {
                        System.out.println("Erro ao excluir o usuario.");
                    }
                } else {
                    System.out.println("Exclusão cancelada.");
                }
            } else {
                System.out.println("Usuario não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível excluir o usuario!");
            e.printStackTrace();
        }
    }
    //endregion excluirUsuario

    //region mostraUsuario
    public void mostraUsuario(Usuario usuario) {
    if (usuario != null) {
        System.out.println("\nDetalhes do Usuario:");
        System.out.println("----------------------");
        System.out.printf("Nome......: %s%n", usuario.nome);
        System.out.printf("CPF.......: %s%n", usuario.cpf);
        System.out.printf("Email.....: %s%n", usuario.email);
        System.out.printf("Nascimento: %s%n", usuario.nascimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("----------------------");
        }
    }
    //endregion mostraUsuario

    //region meusDados
    public void meusDados(){

        int opcao = -1;

        do {

            System.out.println("\n\nPresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Home > Meus Dados\n");

            try{
                Usuario usuario = arqUsuarios.read(CPF_GLOBAL);
                mostraUsuario(usuario);

                System.out.println("\n1 - Alterar Informações");
                System.out.println("2 - Excluir Usuário");
                System.out.println("0 - Voltar");

                System.out.print("\nOpção: ");
                try {
                    opcao = Integer.valueOf(console.nextLine());
                } catch(NumberFormatException e) {
                    opcao = -1;
                }

                switch (opcao) {
                    case 1:
                        System.out.println("\n\nPresenteFácil 1.0");
                        System.out.println("-----------------");
                        System.out.println("> Home > Meus Dados > Alterar Informações\n");
                        alterarUsuario(CPF_GLOBAL);
                        break;                
                    case 2:
                        System.out.println("\n\nPresenteFácil 1.0");
                        System.out.println("-----------------");
                        System.out.println("> Home > Meus Dados > Excluir Usuário\n");
                        excluirUsuario(CPF_GLOBAL);
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }

                
            } catch(Exception e) {
            System.out.println("Erro no Sistema. O CPF ou a Senha estão incorretos");
            e.printStackTrace();
            }
            
        } while (opcao != 0);

    }
    //endregion meusDados

    //region recuperarUsuario
    public void recuperarUsuario(){
        String cpf;
        boolean cpfValido = false;

        do {
            System.out.print("\nCPF (11 dígitos): ");
            cpf = console.nextLine();  // Lê o CPF digitado pelo usuário

            if(cpf.isEmpty())
                return; 

            // Validação do CPF (11 dígitos e composto apenas por números)
            if (cpf.length() == 11 && cpf.matches("\\d{11}")) {
                cpfValido = true;  // CPF válido
            } else {
                System.out.println("CPF inválido. O CPF deve conter exatamente 11 dígitos numéricos, sem pontos e traços.");
            }
        } while (!cpfValido);


        try {
            // Tenta ler o usuario com o ID fornecido
            Usuario usuario = arqUsuarios.read(cpf);
            if (usuario != null) {
                System.out.print("Pergunta Secreta: " + usuario.perguntaSecreta);
                // Resposta Secreta
                System.out.print("\nResposta Secreta: ");
                String resposta = console.nextLine().trim().toLowerCase();
                if(resposta.equals(usuario.respostaSecreta.trim().toLowerCase())){
                    System.out.println("\nSua resposta está correta!\n");
                    System.out.print("Insira a nova Senha: ");
                    String novaSenha = Usuario.gerarHash(console.nextLine());
                    usuario.hashSenha = novaSenha;
                    arqUsuarios.update(usuario);
                    System.out.println("\nSua senha foi alterada!\n");
                }else{
                    System.out.println("\nResposta Errada!");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível alterar o usuario!");
            e.printStackTrace();
        }



    }
}