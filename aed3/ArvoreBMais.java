package aed3;

import java.io.*;
import java.util.ArrayList;
import java.lang.reflect.Constructor;

public class ArvoreBMais<T extends RegistroArvoreBMais<T>> {

    private int ordem; // Número máximo de filhos que uma página pode conter
    private int maxElementos; // Variável igual a ordem - 1 para facilitar a clareza do código
    private int maxFilhos; // Variável igual a ordem para facilitar a clareza do código
    private RandomAccessFile arquivo; // Arquivo em que a árvore será armazenada
    private String nomeArquivo;
    private Constructor<T> construtor;

    private T elemAux;
    private long paginaAux;
    private boolean cresceu;
    private boolean diminuiu;
    public ArvoreBMais(Constructor<T> construtor, String nomeArquivo) throws Exception {
        this(construtor, 4, nomeArquivo); // Ordem padrão = 4
    }

    private class Pagina {

        protected int ordem; // Número máximo de filhos que uma página pode ter
        protected Constructor<T> construtor;
        protected int maxElementos; // Variável igual a ordem - 1 para facilitar a clareza do código
        protected int maxFilhos; // Variável igual a ordem para facilitar a clareza do código
        protected int TAMANHO_ELEMENTO; // Os elementos são de tamanho fixo
        protected int TAMANHO_PAGINA; // A página será de tamanho fixo, calculado a partir da ordem

        protected ArrayList<T> elementos; // Elementos da página
        protected ArrayList<Long> filhos; // Vetor de ponteiros para os filhos
        protected long proxima; // Próxima folha, quando a página for uma folha

        public Pagina(Constructor<T> ct, int o) throws Exception {

            this.construtor = ct;
            this.ordem = o;
            this.maxFilhos = this.ordem;
            this.maxElementos = this.ordem - 1;
            this.elementos = new ArrayList<>(this.maxElementos);
            this.filhos = new ArrayList<>(this.maxFilhos);
            this.proxima = -1;

            this.TAMANHO_ELEMENTO = this.construtor.newInstance().size();
            this.TAMANHO_PAGINA = 4 + this.maxElementos * this.TAMANHO_ELEMENTO + this.maxFilhos * 8 + 8;
        }

        
        protected byte[] toByteArray() throws IOException {

            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(ba);

            out.writeInt(this.elementos.size());

            int i = 0;
            while (i < this.elementos.size()) {
                out.writeLong(this.filhos.get(i).longValue());
                out.write(this.elementos.get(i).toByteArray());
                i++;
            }
            if (this.filhos.size() > 0)
                out.writeLong(this.filhos.get(i).longValue());
            else
                out.writeLong(-1L);

            byte[] registroVazio = new byte[TAMANHO_ELEMENTO];
            while (i < this.maxElementos) {
                out.write(registroVazio);
                out.writeLong(-1L);
                i++;
            }

            out.writeLong(this.proxima);

            return ba.toByteArray();
        }

        public void fromByteArray(byte[] buffer) throws Exception {

            ByteArrayInputStream ba = new ByteArrayInputStream(buffer);
            DataInputStream in = new DataInputStream(ba);

            int n = in.readInt();

            int i = 0;
            this.elementos = new ArrayList<>(this.maxElementos);
            this.filhos = new ArrayList<>(this.maxFilhos);
            T elem;
            while (i < n) {
                this.filhos.add(in.readLong());
                byte[] registro = new byte[TAMANHO_ELEMENTO];
                in.read(registro);
                elem = this.construtor.newInstance();
                elem.fromByteArray(registro);
                this.elementos.add(elem);
                i++;
            }
            this.filhos.add(in.readLong());
            in.skipBytes((this.maxElementos - i) * (TAMANHO_ELEMENTO + 8));
            this.proxima = in.readLong();
        }
    }

    // ------------------------------------------------------------------------------

    public ArvoreBMais(Constructor<T> c, int o, String na) throws Exception {

        // Inicializa os atributos da árvore
        construtor = c;
        ordem = o;
        maxElementos = o - 1;
        maxFilhos = o;
        nomeArquivo = na;

        // Abre (ou cria) o arquivo, escrevendo uma raiz empty, se necessário.
        arquivo = new RandomAccessFile(nomeArquivo, "rw");
        if (arquivo.length() < 16) {
            arquivo.writeLong(-1); // raiz empty
            arquivo.writeLong(-1); // pointeiro lista excluídos
        }
    }

