package br.com.campanha.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "ErrorInfo", description = "Representa informações de erros que serão retornadas pela API")
public class ErrorInfo {

    @ApiModelProperty(value = "URL que foi chamada durante o erro", dataType = "string", required = true)
    public final String url;

    @ApiModelProperty(value = "Mensagem de exceção", dataType = "string", required = true)
    public final String ex;

    public ErrorInfo(String url, Exception ex) {
        this.url = url;
        this.ex = ex.getLocalizedMessage();
    }
}
