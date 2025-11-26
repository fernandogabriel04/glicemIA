package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;
import java.time.LocalDate;
import java.time.Period;

public class Paciente {

    private Long idPaciente;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String telefone;
    private LocalDate dataCadastro;

    public Paciente(String nome, String cpf, LocalDate dataNascimento)
            throws ValorInvalidoException {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.dataCadastro = LocalDate.now();
        validar();
    }

    private void validar() throws ValorInvalidoException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValorInvalidoException("Nome do paciente é obrigatório");
        }

        if (cpf == null || !validarCPF(cpf)) {
            throw new ValorInvalidoException("CPF inválido: " + cpf);
        }

        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            throw new ValorInvalidoException("Data de nascimento inválida");
        }
    }

    private boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        return cpfLimpo.length() == 11;
    }

    public int getIdade() {
        if (dataNascimento == null) return 0;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws ValorInvalidoException {
        this.nome = nome;
        validar();
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) throws ValorInvalidoException {
        this.dataNascimento = dataNascimento;
        validar();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    @Override
    public String toString() {
        return String.format("Paciente: %s | CPF: %s | Idade: %d anos",
            nome,
            cpf,
            getIdade()
        );
    }
}
