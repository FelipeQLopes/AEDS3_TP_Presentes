const container = document.getElementById("produtosContainer");
const popup = document.getElementById("popup");
const closePopup = document.getElementById("closePopup");
const form = document.getElementById("editForm");
const searchInput = document.getElementById("searchInput");
let produtos = JSON.parse(localStorage.getItem("produtos")) || [];

produtos.sort((a, b) => {
    if (a.nomeProduto < b.nomeProduto) {
        return -1;
    }
    if (a.nomeProduto > b.nomeProduto) {
        return 1;
    }
    return 0;
});

let produtoEditando = null;

const iconOptions = document.querySelectorAll(".icon-option");
const selectedIcon = document.getElementById("selectedIcon");

// Ao clicar em um ícone, atualiza o selecionado
iconOptions.forEach(icon => {
    icon.addEventListener("click", () => {
        // Remove seleção anterior
        iconOptions.forEach(i => i.classList.remove("selected"));
        // Marca o novo
        icon.classList.add("selected");

        // Atualiza o ícone grande de preview
        selectedIcon.className = icon.className;
    });
});


// Exibe os cards na tela
function exibirProdutos(lista) {
    container.innerHTML = "";
    lista.forEach((p, i) => {
        const card = document.createElement("div");
        card.className = "card";
        card.innerHTML = `
      <i class="${p.icone} icon"></i>
      <h3>${p.nomeProduto}</h3>
      <p>${p.descricao}</p>
      <p><strong>GTIN:</strong> ${p.gtin}</p>
    `;
        card.addEventListener("click", () => abrirPopup(p, i));
        container.appendChild(card);
    });
}

// Abre o popup e preenche os campos
function abrirPopup(produto, index) {
    produtoEditando = index;
    document.getElementById("editNome").value = produto.nomeProduto;
    document.getElementById("editGtin").value = produto.gtin;
    document.getElementById("editDescricao").value = produto.descricao;
    document.getElementById("editNome").placeholder = produto.nomeProduto;
    document.getElementById("editGtin").placeholder = produto.gtin;
    document.getElementById("editDescricao").placeholder = produto.descricao;
    document.getElementById("selectedIcon").className = produto.icone;
    popup.style.display = "flex";
}

// Fecha o popup
closePopup.addEventListener("click", () => popup.style.display = "none");

// Atualiza os dados e salva
form.addEventListener("submit", e => {
    e.preventDefault();
    const atualizado = {
        nomeProduto: form.editNome.value,
        gtin: form.editGtin.value,
        descricao: form.editDescricao.value,
        icone: document.getElementById("selectedIcon").className
    };
    produtos[produtoEditando] = atualizado;
    localStorage.setItem("produtos", JSON.stringify(produtos));
    popup.style.display = "none";
    exibirProdutos(produtos);
});

// Pesquisa
searchInput.addEventListener("input", e => {
    const termo = removerAcentos(e.target.value.toLowerCase());
    const filtrados = produtos.filter(p =>
        removerAcentos(p.nomeProduto.toLowerCase()).includes(termo) ||
        removerAcentos(p.descricao.toLowerCase()).includes(termo)
    );
    exibirProdutos(filtrados);
});

function removerAcentos(texto) {
    return texto.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

// Inicializa
exibirProdutos(produtos);
