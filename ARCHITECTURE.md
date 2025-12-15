# 🏛️ Documentação de Arquitetura e Padrões de Projeto

## Visão Geral

O **Painel de Monitoramento de Hidrômetros** segue princípios SOLID e utiliza padrões de projeto bem estabelecidos para criar um sistema modular, escalável e fácil de manter.

---

## 📊 Diagrama de Arquitetura Geral

```
┌────────────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO                      │
│                  (Interface de Visualização)                   │
│              [Dashboard Web / Desktop GUI]                     │
└────────────────────┬─────────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────────┐
│                    CAMADA DE FACHADA                           │
│                  ┌──────────────────────┐                     │
│                  │ PainelMonitoramento  │◄─── Facade Pattern │
│                  │   (Fachada)          │                     │
│                  └──────────────────────┘                     │
└────────────────────┬─────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
┌───────▼──┐  ┌─────▼──────┐  ┌──▼────────┐
│  Captura │  │ Orquestração│  │ Callback  │
│          │  │    de Ciclo │  │  Observer │
│ (Adapter)│  │   (Scheduler)│  │  (Events) │
└─────────┬┘  └──────┬──────┘  └──┬───────┘
          │         │            │
    ┌─────▼─────────▼────────────▼────┐
    │  CAMADA DE SERVIÇOS              │
    │  ┌──────────────────────────────┐│
    │  │ HidrometroSource (Interface) ││◄─── Adapter Pattern
    │  │ ├─ InternalDisplayAdapter   ││
    │  │ └─ ScreenRegionAdapter       ││
    │  └──────────────────────────────┘│
    │  ┌──────────────────────────────┐│
    │  │ HidrometroOCR (Strategy)     ││◄─── Strategy Pattern
    │  │ ├─ TesseractOCR              ││
    │  │ └─ OpenCVOCR                 ││
    │  └──────────────────────────────┘│
    │  ┌──────────────────────────────┐│
    │  │ HidrometroRepository (DAO)   ││◄─── Repository Pattern
    │  │ ├─ JDBCRepository            ││
    │  │ └─ PostgreSQLRepository      ││
    │  └──────────────────────────────┘│
    └────────────────────────────────────┘
                   │
    ┌──────────────▼───────────────┐
    │   CAMADA DE DADOS             │
    │   ┌────────────────────────┐  │
    │   │ Banco de Dados         │  │
    │   │ - PostgreSQL           │  │
    │   │ - MySQL                │  │
    │   │ - SQLite               │  │
    │   └────────────────────────┘  │
    └──────────────────────────────┘
```

---

## 🎯 Padrões de Projeto Implementados

### 1. **ADAPTER PATTERN** ⭐

#### Objetivo
Converter a interface de diferentes fontes de hidrômetros em uma interface unificada que o sistema possa usar.

#### Estrutura

```java
// ============ INTERFACE TARGET ============
public interface HidrometroSource {
    BufferedImage capturarImagem();
    String getIdentificador();
    String getTipoOrigem();
    double getValorReal();
}

// ============ ADAPTER 1 ============
public class InternalDisplayAdapter implements HidrometroSource {
    private final Hidrometro hidrometro;
    
    @Override
    public BufferedImage capturarImagem() {
        // Adapta a captura do Display interno
        Display display = hidrometro.getDisplay();
        BufferedImage imagem = new BufferedImage(...);
        Graphics2D g2d = imagem.createGraphics();
        display.paint(g2d);
        g2d.dispose();
        return imagem;
    }
}

// ============ ADAPTER 2 ============
public class ScreenRegionAdapter implements HidrometroSource {
    private final Rectangle areaCaptura;
    private Robot robot;
    
    @Override
    public BufferedImage capturarImagem() {
        // Adapta a captura de tela do Sistema Operacional
        return robot.createScreenCapture(areaCaptura);
    }
}
```

#### Benefícios
✅ **Flexibilidade**: Suporta múltiplas origens sem modificar código cliente  
✅ **Extensibilidade**: Novos adapters podem ser adicionados facilmente  
✅ **Encapsulamento**: Detalhes específicos de cada origem ficam isolados  
✅ **Testabilidade**: Cada adapter pode ser testado independentemente

#### Implementações Existentes
- `InternalDisplayAdapter` - Captura de componente Swing interno
- `ScreenRegionAdapter` - Captura de região específica da tela

#### Implementações Futuras
- `CameraAdapter` - Captura de câmera em tempo real
- `FileAdapter` - Leitura de arquivo de imagem
- `NetworkStreamAdapter` - Captura de stream de rede

---

### 2. **FACADE PATTERN** ⭐

#### Objetivo
Fornecer uma interface simplificada e unificada para um subsistema complexo.

#### Estrutura

