package br.com.hidrometro.monitoramento;

public class LogNotificador implements Notificador {
    @Override
    public void enviarAlerta(String mensagem, Usuario usuario) {
        System.out.println("========================================");
        System.out.println("[ALERTA] Notificação para: " + usuario.getNome() + " (CPF: " + usuario.getCpf() + ")");
        System.out.println("MENSAGEM: " + mensagem);
        System.out.println("========================================");
    }
}
