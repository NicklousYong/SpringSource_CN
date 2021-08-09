/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.mail.javamail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.activation.FileTypeMap;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.Assert;

/**
 * Production implementation of the {@link JavaMailSender} interface,
 * supporting both JavaMail {@link MimeMessage MimeMessages} and Spring
 * {@link SimpleMailMessage SimpleMailMessages}. Can also be used as a
 * plain {@link org.springframework.mail.MailSender} implementation.
 *
 * <p>Allows for defining all settings locally as bean properties.
 * Alternatively, a pre-configured JavaMail {@link javax.mail.Session} can be
 * specified, possibly pulled from an application server's JNDI environment.
 *
 * <p>Non-default properties in this object will always override the settings
 * in the JavaMail {@code Session}. Note that if overriding all values locally,
 * there is no added value in setting a pre-configured {@code Session}.
 *
 * <p>
 * 支持JavaMail {@link MimeMessage MimeMessages}和Spring {@link SimpleMailMessage SimpleMailMessages}的{@link JavaMailSender}
 * 界面的生产实现也可以用作一个简单的{@link orgspringframeworkmailMailSender}实现。
 * 
 *  <p>允许在本地将所有设置定义为bean属性或者,可以指定预配置的JavaMail {@link javaxmailSession},可能从应用程序服务器的JNDI环境中提取
 * 
 *  <p>此对象中的非默认属性将始终覆盖JavaMail {@code Session}中的设置。请注意,如果在本地覆盖所有值,则在设置预配置的{@code Session}时没有附加值
 * 
 * 
 * @author Dmitriy Kopylenko
 * @author Juergen Hoeller
 * @since 10.09.2003
 * @see javax.mail.internet.MimeMessage
 * @see javax.mail.Session
 * @see #setSession
 * @see #setJavaMailProperties
 * @see #setHost
 * @see #setPort
 * @see #setUsername
 * @see #setPassword
 */
public class JavaMailSenderImpl implements JavaMailSender {

	/** The default protocol: 'smtp' */
	public static final String DEFAULT_PROTOCOL = "smtp";

	/** The default port: -1 */
	public static final int DEFAULT_PORT = -1;

	private static final String HEADER_MESSAGE_ID = "Message-ID";


	private Properties javaMailProperties = new Properties();

	private Session session;

	private String protocol;

	private String host;

	private int port = DEFAULT_PORT;

	private String username;

	private String password;

	private String defaultEncoding;

	private FileTypeMap defaultFileTypeMap;


	/**
	 * Create a new instance of the {@code JavaMailSenderImpl} class.
	 * <p>Initializes the {@link #setDefaultFileTypeMap "defaultFileTypeMap"}
	 * property with a default {@link ConfigurableMimeFileTypeMap}.
	 * <p>
	 * 创建{@code JavaMailSenderImpl}类的新实例<p>使用默认{@link ConfigurableMimeFileTypeMap}初始化{@link #setDefaultFileTypeMap"defaultFileTypeMap"}
	 * 属性。
	 * 
	 */
	public JavaMailSenderImpl() {
		ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
		fileTypeMap.afterPropertiesSet();
		this.defaultFileTypeMap = fileTypeMap;
	}


	/**
	 * Set JavaMail properties for the {@code Session}.
	 * <p>A new {@code Session} will be created with those properties.
	 * Use either this method or {@link #setSession}, but not both.
	 * <p>Non-default properties in this instance will override given
	 * JavaMail properties.
	 * <p>
	 *  为{@code Session}设置JavaMail属性<p>将使用这些属性创建一个新的{@code Session}使用此方法或{@link #setSession},但不能同时使用<p>此默认属性
	 * 实例将覆盖给定的JavaMail属性。
	 * 
	 */
	public void setJavaMailProperties(Properties javaMailProperties) {
		this.javaMailProperties = javaMailProperties;
		synchronized (this) {
			this.session = null;
		}
	}

	/**
	 * Allow Map access to the JavaMail properties of this sender,
	 * with the option to add or override specific entries.
	 * <p>Useful for specifying entries directly, for example via
	 * "javaMailProperties[mail.smtp.auth]".
	 * <p>
	 *  允许映射访问此发件人的JavaMail属性,并添加或覆盖特定条目<p>可用于直接指定条目,例如通过"javaMailProperties [mailsmtpauth]"
	 * 
	 */
	public Properties getJavaMailProperties() {
		return this.javaMailProperties;
	}

