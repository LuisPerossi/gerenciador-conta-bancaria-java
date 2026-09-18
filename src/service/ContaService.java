package service;

import exception.CarregarContasException;
import exception.SalvarContasException;
import model.Conta;
import model.ContaCorrente;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class ContaService {
    public ArrayList<ContaCorrente> contas = new ArrayList<>();
    private final Path caminhoLeitura = Paths.get("contas.txt");
    private final Path caminhoSalvamento = Paths.get("contas_atualizadas.txt");

    public void lerContas() throws CarregarContasException {
        try {
            List<String> linhas = Files.readAllLines(this.caminhoLeitura);

            for (String linha : linhas) {
                String[] dados = linha.split(",");

                int numero = Integer.parseInt(dados[0]);
                String titular = dados[1].trim();
                double saldo = Double.parseDouble(dados[2]);

                this.contas.add(new ContaCorrente(numero, titular, saldo));
            }
        } catch (Exception e) {
            throw new CarregarContasException(e.getMessage());
        }
    }

    public void salvarContas() throws SalvarContasException {
        try {
            List<String> linhas = new ArrayList<>();

            for (Conta c : contas) {
                String dados = c.getNumero() + "," + c.getTitular() + "," + c.getSaldo();
                linhas.add(dados);
            }

            Files.write(caminhoSalvamento, linhas);
        } catch (Exception e) {
            throw new SalvarContasException(e.getMessage());
        }
    }

    public List<ContaCorrente> filtrarMaiorDezMil() {
        return this.contas
            .stream()
            .filter(c -> c.getSaldo() > 10000)
            .toList();
    }

    public double calcularTotal() {
        return this.contas
            .stream()
            .map(ContaCorrente::getSaldo)
            .reduce(0.0, Double::sum);
    }

    public Map<String, List<ContaCorrente>> agruparSaldos() {
        return contas.stream()
            .collect(groupingBy(c -> {
                if (c.getSaldo() <= 5000) return "(a) Até R$5.000";
                if (c.getSaldo() <= 10000) return "(b) De R$5.001 à R$10.000";
                return "(c) Acima de 10.000";
            }));
    }
}
