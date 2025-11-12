document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("createForm");
    const selectedIcon = document.getElementById("selectedIcon");
    const iconOptions = document.querySelectorAll(".icon-option");

    // 1 Trocar o ícone selecionado
    iconOptions.forEach(icon => {
        icon.addEventListener("click", () => {
            const selectedIconElement = document.createElement("i");
            selectedIconElement.className = icon.className.replace("icon-option", "").trim();
            selectedIcon.innerHTML = "";
            selectedIcon.appendChild(selectedIconElement);
        });
    });


    // 2 Interceptar o envio do formulário
    form.addEventListener("submit", (event) => {
        event.preventDefault();

        // 3️ Coletar os dados
        const nomeProduto = document.getElementById("nomeProduto").value.trim();
        const gtin = document.getElementById("gtin").value.trim();

        // Validação extra:
        if (!/^\d{13}$/.test(gtin)) {
            alert("O GTIN deve conter exatamente 13 dígitos numéricos!");
            return;
        }

        const descricao = document.getElementById("descricao").value.trim();
        const icone = selectedIcon.querySelector("i").className;

        // 4️ Validar
        if (!nomeProduto || !gtin || !descricao) {
            alert("Preencha todos os campos!");
            return;
        }

        // 5️ Criar o objeto
        const produto = { nomeProduto, descricao, gtin, icone };

        // 6️ Buscar e atualizar o localStorage
        const produtos = JSON.parse(localStorage.getItem("produtos")) || [];
        produtos.push(produto);
        localStorage.setItem("produtos", JSON.stringify(produtos));

        // 7️ Feedback + limpar
        alert("Produto criado com sucesso!");
        form.reset();
        selectedIcon.innerHTML = '<i class="fa-solid fa-box"></i>';
    });
});
