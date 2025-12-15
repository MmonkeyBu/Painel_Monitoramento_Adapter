# 🤝 Guia de Contribuição

Obrigado por interesse em contribuir para o **Painel de Monitoramento de Hidrômetros**! Este documento fornece orientações para colaboradores.

---

## 📋 Antes de Começar

1. Faça um fork do repositório
2. Clone sua cópia local
3. Crie uma branch para sua feature
4. Leia este guia completamente

---

## 🎯 Tipos de Contribuição

### 🐛 Reportar Bugs
- Descreva o comportamento esperado vs. o real
- Forneça etapas para reproduzir
- Inclua versão do Java e SO
- Se possível, adicione logs/screenshots

### ✨ Sugerir Features
- Descreva o caso de uso
- Explique o benefício
- Cite padrões de projeto aplicáveis
- Relação com roadmap existente

### 📝 Melhorar Documentação
- Correções ortográficas
- Clarificar explicações
- Adicionar exemplos
- Atualizar diagramas

### 💻 Implementar Features
- Veja a seção de desenvolvimento abaixo

---

## 🛠️ Configuração do Ambiente

### Pré-requisitos
```bash
# Java 11+
java -version

# Git
git --version

# Maven (opcional, para build)
mvn --version
```

### Setup Local
```bash
# 1. Fork no GitHub
git clone https://github.com/SEU_USUARIO/Painel_Monitoramento_Adapter.git
cd Painel_Monitoramento_Adapter

# 2. Criar branch de feature
git checkout -b feature/sua-feature-nome

# 3. Instalar dependências
# (A definir conforme evoluir o projeto)
```

---

## 📐 Padrões de Código

### Convenções de Nomenclatura

**Pacotes**:
```java
br.com.hidrometro.monitoramento.*
br.com.hidrometro.monitoramento.adapter.*
br.com.hidrometro.monitoramento.strategy.*
br.com.hidrometro.monitoramento.repository.*
```

**Classes**:
```java
// Adapters
public class ScreenRegionAdapter implements HidrometroSource
public class InternalDisplayAdapter implements HidrometroSource

// Estratégias
public class TesseractOCRStrategy implements OCRStrategy
public class PostgreSQLRepository implements HidrometroRepository

// Modelos
public class Leitura
public class HidrometroEvent
```

**Métodos**:
```java
// Getters
public String getIdentificador()

// Setters
public void setOCRStrategy(OCRStrategy strategy)

// Ações
public void adicionarFonte(HidrometroSource fonte)
public void iniciar()
public void parar()

// Queries
public List<Leitura> listarPorPeriodo(LocalDate inicio, LocalDate fim)
```

### Formatação de Código

```java
// ✅ BOM
public class MinhaClasse {
    
    private String propriedade;
    private int outraPropriedade;
    
    public MinhaClasse(String propriedade) {
        this.propriedade = propriedade;
    }
    
    public void fazerAlgo() {
        if (condicao) {
            // fazer algo
        }
    }
}

// ❌ RUIM
public class MinhaClasse{
private String propriedade;
private int outraPropriedade;
public MinhaClasse(String propriedade){this.propriedade = propriedade;}
public void fazerAlgo(){if(condicao){//fazer algo}}
}
```

### Comentários e Javadoc

```java
/**
 * Captura a imagem atual do hidrômetro para OCR.
 * 
 * Este método sincroniza com o display e cria uma 
 * imagem renderizada de alta qualidade.
 * 
 * @return BufferedImage contendo os dígitos do hidrômetro
 * @throws IllegalStateException se o display não foi inicializado
 * 
 * @see HidrometroOCR#reconhecer(BufferedImage)
 */
public BufferedImage capturarImagem() {
    // Implementação...
}
```

---

## ✅ Checklist de Qualidade

Antes de fazer commit:

### Código
- [ ] Segue convenções de nomenclatura
- [ ] Sem código comentado
- [ ] Sem imports não utilizados
- [ ] Sem variáveis não utilizadas
- [ ] Sem hardcoded values
- [ ] Sem System.out.println (usar logger)

### Documentação
- [ ] Javadoc completo em métodos públicos
- [ ] README atualizado se necessário
- [ ] ARCHITECTURE.md atualizado se padrão muda
- [ ] PROGRESS.md atualizado com status

### Testes (Quando implementados)
- [ ] Novos testes unitários criados
- [ ] Todos os testes passando
- [ ] Cobertura não diminuiu
- [ ] Testes independentes (sem ordem)

### Commit
- [ ] Mensagem clara e descritiva
- [ ] Uma feature por commit (quando possível)
- [ ] Sem commits com "Fix typo" + feature
- [ ] Historia legível

---

## 📝 Mensagens de Commit

### Formato
```
<tipo>: <descrição>

[corpo opcional]

[rodapé opcional]
```

### Tipos
- **feat**: Nova feature
- **fix**: Correção de bug
- **docs**: Mudanças em documentação
- **style**: Formatação de código
- **refactor**: Refatoração sem mudança de comportamento
- **perf**: Melhorias de performance
- **test**: Testes adicionados ou modificados
- **chore**: Tasks que não afetam código (build, deps)

### Exemplos
```bash
# Feature
git commit -m "feat: Implementar Strategy Pattern para OCR"

# Bug fix
git commit -m "fix: Corrigir NPE em ScreenRegionAdapter"

# Documentação
git commit -m "docs: Adicionar exemplos de uso em README"

# Com body (para mudanças complexas)
git commit -m "feat: Adicionar suporte a PostgreSQL

- Implementar JDBCRepository
- Adicionar configuração de conexão
- Testes de persistência
- Closes #123"
```

---

## 🔄 Processo de Desenvolvimento

