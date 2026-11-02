package com.cps.disenodepruebas.taller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cps.disenodepruebas.taller.dto.Precio;
import com.cps.disenodepruebas.taller.dto.TotalEntrada;
import com.cps.disenodepruebas.taller.service.EnvioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/envio")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @PostMapping
    public ResponseEntity<Precio> obtenerPrecioPagar(
            @Valid @RequestBody TotalEntrada dto) {

        Precio precio = envioService.obtenerPrecio(dto);

        return ResponseEntity.ok(precio);
    }
}
