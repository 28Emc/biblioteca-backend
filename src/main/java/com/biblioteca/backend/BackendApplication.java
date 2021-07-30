package com.biblioteca.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//@ComponentScan(basePackages = "com.biblioteca.backend.*")
public class BackendApplication /*implements CommandLineRunner*/{

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    /*@Override
    public void run(String... args) throws Exception {
        // CREO CONTRASEÑAS PARA USUARIOS SOLO A MODO DE PRUEBA
        String passwordSysadmin = "sysadmin";
        String passwordAdminLocal3 = "adminLocal3";
        String passwordEmpleadoLocal3 = "empleadoLocal3";
        String passwordEmpleadaLocal3 = "empleadaLocal3";
        String passwordUsuario = "usuario";
        String passwordUsuaria = "usuaria";
        String edmech = "Sinfonico29";
        for (int i = 0; i < 1; i++) {
            String bcryptPasswordSysadmin = new BCryptPasswordEncoder().encode(passwordSysadmin);
            String bcryptPasswordAdmin = new BCryptPasswordEncoder().encode(passwordAdminLocal3);
            String bcryptPasswordEmpleado = new BCryptPasswordEncoder().encode(passwordEmpleadoLocal3);
            String bcryptPasswordEmpleada = new BCryptPasswordEncoder().encode(passwordEmpleadaLocal3);
            String bcryptPasswordUsuario = new BCryptPasswordEncoder().encode(passwordUsuario);
            String bcryptPasswordUsuaria = new BCryptPasswordEncoder().encode(passwordUsuaria);
            String bcryptPasswordEdmech = new BCryptPasswordEncoder().encode(edmech);
            System.out.println("PASSWORD SYSADMIN: ".concat(bcryptPasswordSysadmin).concat(" (sysadmin)"));
            System.out.println("PASSWORD ADMIN: ".concat(bcryptPasswordAdmin).concat(" (adminLocal3)"));
            System.out.println("PASSWORD EMPLEADO: ".concat(bcryptPasswordEmpleado).concat(" (empleadoLocal3)"));
            System.out.println("PASSWORD EMPLEADA: ".concat(bcryptPasswordEmpleada).concat(" (empleadaLocal3)"));
            System.out.println("PASSWORD USUARIO: ".concat(bcryptPasswordUsuario).concat(" (usuario)"));
            System.out.println("PASSWORD USUARIA: ".concat(bcryptPasswordUsuaria).concat(" (usuaria)"));
            System.out.println("PASSWORD EDMECH: ".concat(bcryptPasswordEdmech).concat(" (Sinfonico29)"));
			SALIDA
			PASSWORD SYSADMIN: $2a$10$OOpPymbNoOD6rvxtpaeHH.QzXNIrAwYrRyiveimAPf3VexZd7.liq (sysadmin)
            PASSWORD ADMIN: $2a$10$amSwPXBMTnsJUt5B3T/uyerytpZw2FcyJAjVrVH0SMoMGGiLefFrq (adminLocal3)
            PASSWORD EMPLEADO: $2a$10$l4YL98gcsO6MGazpsTe.ou5VdFkZzqq26siGrhkGJTbUo/P7L23vm (empleadoLocal3)
            PASSWORD EMPLEADA: $2a$10$1gywxk8qok94jiaWYAOUROXzYOzcGir8BVswy0dOSJP4J4FWMYF.u (empleadaLocal3)
            PASSWORD USUARIO: $2a$10$L5qe/cWzeetBuvWEzQXWc.OO3PlbbLf3FCte.btNsRNZclWUlWE.6 (usuario)
            PASSWORD USUARIA: $2a$10$tf116QfW2P8iSMJ0dQd4zeW7xyvInpOacDJ1iYPOvmUGzwbk9mnB6 (usuaria)
            PASSWORD EDMECH: $2a$10$vk1zyZ0gr6hqQFZHlrRwMeC9fD4YbdyXY4MMk9DQ1hy2WOZcLTN9q (Sinfonico29)
        }
    }*/
}
