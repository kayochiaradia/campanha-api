package br.com.campanha.vo;

import br.com.campanha.model.Campanha;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
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
import java.time.LocalDate;

@ApiModel(value = "CampanhaVO", description = "Representa os dados da campanha que devem ser recebidos e retornados pela API Rest de campanha")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CampanhaVO extends RepresentationModel<CampanhaVO> implements Serializable {

    @Size(min = 5, max = 100, message = "Nome tem capacidade de 5 a 100 caracteres.")
    @NotNull(message = "Nome da campanha é obrigatório!")
    @ApiModelProperty(value = "Nome da campanha", dataType = "string", required = true)
    private String nome;

    @NotNull(message = "A identificação do time é obrigatório!")
    @ApiModelProperty(value = "Id do time do coração", dataType = "integer", required = true)
    private Integer timeCoracaoId;

    @NotNull(message = "O inicio da vigência é obrigatório!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @ApiModelProperty(value = "Data de inicio de vigência", dataType = "date", required = true)
    private LocalDate inicioVigencia;

    @NotNull(message = "O fim vigência é obrigatório!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @ApiModelProperty(value = "Data de fim de vigência", dataType = "date", required = true)
    private LocalDate fimVigencia;

    @JsonIgnore
    private Integer chave;

    public CampanhaVO(Campanha campanha) {
        this.nome = campanha.getNome();
        this.timeCoracaoId = campanha.getTime().getId();
        this.inicioVigencia = campanha.getInicioVigencia();
        this.fimVigencia = campanha.getFimVigencia();
        this.chave = campanha.getId();
    }
}