### 1. Criar a Feature
```bash
# Criar branch descritiva
git checkout -b feature/implementar-observer-pattern
```

### 2. Desenvolver
- Implementar a funcionalidade
- Adicionar testes
- Atualizar documentação

### 3. Testar Localmente
```bash
# Compilar (quando tiver Maven)
mvn clean compile

# Testar
mvn test

# Ou usar IDE (Eclipse, IntelliJ)
```

### 4. Commit
```bash
# Adicionar mudanças
git add .

# Commit com mensagem clara
git commit -m "feat: descrição clara da feature"

# Ou stage parcial
git add src/main/java/...
git commit -m "feat: primeira parte"
git add docs/...
git commit -m "docs: documentação da feature"
```

### 5. Push
```bash
git push origin feature/implementar-observer-pattern
```

### 6. Pull Request
- Ir ao GitHub
- Criar PR com:
  - Título descritivo
  - Descrição clara do que foi feito
  - Referência a issues relacionadas (Closes #123)
  - Screenshots se for UI

---

## 🎯 Diretrizes por Tipo de Contribuição

### 🐛 Corrigindo Bugs

1. **Criar issue primeiro** com:
   - Passos para reproduzir
   - Comportamento esperado
   - Comportamento real
   - Ambiente (Java version, SO, etc)

2. **Implementar fix**:
   - Branch: `bugfix/descricao-breve`
   - Teste que reproduz o bug
   - Correção mínima necessária

3. **Exemplo**:
   ```bash
   git checkout -b bugfix/screen-adapter-null-pointer
   # Implementar teste que falha
   # Corrigir o bug
   # Commit: "fix: Prevenir NPE em ScreenRegionAdapter"
   ```

### ✨ Implementando Features

1. **Começar com discussão**:
   - Criar issue descrevendo a feature
   - Discutir design antes de implementar
   - Alinhar com roadmap

2. **Implementar com testes**:
   - TDD: escrever teste primeiro
   - Implementar feature
   - Refatorar se necessário

3. **Exemplo**:
   ```bash
   git checkout -b feature/observer-pattern
   # 1. Criar interfaces
   # 2. Criar testes
   # 3. Implementar
   # Commits:
   # - feat: Definir interface MonitoringObserver
   # - feat: Implementar Observer Pattern
   # - test: Testes para Observer Pattern
   ```

### 📝 Atualizando Documentação

1. **Quando atualizar**:
   - README: Para features visíveis ao usuário
   - ARCHITECTURE.md: Para padrões de projeto novos
   - PROGRESS.md: Após completar feature
   - Javadoc: Sempre em métodos públicos

2. **Revisar clareza**:
   - Outros entendem facilmente?
   - Exemplos são claros?
   - Links funcionam?

3. **Exemplo**:
   ```bash
   git checkout -b docs/adicionar-exemplo-adapter
   # Editar README.md com exemplos
   # Commit: "docs: Adicionar exemplo de uso do Adapter"
   ```

---

## 🧪 Testes

### Quando Implementados

```bash
# Rodar todos os testes
mvn test

# Rodar teste específico
mvn test -Dtest=ScreenRegionAdapterTest

# Com cobertura
mvn test jacoco:report
```

### Estrutura de Testes
```
src/test/java/
├── br/com/hidrometro/monitoramento/
│   ├── PainelMonitoramentoTest.java
│   └── adapter/
│       ├── ScreenRegionAdapterTest.java
│       └── InternalDisplayAdapterTest.java
```

### Exemplo de Teste
```java
public class ScreenRegionAdapterTest {
    
    private ScreenRegionAdapter adapter;
    
    @Before
    public void setup() {
        adapter = new ScreenRegionAdapter("test_id", "TEST", 0, 0, 100, 100);
    }
    
    @Test
    public void testCaptureImage() {
        BufferedImage imagem = adapter.capturarImagem();
        assertNotNull(imagem);
        assertEquals(100, imagem.getWidth());
        assertEquals(100, imagem.getHeight());
    }
    
    @Test
    public void testGetIdentificador() {
        assertEquals("test_id", adapter.getIdentificador());
    }
}
```

---

## 🔍 Review Process

### O que será analisado

✅ **Código**:
- Segue padrões do projeto
- Qualidade e clareza
- Sem duplicação
- Performance

✅ **Testes**:
- Testes novos escritos
- Testes passando
- Cobertura adequada

✅ **Documentação**:
- Javadoc presente
- README atualizado
- Exemplos claros

✅ **Padrões**:
- Segue SOLID
- Usa padrões apropriados
- Coesão alta, acoplamento baixo

### Feedback Esperado
- Críticas construtivas
- Sugestões de melhoria
- Reconhecimento de bom trabalho

### Se o PR for Rejeitado
- Leia o feedback cuidadosamente
- Faça as mudanças solicitadas
- Resubmeta para revisão

---

## 🚀 Processo de Release

Mantido pela equipe principal:

1. **Versioning**: Semver (MAJOR.MINOR.PATCH)
2. **Release Branch**: `release/v1.0.0`
3. **Tag**: `v1.0.0`
4. **Changelog**: Documentado em CHANGELOG.md

---

## 📞 Perguntas?

- **Dúvidas sobre Issue**: Comente na issue no GitHub
- **Dúvidas sobre Code**: Pergunte no PR
- **Dúvidas Gerais**: Abra uma discussion no GitHub

---

## 🎓 Recursos Úteis

- [Git Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Clean Code](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/)

---

## 🙏 Agradecimentos

Obrigado por contribuir! Sua ajuda é muito valorizada e ajuda a melhorar este projeto para toda a comunidade.

---

**Última atualização**: 15 de dezembro de 2025  
**Mantido por**: MmonkeyBu
