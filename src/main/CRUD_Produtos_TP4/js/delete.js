const cardsContainer = document.getElementById("cardsContainer");
const searchInput = document.getElementById("searchInput");
const selectAllBtn = document.getElementById("selectAll");
const clearSelectionBtn = document.getElementById("clearSelection");
const deleteSelectedBtn = document.getElementById("deleteSelected");

let produtos = JSON.parse(localStorage.getItem("produtos")) || [];
let selecionados = new Set();
let ultimaExclusao = []; // Guarda os últimos produtos removidos

// ===== Exibir produtos =====
function exibirProdutos(lista) {
    cardsContainer.innerHTML = "";
    lista.forEach((p, i) => {
        const card = document.createElement("div");
        card.className = "card";
        if (selecionados.has(i)) card.classList.add("selected");

        card.innerHTML = `
            <input type="checkbox" ${selecionados.has(i) ? "checked" : ""}>
            <i class="${p.icone} icon"></i>
            <h3>${p.nomeProduto}</h3>
            <p>${p.descricao}</p>
            <p><strong>GTIN:</strong> ${p.gtin}</p>
        `;

        const checkbox = card.querySelector("input");
        checkbox.addEventListener("click", (e) => {
            e.stopPropagation();
            toggleSelecao(i);
        });

        card.addEventListener("click", () => toggleSelecao(i));
        cardsContainer.appendChild(card);
    });
}

// ===== Alternar seleção =====
function toggleSelecao(index) {
    if (selecionados.has(index)) selecionados.delete(index);
    else selecionados.add(index);
    exibirProdutos(produtos);
}

// ===== Botões de controle =====
selectAllBtn.addEventListener("click", () => {
    produtos.forEach((_, i) => selecionados.add(i));
    exibirProdutos(produtos);
});

clearSelectionBtn.addEventListener("click", () => {
    selecionados.clear();
    exibirProdutos(produtos);
});

deleteSelectedBtn.addEventListener("click", () => {
    if (selecionados.size === 0) {
        alert("Nenhum produto selecionado.");
        return;
    }

    if (!confirm("Tem certeza que deseja excluir os produtos selecionados?")) return;

    const indicesParaExcluir = [...selecionados];
    const cards = document.querySelectorAll(".card");

    // Adiciona a animação de fade-out nos cards selecionados
    indicesParaExcluir.forEach((i) => {
        const card = cards[i];
        if (card) card.classList.add("fade-out");
    });

    // Após a animação, remove os produtos
    setTimeout(() => {
        ultimaExclusao = produtos.filter((_, i) => selecionados.has(i));
        produtos = produtos.filter((_, i) => !selecionados.has(i));
        selecionados.clear();
        localStorage.setItem("produtos", JSON.stringify(produtos));
        exibirProdutos(produtos);
        mostrarBotaoDesfazer();
    }, 400);
});

// ===== Função para mostrar botão "Desfazer" =====
function mostrarBotaoDesfazer() {
    let undoBtn = document.getElementById("undoDelete");
    if (!undoBtn) {
        undoBtn = document.createElement("button");
        undoBtn.id = "undoDelete";
        undoBtn.innerHTML = `<i class="fa-solid fa-rotate-left"></i> Desfazer`;
        undoBtn.classList.add("undo-btn");
        document.querySelector(".controls").appendChild(undoBtn);

        undoBtn.addEventListener("click", desfazerExclusao);
    }

    undoBtn.style.display = "flex";
    undoBtn.disabled = false;

    // Oculta o botão após 8 segundos se não for usado
    setTimeout(() => {
        if (undoBtn) undoBtn.style.display = "none";
    }, 8000);
}

// ===== Função para desfazer exclusão =====
function desfazerExclusao() {
    if (ultimaExclusao.length === 0) return;

    produtos = [...produtos, ...ultimaExclusao];
    produtos.sort((a, b) => a.nomeProduto.localeCompare(b.nomeProduto));
    localStorage.setItem("produtos", JSON.stringify(produtos));

    ultimaExclusao = [];
    exibirProdutos(produtos);

    const undoBtn = document.getElementById("undoDelete");
    if (undoBtn) undoBtn.style.display = "none";
}

// ===== Pesquisa com acento ignorado =====
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

// ===== Inicialização =====
exibirProdutos(produtos);