	/**
	 * Set the JavaMail {@code Session}, possibly pulled from JNDI.
	 * <p>Default is a new {@code Session} without defaults, that is
	 * completely configured via this instance's properties.
	 * <p>If using a pre-configured {@code Session}, non-default properties
	 * in this instance will override the settings in the {@code Session}.
	 * <p>
	 * 设置JavaMail {@code Session},可能从JNDI中提取<p>默认是一个没有默认值的新的{@code Session},通过该实例的属性完全配置<p>如果使用预配置的{@code Session}
	 * 在这种情况下,非默认属性将覆盖{@code Session}中的设置。
	 * 
	 * 
	 * @see #setJavaMailProperties
	 */
	public synchronized void setSession(Session session) {
		Assert.notNull(session, "Session must not be null");
		this.session = session;
	}

	/**
	 * Return the JavaMail {@code Session},
	 * lazily initializing it if hasn't been specified explicitly.
	 * <p>
	 *  返回JavaMail {@code Session},如果尚未被明确指定,则会延迟初始化它
	 * 
	 */
	public synchronized Session getSession() {
		if (this.session == null) {
			this.session = Session.getInstance(this.javaMailProperties);
		}
		return this.session;
	}

	/**
	 * Set the mail protocol. Default is "smtp".
	 * <p>
	 *  设置邮件协议默认为"smtp"
	 * 
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Return the mail protocol.
	 * <p>
	 *  返回邮件协议
	 * 
	 */
	public String getProtocol() {
		return this.protocol;
	}

