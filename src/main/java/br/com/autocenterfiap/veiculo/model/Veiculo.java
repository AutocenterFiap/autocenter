package br.com.autocenterfiap.veiculo.model;

import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.enums.TipoCombustivel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "veiculos")
@Schema(description = "Representa um Veículo da oficina automotiva")
@EntityListeners(AuditingEntityListener.class)
public class Veiculo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do veículo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Placa do veículo", example = "ABC1D23", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(unique = true)
    private String placa;

    @Schema(description = "Chassi do veículo", example = "9BWZZZ377VT004251")
    @Column(unique = true)
    private String chassi;

    @Schema(description = "RENAVAM do veículo", example = "12345678901")
    @Column(unique = true)
    private String renavam;

    @Schema(description = "Marca do veículo", example = "Toyota", requiredMode = Schema.RequiredMode.REQUIRED)
    private String marca;

    @Schema(description = "Modelo do veículo", example = "Corolla", requiredMode = Schema.RequiredMode.REQUIRED)
    private String modelo;

    @Schema(description = "Ano de fabricação do veículo", example = "2020")
    private Integer anoFabricacao;

    @Schema(description = "Ano do modelo do veículo", example = "2021")
    private Integer anoModelo;

    @Schema(description = "Cor do veículo", example = "Preto")
    private String cor;

    @Schema(description = "Quilometragem atual do veículo", example = "45000")
    private Long quilometragem;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Tipo de combustível do veículo", example = "FLEX", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipoCombustivel tipoCombustivel;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Categoria do veículo", example = "CARRO", requiredMode = Schema.RequiredMode.REQUIRED)
    private CategoriaVeiculo categoriaVeiculo;

    @Schema(description = "Data de cadastro do veículo", example = "2024-01-10T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    @CreatedDate
    private LocalDateTime dataCadastro;

    @Schema(description = "Data da última atualização do veículo", example = "2024-01-15T10:20:00", accessMode = Schema.AccessMode.READ_ONLY)
    @LastModifiedDate
    private LocalDateTime dataUltimaAtualizacao;

    public Veiculo(VeiculoDTO veiculoDTO) {
        setDados(veiculoDTO);
    }

    public void atualizarDados(VeiculoDTO veiculoDTO){
        setDados(veiculoDTO);
    }

    private void setDados(VeiculoDTO veiculoDTO){
        this.placa = veiculoDTO.placa();
        this.chassi = veiculoDTO.chassi();
        this.renavam = veiculoDTO.renavam();
        this.marca = veiculoDTO.marca();
        this.modelo = veiculoDTO.modelo();
        this.anoFabricacao = veiculoDTO.anoFabricacao();
        this.anoModelo = veiculoDTO.anoModelo();
        this.cor = veiculoDTO.cor();
        this.quilometragem = veiculoDTO.quilometragem();
        this.tipoCombustivel = veiculoDTO.tipoCombustivel();
        this.categoriaVeiculo = veiculoDTO.categoriaVeiculo();
    }
}
