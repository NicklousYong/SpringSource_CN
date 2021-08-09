/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;

/**
 * Extended {@link org.springframework.mail.MailSender} interface for JavaMail,
 * supporting MIME messages both as direct arguments and through preparation
 * callbacks. Typically used in conjunction with the {@link MimeMessageHelper}
 * class for convenient creation of JavaMail {@link MimeMessage MimeMessages},
 * including attachments etc.
 *
 * <p>Clients should talk to the mail sender through this interface if they need
 * mail functionality beyond {@link org.springframework.mail.SimpleMailMessage}.
 * The production implementation is {@link JavaMailSenderImpl}; for testing,
 * mocks can be created based on this interface. Clients will typically receive
 * the JavaMailSender reference through dependency injection.
 *
 * <p>The recommended way of using this interface is the {@link MimeMessagePreparator}
 * mechanism, possibly using a {@link MimeMessageHelper} for populating the message.
 * See {@link MimeMessageHelper MimeMessageHelper's javadoc} for an example.
 *
 * <p>The entire JavaMail {@link javax.mail.Session} management is abstracted
 * by the JavaMailSender. Client code should not deal with a Session in any way,
 * rather leave the entire JavaMail configuration and resource handling to the
 * JavaMailSender implementation. This also increases testability.
 *
 * <p>A JavaMailSender client is not as easy to test as a plain
 * {@link org.springframework.mail.MailSender} client, but still straightforward
 * compared to traditional JavaMail code: Just let {@link #createMimeMessage()}
 * return a plain {@link MimeMessage} created with a
 * {@code Session.getInstance(new Properties())} call, and check the passed-in
 * messages in your mock implementations of the various {@code send} methods.
 *
 * <p>
 * JavaMail扩展的{@link orgspringframeworkmailMailSender}接口,支持MIME消息作为直接参数和准备回调通常与{@link MimeMessageHelper}
 * 类一起使用,方便创建JavaMail {@link MimeMessage MimeMessages},包括附件等。
 * 
 *  <p>如果客户端需要超出{@link orgspringframeworkmailSimpleMailMessage}的邮件功能,客户端应通过此界面与邮件发件人进行通信。
 * 生产实现为{@link JavaMailSenderImpl};为了测试,可以基于此接口创建嘲笑。客户端通常将通过依赖注入来接收JavaMailSender引用。
 * 
 * <p>使用此界面的推荐方法是{@link MimeMessagePreparator}机制,可能使用{@link MimeMessageHelper}填充消息。
 * 有关示例,请参阅{@link MimeMessageHelper MimeMessageHelper的javadoc}。
 * 
 *  整个JavaMail {@link javaxmailSession}管理由JavaMailSender客户端代码抽象出来不应该以任何方式处理会话,而是将整个JavaMail配置和资源处理留给Java
 * MailSender实现这也增加了可测试性。
 * 
 * <p> JavaMailSender客户端不像普通的{@link orgspringframeworkmailMailSender}客户端那么容易测试,但与传统的JavaMail代码相比仍然很简单：只需
 * 让{@link #createMimeMessage()}返回一个简单的{@link MimeMessage}创建使用{@code SessiongetInstance(new Properties())}
 * 调用,并检查您的模拟实现中的各种{@code send}方法的传入消息。
 * 
 * 
 * @author Juergen Hoeller
 * @since 07.10.2003
 * @see javax.mail.internet.MimeMessage
 * @see javax.mail.Session
 * @see JavaMailSenderImpl
 * @see MimeMessagePreparator
 * @see MimeMessageHelper
 */
public interface JavaMailSender extends MailSender {

	/**
	 * Create a new JavaMail MimeMessage for the underlying JavaMail Session
	 * of this sender. Needs to be called to create MimeMessage instances
	 * that can be prepared by the client and passed to send(MimeMessage).
	 * <p>
	 *  为此发件人的底层JavaMail会话创建新的JavaMail MimeMessage需要调用以创建可由客户端准备并传递给发送(MimeMessage)的MimeMessage实例,
	 * 
	 * 
	 * @return the new MimeMessage instance
	 * @see #send(MimeMessage)
	 * @see #send(MimeMessage[])
	 */
	MimeMessage createMimeMessage();

	/**
	 * Create a new JavaMail MimeMessage for the underlying JavaMail Session
	 * of this sender, using the given input stream as the message source.
	 * <p>
	 *  为此发件人的底层JavaMail会话创建一个新的JavaMail MimeMessage,使用给定的输入流作为消息源
	 * 
	 * 
	 * @param contentStream the raw MIME input stream for the message
	 * @return the new MimeMessage instance
	 * @throws org.springframework.mail.MailParseException
	 * in case of message creation failure
	*/
	MimeMessage createMimeMessage(InputStream contentStream) throws MailException;

	/**
	 * Send the given JavaMail MIME message.
	 * The message needs to have been created with {@link #createMimeMessage()}.
	 * <p>
	 * 发送给定的JavaMail MIME消息该消息需要使用{@link #createMimeMessage()}创建
	 * 
	 * 
	 * @param mimeMessage message to send
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending the message
	 * @see #createMimeMessage
	 */
	void send(MimeMessage mimeMessage) throws MailException;

	/**
	 * Send the given array of JavaMail MIME messages in batch.
	 * The messages need to have been created with {@link #createMimeMessage()}.
	 * <p>
	 *  批量发送给定的JavaMail MIME邮件数组消息需要使用{@link #createMimeMessage()}创建
	 * 
	 * 
	 * @param mimeMessages messages to send
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending a message
	 * @see #createMimeMessage
	 */
	void send(MimeMessage... mimeMessages) throws MailException;

	/**
	 * Send the JavaMail MIME message prepared by the given MimeMessagePreparator.
	 * <p>Alternative way to prepare MimeMessage instances, instead of
	 * {@link #createMimeMessage()} and {@link #send(MimeMessage)} calls.
	 * Takes care of proper exception conversion.
	 * <p>
	 *  发送由给定的MimeMessagePreparator准备的JavaMail MIME消息准备MimeMessage实例,而不是{@link #createMimeMessage()}和{@link #send(MimeMessage))调用的备用方式处理正确的异常转换。
	 * 
	 * 
	 * @param mimeMessagePreparator the preparator to use
	 * @throws org.springframework.mail.MailPreparationException
	 * in case of failure when preparing the message
	 * @throws org.springframework.mail.MailParseException
	 * in case of failure when parsing the message
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending the message
	 */
	void send(MimeMessagePreparator mimeMessagePreparator) throws MailException;

	/**
	 * Send the JavaMail MIME messages prepared by the given MimeMessagePreparators.
	 * <p>Alternative way to prepare MimeMessage instances, instead of
	 * {@link #createMimeMessage()} and {@link #send(MimeMessage[])} calls.
	 * Takes care of proper exception conversion.
	 * <p>
	 *  发送由给定的MimeMessagePreparators准备的JavaMail MIME消息准备MimeMessage实例的替代方式,而不是{@link #createMimeMessage()}和{@link #send(MimeMessage [])}
	 * 调用正确的异常转换。
	 * 
	 * @param mimeMessagePreparators the preparator to use
	 * @throws org.springframework.mail.MailPreparationException
	 * in case of failure when preparing a message
	 * @throws org.springframework.mail.MailParseException
	 * in case of failure when parsing a message
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending a message
	 */
	void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException;

}
