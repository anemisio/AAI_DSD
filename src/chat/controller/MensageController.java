package chat.controller;

import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import chat.Propriedade;
import chat.mensage.MensagemRecebida;

public class MensageController {

	public static final String ENDERECO = "tcp://localhost:61616";

	public static final String TAG_MENSAGEM_SERVIDOR = "servidor";
	public static final String TAG_MENSAGEM_LOGIN = "login";
	public static final String TAG_MENSAGEM_GLOBAL = "global";
	public static final String TAG_MENSAGEM_LOGOUT = "logout";

	public static final String RESERVADO_NICK = "#LOGIN";

	private String idUSuario;

	private static final Logger LOGGER = Logger.getLogger(MensageController.class.toString());

	public MensageController(String idUSuario) {
		this.idUSuario = idUSuario;
	}

	public void enviarMensagemLoginStatus(final String idUsuarioDestino, final boolean loginValido) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(RESERVADO_NICK + idUsuarioDestino);

					MessageProducer producer = session.createProducer(destination);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					Message mensagem = session.createMessage();
					mensagem.setBooleanProperty(Propriedade.LOGIN_STATUS.toString(), loginValido);

					producer.send(mensagem);

					session.close();
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void enviarMensagemDoServidor(final String texto, final String tag) {
		enviarMensagemDoServidor(texto, tag, null);
	}
	public void enviarMensagemDoServidor(final String texto, final String tag,
			final String idUsuarioRemetenteOriginal) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("Enviando mensagem do servidor.");
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(tag);

					MessageProducer producer = session.createProducer(destination);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					Message mensagem = session.createMessage();

					if (idUsuarioRemetenteOriginal != null) {
						mensagem.setStringProperty(Propriedade.ID_REMETENTE.toString(), idUsuarioRemetenteOriginal);
					} else {
						mensagem.setStringProperty(Propriedade.ID_REMETENTE.toString(),
								MensageController.this.idUSuario);
					}

					mensagem.setStringProperty(Propriedade.TEXTO.toString(), texto);

					producer.send(mensagem);

					session.close();
					connection.close();
				} catch (Exception e) {
					System.out.println("Caught: " + e);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void enviarMensagem(final String texto, final String tag, final String idUsuarioDestino) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("Enviando mensagem");
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(tag);

					MessageProducer producer = session.createProducer(destination);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					Message mensagem = session.createMessage();
					mensagem.setStringProperty(Propriedade.ID_REMETENTE.toString(), MensageController.this.idUSuario);
					mensagem.setStringProperty(Propriedade.TEXTO.toString(), texto);

					if (idUsuarioDestino != null) {
						mensagem.setStringProperty(Propriedade.ID_DESTINO.toString(), idUsuarioDestino);
					}

					producer.send(mensagem);

					session.close();
					connection.close();
				} catch (Exception e) {
					System.out.println("Caught: " + e);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void receberMensagem(final MensagemRecebida event) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("Recebendo mensagem.");
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(MensageController.this.idUSuario);

					MessageConsumer consumer = session.createConsumer(destination);

					consumer.setMessageListener(new MessageListener() {

						@Override
						public void onMessage(Message message) {
							if (message != null) {
								event.recebida(message);
							}
						}
					});

				} catch (Exception e) {
					System.out.println("Caught: " + e);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void receberMensagemLogin(final MensagemRecebida event) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ENDERECO);

					Connection connection = connectionFactory.createConnection();
					connection.start();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = session.createQueue(RESERVADO_NICK + MensageController.this.idUSuario);

					MessageConsumer consumer = session.createConsumer(destination);

					event.recebida(consumer.receive(1000));

					consumer.close();
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public String getSessionId() {
		return idUSuario;
	}
}
