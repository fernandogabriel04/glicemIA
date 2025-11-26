package br.com.glicemia.model.exceptions;

public class ValorInvalidoException extends Exception {

    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }

    public ValorInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
