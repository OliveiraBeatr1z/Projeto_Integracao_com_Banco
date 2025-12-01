// Configuração da API
const API_BASE_URL = 'http://localhost:8081/api';

// Funções Utilitárias
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function showSection(sectionId) {
    // Esconder todas as seções
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Remover active dos links
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });
    
    // Mostrar seção selecionada
    document.getElementById(sectionId).classList.add('active');
    
    // Adicionar active ao link correspondente
    event.target.closest('.nav-link')?.classList.add('active');
    
    // Carregar dados se necessário
    if (sectionId === 'contas') {
        carregarContas();
    }
}

function toggleForm(formId) {
    const form = document.getElementById(formId);
    form.style.display = form.style.display === 'none' ? 'block' : 'none';
}

function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

function formatarData(dataString) {
    const data = new Date(dataString);
    return new Intl.DateTimeFormat('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).format(data);
}

// ========== GERENCIAR CONTAS ==========

async function carregarContas() {
    try {
        const response = await fetch(`${API_BASE_URL}/contas`);
        const contas = await response.json();
        
        const listaContas = document.getElementById('listaContas');
        
        if (contas.length === 0) {
            listaContas.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>Nenhuma conta cadastrada ainda</p>
                </div>
            `;
            return;
        }
        
        listaContas.innerHTML = contas.map(conta => `
            <div class="conta-card">
                <div class="conta-card-header">
                    <div class="conta-numero">
                        <i class="fas fa-credit-card"></i> ${conta.numero}
                    </div>
                    <span class="conta-status ${conta.estaAtiva ? 'status-ativa' : 'status-inativa'}">
                        ${conta.estaAtiva ? 'ATIVA' : 'INATIVA'}
                    </span>
                </div>
                <div class="conta-saldo">${formatarMoeda(conta.saldo)}</div>
                <div class="conta-cliente">
                    <div><strong>Titular:</strong> ${conta.titular.nome}</div>
                    <div><strong>CPF:</strong> ${conta.titular.cpf}</div>
                    <div><strong>Email:</strong> ${conta.titular.email}</div>
                </div>
                <button class="btn-desativar" onclick="desativarContaWeb(${conta.numero})" ${conta.estaAtiva ? '' : 'disabled'}>
                    Desativar
                </button>
            </div>
        `).join('');
        
    } catch (error) {
        showToast('Erro ao carregar contas: ' + error.message, 'error');
    }
}

async function criarConta(event) {
    event.preventDefault();
    
    const dados = {
        numero: parseInt(document.getElementById('numeroConta').value),
        dadosCliente: {
            nome: document.getElementById('nomeCliente').value,
            cpf: document.getElementById('cpfCliente').value,
            email: document.getElementById('emailCliente').value
        }
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/contas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });
        
        if (response.ok) {
            showToast('Conta criada com sucesso!', 'success');
            event.target.reset();
            toggleForm('formNovaConta');
            carregarContas();
        } else {
            const erro = await response.json();
            showToast(erro.erro || 'Erro ao criar conta', 'error');
        }
    } catch (error) {
        showToast('Erro ao criar conta: ' + error.message, 'error');
    }
}

// ========== OPERAÇÕES ==========

async function realizarDeposito(event) {
    event.preventDefault();
    
    const numero = document.getElementById('depositoConta').value;
    const valor = parseFloat(document.getElementById('depositoValor').value);
    
    try {
        const response = await fetch(`${API_BASE_URL}/contas/${numero}/deposito`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ valor })
        });
        
        if (response.ok) {
            showToast(`Depósito de ${formatarMoeda(valor)} realizado com sucesso!`, 'success');
            event.target.reset();
        } else {
            const erro = await response.json();
            showToast(erro.erro || 'Erro ao realizar depósito', 'error');
        }
    } catch (error) {
        showToast('Erro ao realizar depósito: ' + error.message, 'error');
    }
}

async function realizarSaque(event) {
    event.preventDefault();
    
    const numero = document.getElementById('saqueConta').value;
    const valor = parseFloat(document.getElementById('saqueValor').value);
    
    try {
        const response = await fetch(`${API_BASE_URL}/contas/${numero}/saque`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ valor })
        });
        
        if (response.ok) {
            showToast(`Saque de ${formatarMoeda(valor)} realizado com sucesso!`, 'success');
            event.target.reset();
        } else {
            const erro = await response.json();
            showToast(erro.erro || 'Erro ao realizar saque', 'error');
        }
    } catch (error) {
        showToast('Erro ao realizar saque: ' + error.message, 'error');
    }
}

async function realizarTransferencia(event) {
    event.preventDefault();
    
    const dados = {
        numeroOrigem: parseInt(document.getElementById('transferenciaOrigem').value),
        numeroDestino: parseInt(document.getElementById('transferenciaDestino').value),
        valor: parseFloat(document.getElementById('transferenciaValor').value)
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/contas/transferencia`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });
        
        if (response.ok) {
            showToast(`Transferência de ${formatarMoeda(dados.valor)} realizada com sucesso!`, 'success');
            event.target.reset();
        } else {
            const erro = await response.json();
            showToast(erro.erro || 'Erro ao realizar transferência', 'error');
        }
    } catch (error) {
        showToast('Erro ao realizar transferência: ' + error.message, 'error');
    }
}

