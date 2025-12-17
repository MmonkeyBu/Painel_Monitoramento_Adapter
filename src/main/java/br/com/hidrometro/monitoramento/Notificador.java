package br.com.hidrometro.monitoramento;

public interface Notificador {
    void enviarAlerta(String mensagem, Usuario usuario);
}