	/**
	 * Set the mail server host, typically an SMTP host.
	 * <p>Default is the default host of the underlying JavaMail Session.
	 * <p>
	 *  设置邮件服务器主机,通常是SMTP主机<p>默认是底层JavaMail会话的默认主机
	 * 
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Return the mail server host.
	 * <p>
	 *  返回邮件服务器主机
	 * 
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * Set the mail server port.
	 * <p>Default is {@link #DEFAULT_PORT}, letting JavaMail use the default
	 * SMTP port (25).
	 * <p>
	 *  设置邮件服务器端口<p>默认为{@link #DEFAULT_PORT},让JavaMail使用默认SMTP端口(25)
	 * 
	*/
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Return the mail server port.
	 * <p>
	 *  返回邮件服务器端口
	 * 
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Set the username for the account at the mail host, if any.
	 * <p>Note that the underlying JavaMail {@code Session} has to be
	 * configured with the property {@code "mail.smtp.auth"} set to
	 * {@code true}, else the specified username will not be sent to the
	 * mail server by the JavaMail runtime. If you are not explicitly passing
	 * in a {@code Session} to use, simply specify this setting via
	 * {@link #setJavaMailProperties}.
	 * <p>
	 * 在邮件主机中设置帐户的用户名(如果有的话)<p>请注意,底层的JavaMail {@code Session}必须配置为{@code"mailsmtpauth"}设置为{@code true}的属性,否
	 * 则指定的用户名不会被JavaMail运行时发送到邮件服务器如果您没有明确传递{@code Session}来使用,只需通过{@link #setJavaMailProperties}指定此设置即可。
	 * 
	 * 
	 * @see #setSession
	 * @see #setPassword
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Return the username for the account at the mail host.
	 * <p>
	 *  在邮件主机上返回该帐户的用户名
	 * 
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Set the password for the account at the mail host, if any.
	 * <p>Note that the underlying JavaMail {@code Session} has to be
	 * configured with the property {@code "mail.smtp.auth"} set to
	 * {@code true}, else the specified password will not be sent to the
	 * mail server by the JavaMail runtime. If you are not explicitly passing
	 * in a {@code Session} to use, simply specify this setting via
	 * {@link #setJavaMailProperties}.
	 * <p>
	 * 在邮件主机上设置帐户的密码(如果有的话)<p>请注意,底层的JavaMail {@code Session}必须配置为{@code"mailsmtpauth"}设置为{@code true}的属性,否则
	 * 指定的密码不会被JavaMail运行时发送到邮件服务器如果您没有明确传递{@code Session}来使用,只需通过{@link #setJavaMailProperties}指定此设置即可。
	 * 
	 * 
	 * @see #setSession
	 * @see #setUsername
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Return the password for the account at the mail host.
	 * <p>
	 *  在邮件主机上返回该帐户的密码
	 * 
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Set the default encoding to use for {@link MimeMessage MimeMessages}
	 * created by this instance.
	 * <p>Such an encoding will be auto-detected by {@link MimeMessageHelper}.
	 * <p>
	 *  设置由此实例创建的{@link MimeMessage MimeMessages}使用的默认编码<p>这样的编码将被{@link MimeMessageHelper}自动检测到
	 * 
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * Return the default encoding for {@link MimeMessage MimeMessages},
	 * or {@code null} if none.
	 * <p>
	 *  返回{@link MimeMessage MimeMessages}的默认编码,否则返回{@code null}
	 * 
	 */
	public String getDefaultEncoding() {
		return this.defaultEncoding;
	}

	/**
	 * Set the default Java Activation {@link FileTypeMap} to use for
	 * {@link MimeMessage MimeMessages} created by this instance.
	 * <p>A {@code FileTypeMap} specified here will be autodetected by
	 * {@link MimeMessageHelper}, avoiding the need to specify the
	 * {@code FileTypeMap} for each {@code MimeMessageHelper} instance.
	 * <p>For example, you can specify a custom instance of Spring's
	 * {@link ConfigurableMimeFileTypeMap} here. If not explicitly specified,
	 * a default {@code ConfigurableMimeFileTypeMap} will be used, containing
	 * an extended set of MIME type mappings (as defined by the
	 * {@code mime.types} file contained in the Spring jar).
	 * <p>
	 * 设置默认的Java Activation {@link FileTypeMap}用于由此实例创建的{@link MimeMessage MimeMessages} <p>此处指定的{@code FileTypeMap}
	 * 将由{@link MimeMessageHelper}自动检测,避免了指定{@code MimeMessageHelper}实例的{@code FileTypeMap}例如,您可以在此处指定Spring
	 * 的{@link ConfigurableMimeFileTypeMap}的自定义实例如果未明确指定,将使用默认的{@code ConfigurableMimeFileTypeMap},其中包含扩展的一组
	 * MIME类型映射(由Spring jar中包含的{@code mimetypes}文件定义)。
	 * 
	 * 
	 * @see MimeMessageHelper#setFileTypeMap
	 */
	public void setDefaultFileTypeMap(FileTypeMap defaultFileTypeMap) {
		this.defaultFileTypeMap = defaultFileTypeMap;
	}

	/**
	 * Return the default Java Activation {@link FileTypeMap} for
	 * {@link MimeMessage MimeMessages}, or {@code null} if none.
	 * <p>
	 *  返回{@link MimeMessage MimeMessages}的默认Java激活{@link FileTypeMap},否则返回{@code null}
	 * 
	 */
	public FileTypeMap getDefaultFileTypeMap() {
		return this.defaultFileTypeMap;
	}


	//---------------------------------------------------------------------
	// Implementation of MailSender
	//---------------------------------------------------------------------

	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		send(new SimpleMailMessage[] {simpleMessage});
	}

	@Override
	public void send(SimpleMailMessage... simpleMessages) throws MailException {
		List<MimeMessage> mimeMessages = new ArrayList<MimeMessage>(simpleMessages.length);
		for (SimpleMailMessage simpleMessage : simpleMessages) {
			MimeMailMessage message = new MimeMailMessage(createMimeMessage());
			simpleMessage.copyTo(message);
			mimeMessages.add(message.getMimeMessage());
		}
		doSend(mimeMessages.toArray(new MimeMessage[mimeMessages.size()]), simpleMessages);
	}


	//---------------------------------------------------------------------
	// Implementation of JavaMailSender
	//---------------------------------------------------------------------

	/**
	 * This implementation creates a SmartMimeMessage, holding the specified
	 * default encoding and default FileTypeMap. This special defaults-carrying
	 * message will be autodetected by {@link MimeMessageHelper}, which will use
	 * the carried encoding and FileTypeMap unless explicitly overridden.
	 * <p>
	 * 此实现创建一个SmartMimeMessage,保存指定的默认编码和默认FileTypeMap这个特殊的默认携带消息将被{@link MimeMessageHelper}自动检测,它将使用携带的编码和F
	 * ileTypeMap,除非明确覆盖。
	 * 
	 * 
	 * @see #setDefaultEncoding
	 * @see #setDefaultFileTypeMap
	 */
	@Override
	public MimeMessage createMimeMessage() {
		return new SmartMimeMessage(getSession(), getDefaultEncoding(), getDefaultFileTypeMap());
	}

	@Override
	public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
		try {
			return new MimeMessage(getSession(), contentStream);
		}
		catch (Exception ex) {
			throw new MailParseException("Could not parse raw MIME content", ex);
		}
	}

	@Override
	public void send(MimeMessage mimeMessage) throws MailException {
		send(new MimeMessage[] {mimeMessage});
	}

	@Override
	public void send(MimeMessage... mimeMessages) throws MailException {
		doSend(mimeMessages, null);
	}

	@Override
	public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
		send(new MimeMessagePreparator[] {mimeMessagePreparator});
	}

	@Override
	public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
		try {
			List<MimeMessage> mimeMessages = new ArrayList<MimeMessage>(mimeMessagePreparators.length);
			for (MimeMessagePreparator preparator : mimeMessagePreparators) {
				MimeMessage mimeMessage = createMimeMessage();
				preparator.prepare(mimeMessage);
				mimeMessages.add(mimeMessage);
			}
			send(mimeMessages.toArray(new MimeMessage[mimeMessages.size()]));
		}
		catch (MailException ex) {
			throw ex;
		}
		catch (MessagingException ex) {
			throw new MailParseException(ex);
		}
		catch (Exception ex) {
			throw new MailPreparationException(ex);
		}
	}