```java
public class PainelMonitoramento {
    // Componentes complexos do subsistema
    private final List<HidrometroSource> fontes;
    private final HidrometroOCR ocr;
    private final HidrometroRepository repository;
    private ScheduledExecutorService scheduler;
    
    // ========== INTERFACE SIMPLIFICADA ==========
    
    // 1. Gerenciar fontes
    public void adicionarFonte(HidrometroSource fonte) {
        fontes.add(fonte);
    }
    
    public void removerFonte(HidrometroSource fonte) {
        fontes.remove(fonte);
    }
    
    // 2. Controlar ciclo
    public void iniciar() {
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
            this::cicloMonitoramento, 
            2, 5, TimeUnit.SECONDS
        );
    }
    
    public void parar() {
        running = false;
        scheduler.shutdownNow();
    }
    
    // 3. Operação automatizada (Orquestração)
    private void cicloMonitoramento() {
        for (HidrometroSource fonte : fontes) {
            // 1. Captura imagem
            BufferedImage imagem = fonte.capturarImagem();
            
            // 2. Realiza OCR
            String valorLido = ocr.reconhecer(imagem);
            
            // 3. Persiste no banco
            repository.salvar(
                fonte.getIdentificador(), 
                valorLido
            );
        }
    }
}
```

#### Uso Pelo Cliente

```java
// ❌ Sem Facade (Complexo)
List<HidrometroSource> fontes = new ArrayList<>();
HidrometroOCR ocr = new HidrometroOCR();
HidrometroRepository repo = new HidrometroRepository();
ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

// Várias linhas de configuração...

// ✅ Com Facade (Simples)
PainelMonitoramento painel = new PainelMonitoramento();
painel.adicionarFonte(new ScreenRegionAdapter(...));
painel.adicionarFonte(new InternalDisplayAdapter(...));
painel.iniciar(); // Tudo funciona automaticamente!
```

#### Responsabilidades da Fachada
- Coordenação de componentes
- Orquestração do fluxo de trabalho
- Gerenciamento de ciclo de vida
- Abstração de complexidade para o cliente

---

### 3. **STRATEGY PATTERN** (Em Desenvolvimento)

#### Objetivo
Permitir diferentes estratégias para operações variáveis (OCR, Persistência) sem modificar o código cliente.

#### Estrutura Planejada

```java
// ========== STRATEGIES ==========

// Strategy para OCR
public interface OCRStrategy {
    String reconhecer(BufferedImage imagem);
    double getConfianca();
}

public class TesseractOCRStrategy implements OCRStrategy {
    @Override
    public String reconhecer(BufferedImage imagem) {
        // Usa Tesseract
    }
}

public class OpenCVOCRStrategy implements OCRStrategy {
    @Override
    public String reconhecer(BufferedImage imagem) {
        // Usa OpenCV
    }
}

// Strategy para Persistência
public interface PersistenceStrategy {
    void salvar(String id, String valor);
    List<String> listar();
}

public class PostgreSQLStrategy implements PersistenceStrategy {
    @Override
    public void salvar(String id, String valor) {
        // Usa PostgreSQL
    }
}

public class MySQLStrategy implements PersistenceStrategy {
    @Override
    public void salvar(String id, String valor) {
        // Usa MySQL
    }
}

// ========== CONTEXT (Painel) ==========

public class PainelMonitoramento {
    private OCRStrategy ocrStrategy;
    private PersistenceStrategy persistenceStrategy;
    
    // Permitir mudar estratégias em tempo de execução
    public void setOCRStrategy(OCRStrategy strategy) {
        this.ocrStrategy = strategy;
    }
    
    public void setPersistenceStrategy(PersistenceStrategy strategy) {
        this.persistenceStrategy = strategy;
    }
    
    private void cicloMonitoramento() {
        BufferedImage imagem = fonte.capturarImagem();
        
        // Usa a estratégia configurada
        String valor = ocrStrategy.reconhecer(imagem);
        persistenceStrategy.salvar(fonte.getIdentificador(), valor);
    }
}
```

#### Uso

```java
PainelMonitoramento painel = new PainelMonitoramento();

// Mudar estratégia em tempo de execução
painel.setOCRStrategy(new TesseractOCRStrategy());
painel.setPersistenceStrategy(new PostgreSQLStrategy());

// Ou mudar para outra estratégia
painel.setOCRStrategy(new OpenCVOCRStrategy());
painel.setPersistenceStrategy(new MySQLStrategy());
```

---

### 4. **OBSERVER PATTERN** (Planejado)

#### Objetivo
Notificar múltiplos objetos sobre eventos importantes no sistema.

#### Implementação Futura

```java
// ========== OBSERVER ==========

public interface MonitoringObserver {
    void aoCapturar(HidrometroEvent evento);
    void aoErro(ErrorEvent evento);
    void aoAnomaliaDetectada(AnomalyEvent evento);
}

// ========== SUBJECT (Observable) ==========

public class PainelMonitoramento {
    private List<MonitoringObserver> observers = new ArrayList<>();
    
    public void adicionarObservador(MonitoringObserver observer) {
        observers.add(observer);
    }
    
    public void removerObservador(MonitoringObserver observer) {
        observers.remove(observer);
    }
    
    private void notificarCaptura(String id, String valor) {
        observers.forEach(observer -> 
            observer.aoCapturar(new HidrometroEvent(id, valor))
        );
    }
}

// ========== CONCRETE OBSERVERS ==========

public class LoggingObserver implements MonitoringObserver {
    @Override
    public void aoCapturar(HidrometroEvent evento) {
        System.out.println("Captura: " + evento);
    }
}

public class AlertObserver implements MonitoringObserver {
    @Override
    public void aoAnomaliaDetectada(AnomalyEvent evento) {
        enviarAlerta(evento.mensagem);
    }
}
```

