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

package org.springframework.web.servlet.view.document;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.web.servlet.view.AbstractView;

/**
 * Abstract superclass for PDF views, using Bruno Lowagie's
 * <a href="http://www.lowagie.com/iText">iText</a> package.
 * Application-specific view classes will extend this class.
 * The view will be held in the subclass itself, not in a template.
 *
 * <p>Note: Internet Explorer requires a ".pdf" extension, as
 * it doesn't always respect the declared content type.
 *
 * <p>
 * 使用Bruno Lowagie的<a href=\"http://wwwlowagiecom/iText\"> iText </a>包装的PDF视图的抽象超类应用程序特定的视图类将扩展此类视图将保留在子
 * 类本身中,而不是一个模板。
 * 
 *  <p>注意：Internet Explorer需要一个"pdf"扩展名,因为它并不总是尊重声明的内容类型
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @see AbstractPdfStamperView
 */
public abstract class AbstractPdfView extends AbstractView {

	/**
	 * This constructor sets the appropriate content type "application/pdf".
	 * Note that IE won't take much notice of this, but there's not a lot we
	 * can do about this. Generated documents should have a ".pdf" extension.
	 * <p>
	 *  这个构造函数设置适当的内容类型"application / pdf"请注意,IE不会太多注意到这一点,但是我们可以做的不多,生成的文档应该有一个"pdf"扩展名
	 * 
	 */
	public AbstractPdfView() {
		setContentType("application/pdf");
	}


	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	@Override
	protected final void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// IE workaround: write into byte array first.
		ByteArrayOutputStream baos = createTemporaryOutputStream();

		// Apply preferences and build metadata.
		Document document = newDocument();
		PdfWriter writer = newWriter(document, baos);
		prepareWriter(model, writer, request);
		buildPdfMetadata(model, document, request);

		// Build PDF document.
		document.open();
		buildPdfDocument(model, document, writer, request, response);
		document.close();

		// Flush to HTTP response.
		writeToResponse(response, baos);
	}

	/**
	 * Create a new document to hold the PDF contents.
	 * <p>By default returns an A4 document, but the subclass can specify any
	 * Document, possibly parameterized via bean properties defined on the View.
	 * <p>
	 *  创建一个新文档以保存PDF内容<p>默认情况下返回一个A4文档,但子类可以指定任何Document,可能通过在View上定义的bean属性进行参数化
	 * 
	 * 
	 * @return the newly created iText Document instance
	 * @see com.lowagie.text.Document#Document(com.lowagie.text.Rectangle)
	 */
	protected Document newDocument() {
		return new Document(PageSize.A4);
	}

	/**
	 * Create a new PdfWriter for the given iText Document.
	 * <p>
	 * 为给定的iText文档创建一个新的PdfWriter
	 * 
	 * 
	 * @param document the iText Document to create a writer for
	 * @param os the OutputStream to write to
	 * @return the PdfWriter instance to use
	 * @throws DocumentException if thrown during writer creation
	 */
	protected PdfWriter newWriter(Document document, OutputStream os) throws DocumentException {
		return PdfWriter.getInstance(document, os);
	}

	/**
	 * Prepare the given PdfWriter. Called before building the PDF document,
	 * that is, before the call to {@code Document.open()}.
	 * <p>Useful for registering a page event listener, for example.
	 * The default implementation sets the viewer preferences as returned
	 * by this class's {@code getViewerPreferences()} method.
	 * <p>
	 *  准备给定的PdfWriter在构建PDF文档之前调用,也就是在调用{@code Documentopen()} <p>之前调用,用于注册页面事件侦听器,例如默认实现设置由此类返回的查看器首选项{@code getViewerPreferences()}
	 * 方法。
	 * 
	 * 
	 * @param model the model, in case meta information must be populated from it
	 * @param writer the PdfWriter to prepare
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @throws DocumentException if thrown during writer preparation
	 * @see com.lowagie.text.Document#open()
	 * @see com.lowagie.text.pdf.PdfWriter#setPageEvent
	 * @see com.lowagie.text.pdf.PdfWriter#setViewerPreferences
	 * @see #getViewerPreferences()
	 */
	protected void prepareWriter(Map<String, Object> model, PdfWriter writer, HttpServletRequest request)
			throws DocumentException {

		writer.setViewerPreferences(getViewerPreferences());
	}

	/**
	 * Return the viewer preferences for the PDF file.
	 * <p>By default returns {@code AllowPrinting} and
	 * {@code PageLayoutSinglePage}, but can be subclassed.
	 * The subclass can either have fixed preferences or retrieve
	 * them from bean properties defined on the View.
	 * <p>
	 *  返回PDF文件的查看器首选项<p>默认情况下返回{@code AllowPrinting}和{@code PageLayoutSinglePage},但可以被子类化子类可以具有固定首选项或从视图中定义
	 * 的bean属性中检索它们。
	 * 
	 * 
	 * @return an int containing the bits information against PdfWriter definitions
	 * @see com.lowagie.text.pdf.PdfWriter#AllowPrinting
	 * @see com.lowagie.text.pdf.PdfWriter#PageLayoutSinglePage
	 */
	protected int getViewerPreferences() {
		return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
	}

	/**
	 * Populate the iText Document's meta fields (author, title, etc.).
	 * <br>Default is an empty implementation. Subclasses may override this method
	 * to add meta fields such as title, subject, author, creator, keywords, etc.
	 * This method is called after assigning a PdfWriter to the Document and
	 * before calling {@code document.open()}.
	 * <p>
	 * 填充iText文档的元字段(作者,标题等)<br>默认为空实现子类可以覆盖此方法以添加元字段,如标题,主题,作者,创建者,关键字等。
	 * 此方法在分配PdfWriter到文档,然后调用{@code documentopen()}。
	 * 
	 * 
	 * @param model the model, in case meta information must be populated from it
	 * @param document the iText document being populated
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @see com.lowagie.text.Document#addTitle
	 * @see com.lowagie.text.Document#addSubject
	 * @see com.lowagie.text.Document#addKeywords
	 * @see com.lowagie.text.Document#addAuthor
	 * @see com.lowagie.text.Document#addCreator
	 * @see com.lowagie.text.Document#addProducer
	 * @see com.lowagie.text.Document#addCreationDate
	 * @see com.lowagie.text.Document#addHeader
	 */
	protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
	}

	/**
	 * Subclasses must implement this method to build an iText PDF document,
	 * given the model. Called between {@code Document.open()} and
	 * {@code Document.close()} calls.
	 * <p>Note that the passed-in HTTP response is just supposed to be used
	 * for setting cookies or other HTTP headers. The built PDF document itself
	 * will automatically get written to the response after this method returns.
	 * <p>
	 *  给定在{@code Documentopen()}和{@code Documentclose()}调用之间调用的模型,子类必须实现此方法来构建iText PDF文档。
	 * 请注意,传入的HTTP响应应该被使用用于设置Cookie或其他HTTP标头此方法返回后,内置的PDF文档本身将自动写入响应。
	 * 
	 * @param model the model Map
	 * @param document the iText Document to add elements to
	 * @param writer the PdfWriter to use
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 * @throws Exception any exception that occurred during document building
	 * @see com.lowagie.text.Document#open()
	 * @see com.lowagie.text.Document#close()
	 */
	protected abstract void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception;

}
