package br.com.hidrometro.monitoramento;

import java.awt.image.BufferedImage;

/**
 * Interface Target do Adapter Pattern.
 * Define o contrato para qualquer fonte de hidrometro (interno ou externo).
 */
public interface HidrometroSource {

    /**
     * Captura a imagem atual do hidrômetro para OCR.
     * 
     * @return BufferedImage contendo a área dos dígitos.
     */
    BufferedImage capturarImagem();

    /**
     * Retorna o identificador único deste hidrômetro.
     */
    String getIdentificador();

    /**
     * Retorna a origem do hidrômetro (ex: "SimuladorLocal", "PC_do_Joao", etc).
     */
    String getTipoOrigem();

    /**
     * Opcional: Retorna o valor real se disponível (apenas para simulação interna),
     * ou -1 se não disponível.
     */
    double getValorReal();
}
