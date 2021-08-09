/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.oxm.mime;

import javax.activation.DataHandler;

/**
 * Represents a container for MIME attachments
 * Concrete implementations might adapt a SOAPMessage or an email message.
 *
 * <p>
 *  表示MIME附件的容器具体实现可能会调整SOAPMessage或电子邮件消息
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/">XML-binary Optimized Packaging</a>
 */
public interface MimeContainer {

	/**
	 * Indicate whether this container is a XOP package.
	 * <p>
	 *  指示此容器是否为XOP包
	 * 
	 * 
	 * @return {@code true} when the constraints specified in
	 * <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#identifying_xop_documents">Identifying XOP Documents</a>
	 * are met
	 * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#xop_packages">XOP Packages</a>
	 */
	boolean isXopPackage();

	/**
	 * Turn this message into a XOP package.
	 * <p>
	 *  将此消息转换为XOP包
	 * 
	 * 
	 * @return {@code true} when the message actually is a XOP package
	 * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#xop_packages">XOP Packages</a>
	 */
	boolean convertToXopPackage();

	/**
	 * Add the given data handler as an attachment to this container.
	 * <p>
	 * 将给定的数据处理程序作为附件添加到此容器
	 * 
	 * 
	 * @param contentId  the content id of the attachment
	 * @param dataHandler the data handler containing the data of the attachment
	 */
	void addAttachment(String contentId, DataHandler dataHandler);

	/**
	 * Return the attachment with the given content id, or {@code null} if not found.
	 * <p>
	 *  返回带有给定内容ID的附件,否则返回{@code null}
	 * 
	 * @param contentId the content id
	 * @return the attachment, as a data handler
	 */
	DataHandler getAttachment(String contentId);

}
