package tp.presentes;
import tp.presentes.aed3.*;
import java.util.*;

public class ArquivoListaProduto extends Arquivo<ListaProduto> {

    HashExtensivel<ParIDListaID> indiceIndiretoIdListaProduto;

    public ArquivoListaProduto() throws Exception {
        super("listaproduto", ListaProduto.class.getConstructor());
        indiceIndiretoIdListaProduto = new HashExtensivel<>(
            ParIDListaID.class.getConstructor(),
            4,
            "./src/main/resources/dados/listaProduto/indiceIdLista.d.db",
            "./src/main/resources/dados/listaProduto/indiceIdLista.c.db"
        );
    }

    @Override
    public int create(ListaProduto listaProduto) throws Exception {
        int id = super.create(listaProduto);
        indiceIndiretoIdListaProduto.create(new ParIDListaID(listaProduto.getIdLista(), listaProduto.getIdProduto(), id));
        return id;
    }

    public ListaProduto read(int idLista, int idProduto) throws Exception {
        ParIDListaID pci = indiceIndiretoIdListaProduto.read(ParIDListaID.hash(idLista, idProduto));
        if (pci == null)
            return null;
        return read(pci.getId());
    }

    @Override
    public boolean delete(int id) throws Exception {
        ListaProduto listaProduto = super.read(id);
        if (listaProduto != null) {
            if (super.delete(id)) {
                return indiceIndiretoIdListaProduto.delete(ParIDListaID.hash(listaProduto.getIdLista(), listaProduto.getIdProduto()));
            }
        }
        return false;
    }

    public boolean delete(int idLista, int idProduto) throws Exception {
        ParIDListaID pci = indiceIndiretoIdListaProduto.read(ParIDListaID.hash(idLista, idProduto));
        if (pci != null) {
            return delete(pci.getId());
        }
        return false;
    }

   @Override
public boolean update(ListaProduto novoListaProduto) throws Exception {
    ListaProduto listaProdutoVelha = read(novoListaProduto.getIdLista(), novoListaProduto.getIdProduto());

    // Caso o produto antigo não seja encontrado, apenas atualiza diretamente
    if (listaProdutoVelha == null) {
        return super.update(novoListaProduto);
    }

    if (super.update(novoListaProduto)) {
        // Se o ID de lista ou produto mudou, atualiza o índice indireto
        if (novoListaProduto.getIdLista() != listaProdutoVelha.getIdLista() ||
            novoListaProduto.getIdProduto() != listaProdutoVelha.getIdProduto()) {

            indiceIndiretoIdListaProduto.delete(
                ParIDListaID.hash(listaProdutoVelha.getIdLista(), listaProdutoVelha.getIdProduto())
            );

            indiceIndiretoIdListaProduto.create(
                new ParIDListaID(novoListaProduto.getIdLista(), novoListaProduto.getIdProduto(), novoListaProduto.getId())
            );
        }
        return true;
    }

    return false;
}


    public ArrayList<ListaProduto> readAll() throws Exception {
        ArrayList<ListaProduto> lista = new ArrayList<>();
        int id = 1;
        ListaProduto lp;
        while (true) {
            lp = super.read(id);
            if (lp == null)
                break;
            lista.add(lp);
            id++;
        }
        return lista;
    }
}
