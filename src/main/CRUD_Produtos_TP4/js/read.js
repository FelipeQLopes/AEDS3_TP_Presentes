document.addEventListener("DOMContentLoaded", () => {
    const cardsContainer = document.getElementById("cardsContainer");
    const searchInput = document.getElementById("searchInput");

    // Função para carregar produtos
    function carregarProdutos() {
        const produtos = JSON.parse(localStorage.getItem("produtos")) || [];
        produtos.sort((a, b) => {
            if (a.nomeProduto < b.nomeProduto) {
                return -1;
            }
            if (a.nomeProduto > b.nomeProduto) {
                return 1;
            }
            return 0;
        });

        return produtos;
    }

    // Função para exibir os cards
    function exibirProdutos(lista) {
        cardsContainer.innerHTML = ""; // limpa antes de renderizar

        if (lista.length === 0) {
            cardsContainer.innerHTML = "<p>Nenhum produto encontrado.</p>";
            return;
        }

        lista.forEach(produto => {
            const card = document.createElement("div");
            card.classList.add("card");
            card.innerHTML = `
                <i class="${produto.icone}"></i>
                <h3>${produto.nomeProduto}</h3>
                <p>${produto.descricao}</p>
                <p><strong>GTIN:</strong> ${produto.gtin}</p>
            `;
            cardsContainer.appendChild(card);
        });
    }

    // Exibir todos ao carregar
    const produtos = carregarProdutos();
    exibirProdutos(produtos);

    searchInput.addEventListener("input", e => {
        const termo = removerAcentos(e.target.value.toLowerCase());
        const filtrados = produtos.filter(p =>
            removerAcentos(p.nomeProduto.toLowerCase()).includes(termo) ||
            removerAcentos(p.descricao.toLowerCase()).includes(termo) ||
            p.gtin.includes(termo)
        );
        exibirProdutos(filtrados);
    });

    // Função auxiliar para remover acentos
    function removerAcentos(texto) {
        return texto.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }

});
