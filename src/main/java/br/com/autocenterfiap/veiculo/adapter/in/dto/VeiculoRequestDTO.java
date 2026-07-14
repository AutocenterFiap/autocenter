package br.com.autocenterfiap.veiculo.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de entrada para cadastro/atualização de veículo")
public class VeiculoRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Placa é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$", message = "Placa inválida")
    @Schema(description = "Placa do veículo", example = "ABC1D23", requiredMode = Schema.RequiredMode.REQUIRED)
    private String placa;

    @Schema(description = "Chassi do veículo", example = "9BWZZZ377VT004251")
    private String chassi;

    @Schema(description = "RENAVAM do veículo", example = "12345678901")
    private String renavam;

    @NotBlank(message = "Especificar a Marca do veículo é Obrigatório!")
    @Schema(description = "Marca do veículo", example = "Toyota", requiredMode = Schema.RequiredMode.REQUIRED)
    private String marca;

    @NotBlank(message = "Especificar o Modelo do veículo é Obrigatório!")
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

    @NotNull(message = "Tipo de Combustível Inválido")
    @Schema(description = "Tipo de combustível do veículo", example = "FLEX", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipoCombustivel tipoCombustivel;

    @NotNull(message = "Categoria Inválida")
    @Schema(description = "Categoria do veículo", example = "CARRO", requiredMode = Schema.RequiredMode.REQUIRED)
    private CategoriaVeiculo categoriaVeiculo;
}
