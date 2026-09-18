package view;

import exception.SalvarContasException;
import model.ContaCorrente;
import service.ContaService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

public class ContaGUI extends JFrame {
    private DefaultListModel<ContaCorrente> listModel = new DefaultListModel<>();
    private JList<ContaCorrente> contaList = new JList<>(listModel);
    private ContaCorrente contaSelecionada;
    private final ContaService cs;

    public ContaGUI(ContaService cs) {
        //Configurações da janela
        setTitle("Gerenciador de Contas Bancárias");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(400, 400);

        //Inicializando o ContaService e carregando contas
        this.cs = cs;
        carregarContas();

        //Adiciona um listener para salvar as contas ao fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    cs.salvarContas();
                    dispose();
                } catch (SalvarContasException ex) {
                    MensagemGUI.exibirErro("Erro ao salvar contas:\n" + ex.getMessage());
                }
            }
        });

        //Selecionar contas ao clicar
        contaList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) { return; }
            contaSelecionada = contaList.getSelectedValue();
        });

        //Painel da lista de contas
        JScrollPane painelListaContas = new JScrollPane(contaList);
        painelListaContas.setBorder(BorderFactory.createTitledBorder("Contas"));

        //Painel de opções (Botões)
        JButton botaoSacar = new JButton("Sacar");
        JButton botaoDepositar = new JButton("Depositar");
        JButton botaoAdicionarConta = new JButton("Adicionar Conta");

        //Botão de saque
        botaoSacar.addActionListener(e -> {
            if (contaSelecionada == null) {
                MensagemGUI.exibirAlerta("Nenhuma conta selecionada.");
                return;
            }

            String valorSaqueString = JOptionPane.showInputDialog("Valor para saque:");

            try {
                double valorSaque = Double.parseDouble(valorSaqueString);
                contaSelecionada.sacar(valorSaque);
                MensagemGUI.exibirMensagem("Sucesso ao sacar!");
                carregarContas();
            } catch (Exception ex) {
                MensagemGUI.exibirErro("Erro ao sacar:\n" + ex.getMessage());
            }
        });

        //Botão de depósito
        botaoDepositar.addActionListener(e -> {
            if (contaSelecionada == null) {
                MensagemGUI.exibirAlerta("Nenhuma conta selecionada.");
                return;
            }

            String valorDepositoString = JOptionPane.showInputDialog("Valor para depósito:");

            try {
                double valorDeposito = Double.parseDouble(valorDepositoString);
                contaSelecionada.depositar(valorDeposito);
                MensagemGUI.exibirMensagem("Sucesso ao depositar!");
                carregarContas();
            } catch (Exception ex) {
                MensagemGUI.exibirErro("Erro ao depositar:\n" + ex.getMessage());
            }
        });

        //Botão de adicionar conta
        botaoAdicionarConta.addActionListener(e -> {
            JTextField campoNumero = new JTextField();
            JTextField campoTitular = new JTextField();
            JPanel formulario = new JPanel(new GridLayout(2, 2));

            //Adicionar bloqueio de duplicidade no futuro
            formulario.add(new JLabel("Número da conta:"));
            formulario.add(campoNumero);

            formulario.add(new JLabel("Titular da conta:"));
            formulario.add(campoTitular);

            int resultado = JOptionPane.showConfirmDialog(
                    this,
                    formulario,
                    "Adicionar Conta",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (resultado != JOptionPane.OK_OPTION) { return; }

            try {
                int numero = Integer.parseInt(campoNumero.getText());
                String titular = campoTitular.getText().trim();

                if (titular.isEmpty()) {
                    throw new Exception("O titular não pode ser vazio");
                }

                ContaCorrente c = new ContaCorrente(numero, titular, 0);
                cs.contas.add(c);
                carregarContas();

                MensagemGUI.exibirMensagem("Conta adicionada com sucesso");
            } catch (Exception ex) {
                MensagemGUI.exibirErro("Erro ao adicionar conta:\n" + ex.getMessage());
            }
        });

        //Painel de opções da conta
        JPanel painelBotoes = new JPanel();
        painelBotoes.setBorder(BorderFactory.createTitledBorder("Opções"));
        painelBotoes.add(botaoAdicionarConta);
        painelBotoes.add(botaoSacar);
        painelBotoes.add(botaoDepositar);

        //Painel de adicionais (Botões)
        JButton botaoFiltrarMaiorDezMil = new JButton("> 10.000");
        JButton botaoCalcularTotal = new JButton("Saldo total");
        JButton botaoAgruparSaldos = new JButton("Agrupar por saldo");

        //Botão de filtrar > 10k
        botaoFiltrarMaiorDezMil.addActionListener(e -> {
            List<ContaCorrente> contasFiltradas = cs.filtrarMaiorDezMil();
            StringBuilder resultado = new StringBuilder();

            for (ContaCorrente c : contasFiltradas) {
                resultado.append(c).append("\n");
            }

            JTextArea areaTexto = new JTextArea(resultado.toString());
            areaTexto.setEditable(false);
            areaTexto.setLineWrap(true);
            areaTexto.setWrapStyleWord(true);

            JScrollPane painelScroll = new JScrollPane(areaTexto);
            painelScroll.setPreferredSize(new Dimension(350, 250));

            JPanel painelContas = new JPanel();
            painelContas.setLayout(new BoxLayout(painelContas, BoxLayout.Y_AXIS));
            painelContas.add(new JLabel("Contas com saldo maior que R$10.000"));
            painelContas.add(painelScroll);

            JOptionPane.showMessageDialog(
                    this,
                    painelContas,
                    "Contas com saldo maior que R$10.000",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        //Botao de calcular saldo total
        botaoCalcularTotal.addActionListener(e -> {
            double total = cs.calcularTotal();
            String mensagem = "Saldo total: R$" + total;
            MensagemGUI.exibirMensagem(mensagem);
        });

        //Botao de agrupar saldos
        botaoAgruparSaldos.addActionListener(e -> {
            Map<String, List<ContaCorrente>> contasAgrupadas = cs.agruparSaldos();
            MensagemGUI.exibirMensagem(contasAgrupadas.toString());
        });

        //Painel de adicionais
        JPanel painelAdicionais = new JPanel();
        painelAdicionais.setBorder(BorderFactory.createTitledBorder("Adicionais"));
        painelAdicionais.add(botaoFiltrarMaiorDezMil);
        painelAdicionais.add(botaoCalcularTotal);
        painelAdicionais.add(botaoAgruparSaldos);

        //Posicionando os painéis
        add(painelListaContas, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.NORTH);
        add(painelAdicionais, BorderLayout.SOUTH);
    }

    private void carregarContas() {
        this.listModel.clear();
        for (ContaCorrente c : cs.contas) { listModel.addElement(c); }
    }
}
