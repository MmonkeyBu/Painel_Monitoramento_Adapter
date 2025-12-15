# 📊 Painel de Monitoramento de Hidrômetros

Sistema inteligente para monitoramento e leitura automática de hidrômetros utilizando visão computacional (OCR) e tecnologia de captura de tela em tempo real.

---

## 📈 Medidor de Progresso Semanal

### Semana Atual (Semana de 15-21 de Dezembro de 2025)

| Componente | Status | Progresso |
|---|---|---|
| **Especificação da Fachada** | ✅ Completo | 100% |
| **Padrão Adapter (Múltiplas Fontes)** | ✅ Completo | 100% |
| **OCR e Processamento de Imagem** | 🔄 Em Progresso | 60% |
| **Persistência em Banco de Dados** | 🔄 Em Progresso | 40% |
| **Scheduler e Ciclo de Monitoramento** | ✅ Completo | 100% |
| **Interface de Visualização** | ⏳ Planejado | 0% |
| **Testes Unitários** | ⏳ Planejado | 0% |
| **Documentação API** | ⏳ Planejado | 0% |

**Progresso Total: 57%** 

```
████████████████░░░░░░░░░░ 57%
```

---

## 🏗️ Arquitetura e Padrões de Projeto

### 1. **Adapter Pattern** 
**Localização**: `HidrometroSource.java` | `InternalDisplayAdapter.java` | `ScreenRegionAdapter.java`

O padrão **Adapter** permite que diferentes fontes de hidrômetros (internas e externas) sejam integradas através de uma interface unificada `HidrometroSource`.

```
┌─────────────────────────────────────────────┐
│        Interface: HidrometroSource           │
│  - capturarImagem()                         │
│  - getIdentificador()                       │
│  - getTipoOrigem()                          │
│  - getValorReal()                           │
└────────────────┬────────────────────────────┘
         ▲       │       ▲
         │       │       │
    Implementa  Implementa  Implementa
         │       │       │
    ┌────┴─┐ ┌──┴──┐ ┌──┴──────────┐
    │Internal Display│ │ Screen Region│
    │ Adapter   │ │  Adapter  │
    └────────────┘ └──────────┘
```

**Benefícios:**
- ✅ Permite adicionar novas fontes sem modificar código existente
- ✅ Isolamento da lógica de captura específica de cada origem
- ✅ Facilita testes e manutenção

---

### 2. **Facade Pattern**
**Localização**: `PainelMonitoramento.java`

A classe `PainelMonitoramento` atua como **Facade**, simplificando a interface complexa do subsistema de monitoramento para o cliente.

**Funcionalidades da Fachada:**
- Gerenciamento de múltiplas fontes (`adicionarFonte`, `removerFonte`)
- Orquestração do ciclo de monitoramento (`iniciar`, `parar`)
- Coordenação entre OCR e Persistência
- Agendamento automático com `ScheduledExecutorService`

```java
// Uso Simples via Fachada
PainelMonitoramento painel = new PainelMonitoramento();
painel.adicionarFonte(new ScreenRegionAdapter(...));
painel.adicionarFonte(new InternalDisplayAdapter(...));
painel.iniciar();  // Todo o resto é automático!
```

---

### 3. **Strategy Pattern** (Em Desenvolvimento)
**Localização**: `HidrometroOCR.java` | `DB/HidrometroRepository.java`

O padrão **Strategy** será implementado para permitir diferentes estratégias de:
- **OCR**: Diferentes algoritmos de reconhecimento
- **Persistência**: Diferentes backends de armazenamento

**Exemplo de Uso Futuro:**
```java
painel.setOCRStrategy(new TesseractOCRStrategy());
painel.setRepositoryStrategy(new PostgreSQLRepository());
```

---

### 4. **Observer Pattern** (Planejado)
Será implementado para notificar interessados sobre eventos:
- Nova leitura capturada
- Anomalia detectada
- Falha na captura

---

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
| **OCR** | Reconhecimento óptico de caracteres | 🔄 Em Progresso |
| **Persistência** | Armazenamento de leituras | 🔄 Em Progresso |
| **Agendamento** | Ciclo de monitoramento automático | ✅ Ativo |
| **Visualização** | Interface gráfica | ⏳ Planejado |
| **Alertas** | Notificações de anomalias | ⏳ Planejado |

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

## 📝 Roadmap

### Q4 2025
- ✅ Especificação de arquitetura
- ✅ Implementação do Adapter Pattern
- 🔄 Integração OCR
- 🔄 Banco de dados

### Q1 2026
- [ ] Dashboard web
- [ ] API REST
- [ ] Testes abrangentes
- [ ] Documentação completa

### Q2 2026
- [ ] Suporte a múltiplos formatos de hidrômetro
- [ ] Machine Learning para anomalias
- [ ] Exportação de relatórios

---

## 📞 Contato

**Desenvolvedor**: MmonkeyBu  
**Email**: [seu-email@exemplo.com]  
**GitHub**: [github.com/MmonkeyBu/Painel_Monitoramento_Adapter](https://github.com/MmonkeyBu/Painel_Monitoramento_Adapter)

---

## 📄 Licença

Este projeto é licenciado sob a **Licença MIT** - veja o arquivo LICENSE para detalhes.

---

**Última atualização**: 15 de dezembro de 2025  
**Versão**: 0.1.0-SNAPSHOT
