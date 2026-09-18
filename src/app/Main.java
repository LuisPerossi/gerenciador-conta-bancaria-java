import exception.CarregarContasException;
import service.ContaService;
import view.ContaGUI;
import view.MensagemGUI;

void main() {
    try {
        ContaService cs = new ContaService();
        cs.lerContas();

        ContaGUI gui = new ContaGUI(cs);
        gui.setVisible(true);
    } catch (CarregarContasException e) {
        MensagemGUI.exibirErro("Erro ao carregar contas:\n" + e.getMessage());
    } catch (Exception e) {
        MensagemGUI.exibirErro("Erro inesperado:\n" + e.getMessage());
    }
}