	/**
	 * Validate that this instance can connect to the server that it is configured
	 * for. Throws a {@link MessagingException} if the connection attempt failed.
	 * <p>
	 *  如果连接尝试失败,则验证此实例可以连接到其配置的服务器以引发{@link MessagingException}
	 * 
	 */
	public void testConnection() throws MessagingException {
		Transport transport = null;
		try {
			transport = connectTransport();
		}
		finally {
			if (transport != null) {
				transport.close();
			}
		}
	}

	/**
	 * Actually send the given array of MimeMessages via JavaMail.
	 * <p>
	 *  实际上通过JavaMail发送给定的MimeMessages数组
	 * 
	 * 
	 * @param mimeMessages MimeMessage objects to send
	 * @param originalMessages corresponding original message objects
	 * that the MimeMessages have been created from (with same array
	 * length and indices as the "mimeMessages" array), if any
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending a message
	 */
	protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
		Map<Object, Exception> failedMessages = new LinkedHashMap<Object, Exception>();
		Transport transport = null;

		try {
			for (int i = 0; i < mimeMessages.length; i++) {

				// Check transport connection first...
				if (transport == null || !transport.isConnected()) {
					if (transport != null) {
						try {
							transport.close();
						}
						catch (Exception ex) {
							// Ignore - we're reconnecting anyway
						}
						transport = null;
					}
					try {
						transport = connectTransport();
					}
					catch (AuthenticationFailedException ex) {
						throw new MailAuthenticationException(ex);
					}
					catch (Exception ex) {
						// Effectively, all remaining messages failed...
						for (int j = i; j < mimeMessages.length; j++) {
							Object original = (originalMessages != null ? originalMessages[j] : mimeMessages[j]);
							failedMessages.put(original, ex);
						}
						throw new MailSendException("Mail server connection failed", ex, failedMessages);
					}
				}

				// Send message via current transport...
				MimeMessage mimeMessage = mimeMessages[i];
				try {
					if (mimeMessage.getSentDate() == null) {
						mimeMessage.setSentDate(new Date());
					}
					String messageId = mimeMessage.getMessageID();
					mimeMessage.saveChanges();
					if (messageId != null) {
						// Preserve explicitly specified message id...
						mimeMessage.setHeader(HEADER_MESSAGE_ID, messageId);
					}
					transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
				}
				catch (Exception ex) {
					Object original = (originalMessages != null ? originalMessages[i] : mimeMessage);
					failedMessages.put(original, ex);
				}
			}
		}
		finally {
			try {
				if (transport != null) {
					transport.close();
				}
			}
			catch (Exception ex) {
				if (!failedMessages.isEmpty()) {
					throw new MailSendException("Failed to close server connection after message failures", ex,
							failedMessages);
				}
				else {
					throw new MailSendException("Failed to close server connection after message sending", ex);
				}
			}
		}

		if (!failedMessages.isEmpty()) {
			throw new MailSendException(failedMessages);
		}
	}

	/**
	 * Obtain and connect a Transport from the underlying JavaMail Session,
	 * passing in the specified host, port, username, and password.
	 * <p>
	 *  从底层JavaMail会话获取并连接传输,传入指定的主机,端口,用户名和密码
	 * 
	 * 
	 * @return the connected Transport object
	 * @throws MessagingException if the connect attempt failed
	 * @since 4.1.2
	 * @see #getTransport
	 * @see #getHost()
	 * @see #getPort()
	 * @see #getUsername()
	 * @see #getPassword()
	 */
	protected Transport connectTransport() throws MessagingException {
		String username = getUsername();
		String password = getPassword();
		if ("".equals(username)) {  // probably from a placeholder
			username = null;
			if ("".equals(password)) {  // in conjunction with "" username, this means no password to use
				password = null;
			}
		}

		Transport transport = getTransport(getSession());
		transport.connect(getHost(), getPort(), username, password);
		return transport;
	}

	/**
	 * Obtain a Transport object from the given JavaMail Session,
	 * using the configured protocol.
	 * <p>Can be overridden in subclasses, e.g. to return a mock Transport object.
	 * <p>
	 *  从给定的JavaMail会话中获取Transport对象,使用配置的协议<p>可以在子类中覆盖,例如返回一个mock Transport对象
	 * 
	 * @see javax.mail.Session#getTransport(String)
	 * @see #getSession()
	 * @see #getProtocol()
	 */
	protected Transport getTransport(Session session) throws NoSuchProviderException {
		String protocol	= getProtocol();
		if (protocol == null) {
			protocol = session.getProperty("mail.transport.protocol");
			if (protocol == null) {
				protocol = DEFAULT_PROTOCOL;
			}
		}
		return session.getTransport(protocol);
	}

}
