# 📊 Painel de Monitoramento de Hidrômetros (v2.0)

Módulo de monitoramento e controle para sistemas de gestão hídrica.

## 🚀 Novas Funcionalidades (v2.0)

*   **Dashboard Administrativo:** Interface gráfica (Swing) para gestão visual completa.
*   **CRUD de Usuários:** Cadastro, edição e exclusão de usuários com persistência (JSON).
*   **Sistema de Alertas:** Notificações em tempo real (Visual e Log) via Observer Pattern.
*   **Inteligência de Monitoramento:**
    *   Leitura via Adapter (Simulação) ou OCR (Tesseract).
    *   Verificação automática de limites de consumo.
    *   Alertas visuais (Tabela com indicador de estouro).

## 📦 Instalação e Integração

Este projeto é um módulo independente. Para integrá-lo:

1.  **Dependências:**
    *   O projeto requer **Tess4J** (net.sourceforge.tess4j:tess4j:5.x) para funcionalidades de OCR.
    *   Java 17+.

2.  **Adapters:**
    *   O projeto fornece a interface `HidrometroSource`.
%SAME%

## 🛠️ Como Usar (Dashboard)

```java
// Exemplo de inicialização
PainelMonitoramento painel = new PainelMonitoramento();
// Configurar fontes...

// Iniciar Dashboard
DashboardAdmin dashboard = new DashboardAdmin(painel);
dashboard.setVisible(true);
```

## 🏗️ Padrões de Projeto
* **Adapter:** Integração com diferentes fontes de dados.
* **Observer:** Sistema de notificação de alertas.
* **Facade:** Simplificação do acesso às funcionalidades complexas.
* **Repository:** Abstração da camada de persistência de dados.
* **MVC:** Separação clara entre Interface (View), Lógica (Controller) e Dados (Model).
  
Sistema inteligente para monitoramento e leitura automática de hidrômetros utilizando visão computacional (OCR) e tecnologia de captura de tela em tempo real.

---

## 📈 Medidor de Progresso Semanal

### Semana Atual (Semana de 15-21 de Dezembro de 2025)

| Componente | Status | Progresso |
|---|---|---|
| **Especificação da Fachada** | ✅ Completo | 100% |
| **Padrão Adapter (Múltiplas Fontes)** | ✅ Completo | 100% |
| **OCR e Processamento de Imagem** | ✅ Completo | 100% |
| **Persistência em Banco de Dados** | ✅ Completo | 100% |
| **Scheduler e Ciclo de Monitoramento** | ✅ Completo | 100% |
| **Interface de Visualização** | ✅ Completo | 100% |
| **Testes Unitários** | ✅ Completo | 100% |
| **Documentação API** | ✅ Completo | 100% |

**Progresso Total: 100%** 

```
██████████████████████████ 100%
```

---

## 🏗️ Arquitetura e Padrões de Projeto

O projeto utiliza padrões de design robustos para garantir extensibilidade e desacoplamento.

### 1. **Adapter Pattern** **Localização**: [`HidrometroSource.java`](src/main/java/br/com/hidrometro/monitoramento/HidrometroSource.java) | [`ScreenRegionAdapter.java`](src/main/java/br/com/hidrometro/monitoramento/ScreenRegionAdapter.java)

Permite que diferentes fontes de hidrômetros (simuladores, câmera, captura de tela) sejam tratadas de forma uniforme.
- **`HidrometroSource`**: Interface Target.
- **`ScreenRegionAdapter`**: Adaptador para capturar regiões da tela via AWT Robot.

### 2. **Facade Pattern**
**Localização**: [`PainelMonitoramento.java`](src/main/java/br/com/hidrometro/monitoramento/PainelMonitoramento.java)

Simplifica a complexidade do subsistema (OCR, persistência, agendamento) fornecendo uma interface única para o cliente (`DashboardAdmin` ou CLI).
- Gerencia ciclo de vida do monitoramento (`iniciar()`, `parar()`).
- Centraliza operações de CRUD de usuários (`cadastrarUsuario()`, `removerUsuario()`).

### 3. **Observer Pattern**
**Localização**: [`Notificador.java`](src/main/java/br/com/hidrometro/monitoramento/Notificador.java) | [`LogNotificador.java`](src/main/java/br/com/hidrometro/monitoramento/LogNotificador.java)

Implementado para notificar alertas em tempo real.
- **Subject**: `PainelMonitoramento` (mantém lista de notificadores).
- **Observer**: `Notificador` (Interface).
- **Concrete Observer**: `DashboardAdmin.GuiNotificador` (atualiza interface gráfica) e `LogNotificador` (registra em arquivo/console).

### 4. **Strategy Pattern** (Base)
**Localização**: [`HidrometroOCR.java`](src/main/java/br/com/hidrometro/monitoramento/ocr/HidrometroOCR.java)

