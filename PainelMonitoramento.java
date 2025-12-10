package br.com.hidrometro.monitoramento;

import br.com.hidrometro.monitoramento.ocr.HidrometroOCR;
import br.com.hidrometro.monitoramento.db.HidrometroRepository;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PainelMonitoramento {

    private final List<HidrometroSource> fontes = new ArrayList<>();
    private final HidrometroOCR ocr;
    private final HidrometroRepository repository;
    private ScheduledExecutorService scheduler;
    private boolean running = false;

    public PainelMonitoramento() {
        this.ocr = new HidrometroOCR();
        this.repository = new HidrometroRepository();
    }

    public void adicionarFonte(HidrometroSource fonte) {
        this.fontes.add(fonte);
        System.out.println(
                "[Monitoramento] Fonte adicionada: " + fonte.getIdentificador() + " (" + fonte.getTipoOrigem() + ")");
    }

    public void removerFonte(HidrometroSource fonte) {
        this.fontes.remove(fonte);
    }

    public void iniciar() {
        if (running)
            return;
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(this::cicloMonitoramento, 2, 5, TimeUnit.SECONDS);
        System.out.println("[Monitoramento] Painel iniciado.");
    }

    public void parar() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        System.out.println("[Monitoramento] Painel parado.");
    }

    private void cicloMonitoramento() {
        if (!running)
            return;

        for (HidrometroSource fonte : fontes) {
            try {
                BufferedImage imagem = fonte.capturarImagem();
                if (imagem != null) {
                    String valorLido = ocr.lerDigitos(imagem);

                    // Salva no banco com origem
                    repository.salvarLeitura(
                            fonte.getIdentificador(),
                            fonte.getTipoOrigem(),
                            valorLido,
                            fonte.getValorReal() // Será -1 para externos
                    );

                    // Debug
                    // System.out.println("Lido de " + fonte.getIdentificador() + ": " + valorLido);
                }
            } catch (Exception e) {
                System.err.println("Erro ao monitorar " + fonte.getIdentificador() + ": " + e.getMessage());
            }
        }
    }
}
