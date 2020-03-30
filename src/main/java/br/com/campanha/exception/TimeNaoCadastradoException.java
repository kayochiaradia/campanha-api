package br.com.campanha.exception;

public class TimeNaoCadastradoException extends RuntimeException {
    public TimeNaoCadastradoException() {
        super("Não existe nenhum time cadastrado para criar a campanha");
    }
}
