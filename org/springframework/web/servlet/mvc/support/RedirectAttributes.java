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

package org.springframework.web.servlet.mvc.support;

import java.util.Collection;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.servlet.FlashMap;

/**
 * A specialization of the {@link Model} interface that controllers can use to
 * select attributes for a redirect scenario. Since the intent of adding
 * redirect attributes is very explicit --  i.e. to be used for a redirect URL,
 * attribute values may be formatted as Strings and stored that way to make
 * them eligible to be appended to the query string or expanded as URI
 * variables in {@code org.springframework.web.servlet.view.RedirectView}.
 *
 * <p>This interface also provides a way to add flash attributes. For a
 * general overview of flash attributes see {@link FlashMap}. You can use
 * {@link RedirectAttributes} to store flash attributes and they will be
 * automatically propagated to the "output" FlashMap of the current request.
 *
 * <p>Example usage in an {@code @Controller}:
 * <pre class="code">
 * &#064;RequestMapping(value = "/accounts", method = RequestMethod.POST)
 * public String handle(Account account, BindingResult result, RedirectAttributes redirectAttrs) {
 *   if (result.hasErrors()) {
 *     return "accounts/new";
 *   }
 *   // Save account ...
 *   redirectAttrs.addAttribute("id", account.getId()).addFlashAttribute("message", "Account created!");
 *   return "redirect:/accounts/{id}";
 * }
 * </pre>
 *
 * <p>A RedirectAttributes model is empty when the method is called and is never
 * used unless the method returns a redirect view name or a RedirectView.
 *
 * <p>After the redirect, flash attributes are automatically added to the model
 * of the controller that serves the target URL.
 *
 * <p>
 * 控制器可用于为重定向场景选择属性的{@link Model}接口的专业化由于添加重定向属性的意图非常明确 - 即要用于重定向URL,属性值可以格式化为字符串和以这种方式存储,使其符合附加到查询字符串或扩
 * 展为{@code orgspringframeworkwebservletviewRedirectView}中的URI变量。
 * 
 *  <p>此界面还提供了添加Flash属性的方法有关Flash属性的一般概述,请参阅{@link FlashMap}您可以使用{@link RedirectAttributes}来存储Flash属性,并将
 * 自动传播到"输出"FlashMap的当前请求。
 * 
 *  <p> {@code @Controller}中的用法示例：
 * <pre class="code">
 * @RequestMapping(value ="/ accounts",method = RequestMethodPOST)public String handle(Account account,B
 * indingResult result,RedirectAttributes redirectAttrs){if(resulthasErrors()){return"accounts / new"; }
 *  //保存帐户redirectAttrsaddAttribute("id",accountgetId())addFlashAttribute("message","Account created！");
 * 返回"redirect：/ accounts / {id}"; }。
 * </pre>
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public interface RedirectAttributes extends Model {

	@Override
	RedirectAttributes addAttribute(String attributeName, Object attributeValue);

	@Override
	RedirectAttributes addAttribute(Object attributeValue);

	@Override
	RedirectAttributes addAllAttributes(Collection<?> attributeValues);

	@Override
	RedirectAttributes mergeAttributes(Map<String, ?> attributes);

	/**
	 * Add the given flash attribute.
	 * <p>
	 *  <p>当方法被调用时,RedirectAttributes模型为空,除非该方法返回重定向视图名称或RedirectView
	 * 
	 *  <p>重定向后,Flash属性将自动添加到为目标URL提供服务的控制器模型中
	 * 
	 * 
	 * @param attributeName the attribute name; never {@code null}
	 * @param attributeValue the attribute value; may be {@code null}
	 */
	RedirectAttributes addFlashAttribute(String attributeName, Object attributeValue);

	/**
	 * Add the given flash storage using a
	 * {@link org.springframework.core.Conventions#getVariableName generated name}.
	 * <p>
	 *  添加给定的Flash属性
	 * 
	 * 
	 * @param attributeValue the flash attribute value; never {@code null}
	 */
	RedirectAttributes addFlashAttribute(Object attributeValue);

	/**
	 * Return the attributes candidate for flash storage or an empty Map.
	 * <p>
	 *  使用{@link orgspringframeworkcoreConventions#getVariableName生成的名称}添加给定的闪存存储
	 * 
	 */
	Map<String, ?> getFlashAttributes();
}
