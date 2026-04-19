package br.com.autocenterfiap.security.repository;

import br.com.autocenterfiap.security.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNome(String nome);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.perfis WHERE u.nome = :nome")
    Optional<Usuario> findByNomeWithPerfis(@Param("nome") String nome);
}
