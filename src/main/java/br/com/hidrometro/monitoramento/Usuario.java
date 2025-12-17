package br.com.hidrometro.monitoramento;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nome;
    private String cpf;
    private double limiteConsumoAlerta;
    private final List<String> hidrometrosIds;
    private String ultimaLeitura = "N/A";

    public Usuario() {
        this.hidrometrosIds = new ArrayList<>();
    }

    public Usuario(String nome, String cpf, double limiteConsumoAlerta) {
        this.nome = nome;
        this.cpf = cpf;
        this.limiteConsumoAlerta = limiteConsumoAlerta;
        this.hidrometrosIds = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public double getLimiteConsumoAlerta() {
        return limiteConsumoAlerta;
    }

    public void setLimiteConsumoAlerta(double limiteConsumoAlerta) {
        this.limiteConsumoAlerta = limiteConsumoAlerta;
    }

    public List<String> getHidrometrosIds() {
        return hidrometrosIds;
    }

    public void adicionarHidrometro(String id) {
        if (!this.hidrometrosIds.contains(id)) {
            this.hidrometrosIds.add(id);
        }
    }

    public String getUltimaLeitura() {
        return ultimaLeitura;
    }

    public void setUltimaLeitura(String ultimaLeitura) {
        this.ultimaLeitura = ultimaLeitura;
    }
}