async function consultarSaldo(event) {
    event.preventDefault();
    
    const numero = document.getElementById('saldoConta').value;
    const resultado = document.getElementById('resultadoSaldo');
    
    try {
        const response = await fetch(`${API_BASE_URL}/contas/${numero}/saldo`);
        
        if (response.ok) {
            const data = await response.json();
            resultado.innerHTML = `
                <i class="fas fa-check-circle"></i>
                Saldo: <strong>${formatarMoeda(data.saldo)}</strong>
            `;
            resultado.className = 'resultado success show';
        } else {
            const erro = await response.json();
            resultado.innerHTML = `<i class="fas fa-times-circle"></i> ${erro.erro}`;
            resultado.className = 'resultado error show';
        }
    } catch (error) {
        resultado.innerHTML = `<i class="fas fa-times-circle"></i> Erro: ${error.message}`;
        resultado.className = 'resultado error show';
    }
}

async function encerrarConta(event) {
    event.preventDefault();
    
    const numero = document.getElementById('encerrarConta').value;
    
    if (!confirm(`Tem certeza que deseja encerrar a conta ${numero}?`)) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/contas/${numero}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showToast('Conta encerrada com sucesso!', 'success');
            event.target.reset();
        } else {
            const erro = await response.json();
            showToast(erro.erro || 'Erro ao encerrar conta', 'error');
        }
    } catch (error) {
        showToast('Erro ao encerrar conta: ' + error.message, 'error');
    }
}

async function desativarContaWeb(numero) {
    if (!confirm(`Deseja realmente desativar a conta ${numero}?`)) return;
    try {
        const response = await fetch(`${API_BASE_URL}/contas/${numero}/desativar`, {
            method: 'PUT'
        });
        if (response.ok) {
            showToast('Conta desativada com sucesso!', 'success');
            carregarContas();
        } else {
            const erro = await response.text();
            showToast(erro || 'Erro ao desativar conta', 'error');
        }
    } catch (error) {
        showToast('Erro ao desativar conta: ' + error.message, 'error');
    }
}

// ========== HISTÓRICO ==========

