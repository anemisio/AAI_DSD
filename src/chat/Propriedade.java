package chat;

public enum Propriedade {
	TEXTO, ID_DESTINO, ID_REMETENTE, LOGIN_STATUS;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
