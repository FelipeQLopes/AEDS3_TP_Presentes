package tp.presentes;
import tp.presentes.aed3.*;
import java.util.*;

public class ArquivoListaProduto extends Arquivo<ListaProduto> {

    HashExtensivel<ParIDListaID> indiceIndiretoIdLista;

    public ArquivoListaProduto() throws Exception {
        super("listaproduto", ListaProduto.class.getConstructor());
        indiceIndiretoIdLista = new HashExtensivel<>(
            ParIDListaID.class.getConstructor(),
            4,
            "./src/main/resources/dados/listaProduto/indiceIdLista.d.db",
            "./src/main/resources/dados/listaProduto/indiceIdLista.c.db"
        );
    }

    @Override
    public int create(ListaProduto listaProduto) throws Exception {
        int id = super.create(listaProduto);
        indiceIndiretoIdLista.create(new ParIDListaID(listaProduto.getIdLista(), id));
        return id;
    }

    public ListaProduto read(int idLista) throws Exception {
        ParIDListaID pci = indiceIndiretoIdLista.read(ParIDListaID.hash(idLista));
        if(pci == null)
            return null;
        return read(pci.getId());
    }

    @Override
    public boolean delete(int id) throws Exception {
        ListaProduto listaProduto = super.read(id);
        if(listaProduto != null) {
            if(super.delete(id)) {
                return indiceIndiretoIdLista.delete(ParIDListaID.hash(listaProduto.getIdLista()));
            }
        }
        return false;
    }

    public boolean deleteID(int idLista) throws Exception {
        ParIDListaID pci = indiceIndiretoIdLista.read(ParIDListaID.hash(idLista));
        if(pci != null) {
            return delete(pci.getId());
        }
        return false;
    }

    @Override
    public boolean update(ListaProduto novoListaProduto) throws Exception {
        ListaProduto listaProdutoVelha = read(novoListaProduto.getIdLista());
        if(super.update(novoListaProduto)) {
            if(novoListaProduto.getIdLista() != listaProdutoVelha.getIdLista()) {
                indiceIndiretoIdLista.delete(ParIDListaID.hash(listaProdutoVelha.getIdLista()));
                indiceIndiretoIdLista.create(new ParIDListaID(novoListaProduto.getIdLista(), novoListaProduto.getId()));
            }
            return true;
        }
        return false;
    }
}
