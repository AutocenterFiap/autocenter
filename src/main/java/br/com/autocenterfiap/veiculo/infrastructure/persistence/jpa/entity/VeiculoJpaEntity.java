package br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "veiculos")
@EntityListeners(AuditingEntityListener.class)
public class VeiculoJpaEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String placa;

    @Column(unique = true)
    private String chassi;

    @Column(unique = true)
    private String renavam;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    private Integer anoFabricacao;

    private Integer anoModelo;

    private String cor;

    private Long quilometragem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCombustivel tipoCombustivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaVeiculo categoriaVeiculo;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    @LastModifiedDate
    private LocalDateTime dataUltimaAtualizacao;
}
