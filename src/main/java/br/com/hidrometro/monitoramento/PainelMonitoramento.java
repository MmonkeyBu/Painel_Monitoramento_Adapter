package br.com.hidrometro.monitoramento;

import br.com.hidrometro.monitoramento.ocr.HidrometroOCR;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PainelMonitoramento {

    private final List<HidrometroSource> fontes = new ArrayList<>();
    private final HidrometroOCR ocr;

    // Simulação de banco de dados em memória (RF01)
    private final Map<String, Usuario> usuarios = new HashMap<>(); // Chave: CPF

    // Lista de observadores (RF03)
    private final List<Notificador> notificadores = new ArrayList<>();

    private ScheduledExecutorService scheduler;
    private boolean monitorando = false;

    // Persistencia
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivoUsuarios = new File("usuarios.json");

    public PainelMonitoramento() {
        this.ocr = new HidrometroOCR();

        this.scheduler = Executors.newScheduledThreadPool(1);

        // Registrar o LogNotificador padrão
        registrarNotificador(new LogNotificador());

        carregarUsuarios();
    }

    // --- Persistencia ---
    private void carregarUsuarios() {
        if (arquivoUsuarios.exists()) {
            try {
                // Leitura de Lista e conversao para Map
                List<Usuario> lista = Arrays.asList(mapper.readValue(arquivoUsuarios, Usuario[].class));
                for (Usuario u : lista) {
                    usuarios.put(u.getCpf(), u);
                }
                System.out.println("[Monitoramento] Usuarios carregados: " + usuarios.size());
            } catch (Exception e) {
                System.err.println("[Monitoramento] Erro ao carregar usuarios: " + e.getMessage());
            }
        }
    }

    private void salvarUsuarios() {
        try {
            mapper.writeValue(arquivoUsuarios, usuarios.values());
            System.out.println("[Monitoramento] Usuarios salvos.");
        } catch (Exception e) {
            System.err.println("[Monitoramento] Erro ao salvar usuarios: " + e.getMessage());
        }
    }

    // --- CRUD DE USUÁRIOS (RF01) ---

    public void cadastrarUsuario(String nome, String cpf, double limiteConsumoAlerta) {
        Usuario usuario = new Usuario(nome, cpf, limiteConsumoAlerta);
        usuarios.put(cpf, usuario);
        salvarUsuarios();
        System.out.println("[Monitoramento] Usuário cadastrado: " + nome + " (CPF: " + cpf + ")");
    }

    public void atualizarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getCpf())) {
            usuarios.put(usuario.getCpf(), usuario);
            salvarUsuarios();
            System.out.println("[Monitoramento] Usuário " + usuario.getCpf() + " atualizado.");
        } else {
            System.err.println(
                    "[Monitoramento] Erro: Usuário com CPF " + usuario.getCpf() + " não encontrado para atualização.");
        }
    }

    public void removerUsuario(String cpf) {
        if (usuarios.remove(cpf) != null) {
            salvarUsuarios();
            System.out.println("[Monitoramento] Usuário " + cpf + " removido.");
        } else {
            System.err.println("[Monitoramento] Erro: Usuário com CPF " + cpf + " não encontrado para remoção.");
        }
    }

    public Usuario buscarUsuario(String cpf) {
        return usuarios.get(cpf);
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    public void vincularHidrometroUsuario(String cpf, String idHidrometro) {
        Usuario usuario = buscarUsuario(cpf);
        if (usuario != null) {
            usuario.adicionarHidrometro(idHidrometro);
            salvarUsuarios(); // Salva o vinculo
            System.out.println("[Monitoramento] Hidrômetro " + idHidrometro + " vinculado ao CPF " + cpf);
        } else {
            System.err.println("[Monitoramento] Erro: Usuário com CPF " + cpf + " não encontrado.");
        }
    }

    // --- SISTEMA DE ALERTAS (RF03) ---

    public void registrarNotificador(Notificador notificador) {
        this.notificadores.add(notificador);
    }

    private void notificarTodos(String mensagem, Usuario usuario) {
        for (Notificador notificador : notificadores) {
            notificador.enviarAlerta(mensagem, usuario);
        }
    }

    // --- MONITORAMENTO ---

    public void adicionarFonte(HidrometroSource fonte) {
        this.fontes.add(fonte);
        System.out.println(
                "[Monitoramento] Fonte adicionada: " + fonte.getIdentificador() + " (" + fonte.getTipoOrigem() + ")");
    }

    public void removerFonte(HidrometroSource fonte) {
        this.fontes.remove(fonte);
    }

    public void iniciar() {
        if (monitorando)
            return;
        monitorando = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(this::cicloMonitoramento, 2, 5, TimeUnit.SECONDS);
        System.out.println("[Monitoramento] Painel iniciado.");
    }

    public void parar() {
        monitorando = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        System.out.println("[Monitoramento] Painel parado.");
    }

    // --- LÓGICA INTELIGENTE (RF02) ---
    private void cicloMonitoramento() {
        if (!monitorando)
            return;

        for (HidrometroSource fonte : fontes) {
            try {
                BufferedImage imagem = fonte.capturarImagem();
                if (imagem != null) {
                    String valorLido = ocr.lerDigitos(imagem);

                    processarLeitura(fonte.getIdentificador(), valorLido);

                    System.out.println("[Monitoramento] Leitura de " + fonte.getIdentificador() + ": " + valorLido);
                }
            } catch (Exception e) {
                System.err.println("Erro ao monitorar " + fonte.getIdentificador() + ": " + e.getMessage());
            }
        }
    }

    private void processarLeitura(String idHidrometro, String valorLido) {
        try {
            double valor = Double.parseDouble(valorLido.replace(",", "."));

            // Buscar dono do hidrômetro
            Usuario dono = encontrarDonoDoHidrometro(idHidrometro);

            if (dono != null) {
                // Atualiza leitura em tempo real (RF04 - Novo)
                dono.setUltimaLeitura(valorLido);

                // Verificar regra de alerta
                if (valor > dono.getLimiteConsumoAlerta()) {
                    String msg = String.format("Consumo %.2f excede o limite de %.2f no hidrômetro %s",
                            valor, dono.getLimiteConsumoAlerta(), idHidrometro);
                    notificarTodos(msg, dono);
                }
            }

        } catch (NumberFormatException ignored) {
            // Leitura inválida, ignora
        }
    }

    private Usuario encontrarDonoDoHidrometro(String idHidrometro) {
        for (Usuario u : usuarios.values()) {
            if (u.getHidrometrosIds().contains(idHidrometro)) {
                return u;
            }
        }
        return null;
    }
}