#### Uso

```java
PainelMonitoramento painel = new PainelMonitoramento();
painel.adicionarObservador(new LoggingObserver());
painel.adicionarObservador(new AlertObserver());
painel.iniciar(); // Eventos serão notificados automaticamente
```

---

### 5. **REPOSITORY PATTERN** (Em Desenvolvimento)

#### Objetivo
Abstrair a camada de acesso a dados, permitindo trocar o backend de persistência facilmente.

#### Estrutura

```java
// ========== INTERFACE DO REPOSITÓRIO ==========

public interface HidrometroRepository {
    void salvar(String id, String valor);
    void salvar(Leitura leitura);
    Optional<Leitura> obter(String id);
    List<Leitura> listarTodas();
    List<Leitura> listarPorPeriodo(LocalDate inicio, LocalDate fim);
    void atualizar(Leitura leitura);
    void deletar(String id);
}

// ========== MODELO ==========

public class Leitura {
    private String id;
    private String identificadorHidrometro;
    private String valor;
    private LocalDateTime dataCaptura;
    private double confianca;
    private String origem;
    
    // Getters e setters...
}

// ========== IMPLEMENTAÇÕES ==========

public class JDBCRepository implements HidrometroRepository {
    private Connection conexao;
    
    @Override
    public void salvar(Leitura leitura) {
        String sql = "INSERT INTO leituras (id, valor, data) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, leitura.getId());
            stmt.setString(2, leitura.getValor());
            stmt.setTimestamp(3, Timestamp.valueOf(leitura.getDataCaptura()));
            stmt.executeUpdate();
        }
    }
}

public class PostgreSQLRepository implements HidrometroRepository {
    // Implementação específica para PostgreSQL
}
```

---

## 🔄 Fluxo de Execução

```
┌─────────────────────────────────────────────┐
│  Cliente cria PainelMonitoramento (Facade)  │
└──────────────┬────────────────────────────┘
               │
┌──────────────▼────────────────────────────┐
│  Adiciona HidrometroSources (Adapters)    │
│  - InternalDisplayAdapter                 │
│  - ScreenRegionAdapter                    │
└──────────────┬────────────────────────────┘
               │
┌──────────────▼────────────────────────────┐
│  Cliente chama painel.iniciar()           │
└──────────────┬────────────────────────────┘
               │
         ┌─────▼──────┐
         │ A cada 5s  │
         └─────┬──────┘
               │
    ┌──────────▼──────────┐
    │ cicloMonitoramento()│
    └──────────┬──────────┘
               │
    ┌──────────┴───────────┬────────────────┐
    │                      │                │
    ▼                      ▼                ▼
┌────────────┐  ┌──────────────┐  ┌──────────────┐
│   Captura  │  │     OCR      │  │ Persistência │
│  (Adapter) │  │  (Strategy)  │  │(Repository)  │
└────────────┘  └──────────────┘  └──────────────┘
    │                  │                │
    └──────────────────┼────────────────┘
                       │
            ┌──────────▼──────────┐
            │  Notifica Observers │ (Planejado)
            │  (Observer Pattern) │
            └─────────────────────┘
```

---

## 📋 Princípios SOLID Aplicados

| Princípio | Aplicação | Exemplo |
|---|---|---|
| **S**ingle Responsibility | Cada classe tem uma responsabilidade | `InternalDisplayAdapter` só cuida de captura interna |
| **O**pen/Closed | Aberto para extensão, fechado para modificação | Novos adapters sem modificar `HidrometroSource` |
| **L**iskov Substitution | Subtipos podem substituir supertipo | Todo `HidrometroSource` pode ser usado no lugar de outro |
| **I**nterface Segregation | Interfaces específicas, não genéricas | `HidrometroSource` é específica, não se mistura com outras responsabilidades |
| **D**ependency Inversion | Depender de abstrações, não de implementações | `PainelMonitoramento` depende de interfaces, não classes concretas |

---

## 🔐 Considerações de Segurança

- [ ] Validação de entrada de imagens
- [ ] Controle de acesso ao banco de dados
- [ ] Criptografia de dados sensíveis
- [ ] Auditoria de leituras
- [ ] Detecção de manipulação de valores

---

## 📈 Escalabilidade

O design permite:

1. **Escalabilidade Horizontal**: Múltiplos painéis independentes
2. **Escalabilidade Vertical**: Mais threads para processamento
3. **Extensibilidade**: Novos padrões e estratégias sem quebra
4. **Configurabilidade**: Comportamento ajustável em tempo de execução

---

## 📚 Referências

- **Design Patterns**: Gang of Four (GoF)
- **Clean Architecture**: Robert C. Martin
- **SOLID Principles**: UNCLE BOB
- **Refactoring**: Martin Fowler

---

**Documento versão**: 1.0  
**Última atualização**: 15 de dezembro de 2025
