package tp.presentes;
import tp.presentes.aed3.*;
import java.util.*;

public class ArquivoLista extends Arquivo<Lista> {

    HashExtensivel<ParCodigoID> indiceIndiretoCodigo;

    public ArquivoLista() throws Exception {
        super("listas", Lista.class.getConstructor());
        indiceIndiretoCodigo = new HashExtensivel<>(
            ParCodigoID.class.getConstructor(),
            4,
            "./src/main/resources/dados/listas/indiceCodigo.d.db",
            "./src/main/resources/dados/listas/indiceCodigo.c.db"
        );
    }

    @Override
    public int create(Lista lista) throws Exception {
        int id = super.create(lista);
        indiceIndiretoCodigo.create(new ParCodigoID(lista.getCodigo(), id));
        return id;
    }

    public Lista read(String codigo) throws Exception {
        ParCodigoID pci = indiceIndiretoCodigo.read(ParCodigoID.hash(codigo));
        if(pci == null)
            return null;
        return read(pci.getId());
    }

    @Override
    public boolean delete(int id) throws Exception {
        Lista lista = super.read(id);
        if(lista != null) {
            if(super.delete(id)) {
                return indiceIndiretoCodigo.delete(ParCodigoID.hash(lista.getCodigo()));
            }
        }
        return false;
    }

    public boolean delete(String codigo) throws Exception {
        ParCodigoID pci = indiceIndiretoCodigo.read(ParCodigoID.hash(codigo));
        if(pci != null) {
            return delete(pci.getId());
        }
        return false;
    }

    @Override
    public boolean update(Lista novoLista) throws Exception {
        Lista listaVelha = read(novoLista.getCodigo());
        if(super.update(novoLista)) {
            if(!novoLista.getCodigo().equals(listaVelha.getCodigo())) {
                indiceIndiretoCodigo.delete(ParCodigoID.hash(listaVelha.getCodigo()));
                indiceIndiretoCodigo.create(new ParCodigoID(novoLista.getCodigo(), novoLista.getId()));
            }
            return true;
        }
        return false;
    }
}
