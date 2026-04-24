package br.com.autocenterfiap.veiculo.validator;

import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.enums.TipoOperacao;
import br.com.autocenterfiap.veiculo.model.Veiculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoValidationContext {
    private Long veiculoId;
    private VeiculoDTO veiculoDTO;
    private TipoOperacao operation;

    public VeiculoValidationContext(VeiculoDTO veiculoDTO, TipoOperacao operation) {
        this.veiculoDTO = veiculoDTO;
        this.operation = operation;
    }

    public boolean isUpdate(){
        return operation.equals(TipoOperacao.UPDATE);
    }

}
