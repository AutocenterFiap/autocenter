package br.com.autocenterfiap.security.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByNome(String nome);

    @Query("SELECT u FROM UsuarioJpaEntity u JOIN FETCH u.perfis WHERE u.nome = :nome")
    Optional<UsuarioJpaEntity> findByNomeWithPerfis(@Param("nome") String nome);
}
