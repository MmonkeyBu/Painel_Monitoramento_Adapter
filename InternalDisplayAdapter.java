package br.com.hidrometro.monitoramento;

import br.com.hidrometro.core.ui.Display;
import br.com.hidrometro.core.components.Hidrometro;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class InternalDisplayAdapter implements HidrometroSource {

    private final Hidrometro hidrometro;
    private final String origem;

    public InternalDisplayAdapter(Hidrometro hidrometro, String origem) {
        this.hidrometro = hidrometro;
        this.origem = origem;
    }

    @Override
    public BufferedImage capturarImagem() {
        Display display = hidrometro.getDisplay();
        if (display.getWidth() <= 0 || display.getHeight() <= 0) {
            return null; // Ainda não renderizado
        }

        BufferedImage imagem = new BufferedImage(
                display.getWidth(),
                display.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = imagem.createGraphics();
        display.paint(g2d); // Paint direto do componente Swing
        g2d.dispose();

        return imagem;
    }

    @Override
    public String getIdentificador() {
        // Como o hidrometro interno não tem ID publico facil as vezes, vamos usar hash
        // ou passar no construtor.
        // O Simulador.java tem o ID. Vamos assumir que passamos no construtor ou
        // pegamos de algum lugar.
        // Simplificação: HashCode
        return "Internal_" + hidrometro.hashCode();
    }

    @Override
    public String getTipoOrigem() {
        return origem;
    }

    @Override
    public double getValorReal() {
        return hidrometro.getValorPassagemAguaTotalAtual();
    }
}
