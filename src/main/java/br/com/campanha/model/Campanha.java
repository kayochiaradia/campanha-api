package br.com.campanha.model;

import br.com.campanha.vo.CampanhaVO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CAMPANHA")
public class Campanha implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "inicioVigencia", nullable = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate inicioVigencia;

    @Column(name = "fimVigencia", nullable = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate fimVigencia;

    @ManyToOne
    @JoinColumn(name = "TIME_ID")
    private Time time;

    public Campanha(String nome, LocalDate inicioVigencia, LocalDate fimVigencia, Time time) {
        this.nome = nome;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
        this.time = time;
    }

    public void atualizarDados(CampanhaVO campanhaVO) {
        setNome(campanhaVO.getNome());
        setInicioVigencia(campanhaVO.getInicioVigencia());
        setFimVigencia(campanhaVO.getFimVigencia());
    }
}
