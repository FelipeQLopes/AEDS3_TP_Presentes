package tp.presentes;
import tp.presentes.aed3.*;

public class ArquivoUsuario extends Arquivo<Usuario> {

    Arquivo<Usuario> arqUsuarios;
    HashExtensivel<ParCPFID> indiceIndiretoCPF;

    public ArquivoUsuario() throws Exception {
        super("usuarios", Usuario.class.getConstructor());
        indiceIndiretoCPF = new HashExtensivel<>(
            ParCPFID.class.getConstructor(), 
            4, 
            "./src/main/resources/dados/usuarios/indiceCPF.d.db",   // diretório
            "./src/main/resources/dados/usuarios/indiceCPF.c.db"    // cestos 
        );
    }

    @Override
    public int create(Usuario c) throws Exception {
        int id = super.create(c);
        indiceIndiretoCPF.create(new ParCPFID(c.getCpf(), id));
        return id;
    }

    public Usuario read(String cpf) throws Exception {
        ParCPFID pci = indiceIndiretoCPF.read(ParCPFID.hash(cpf));
        if(pci == null)
            return null;
        return read(pci.getId());
    }
    
    public boolean delete(String cpf) throws Exception {
        ParCPFID pci = indiceIndiretoCPF.read(ParCPFID.hash(cpf));
        if(pci != null) 
            if(delete(pci.getId())){
                return true;
            }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Usuario c = super.read(id);
        if(c != null) {
            if(super.delete(id))
                return indiceIndiretoCPF.delete(ParCPFID.hash(c.getCpf()));
        }
        return false;
    }

    @Override
    public boolean update(Usuario novoUsuario) throws Exception {
        Usuario usuarioVelho = read(novoUsuario.getCpf());
        if(super.update(novoUsuario)) {
            if(novoUsuario.getCpf().compareTo(usuarioVelho.getCpf())!=0) {
                indiceIndiretoCPF.delete(ParCPFID.hash(usuarioVelho.getCpf()));
                indiceIndiretoCPF.create(new ParCPFID(novoUsuario.getCpf(), novoUsuario.getId()));
            }
            return true;
        }
        return false;
    }
}