async function consultarHistorico(event) {
    event.preventDefault();
    
    const numero = document.getElementById('historicoConta').value;
    const dataInicio = document.getElementById('historicoDataInicio').value;
    const dataFim = document.getElementById('historicoDataFim').value;
    
    let url = `${API_BASE_URL}/historico/extrato/${numero}`;
    
    if (dataInicio && dataFim) {
        url += `?dataInicio=${dataInicio}&dataFim=${dataFim}`;
    }
    
    try {
        const response = await fetch(url);
        const data = await response.json();
        
        const listaHistorico = document.getElementById('listaHistorico');
        
        if (!data.transacoes || data.transacoes.length === 0) {
            listaHistorico.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-file-invoice"></i>
                    <p>Nenhuma transação encontrada</p>
                </div>
            `;
            return;
        }
        
        listaHistorico.innerHTML = `
            <h3>Extrato da Conta ${data.numeroConta}</h3>
            <p style="color: var(--gray-color); margin-bottom: 2rem;">
                Total de transações: ${data.totalTransacoes}
            </p>
            ${data.transacoes.map(item => `
                <div class="historico-item">
                    <div class="historico-header">
                        <span class="tipo-operacao tipo-${item.tipoOperacao}">
                            ${getIconeOperacao(item.tipoOperacao)}
                            ${item.tipoOperacao.replace(/_/g, ' ')}
                        </span>
                        <span class="historico-valor">${formatarMoeda(item.valor)}</span>
                    </div>
                    <div class="historico-detalhes">
                        <div>${item.descricao}</div>
                        <div>
                            Saldo: ${formatarMoeda(item.saldoAnterior)} 
                            <i class="fas fa-arrow-right"></i> 
                            ${formatarMoeda(item.saldoNovo)}
                        </div>
                        <div><i class="far fa-clock"></i> ${formatarData(item.dataHora)}</div>
                    </div>
                </div>
            `).join('')}
        `;
        
    } catch (error) {
        showToast('Erro ao consultar histórico: ' + error.message, 'error');
    }
}

function getIconeOperacao(tipo) {
    const icones = {
        'DEPOSITO': '<i class="fas fa-arrow-down"></i>',
        'SAQUE': '<i class="fas fa-arrow-up"></i>',
        'TRANSFERENCIA_ENVIADA': '<i class="fas fa-arrow-right"></i>',
        'TRANSFERENCIA_RECEBIDA': '<i class="fas fa-arrow-left"></i>',
        'ABERTURA': '<i class="fas fa-plus-circle"></i>',
        'TAXA_MANUTENCAO': '<i class="fas fa-receipt"></i>'
    };
    return icones[tipo] || '<i class="fas fa-circle"></i>';
}

// ========== RELATÓRIOS ==========

async function carregarSaldoTotal() {
    try {
        const response = await fetch(`${API_BASE_URL}/historico/relatorio/saldo-total`);
        const data = await response.json();
        
        const container = document.getElementById('resultadoRelatorios');
        container.innerHTML = `
            <div class="relatorio-card">
                <h3><i class="fas fa-chart-pie"></i> Estatísticas do Banco</h3>
                <div class="stats-grid">
                    <div class="stat-item">
                        <i class="fas fa-users" style="font-size: 2rem; color: var(--primary-color);"></i>
                        <div class="stat-value">${data.totalContas || 0}</div>
                        <div class="stat-label">Total de Contas</div>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-dollar-sign" style="font-size: 2rem; color: var(--success-color);"></i>
                        <div class="stat-value">${formatarMoeda(data.saldoTotal || 0)}</div>
                        <div class="stat-label">Saldo Total</div>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-chart-line" style="font-size: 2rem; color: var(--info-color);"></i>
                        <div class="stat-value">${formatarMoeda(data.saldoMedio || 0)}</div>
                        <div class="stat-label">Saldo Médio</div>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-arrow-up" style="font-size: 2rem; color: var(--warning-color);"></i>
                        <div class="stat-value">${formatarMoeda(data.maiorSaldo || 0)}</div>
                        <div class="stat-label">Maior Saldo</div>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-arrow-down" style="font-size: 2rem; color: var(--danger-color);"></i>
                        <div class="stat-value">${formatarMoeda(data.menorSaldo || 0)}</div>
                        <div class="stat-label">Menor Saldo</div>
                    </div>
                </div>
            </div>
        `;
        
        showToast('Relatório carregado com sucesso!', 'success');
    } catch (error) {
        showToast('Erro ao carregar relatório: ' + error.message, 'error');
    }
}

async function carregarContasSaldoBaixo() {
    const limite = prompt('Digite o valor limite (padrão: R$ 1000):', '1000');
    if (!limite) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/historico/relatorio/contas-saldo-baixo?limiteSaldo=${limite}`);
        const contas = await response.json();
        
        const container = document.getElementById('resultadoRelatorios');
        
        if (contas.length === 0) {
            container.innerHTML = `
                <div class="relatorio-card">
                    <div class="empty-state">
                        <i class="fas fa-check-circle"></i>
                        <p>Nenhuma conta com saldo abaixo de ${formatarMoeda(limite)}</p>
                    </div>
                </div>
            `;
            return;
        }
        
        container.innerHTML = `
            <div class="relatorio-card">
                <h3><i class="fas fa-exclamation-triangle"></i> Contas com Saldo Baixo (< ${formatarMoeda(limite)})</h3>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Conta</th>
                                <th>Saldo</th>
                                <th>Titular</th>
                                <th>CPF</th>
                                <th>Email</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${contas.map(conta => `
                                <tr>
                                    <td><strong>${conta.numero}</strong></td>
                                    <td><strong>${formatarMoeda(conta.saldo)}</strong></td>
                                    <td>${conta.nome}</td>
                                    <td>${conta.cpf}</td>
                                    <td>${conta.email}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
        
        showToast(`${contas.length} conta(s) encontrada(s)`, 'info');
    } catch (error) {
        showToast('Erro ao carregar relatório: ' + error.message, 'error');
    }
}

