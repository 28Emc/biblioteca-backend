package com.biblioteca.backend.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_empresas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "ID Autogenerado")
	private Long id;

	@Column(length = 100, name = "razon_social", unique = true)
	@ApiModelProperty(notes = "Razón social de la empresa", required = true, example = "Pepito S.A.C.")
	private String razonSocial;

	@Column(name = "RUC", length = 11, unique = true)
	@ApiModelProperty(notes = "RUC de la empresa", required = true, example = "10431143201")
	private String ruc;

	@Column(length = 255, nullable = true)
	@ApiModelProperty(notes = "Dirección de la empresa", required = true, example = "Av. Arequipa 456")
	private String direccion;

	@Column(name = "estado", nullable = false)
	@ApiModelProperty(notes = "Estado de la empresa", required = true, example = "true")
	private boolean isActivo;

	// EMPRESA(1):LOCAL(*)
	@JsonIgnore()
	@OneToMany(mappedBy = "empresa")
	private List<Local> locales;

}