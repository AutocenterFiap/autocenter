package br.com.autocenterfiap.cliente.model;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCliente tipoCliente;

    @Column(nullable = false, unique = true, length = 14)
    private String documento;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Embedded
    private Endereco endereco;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Schema(description = "Data de cadastro do cliente", example = "2024-01-10T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    @CreatedDate
    private LocalDateTime dataCriacao;

    @Schema(description = "Data da última atualização do cliente", example = "2024-01-15T10:20:00", accessMode = Schema.AccessMode.READ_ONLY)
    @LastModifiedDate
    private LocalDateTime dataUltimaAtualizacao;
}
