package com.biblioteca.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tb_empresas")
@Data
public class Empresa {
    
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// EMPRESA(1):LOCAL(*)
	/*@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Local> locales;*/

	@Column(length = 100, name = "razon_social", unique = true)
	private String razonSocial;

	@Column(name = "RUC", length = 11, unique = true)
	private String ruc;

	@Column(length = 100, nullable = true)
	private String direccion;

	@Column(name = "estado", nullable = false)
	private boolean isActivo;

}