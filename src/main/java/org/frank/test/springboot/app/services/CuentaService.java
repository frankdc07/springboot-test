package org.frank.test.springboot.app.services;

import org.frank.test.springboot.app.models.Cuenta;

import java.math.BigDecimal;

public interface CuentaService {
    Cuenta findById(Long id);
    int revisarTotalransferencias(Long bancoId);
    BigDecimal revisarSaldo(Long cuentaId);
    void transferir(Long cuentaOrigen, Long cuentaDestino, BigDecimal monto, Long bancoId);
}
