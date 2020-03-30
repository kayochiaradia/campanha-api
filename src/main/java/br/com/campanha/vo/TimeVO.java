package br.com.campanha.vo;

import br.com.campanha.model.Time;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ApiModel(value = "TimeVO", description = "Representa os dados do time que devem ser recebidos e retornados pela API Rest do time")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TimeVO extends RepresentationModel<TimeVO> implements Serializable {

    @Size(min = 5, max = 100, message = "Nome tem capacidade de 5 a 100 caracteres.")
    @NotNull(message = "Nome do time é obrigatório!")
    @ApiModelProperty(value = "Nome do time", dataType = "string", required = true)
    private String nome;

    @JsonIgnore
    private Integer chave;

    public TimeVO(Time time) {
        this.nome = time.getNome();
        this.chave = time.getId();
    }
}
