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
        ListaProduto lp = super.read(pci.getId());
        if (lp != null && super.delete(pci.getId())) {
            return indiceIndiretoIdListaProduto.delete(ParIDListaID.hash(idLista, idProduto));
        }
    }
    return false;
}


  @Override
public boolean update(ListaProduto novoListaProduto) throws Exception {
    ListaProduto listaProdutoVelha = read(novoListaProduto.getIdLista(), novoListaProduto.getIdProduto());

    if (listaProdutoVelha == null) {
        return false; // nada pra atualizar
    }

    novoListaProduto.setId(listaProdutoVelha.getId());

    if (super.update(novoListaProduto)) {
        if (novoListaProduto.getIdLista() != listaProdutoVelha.getIdLista() ||
            novoListaProduto.getIdProduto() != listaProdutoVelha.getIdProduto()) {

            indiceIndiretoIdListaProduto.delete(
                ParIDListaID.hash(listaProdutoVelha.getIdLista(), listaProdutoVelha.getIdProduto())
            );

            indiceIndiretoIdListaProduto.create(
                new ParIDListaID(
                    novoListaProduto.getIdLista(),
                    novoListaProduto.getIdProduto(),
                    novoListaProduto.getId()
                )
            );
        }
        return true;
    }

    return false;
}



    public ArrayList<ListaProduto> readAll() throws Exception {
    ArrayList<ListaProduto> lista = new ArrayList<>();
    int id = 1;
    int falhasSeguidas = 0;
    ListaProduto lp;

    while (falhasSeguidas < 10_000) { // evita loop infinito se IDs forem espaçados
        lp = super.read(id);
        if (lp != null) {
            lista.add(lp);
            falhasSeguidas = 0;
        } else {
            falhasSeguidas++;
        }
        id++;
    }

    return lista;
}


}
