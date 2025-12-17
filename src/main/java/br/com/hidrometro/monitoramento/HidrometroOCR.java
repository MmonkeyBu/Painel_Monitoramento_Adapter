package br.com.hidrometro.monitoramento;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class HidrometroOCR {

    private final ITesseract tesseract;

    public HidrometroOCR() {
        this.tesseract = new Tesseract();
        // Define o caminho para o diretório tessdata
        tesseract.setDatapath("./tessdata");

        // Configura para PSM 7 (Single Text Line) -> Melhora leitura de linha unica de
        // digitos
        tesseract.setPageSegMode(7);

        // Whitelist apenas números
        tesseract.setTessVariable("tessedit_char_whitelist", "0123456789");
    }

    /**
     * Realiza OCR na imagem fornecida, focando na região dos dígitos.
     * 
     * @param imagem Arquivo da imagem do hidrômetro
     * @return String contendo os dígitos reconhecidos
     */
    public String lerDigitos(File imagem) {
        try {
            // Região de interesse baseada no Display.java
            // xBase = 346, yDigitos = 285, 6 digitos, espaco 34
            // Altura da fonte 36.
            // Ajustando retângulo de captura com alguma margem
            Rectangle rect = new Rectangle(340, 245, 230, 50);

            return tesseract.doOCR(imagem, rect).trim();
        } catch (TesseractException e) {
            System.err.println("Erro ao realizar OCR: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Realiza OCR em um BufferedImage.
     * Se a imagem for pequena (possivelmente ja recortada), faz OCR nela inteira.
     */
    public String lerDigitos(BufferedImage imagem) {
        try {
            // Verifica se a imagem é grande o suficiente para precisar de crop
            // Se for pequena (ex: veio de um recorte de tela manual), processa inteira
            if (imagem.getWidth() < 400 || imagem.getHeight() < 100) {
                return tesseract.doOCR(imagem).trim();
            }

            Rectangle rect = new Rectangle(340, 245, 230, 50);
            return tesseract.doOCR(imagem, rect).trim();
        } catch (TesseractException e) {
            System.err.println("Erro ao realizar OCR: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
