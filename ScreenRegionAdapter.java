package br.com.hidrometro.monitoramento;

import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class ScreenRegionAdapter implements HidrometroSource {

    private final Rectangle areaCaptura;
    private final String identificador;
    private final String origem;
    private Robot robot;

    public ScreenRegionAdapter(String identificador, String origem, int x, int y, int width, int height) {
        this.identificador = identificador;
        this.origem = origem;
        this.areaCaptura = new Rectangle(x, y, width, height);
        try {
            this.robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BufferedImage capturarImagem() {
        if (robot == null)
            return null;
        // Captura a tela na região definida
        return robot.createScreenCapture(areaCaptura);
    }

    @Override
    public String getIdentificador() {
        return identificador;
    }

    @Override
    public String getTipoOrigem() {
        return origem;
    }

    @Override
    public double getValorReal() {
        return -1.0; // Não temos acesso ao valor real de app externo
    }
}
