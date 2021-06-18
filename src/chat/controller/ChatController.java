package chat.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;

import chat.Propriedade;
import chat.mensage.Mensagem;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class ChatController {

	@FXML
	private TextArea mensagemTextArea;
	@FXML
	private Text alertaText;
	@FXML
	private Text inforText;
	@FXML
	private TextField recipientTextField;
	@FXML
	private boolean editable = false;
	@FXML
	public CheckBox box;
	@FXML
	private TableView<Mensagem> mensagensTableView;
	@FXML
	private TableColumn<Mensagem, String> nameTableColumn;
	@FXML
	private TableColumn<Mensagem, String> mensagemTableColumn;
	@FXML
	private ComboBox<String> usuariosConectados;

	private MensageController app;

	private List<Mensagem> mensagens;

	public ChatController() {
		mensagens = new ArrayList<Mensagem>();
	}
	
	@FXML
	private void editable() {
		if (editable) {
			editable = false;
			recipientTextField.setDisable(editable);
		} else {
			editable = true;
			recipientTextField.setDisable(editable);
		}
	}
	
	@FXML
	private void btnEnviarClick(ActionEvent event) {
		enviarMensagem();
	}
	
	@FXML
	private void btnLimparClick(ActionEvent event) {
		mensagemTextArea.setText("");
		recipientTextField.setText("");
		box.setSelected(false);
		recipientTextField.setDisable(false);
		editable = false;
		cleanAlerts();
	}

	@FXML
	private void buttonPressed(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			enviarMensagem();
		}
	}
	
	@FXML
	private void enviarMensagem() {
		String mensagem = mensagemTextArea.getText();
		cleanAlerts();
		if (isMensagemValida(mensagem)) {
			String destinatario = (!recipientTextField.getText().isEmpty())?recipientTextField.getText():null;
			if (destinatario != null) {
				mensagem = "Para @" + destinatario + ":	" + mensagem;
				inforText.setText("Mensagem enviada para: " + destinatario + ".");
			}else {
				mensagem = "Para Global: " + mensagem;
				inforText.setText("Mensagem enviada.");
			}
			app.enviarMensagem(mensagem, MensageController.TAG_MENSAGEM_SERVIDOR, destinatario);
			adicionarMensagem(mensagem, "Eu");
			mensagemTextArea.setText("");
		} else {
			alertaText.setText("Campo de mensagem vazio.");
		}
	}
	
	private void cleanAlerts() {
		alertaText.setText("");
		inforText.setText("");
	}

	private boolean isMensagemValida(final String mensagem) {
		if (mensagem.isEmpty()) {
			return false;
		}

		Pattern pattern = Pattern.compile("^(" + MensageController.RESERVADO_NICK + ").*");
		Matcher matcher = pattern.matcher(mensagem);
		return !matcher.find();
	}

	public void initData(MensageController app) {
		this.app = app;
		atualizarMensagens();
	}
	
	private void atualizarMensagens() {
		nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("autor"));
		mensagemTableColumn.setCellValueFactory(new PropertyValueFactory<>("mensagem"));

		app.receberMensagem(m -> {
			try {
				String mensagem = m.getStringProperty(Propriedade.TEXTO.toString()),
						remetente = m.getStringProperty(Propriedade.ID_REMETENTE.toString());

				adicionarMensagem(mensagem, remetente);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void adicionarMensagem(String mensagem, String remetente) {
		mensagens.add(new Mensagem(remetente, mensagem));
		mensagensTableView.setItems(FXCollections.observableList(mensagens));
	}
	
}
