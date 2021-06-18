/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.controller;


import javax.jms.JMSException;

import chat.Propriedade;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LoginController {

	@FXML
	private TextField txt_name;

	@FXML
	private Text msg;
	
	@FXML
	private void onKeyPressed(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			btnVerificarClick(new ActionEvent());
		} else if (e.getCode().equals(KeyCode.ESCAPE)) {
			fecharJanela();
		}
	}

	@FXML
	private void btnVerificarClick(ActionEvent event) {
		String nick = this.txt_name.getText();
		if (!nick.isEmpty()) {
			MensageController app = new MensageController(nick);
			app.enviarMensagemDoServidor(nick, MensageController.TAG_MENSAGEM_LOGIN);
			
			app.receberMensagemLogin(m -> {
				if (m != null) {
					try {
						if (m.getBooleanProperty(Propriedade.LOGIN_STATUS.toString())) {
							Platform.runLater(() -> {
								showChatDialog(app);	
							});
						} else {
							msg.setText("Este Usuário já esta sendo usado.");
						}
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
		}else {
			msg.setText("Usuário vazio. Insira um nome válido.");
		}
	}

	private void showChatDialog(MensageController app) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat/telas/Chat.fxml"));

			Window janela = txt_name.getScene().getWindow();
			Stage anterior = (Stage) janela;
			anterior.close();

			Stage stage = new Stage();

			Scene scene = new Scene((AnchorPane) loader.load(), 480, 600);

			ChatController chat = loader.<ChatController>getController();
			chat.initData(app);

			stage.setMaxHeight(600);
			stage.setMaxWidth(480);
			
			stage.setOnCloseRequest(e -> {
				app.enviarMensagem(app.getSessionId(), MensageController.TAG_MENSAGEM_LOGOUT, null);
			});

			stage.setScene(scene);

			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fecharJanela() {
		Window janela = txt_name.getScene().getWindow();
		Stage atual = (Stage) janela;
		atual.close();
	}
}
