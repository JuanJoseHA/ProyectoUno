package tspw.proyuno.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import tspw.proyuno.modelo.Perfil;
import tspw.proyuno.repository.PerfilRepository;

@Component
public class PerfilConverter implements Converter<String, Perfil> {

  private final PerfilRepository repo;

  public PerfilConverter(PerfilRepository repo) {
    this.repo = repo;
  }

  @Override
  public Perfil convert(String source) {
    if (source == null || source.isBlank()) return null;
    Integer id = Integer.valueOf(source);
    return repo.findById(id).orElse(null);
  }
}