    public boolean empty() throws IOException {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        return raiz == -1;
    }

    public ArrayList<T> read(T elem) throws Exception {

        // Recupera a raiz da árvore
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();

        // Executa a busca recursiva
        if (raiz != -1)
            return read1(elem, raiz);
        else {
            ArrayList<T> resposta = new ArrayList<>();
            return resposta;
        }
    }

    private ArrayList<T> read1(T elem, long pagina) throws Exception {

        if (pagina == -1) {
            ArrayList<T> resposta = new ArrayList<>();
            return resposta;
        }

        arquivo.seek(pagina);
        Pagina pa = new Pagina(construtor, ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.fromByteArray(buffer);

        int i = 0;
        while (elem!=null && i < pa.elementos.size() && elem.compareTo(pa.elementos.get(i)) > 0) {
            i++;
        }
        if (i < pa.elementos.size() && pa.filhos.get(0) == -1 && (elem==null || elem.compareTo(pa.elementos.get(i)) == 0)) {

            ArrayList<T> lista = new ArrayList<>();
            while (elem==null || elem.compareTo(pa.elementos.get(i)) <= 0) {

                if (elem==null || elem.compareTo(pa.elementos.get(i)) == 0)
                    lista.add(pa.elementos.get(i));
                i++;

                if (i == pa.elementos.size()) {
                    if (pa.proxima == -1)
                        break;
                    arquivo.seek(pa.proxima);
                    arquivo.read(buffer);
                    pa.fromByteArray(buffer);
                    i = 0;
                }
            }
            return lista;
        }

        else if (i == pa.elementos.size() && pa.filhos.get(0) == -1) {

            if (pa.proxima == -1) {
                ArrayList<T> resposta = new ArrayList<>();
                return resposta;
            }

            arquivo.seek(pa.proxima);
            arquivo.read(buffer);
            pa.fromByteArray(buffer);

            i = 0;
            if (elem.compareTo(pa.elementos.get(i)) <= 0) {

                ArrayList<T> lista = new ArrayList<>();

                while (elem.compareTo(pa.elementos.get(i)) <= 0) {
                    if (elem.compareTo(pa.elementos.get(i)) == 0)
                        lista.add(pa.elementos.get(i));
                    i++;
                    if (i == pa.elementos.size()) {
                        if (pa.proxima == -1)
                            break;
                        arquivo.seek(pa.proxima);
                        arquivo.read(buffer);
                        pa.fromByteArray(buffer);
                        i = 0;
                    }
                }

                return lista;
            }

            else {
                ArrayList<T> resposta = new ArrayList<>();
                return resposta;
            }
        }

        if (elem==null || i == pa.elementos.size() || elem.compareTo(pa.elementos.get(i)) <= 0)
            return read1(elem, pa.filhos.get(i));
        else
            return read1(elem, pa.filhos.get(i + 1));
    }
    public boolean create(T elem) throws Exception {

        // Carrega a raiz
        arquivo.seek(0);
        long pagina;
        pagina = arquivo.readLong();

        elemAux = elem.clone();

        paginaAux = -1;
        cresceu = false;

        boolean inserido = create1(pagina);

        if (cresceu) {

            Pagina novaPagina = new Pagina(construtor, ordem);
            novaPagina.elementos = new ArrayList<>(this.maxElementos);
            novaPagina.elementos.add(elemAux);
            novaPagina.filhos = new ArrayList<>(this.maxFilhos);
            novaPagina.filhos.add(pagina);
            novaPagina.filhos.add(paginaAux);

            arquivo.seek(8);
            long end = arquivo.readLong();
            if(end==-1) {
                end = arquivo.length();
            } else { // reusa um endereço e atualiza a lista de excluídos no cabeçalho
                arquivo.seek(end);
                Pagina pa_excluida = new Pagina(construtor, ordem);
                byte[] buffer = new byte[pa_excluida.TAMANHO_PAGINA];
                arquivo.read(buffer);
                pa_excluida.fromByteArray(buffer);
                arquivo.seek(8);
                arquivo.writeLong(pa_excluida.proxima);
            }
            arquivo.seek(end);
            long raiz = arquivo.getFilePointer();
            arquivo.write(novaPagina.toByteArray());
            arquivo.seek(0);
            arquivo.writeLong(raiz);
            inserido = true;
        }

        return inserido;
    }

    private boolean create1(long pagina) throws Exception {

        if (pagina == -1) {
            cresceu = true;
            paginaAux = -1;
            return false;
        }

        // Lê a página passada como referência
        arquivo.seek(pagina);
        Pagina pa = new Pagina(construtor, ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.fromByteArray(buffer);

        int i = 0;
        while (i < pa.elementos.size() && (elemAux.compareTo(pa.elementos.get(i)) > 0)) {
            i++;
        }
        if (i < pa.elementos.size() && pa.filhos.get(0) == -1 && elemAux.compareTo(pa.elementos.get(i)) == 0) {
            cresceu = false;
            return false;
        }

        // Continua a busca recursiva por uma nova página. A busca continuará até o
        // filho inexistente de uma página folha ser alcançado.
        boolean inserido;
        if (i == pa.elementos.size() || elemAux.compareTo(pa.elementos.get(i)) < 0)
            inserido = create1(pa.filhos.get(i));
        else
            inserido = create1(pa.filhos.get(i + 1));

        if (!cresceu)
            return inserido;

        // Se tiver espaço na página, faz a inclusão nela mesmo
        if (pa.elementos.size() < maxElementos) {

            pa.elementos.add(i, elemAux);
            pa.filhos.add(i + 1, paginaAux);

            // Escreve a página atualizada no arquivo
            arquivo.seek(pagina);
            arquivo.write(pa.toByteArray());

            // Encerra o processo de crescimento e retorna
            cresceu = false;
            return true;
        }

        // Cria uma nova página
        Pagina np = new Pagina(construtor, ordem);

        int meio = maxElementos / 2;
        np.filhos.add(pa.filhos.get(meio)); // COPIA o primeiro ponteiro
        for (int j = 0; j < (maxElementos - meio); j++) {
            np.elementos.add(pa.elementos.remove(meio)); // MOVE os elementos
            np.filhos.add(pa.filhos.remove(meio + 1)); // MOVE os demais ponteiros
        }

        if (i <= meio) {
            pa.elementos.add(i, elemAux);
            pa.filhos.add(i + 1, paginaAux);

            if (pa.filhos.get(0) == -1)
                elemAux = np.elementos.get(0).clone();

            else {
                elemAux = pa.elementos.remove(pa.elementos.size() - 1);
                pa.filhos.remove(pa.filhos.size() - 1);
            }
        }

        else {

            int j = maxElementos - meio;
            while (elemAux.compareTo(np.elementos.get(j - 1)) < 0)
                j--;
            np.elementos.add(j, elemAux);
            np.filhos.add(j + 1, paginaAux);

            // Seleciona o primeiro elemento da página da direita para ser promovido
            elemAux = np.elementos.get(0).clone();

            // Se não for folha, remove o elemento promovido da página
            if (pa.filhos.get(0) != -1) {
                np.elementos.remove(0);
                np.filhos.remove(0);
            }

        }

        // Obtém um endereço para a nova página (página excluída ou fim do arquivo)
        arquivo.seek(8);
        long end = arquivo.readLong();
        if(end==-1) {
            end = arquivo.length();
        } else { // reusa um endereço e atualiza a lista de excluídos no cabeçalho
            arquivo.seek(end);
            Pagina pa_excluida = new Pagina(construtor, ordem);
            buffer = new byte[pa_excluida.TAMANHO_PAGINA];
            arquivo.read(buffer);
            pa_excluida.fromByteArray(buffer);
            arquivo.seek(8);
            arquivo.writeLong(pa_excluida.proxima);
        }

        if (pa.filhos.get(0) == -1) {
            np.proxima = pa.proxima;
            pa.proxima = end;
        }

        // Grava as páginas no arquivo
        paginaAux = end;
        arquivo.seek(paginaAux);
        arquivo.write(np.toByteArray());

        arquivo.seek(pagina);
        arquivo.write(pa.toByteArray());

        return true;
    }

    public boolean delete(T elem) throws Exception {

        // Encontra a raiz da árvore
        arquivo.seek(0);
        long pagina;
        pagina = arquivo.readLong();

        diminuiu = false;

        boolean excluido = delete1(elem, pagina);

        if (excluido && diminuiu) {

            // Lê a raiz
            arquivo.seek(pagina);
            Pagina pa = new Pagina(construtor, ordem);
            byte[] buffer = new byte[pa.TAMANHO_PAGINA];
            arquivo.read(buffer);
            pa.fromByteArray(buffer);

            if (pa.elementos.size() == 0) {
                arquivo.seek(0);
                arquivo.writeLong(pa.filhos.get(0));

                arquivo.seek(8);
                long end = arquivo.readLong();  // cabeça da lista de páginas excluídas
                pa.proxima = end;
                arquivo.seek(8);
                arquivo.writeLong(pagina);
                arquivo.seek(pagina);
                arquivo.write(pa.toByteArray());
            }
        }

        return excluido;
    }

    private boolean delete1(T elem, long pagina) throws Exception {

        // Declaração de variáveis
        boolean excluido = false;
        int diminuido;

        if (pagina == -1) {
            diminuiu = false;
            return false;
        }

        // Lê o registro da página no arquivo
        arquivo.seek(pagina);
        Pagina pa = new Pagina(construtor, ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.fromByteArray(buffer);

        int i = 0;
        while (i < pa.elementos.size() && elem.compareTo(pa.elementos.get(i)) > 0) {
            i++;
        }

        // Chaves encontradas em uma folha
        if (i < pa.elementos.size() && pa.filhos.get(0) == -1 && elem.compareTo(pa.elementos.get(i)) == 0) {

            pa.elementos.remove(i);
            pa.filhos.remove(i + 1);

            // Atualiza o registro da página no arquivo
            arquivo.seek(pagina);
            arquivo.write(pa.toByteArray());

            diminuiu = pa.elementos.size() < maxElementos / 2;
            return true;
        }
        if (i == pa.elementos.size() || elem.compareTo(pa.elementos.get(i)) < 0) {
            excluido = delete1(elem, pa.filhos.get(i));
            diminuido = i;
        } else {
            excluido = delete1(elem, pa.filhos.get(i + 1));
            diminuido = i + 1;
        }
        if (diminuiu) {

            long paginaFilho = pa.filhos.get(diminuido);
            Pagina pFilho = new Pagina(construtor, ordem);
            arquivo.seek(paginaFilho);
            arquivo.read(buffer);
            pFilho.fromByteArray(buffer);

            long paginaIrmaoEsq = -1, paginaIrmaoDir = -1;
            Pagina pIrmaoEsq = null, pIrmaoDir = null; // inicializados com null para controle de existência

            if (diminuido > 0) { // possui um irmão esquerdo, pois não é a primeira filho do pai
                paginaIrmaoEsq = pa.filhos.get(diminuido - 1);
                pIrmaoEsq = new Pagina(construtor, ordem);
                arquivo.seek(paginaIrmaoEsq);
                arquivo.read(buffer);
                pIrmaoEsq.fromByteArray(buffer);
            }
            if (diminuido < pa.elementos.size()) { // possui um irmão direito, pois não é o último filho do pai
                paginaIrmaoDir = pa.filhos.get(diminuido + 1);
                pIrmaoDir = new Pagina(construtor, ordem);
                arquivo.seek(paginaIrmaoDir);
                arquivo.read(buffer);
                pIrmaoDir.fromByteArray(buffer);
            }

            if (pIrmaoEsq != null && pIrmaoEsq.elementos.size() > maxElementos / 2) {

                if (pFilho.filhos.get(0) == -1)
                    pFilho.elementos.add(0, pIrmaoEsq.elementos.remove(pIrmaoEsq.elementos.size() - 1));

                else
                    pFilho.elementos.add(0, pa.elementos.get(diminuido - 1));

                pa.elementos.set(diminuido - 1, pFilho.elementos.get(0));

                pFilho.filhos.add(0, pIrmaoEsq.filhos.remove(pIrmaoEsq.filhos.size() - 1));

            }

            else if (pIrmaoDir != null && pIrmaoDir.elementos.size() > maxElementos / 2) {
                // Se for folha
                if (pFilho.filhos.get(0) == -1) {

                    pFilho.elementos.add(pIrmaoDir.elementos.remove(0));
                    pFilho.filhos.add(pIrmaoDir.filhos.remove(0));

                    pa.elementos.set(diminuido, pIrmaoDir.elementos.get(0));
                }

                else {
                    pFilho.elementos.add(pa.elementos.get(diminuido));
                    pFilho.filhos.add(pIrmaoDir.filhos.remove(0));

                    pa.elementos.set(diminuido, pIrmaoDir.elementos.remove(0));
                }
            }

            else if (pIrmaoEsq != null) {
                if (pFilho.filhos.get(0) != -1) {
                    pIrmaoEsq.elementos.add(pa.elementos.remove(diminuido - 1));
                    pIrmaoEsq.filhos.add(pFilho.filhos.remove(0));
                }
                else {
                    pa.elementos.remove(diminuido - 1);
                    pFilho.filhos.remove(0);
                }
                pa.filhos.remove(diminuido); // remove o ponteiro para a própria página

                pIrmaoEsq.elementos.addAll(pFilho.elementos);
                pIrmaoEsq.filhos.addAll(pFilho.filhos);
                pFilho.elementos.clear(); 
                pFilho.filhos.clear();

                if (pIrmaoEsq.filhos.get(0) == -1)
                    pIrmaoEsq.proxima = pFilho.proxima;

                arquivo.seek(8);
                pFilho.proxima = arquivo.readLong();
                arquivo.seek(8);
                arquivo.writeLong(paginaFilho);

            }

            // Senão, faz a fusão com o irmão direito, assumindo que ele existe
            else {
                if (pFilho.filhos.get(0) != -1) {
                    pFilho.elementos.add(pa.elementos.remove(diminuido));
                    pFilho.filhos.add(pIrmaoDir.filhos.remove(0));
                }
                // Senão, apenas remove o elemento do pai
                else {
                    pa.elementos.remove(diminuido);
                    pFilho.filhos.remove(0);
                }
                pa.filhos.remove(diminuido + 1); // remove o ponteiro para o irmão direito

                // Move todos os registros do irmão da direita
                pFilho.elementos.addAll(pIrmaoDir.elementos);
                pFilho.filhos.addAll(pIrmaoDir.filhos);
                pIrmaoDir.elementos.clear(); 
                pIrmaoDir.filhos.clear();

                // Se a página for folha, copia o ponteiro para a próxima página
                pFilho.proxima = pIrmaoDir.proxima;

                arquivo.seek(8);
                pIrmaoDir.proxima = arquivo.readLong();
                arquivo.seek(8);
                arquivo.writeLong(paginaIrmaoDir);

            }

            // testa se o pai também ficou sem o número mínimo de elementos
            diminuiu = pa.elementos.size() < maxElementos / 2;

            // Atualiza os demais registros
            arquivo.seek(pagina);
            arquivo.write(pa.toByteArray());
            arquivo.seek(paginaFilho);
            arquivo.write(pFilho.toByteArray());
            if (pIrmaoEsq != null) {
                arquivo.seek(paginaIrmaoEsq);
                arquivo.write(pIrmaoEsq.toByteArray());
            }
            if (pIrmaoDir != null) {
                arquivo.seek(paginaIrmaoDir);
                arquivo.write(pIrmaoDir.toByteArray());
            }
        }
        return excluido;
    }

    public void print() throws Exception {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        System.out.println("Raiz: " + String.format("%04d", raiz));
        if (raiz != -1)
            print1(raiz);
        System.out.println();
    }

    // Impressão recursiva
    private void print1(long pagina) throws Exception {

        // Retorna das chamadas recursivas
        if (pagina == -1)
            return;
        int i;

        // Lê o registro da página passada como referência no arquivo
        arquivo.seek(pagina);
        Pagina pa = new Pagina(construtor, ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.fromByteArray(buffer);

        // Imprime a página
        String endereco = String.format("%04d", pagina);
        System.out.print(endereco + "  " + pa.elementos.size() + ":"); // endereço e número de elementos
        for (i = 0; i < pa.elementos.size(); i++) {
            System.out.print("(" + String.format("%04d", pa.filhos.get(i)) + ") " + pa.elementos.get(i) + " ");
        }
        if (i > 0)
            System.out.print("(" + String.format("%04d", pa.filhos.get(i)) + ")");
        else
            System.out.print("(-001)");
        for (; i < maxElementos; i++) {
            System.out.print(" ------- (-001)");
        }
        if (pa.proxima == -1)
            System.out.println();
        else
            System.out.println(" --> (" + String.format("%04d", pa.proxima) + ")");

        // Chama recursivamente cada filho, se a página não for folha
        if (pa.filhos.get(0) != -1) {
            for (i = 0; i < pa.elementos.size(); i++)
                print1(pa.filhos.get(i));
            print1(pa.filhos.get(i));
        }
    }

}