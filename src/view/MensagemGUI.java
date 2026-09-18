package view;

import javax.swing.*;
import java.awt.*;

public class MensagemGUI {
    public static void exibirMensagem(String message) {
        JOptionPane.showMessageDialog(null, message, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void exibirAlerta(String message) {
        JOptionPane.showMessageDialog(null, message, "Alerta", JOptionPane.WARNING_MESSAGE);
    }

    public static void exibirErro(String message) {
        JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