function mostrarFormMovimentacoes() {
    toggleForm('formMovimentacoes');
}

async function carregarRelatorioMovimentacoes(event) {
    event.preventDefault();
    
    const dataInicio = document.getElementById('relatorioDataInicio').value;
    const dataFim = document.getElementById('relatorioDataFim').value;
    
    try {
        const response = await fetch(
            `${API_BASE_URL}/historico/relatorio/movimentacoes?dataInicio=${dataInicio}&dataFim=${dataFim}`
        );
        const movimentacoes = await response.json();
        
        const container = document.getElementById('resultadoRelatorios');
        
        if (movimentacoes.length === 0) {
            container.innerHTML = `
                <div class="relatorio-card">
                    <div class="empty-state">
                        <i class="fas fa-inbox"></i>
                        <p>Nenhuma movimentação no período</p>
                    </div>
                </div>
            `;
            return;
        }
        
        container.innerHTML = `
            <div class="relatorio-card">
                <h3><i class="fas fa-chart-line"></i> Movimentações por Tipo (${dataInicio} a ${dataFim})</h3>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Tipo de Operação</th>
                                <th>Quantidade</th>
                                <th>Valor Total</th>
                                <th>Valor Médio</th>
                                <th>Maior Valor</th>
                                <th>Menor Valor</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${movimentacoes.map(mov => `
                                <tr>
                                    <td><strong>${mov.tipoOperacao}</strong></td>
                                    <td>${mov.quantidadeOperacoes}</td>
                                    <td><strong>${formatarMoeda(mov.valorTotal)}</strong></td>
                                    <td>${formatarMoeda(mov.valorMedio)}</td>
                                    <td>${formatarMoeda(mov.maiorValor)}</td>
                                    <td>${formatarMoeda(mov.menorValor)}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
        
        toggleForm('formMovimentacoes');
        showToast('Relatório gerado com sucesso!', 'success');
    } catch (error) {
        showToast('Erro ao gerar relatório: ' + error.message, 'error');
    }
}

// ========== INICIALIZAÇÃO ==========

document.addEventListener('DOMContentLoaded', () => {
    // Carregar contas ao iniciar
    carregarContas();
});
