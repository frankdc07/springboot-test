package org.frank.test.springboot.app.services;

import org.frank.test.springboot.app.models.Banco;
import org.frank.test.springboot.app.models.Cuenta;
import org.frank.test.springboot.app.repositories.BancoRepository;
import org.frank.test.springboot.app.repositories.CuentaRepository;

import java.math.BigDecimal;

public class CuentaServiceImpl implements CuentaService {

    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id);
    }

    @Override
    public int revisarTotalransferencias(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId);
        return banco.getTotalTransferencias();
    }

    @Override
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId);
        return cuenta.getSaldo();
    }

    @Override
    public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId);
        Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen);
        Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino);
        cuentaOrigen.debito(monto);
        cuentaRepository.update(cuentaOrigen);
        cuentaDestino.credito(monto);
        cuentaRepository.update(cuentaDestino);
        int transferencias = banco.getTotalTransferencias();
        banco.setTotalTransferencias(++transferencias);
        bancoRepository.update(banco);
    }
}
