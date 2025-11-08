package tp.presentes;

import tp.presentes.aed3.*;
import java.util.ArrayList;


public class ArquivoProduto extends Arquivo<Produto> {

    private HashExtensivel<ParCodigoID> indiceGtin;

    public ArquivoProduto() throws Exception {
        super("produtos", Produto.class.getConstructor());
        indiceGtin = new HashExtensivel<>(
            ParCodigoID.class.getConstructor(),
            4,
            "./src/main/resources/dados/produtos/indiceGtin.d.db",
            "./src/main/resources/dados/produtos/indiceGtin.c.db"
        );
    }

    @Override
    public int create(Produto p) throws Exception {
        int id = super.create(p);
        indiceGtin.create(new ParCodigoID(p.getGtin13(), id));
        return id;
    }

    public Produto read(String gtin13) throws Exception {
        ParCodigoID pci = indiceGtin.read(ParCodigoID.hash(gtin13));
        if (pci == null)
            return null;
        return super.read(pci.getId());
    }

    @Override
    public boolean update(Produto pNovo) throws Exception {
        Produto pVelho = read(pNovo.getGtin13());
        if (super.update(pNovo)) {
            if (!pNovo.getGtin13().equals(pVelho.getGtin13())) {
                indiceGtin.delete(ParCodigoID.hash(pVelho.getGtin13()));
                indiceGtin.create(new ParCodigoID(pNovo.getGtin13(), pNovo.getId()));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Produto p = super.read(id);
        if (p != null) {
            if (super.delete(id)) {
                return indiceGtin.delete(ParCodigoID.hash(p.getGtin13()));
            }
        }
        return false;
    }

    public boolean delete(String gtin13) throws Exception {
        ParCodigoID pci = indiceGtin.read(ParCodigoID.hash(gtin13));
        if (pci != null) {
            return delete(pci.getId());
        }
        return false;
    }

    public ArrayList<Produto> listAll() throws Exception {
        ArrayList<Produto> lista = new ArrayList<>();

        int id = 1;
        Produto p;
        while (true) {
            p = super.read(id);
            if (p == null)
                break;
            lista.add(p);
            id++;
        }

        return lista;
    }



}
