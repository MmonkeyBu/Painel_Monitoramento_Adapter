package br.com.hidrometro.monitoramento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DashboardAdmin extends JFrame {

    private final PainelMonitoramento painel;
    private final JTextArea areaLog;
    private DefaultTableModel modelUsuarios;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private JTable tabela; // Promovido para campo
    private boolean blinkState = false;

    public DashboardAdmin(PainelMonitoramento painel) {
        super("Painel Administrativo - Hidrômetro");
        this.painel = painel;
        this.areaLog = new JTextArea();
        this.areaLog.setEditable(false);

        configurarJanela();
        inicializarComponentes();

        // Registrar o notificador da GUI
        this.painel.registrarNotificador(new GuiNotificador());
    }
    // ... (keeps logic until table creation)

    // In createPanelGestaoUsuarios (around line 140 in original file, need to find
    // exact spot)
    // Instead of JTable tabela = new JTable(modelUsuarios); use tabela = new
    // JTable(modelUsuarios);

    private void configurarJanela() {
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha tudo ao fechar o admin
        setLocation(50, 50); // Longe do simulador (normalmente centralizado)
    }

    private void inicializarComponentes() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // ABA 1: Gestão de Usuários
        JPanel panelUsuarios = criarPanelGestaoUsuarios();
        tabbedPane.addTab("Gestão de Usuários", panelUsuarios);

        // ABA 2: Monitoramento
        JPanel panelMonitoramento = criarPanelMonitoramento();
        tabbedPane.addTab("Monitoramento", panelMonitoramento);

        add(tabbedPane);
    }

    private JPanel criarPanelGestaoUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));

        JTextField txtNome = new JTextField();
        JTextField txtCpf = new JTextField();
        JTextField txtLimite = new JTextField();

        // Botoes CRUD
        JButton btnSalvar = new JButton("Salvar / Cadastrar");
        JButton btnExcluir = new JButton("Excluir Selecionado");
        JButton btnLimpar = new JButton("Limpar Formulario");

        formPanel.add(new JLabel("Nome:"));
        formPanel.add(txtNome);
        formPanel.add(new JLabel("CPF:"));
        formPanel.add(txtCpf);
        formPanel.add(new JLabel("Limite Alerta:"));
        formPanel.add(txtLimite);
        formPanel.add(new JLabel("Ações:"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.add(btnSalvar);
        btnPanel.add(new JLabel(" "));
        btnPanel.add(btnLimpar);
        formPanel.add(btnPanel);

        // Acao Salvar (Cadastrar ou Atualizar)
        btnSalvar.addActionListener(e -> {
            try {
                String nome = txtNome.getText();
                String cpf = txtCpf.getText();
                double limite = Double.parseDouble(txtLimite.getText());

                // Se ja existe, atualiza, senao cria
                Usuario existente = painel.buscarUsuario(cpf);
                if (existente != null) {
                    existente.setNome(nome);
                    existente.setLimiteConsumoAlerta(limite);
                    painel.atualizarUsuario(existente);
                    log("Usuario atualizado: " + nome);
                } else {
                    painel.cadastrarUsuario(nome, cpf, limite);
                    log("Usuario cadastrado: " + nome);
                }
                atualizarTabelaUsuarios();
                // Nao limpa para permitir vincular logo em seguida se quiser
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        // Acao Limpar
        btnLimpar.addActionListener(e -> {
            txtNome.setText("");
            txtCpf.setText("");
            txtLimite.setText("");
            txtCpf.setEditable(true);
        });

        // Vinculo
        JPanel vinculoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        vinculoPanel.setBorder(BorderFactory.createTitledBorder("Vincular Hidrômetro"));
        JTextField txtCpfVinculo = new JTextField();
        JTextField txtIdHidro = new JTextField();
        JButton btnVincular = new JButton("Vincular");

        vinculoPanel.add(new JLabel("CPF Usuário:"));
        vinculoPanel.add(txtCpfVinculo);
        vinculoPanel.add(new JLabel("ID Hidrômetro:"));
        vinculoPanel.add(txtIdHidro);
        vinculoPanel.add(new JLabel(""));
        vinculoPanel.add(btnVincular);

        btnVincular.addActionListener(e -> {
            painel.vincularHidrometroUsuario(txtCpfVinculo.getText(), txtIdHidro.getText());
            log("Vinculo solicitado: " + txtCpfVinculo.getText() + " <-> " + txtIdHidro.getText());
            atualizarTabelaUsuarios();
        });

        // Tabela
        String[] colunas = { "Nome", "CPF", "Limite", "Hidro ID", "Última Leitura" };
        modelUsuarios = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modelUsuarios);
        JScrollPane scrollTabela = new JScrollPane(tabela);

        // Renderer para Piscada Vermelha
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                try {
                    String limiteStr = table.getValueAt(row, 2).toString(); // Limite
                    String leituraStr = table.getValueAt(row, 4).toString(); // Leitura

                    double limite = Double.parseDouble(limiteStr);
                    double leitura = leituraStr.equals("N/A") ? 0.0 : Double.parseDouble(leituraStr);

                    if (leitura > limite) {
                        if (blinkState) {
                            c.setBackground(Color.RED);
                            c.setForeground(Color.WHITE);
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.RED);
                        }
                    } else {
                        // Padrao
                        if (isSelected) {
                            c.setBackground(table.getSelectionBackground());
                            c.setForeground(table.getSelectionForeground());
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    }
                } catch (Exception ignore) {
                }

                return c;
            }
        });

        // Listener de Selecao para Preencher Formulario
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                int row = tabela.getSelectedRow();
                txtNome.setText(modelUsuarios.getValueAt(row, 0).toString());
                txtCpf.setText(modelUsuarios.getValueAt(row, 1).toString());
                txtLimite.setText(modelUsuarios.getValueAt(row, 2).toString());

                txtCpfVinculo.setText(modelUsuarios.getValueAt(row, 1).toString()); // Facilita vinculo

                // Bloqueia CPF na edicao para garantir integridade simplificada
                // txtCpf.setEditable(false);
            }
        });

        JButton btnAtualizarLista = new JButton("Atualizar Lista (Manual)");
        btnAtualizarLista.addActionListener(e -> atualizarTabelaUsuarios());

        // Acao Excluir
        btnExcluir.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row != -1) {
                String cpf = (String) modelUsuarios.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja excluir o usuario " + cpf + "?");
                if (confirm == JOptionPane.YES_OPTION) {
                    painel.removerUsuario(cpf);
                    atualizarTabelaUsuarios();
                    log("Usuario removido: " + cpf);
                    JOptionPane.showMessageDialog(this, "Perfil " + cpf + " retirado com sucesso!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um usuario na tabela.");
            }
        });

        // Timer para refresh automatico (Real-Time) + Blink Logic
        Timer timer = new Timer(1000, e -> {
            blinkState = !blinkState; // Alterna estado
            atualizarTabelaUsuarios(); // Refresh dados
            // Force repaint se dados nao mudarem mas cor precisar mudar
            tabela.repaint();
        });
        timer.start();

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.NORTH);
        topContainer.add(vinculoPanel, BorderLayout.SOUTH);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(btnExcluir, BorderLayout.WEST); // Botao excluir
        bottomContainer.add(btnAtualizarLista, BorderLayout.EAST);

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollTabela, BorderLayout.CENTER);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        // Hook para parar timer ao fechar
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                timer.stop();
            }
        });

        return panel;
    }

    private JPanel criarPanelMonitoramento() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log de Operações e Alertas"));

        JPanel botoesPanel = new JPanel(new FlowLayout());
        JButton btnIniciar = new JButton("Iniciar Monitoramento");
        JButton btnParar = new JButton("Parar Monitoramento");

        btnIniciar.addActionListener(e -> {
            painel.iniciar();
            log("Solicitado início do monitoramento.");
        });

        btnParar.addActionListener(e -> {
            painel.parar();
            log("Solicitada parada do monitoramento.");
        });

        botoesPanel.add(btnIniciar);
        botoesPanel.add(btnParar);

        panel.add(scrollLog, BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void atualizarTabelaUsuarios() {
        // Preserva seleção se tiver
        int selectedRow = -1;
        if (tabela != null) {
            selectedRow = tabela.getSelectedRow();
        }

        modelUsuarios.setRowCount(0);
        List<Usuario> usuarios = painel.getUsuarios();
        for (Usuario u : usuarios) {
            modelUsuarios.addRow(new Object[] {
                    u.getNome(),
                    u.getCpf(),
                    u.getLimiteConsumoAlerta(),
                    u.getHidrometrosIds().toString(),
                    u.getUltimaLeitura()
            });
        }

        // Restaura seleção se ainda for válida
        if (selectedRow != -1 && selectedRow < modelUsuarios.getRowCount() && tabela != null) {
            tabela.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            String time = dateFormat.format(new Date());
            areaLog.append("[" + time + "] " + msg + "\n");
        });
    }

    // Inner class para interceptar alertas RF03
    private class GuiNotificador implements Notificador {
        @Override
        public void enviarAlerta(String mensagem, Usuario usuario) {
            SwingUtilities.invokeLater(() -> {
                String time = dateFormat.format(new Date());
                String logMsg = String.format("\n>>> ALERTA [%s] <<<\nUsuario: %s\n%s\n", time, usuario.getNome(),
                        mensagem);
                areaLog.append(logMsg);
                // Auto scroll
                areaLog.setCaretPosition(areaLog.getDocument().getLength());
            });
        }
    }
}
