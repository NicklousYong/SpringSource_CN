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

package org.springframework.web.servlet.view.velocity;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Convenience subclass of VelocityViewResolver, adding support
 * for VelocityLayoutView and its properties.
 *
 * <p>See VelocityViewResolver's javadoc for general usage info.
 *
 * <p>
 *  VelocityViewResolver的便利子类,增加了对VelocityLayoutView及其属性的支持
 * 
 *  <p>请参阅VelocityViewResolver的javadoc以获取常规使用信息
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.7
 * @see VelocityViewResolver
 * @see VelocityLayoutView
 * @see #setLayoutUrl
 * @see #setLayoutKey
 * @see #setScreenContentKey
 * @deprecated as of Spring 4.3, in favor of FreeMarker
 */
@Deprecated
public class VelocityLayoutViewResolver extends VelocityViewResolver {

	private String layoutUrl;

	private String layoutKey;

	private String screenContentKey;


	/**
	 * Requires VelocityLayoutView.
	 * <p>
	 *  需要VelocityLayoutView
	 * 
	 * 
	 * @see VelocityLayoutView
	 */
	@Override
	protected Class<?> requiredViewClass() {
		return VelocityLayoutView.class;
	}

	/**
	 * Set the layout template to use. Default is "layout.vm".
	 * <p>
	 * 设置布局模板使用默认是"layoutvm"
	 * 
	 * 
	 * @param layoutUrl the template location (relative to the template
	 * root directory)
	 * @see VelocityLayoutView#setLayoutUrl
	 */
	public void setLayoutUrl(String layoutUrl) {
		this.layoutUrl = layoutUrl;
	}

	/**
	 * Set the context key used to specify an alternate layout to be used instead
	 * of the default layout. Screen content templates can override the layout
	 * template that they wish to be wrapped with by setting this value in the
	 * template, for example:<br>
	 * {@code #set($layout = "MyLayout.vm" )}
	 * <p>The default key is "layout", as illustrated above.
	 * <p>
	 *  设置用于指定要使用的替代布局的上下文键,而不是默认布局屏幕内容模板可以通过在模板中设置此值来覆盖他们希望包装的布局模板,例如：<br> {@code #set($ layout ="MyLayoutvm")}
	 *  <p>默认键为"layout",如上所示。
	 * 
	 * 
	 * @param layoutKey the name of the key you wish to use in your
	 * screen content templates to override the layout template
	 * @see VelocityLayoutView#setLayoutKey
	 */
	public void setLayoutKey(String layoutKey) {
		this.layoutKey = layoutKey;
	}

	/**
	 * Set the name of the context key that will hold the content of
	 * the screen within the layout template. This key must be present
	 * in the layout template for the current screen to be rendered.
	 * <p>Default is "screen_content": accessed in VTL as
	 * {@code $screen_content}.
	 * <p>
	 * 
	 * @param screenContentKey the name of the screen content key to use
	 * @see VelocityLayoutView#setScreenContentKey
	 */
	public void setScreenContentKey(String screenContentKey) {
		this.screenContentKey = screenContentKey;
	}


	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		VelocityLayoutView view = (VelocityLayoutView) super.buildView(viewName);
		// Use not-null checks to preserve VelocityLayoutView's defaults.
		if (this.layoutUrl != null) {
			view.setLayoutUrl(this.layoutUrl);
		}
		if (this.layoutKey != null) {
			view.setLayoutKey(this.layoutKey);
		}
		if (this.screenContentKey != null) {
			view.setScreenContentKey(this.screenContentKey);
		}
		return view;
	}

}
