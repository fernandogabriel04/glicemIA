package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;

public class PesoCorporal extends SinalVital {

    private double peso;
    private double altura;
    private Double imc;

    public PesoCorporal(Long idPaciente, double peso, double altura)
            throws ValorInvalidoException {
        super(idPaciente, "kg");
        this.peso = peso;
        this.altura = altura;
        validar();
        calcularIMC();
    }

    @Override
    protected void validar() throws ValorInvalidoException {
        if (peso <= 0) {
            throw new ValorInvalidoException(
                "Peso deve ser um valor positivo. Valor fornecido: " + peso
            );
        }

        if (peso < 20 || peso > 300) {
            throw new ValorInvalidoException(
                "Peso fora da faixa esperada (20-300 kg). Verifique a medição."
            );
        }

        if (altura <= 0) {
            throw new ValorInvalidoException(
                "Altura deve ser um valor positivo. Valor fornecido: " + altura
            );
        }

        if (altura < 0.5 || altura > 2.5) {
            throw new ValorInvalidoException(
                "Altura fora da faixa esperada (0.5-2.5 m). Verifique a medição."
            );
        }
    }

    private void calcularIMC() {
        if (altura > 0) {
            this.imc = peso / (altura * altura);
        }
    }

    public String getClassificacaoIMC() {
        if (imc == null) return "Não disponível";

        if (imc < 18.5) return "Abaixo do peso";
        if (imc < 25) return "Peso normal";
        if (imc < 30) return "Sobrepeso";
        if (imc < 35) return "Obesidade Grau I";
        if (imc < 40) return "Obesidade Grau II";
        return "Obesidade Grau III";
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) throws ValorInvalidoException {
        this.peso = peso;
        validar();
        calcularIMC();
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) throws ValorInvalidoException {
        this.altura = altura;
        validar();
        calcularIMC();
    }

    public Double getImc() {
        return imc;
    }

    @Override
    public String getDescricao() {
        return String.format("Peso: %.1f %s | Altura: %.2f m | IMC: %.1f (%s)",
            peso,
            getUnidadeMedida(),
            altura,
            imc != null ? imc : 0.0,
            getClassificacaoIMC()
        );
    }
}
