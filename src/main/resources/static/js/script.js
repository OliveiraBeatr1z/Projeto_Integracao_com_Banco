// ====================================================
// CONFIGURAÇÃO E NAVEGAÇÃO
// ====================================================

const API_BASE_URL = 'http://localhost:8081/api';

// Função utilitária para fazer fetch e tratar erros
async function fetchJson(url, options = {}) {
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(`HTTP error ${response.status}: ${errorBody}`);
        }
        if (response.status === 204 || response.headers.get('Content-Length') === '0') {
            return null; // No Content
        }
        return await response.json();
    } catch (error) {
        console.error('Fetch error:', error.message);
        showMessage(error.message, 'error');
        throw error;
    }
}

// Função para exibir mensagens na tela
function showMessage(text, type = 'success') {
    const msg = document.getElementById('messages');
    msg.textContent = text;
    msg.style.backgroundColor = type === 'success' ? '#d4edda' : '#f8d7da';
    msg.style.color = type === 'success' ? '#155724' : '#721c24';
    msg.style.display = 'block';
    setTimeout(() => msg.style.display = 'none', 5000);
}

// ----------------------------------------------------
// Lógica de Navegação (Trocar de Seção)
// ----------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = e.target.closest('.nav-link').dataset.target;

            document.querySelectorAll('.nav-link').forEach(nav => nav.classList.remove('active'));
            e.target.closest('.nav-link').classList.add('active');

            document.querySelectorAll('.content-section').forEach(section => {
                section.classList.remove('active');
            });
            document.getElementById(targetId).classList.add('active');

            if (targetId === 'contas-section') {
                listarContas();
            }
        });
    });

    document.querySelectorAll('.card').forEach(card => {
        card.addEventListener('click', (e) => {
            const targetId = e.currentTarget.dataset.target;
            document.querySelector(`[data-target="${targetId}"]`).click();
        });
    });

    // Carrega a lista de contas na primeira vez que a página é carregada
    listarContas();
});

// ====================================================
// SEÇÃO CONTAS
// ====================================================

async function listarContas() {
    const tabelaBody = document.querySelector('#tabelaContas tbody');
    tabelaBody.innerHTML = '<tr><td colspan="5">Carregando...</td></tr>';

    try {
        const contas = await fetchJson(`${API_BASE_URL}/contas`);
        tabelaBody.innerHTML = '';
        if (contas && contas.length > 0) {
            contas.forEach(conta => appendContaRow(tabelaBody, conta));
        } else {
            tabelaBody.innerHTML = '<tr><td colspan="5">Nenhuma conta encontrada.</td></tr>';
        }
    } catch (error) {
        tabelaBody.innerHTML = '<tr><td colspan="5">Erro ao carregar contas.</td></tr>';
    }
}

function appendContaRow(tabelaBody, conta) {
    const row = tabelaBody.insertRow();
    row.insertCell().textContent = conta.numero;
    row.insertCell().textContent = conta.titular.nome;
    row.insertCell().textContent = conta.titular.cpf;
    row.insertCell().textContent = `R$ ${Number(conta.saldo).toFixed(2)}`;
    row.insertCell().textContent = conta.estaAtiva ? 'Ativa' : 'Encerrada';
}

