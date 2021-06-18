package chat.mensage;

import javafx.beans.property.SimpleStringProperty;

public class Mensagem {

	private final SimpleStringProperty autor, mensagem;

	public Mensagem(String autor, String mensagem) {
		this.autor = new SimpleStringProperty(autor);
		this.mensagem = new SimpleStringProperty(mensagem);
	}

	public String getAutor() {
		return autor.get();
	}

	public String getMensagem() {
		return mensagem.get();
	}
	
}