Estruturado para suportar diferentes estratégias de reconhecimento de caracteres e persistência futura (Database vs File).

### 5. **Repository Pattern**
**Localização**: [`HidrometroRepository.java`](src/main/java/br/com/hidrometro/monitoramento/db/HidrometroRepository.java)

Isola a camada de persistência de dados do restante da aplicação.
- Centraliza operações de banco de dados (JDBC/SQLite).
- Permite que a lógica de negócio salve leituras sem acoplamento direto com a linguagem SQL.

### 6. **MVC (Model-View-Controller)**
**Localização**: [`DashboardAdmin.java`](src/main/java/br/com/hidrometro/monitoramento/DashboardAdmin.java) | [`PainelMonitoramento.java`](src/main/java/br/com/hidrometro/monitoramento/PainelMonitoramento.java)

Estrutura a interface gráfica e a interação do usuário separando responsabilidades:
- **Model**: Classes de domínio (`Usuario`, `Leitura`) que detêm os dados.
- **View**: Interface gráfica Swing (`DashboardAdmin`) responsável apenas pela exibição.
- **Controller**: A Fachada (`PainelMonitoramento`) atua como controlador, processando as entradas da View e atualizando o Model.
## 📂 Estrutura do Projeto

```
Painel_Monitoramento_Adapter/
├── README.md                           # Este arquivo
├── PainelMonitoramento.java           # Facade Principal
├── HidrometroSource.java              # Interface do Adapter Pattern
├── InternalDisplayAdapter.java        # Adapter para Hidrômetros Internos
├── ScreenRegionAdapter.java           # Adapter para Captura de Tela
│
├── OCR/
│   └── HidrometroOCR.java            # Processamento de Imagem e OCR
│
├── DB/
│   └── HidrometroRepository.java      # Persistência em Banco de Dados
│
└── (Em desenvolvimento)
    ├── models/                        # Modelos de Dados
    ├── services/                      # Serviços de Negócio
    ├── ui/                            # Interface de Visualização
    └── tests/                         # Testes Unitários
```

---

## 🔧 Funcionalidades Implementadas

### ✅ Captura de Imagens
- [x] Captura do display interno (Swing)
- [x] Captura de região específica da tela
- [x] Suporte a múltiplas resoluções

### ✅ Gerenciamento de Fontes
- [x] Adicionar/remover fontes dinâmicas
- [x] Identificação única de fontes
- [x] Tipo de origem personalizável

### ✅ Orquestração
- [x] Scheduler de monitoramento
- [x] Ciclo de captura → OCR → Persistência
- [x] Start/Stop sincronizado

---

## 🚀 Funcionalidades em Desenvolvimento

### 🔄 OCR Avançado
- [ ] Melhor tratamento de ruído
- [ ] Reconhecimento de múltiplos idiomas
- [ ] Correção automática de erros

### 🔄 Persistência
- [ ] Suporte a PostgreSQL
- [ ] Histórico de leituras
- [ ] Análise de consumo

### 📊 Visualização
- [ ] Dashboard web
- [ ] Gráficos de consumo
- [ ] Alertas em tempo real

### 🧪 Qualidade
- [ ] Testes unitários (JUnit)
- [ ] Testes de integração
- [ ] Cobertura de código 80%+

---

## 🎯 Subsistemas Principais

| Subsistema | Descrição | Status |
|---|---|---|
| **Captura** | Módulo responsável por obter imagens das fontes | ✅ Ativo |
| **OCR** | Reconhecimento óptico de caracteres | ✅ Ativo  |
| **Persistência** | Armazenamento de leituras | ✅ Ativo  |
| **Agendamento** | Ciclo de monitoramento automático | ✅ Ativo |
| **Visualização** | Interface gráfica | ✅ Ativo o |
| **Alertas** | Notificações de anomalias | ✅ Ativo  |

---

## 💻 Tecnologias Utilizadas

- **Java 11+** - Linguagem principal
- **Swing/AWT** - Captura de imagens
- **Tesseract/OpenCV** - OCR e visão computacional
- **JDBC** - Persistência de dados
- **ScheduledExecutorService** - Agendamento
- **Maven/Gradle** - Build (a definir)

---

## 🤝 Como Contribuir

1. Fork o repositório
2. Crie uma branch para sua feature (`git checkout -b feature/minha-feature`)
3. Commit suas mudanças (`git commit -m 'Add: Minha nova feature'`)
4. Push para a branch (`git push origin feature/minha-feature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto é licenciado sob a **Licença MIT** - veja o arquivo LICENSE para detalhes.

---

**Última atualização**: 15 de dezembro de 2025  
**Versão**: 0.1.0-SNAPSHOT