document.getElementById('formNovaConta').addEventListener('submit', async function(e) {
    e.preventDefault();
    const numero = parseInt(document.getElementById('c_numero').value);
    const nome = document.getElementById('c_nome').value;
    const cpf = document.getElementById('c_cpf').value;
    const email = document.getElementById('c_email').value;

    const payload = {
        numero: numero,
        dadosCliente: { nome, cpf, email }
    };

    try {
        await fetchJson(`${API_BASE_URL}/contas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        showMessage(`Conta ${numero} criada com sucesso!`);
        this.reset();
        listarContas();
    } catch (error) {
        // O erro já é mostrado pela função fetchJson
    }
});

document.getElementById('btnDesativarConta').addEventListener('click', async function() {
    const numero = parseInt(document.getElementById('c_numero').value);
    if (!numero) {
        showMessage('Informe o número da conta para desativar.', 'error');
        return;
    }
    if (!confirm(`Deseja realmente desativar a conta ${numero}?`)) return;
    try {
        await fetchJson(`${API_BASE_URL}/contas/${numero}/desativar`, {
            method: 'PUT'
        });
        showMessage(`Conta ${numero} desativada com sucesso!`);
        listarContas();
    } catch (error) {
        // O erro já é mostrado pela função fetchJson
    }
});

// ====================================================
// SEÇÃO OPERAÇÕES
// ====================================================

document.getElementById('formConsultarSaldo').addEventListener('submit', async function(e) {
    e.preventDefault();
    const numero = parseInt(document.getElementById('s_consulta_numero').value);
    const resultadoElement = document.getElementById('resultadoSaldo');

    try {
        const data = await fetchJson(`${API_BASE_URL}/contas/${numero}/saldo`);
        resultadoElement.textContent = `Saldo: R$ ${Number(data).toFixed(2)}`;
        resultadoElement.style.color = 'green';
    } catch (error) {
        resultadoElement.textContent = 'Erro ao consultar saldo.';
        resultadoElement.style.color = 'red';
    }
});

document.getElementById('formDeposito').addEventListener('submit', async function(e) {
    e.preventDefault();
    const numero = parseInt(document.getElementById('d_numero').value);
    const valor = parseFloat(document.getElementById('d_valor').value);

    try {
        await fetchJson(`${API_BASE_URL}/contas/${numero}/deposito`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ valor })
        });
        showMessage(`Depósito de R$ ${valor.toFixed(2)} realizado com sucesso!`);
        this.reset();
        listarContas();
    } catch (error) {
        // O erro já é mostrado pela função fetchJson
    }
});

document.getElementById('formSaque').addEventListener('submit', async function(e) {
    e.preventDefault();
    const numero = parseInt(document.getElementById('s_numero').value);
    const valor = parseFloat(document.getElementById('s_valor').value);

    try {
        await fetchJson(`${API_BASE_URL}/contas/${numero}/saque`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ valor })
        });
        showMessage(`Saque de R$ ${valor.toFixed(2)} realizado com sucesso!`);
        this.reset();
        listarContas();
    } catch (error) {
        // O erro já é mostrado pela função fetchJson
    }
});

document.getElementById('formTransferencia').addEventListener('submit', async function(e) {
    e.preventDefault();
    const origem = parseInt(document.getElementById('t_origem').value);
    const destino = parseInt(document.getElementById('t_destino').value);
    const valor = parseFloat(document.getElementById('t_valor').value);

    const payload = { numeroContaOrigem: origem, numeroContaDestino: destino, valor };

    try {
        await fetchJson(`${API_BASE_URL}/contas/transferencia`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        showMessage(`Transferência de R$ ${valor.toFixed(2)} realizada com sucesso!`);
        this.reset();
        listarContas();
    } catch (error) {
        // O erro já é mostrado pela função fetchJson
    }
});

// ====================================================
// SEÇÃO HISTÓRICO
// ====================================================

document.getElementById('formConsultarExtrato').addEventListener('submit', async function(e) {
    e.preventDefault();
    const numero = parseInt(document.getElementById('h_numero').value);
    const dataInicio = document.getElementById('h_data_inicio').value;
    const dataFim = document.getElementById('h_data_fim').value;
    const tabelaBody = document.querySelector('#tabelaExtrato tbody');
    tabelaBody.innerHTML = '<tr><td colspan="5">Consultando...</td></tr>';

    try {
        const params = new URLSearchParams({ inicio: dataInicio, fim: dataFim });
        const transacoes = await fetchJson(`${API_BASE_URL}/contas/${numero}/extrato?${params.toString()}`);
        
        tabelaBody.innerHTML = '';
        document.getElementById('extratoTitulo').textContent = `Extrato da Conta ${numero}`;

        if (transacoes && transacoes.length > 0) {
            transacoes.forEach(t => appendExtratoRow(tabelaBody, t));
        } else {
            tabelaBody.innerHTML = '<tr><td colspan="5">Nenhuma transação encontrada para este período.</td></tr>';
        }
    } catch (error) {
        tabelaBody.innerHTML = '<tr><td colspan="5">Erro ao consultar extrato.</td></tr>';
    }
});

function appendExtratoRow(tabelaBody, transacao) {
    const row = tabelaBody.insertRow();
    const dataHora = new Date(transacao.dataHora).toLocaleString('pt-BR');
    
    row.insertCell().textContent = dataHora;
    row.insertCell().textContent = transacao.tipoOperacao;
    row.insertCell().textContent = `R$ ${Number(transacao.valor).toFixed(2)}`;
    row.insertCell().textContent = `R$ ${Number(transacao.saldoAnterior).toFixed(2)}`;
    row.insertCell().textContent = `R$ ${Number(transacao.saldoNovo).toFixed(2)}`;
}

// ====================================================
// SEÇÃO RELATÓRIOS
// ====================================================

async function consultarRelatorioGeral() {
    const relatorioDiv = document.getElementById('relatorioGeral');
    relatorioDiv.innerHTML = 'Carregando...';

    try {
        const data = await fetchJson(`${API_BASE_URL}/contas/relatorios/geral`);
        relatorioDiv.innerHTML = `
            <p><strong>Total de Contas Ativas:</strong> ${data.totalContasAtivas}</p>
            <p><strong>Saldo Total:</strong> R$ ${Number(data.saldoTotal).toFixed(2)}</p>
            <p><strong>Saldo Médio:</strong> R$ ${Number(data.saldoMedio).toFixed(2)}</p>
            <p><strong>Maior Saldo:</strong> R$ ${Number(data.maiorSaldo).toFixed(2)}</p>
            <p><strong>Menor Saldo:</strong> R$ ${Number(data.menorSaldo).toFixed(2)}</p>
        `;
    } catch (error) {
        relatorioDiv.innerHTML = 'Erro ao gerar relatório.';
    }
}

document.getElementById('formContasBaixoSaldo').addEventListener('submit', async function(e) {
    e.preventDefault();
    const limite = parseFloat(document.getElementById('r_limite_saldo').value);
    const tabela = document.getElementById('tabelaBaixoSaldo');
    tabela.innerHTML = '<thead><tr><th>Conta</th><th>Nome</th><th>Saldo</th></tr></thead><tbody><tr><td colspan="3">Consultando...</td></tr></tbody>';

    try {
        const contas = await fetchJson(`${API_BASE_URL}/contas/relatorios/saldo-baixo?limite=${limite}`);
        const tbody = tabela.querySelector('tbody');
        tbody.innerHTML = '';
        if (contas && contas.length > 0) {
            contas.forEach(conta => {
                const row = tbody.insertRow();
                row.insertCell().textContent = conta.numero;
                row.insertCell().textContent = conta.titular.nome;
                row.insertCell().textContent = `R$ ${Number(conta.saldo).toFixed(2)}`;
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="3">Nenhuma conta encontrada com saldo baixo.</td></tr>';
        }
    } catch (error) {
        tabela.querySelector('tbody').innerHTML = '<tr><td colspan="3">Erro ao consultar.</td></tr>';
    }
});

document.getElementById('formRelatorioMovimentacoes').addEventListener('submit', async function(e) {
    e.preventDefault();
    const dataInicio = document.getElementById('r_data_inicio').value;
    const dataFim = document.getElementById('r_data_fim').value;
    const relatorioDiv = document.getElementById('relatorioMovimentacoes');
    relatorioDiv.innerHTML = 'Gerando...';

    try {
        const params = new URLSearchParams({ inicio: dataInicio, fim: dataFim });
        const data = await fetchJson(`${API_BASE_URL}/contas/relatorios/movimentacoes?${params.toString()}`);
        
        let html = '<h4>Resumo de Movimentações</h4>';
        if (data && Object.keys(data).length > 0) {
            for (const [tipo, quantidade] of Object.entries(data)) {
                html += `<p><strong>${tipo}:</strong> ${quantidade} operações</p>`;
            }
        } else {
            html += '<p>Nenhuma movimentação no período.</p>';
        }
        relatorioDiv.innerHTML = html;
    } catch (error) {
        relatorioDiv.innerHTML = 'Erro ao gerar relatório de movimentações.';
    }
});
