/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

/**
 * Bean definition reader for a simple properties format.
 *
 * <p>Provides bean definition registration methods for Map/Properties and
 * ResourceBundle. Typically applied to a DefaultListableBeanFactory.
 *
 * <p><b>Example:</b>
 *
 * <pre class="code">
 * employee.(class)=MyClass       // bean is of class MyClass
 * employee.(abstract)=true       // this bean can't be instantiated directly
 * employee.group=Insurance       // real property
 * employee.usesDialUp=false      // real property (potentially overridden)
 *
 * salesrep.(parent)=employee     // derives from "employee" bean definition
 * salesrep.(lazy-init)=true      // lazily initialize this singleton bean
 * salesrep.manager(ref)=tony     // reference to another bean
 * salesrep.department=Sales      // real property
 *
 * techie.(parent)=employee       // derives from "employee" bean definition
 * techie.(scope)=prototype       // bean is a prototype (not a shared instance)
 * techie.manager(ref)=jeff       // reference to another bean
 * techie.department=Engineering  // real property
 * techie.usesDialUp=true         // real property (overriding parent value)
 *
 * ceo.$0(ref)=secretary          // inject 'secretary' bean as 0th constructor arg
 * ceo.$1=1000000                 // inject value '1000000' at 1st constructor arg
 * </pre>
 *
 * <p>
 *  Bean定义阅读器,用于简单的属性格式
 * 
 *  <p>为Map / Properties和ResourceBundle提供bean定义注册方法通常应用于DefaultListableBeanFactory
 * 
 *  <P> <B>示例：</b>的
 * 
 * <pre class="code">
 * employee(class)= MyClass // bean是MyClass类的员工(抽象)= true //这个bean不能被直接实例化employeegroup =保险// real prope
 * rty employeeusesDialUp = false //不动产(可能被覆盖)。
 * 
 *  salesrep(parent)= employee //派生自"employee"bean定义salesrep(lazy-init)= true //懒惰地初始化此单例bean salesrepma
 * nager(ref)= tony //引用另一个bean salesrepdepartment = Sales // real property。
 * 
 * techie(parent)= employee // from"employee"bean definition techie(scope)= prototype // bean是一个原型(不是共享实
 * 例)techiemanager(ref)= jeff //引用另一个bean techiedepartment =工程// real property techieusesDialUp = true /
 * /不动产(覆盖父值)。
 * 
 *  ceo $ 0(ref)= secretary //将'secretary'bean作为第0个构造函数arg ceo $ 1 = 1000000 //在第一个构造函数arg注入值'1000000'
 * </pre>
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 26.11.2003
 * @see DefaultListableBeanFactory
 */
public class PropertiesBeanDefinitionReader extends AbstractBeanDefinitionReader {

	/**
	 * Value of a T/F attribute that represents true.
	 * Anything else represents false. Case seNsItive.
	 * <p>
	 *  代表真实的T / F属性的值任何其他都代表虚假案例seNsItive
	 * 
	 */
	public static final String TRUE_VALUE = "true";

	/**
	 * Separator between bean name and property name.
	 * We follow normal Java conventions.
	 * <p>
	 *  bean名称和属性名之间的分隔符遵循正常的Java约定
	 * 
	 */
	public static final String SEPARATOR = ".";

	/**
	 * Special key to distinguish {@code owner.(class)=com.myapp.MyClass}-
	 * <p>
	 *  区分{@code所有者(类)= commyappMyClass}的特殊键 - 
	 * 
	 */
	public static final String CLASS_KEY = "(class)";

	/**
	 * Special key to distinguish {@code owner.(parent)=parentBeanName}.
	 * <p>
	 * 区分{@code owner(parent)= parentBeanName}的特殊键
	 * 
	 */
	public static final String PARENT_KEY = "(parent)";

	/**
	 * Special key to distinguish {@code owner.(scope)=prototype}.
	 * Default is "true".
	 * <p>
	 *  区分{@code owner(scope)= prototype}的特殊键默认为"true"
	 * 
	 */
	public static final String SCOPE_KEY = "(scope)";

	/**
	 * Special key to distinguish {@code owner.(singleton)=false}.
	 * Default is "true".
	 * <p>
	 *  区分{@code owner(singleton)= false}的特殊键默认为"true"
	 * 
	 */
	public static final String SINGLETON_KEY = "(singleton)";

	/**
	 * Special key to distinguish {@code owner.(abstract)=true}
	 * Default is "false".
	 * <p>
	 *  区分{@code owner(abstract)= true}的特殊键默认为"false"
	 * 
	 */
	public static final String ABSTRACT_KEY = "(abstract)";

	/**
	 * Special key to distinguish {@code owner.(lazy-init)=true}
	 * Default is "false".
	 * <p>
	 *  区分{@code所有者(lazy-init)= true}的特殊键默认为"false"
	 * 
	 */
	public static final String LAZY_INIT_KEY = "(lazy-init)";

	/**
	 * Property suffix for references to other beans in the current
	 * BeanFactory: e.g. {@code owner.dog(ref)=fido}.
	 * Whether this is a reference to a singleton or a prototype
	 * will depend on the definition of the target bean.
	 * <p>
	 *  用于引用当前BeanFactory中其他bean的属性后缀：例如{@code ownerdog(ref)= fido}这是对单例或原型的引用将取决于目标bean的定义
	 * 
	 */
	public static final String REF_SUFFIX = "(ref)";

	/**
	 * Prefix before values referencing other beans.
	 * <p>
	 *  在引用其他bean之前的前缀
	 * 
	 */
	public static final String REF_PREFIX = "*";

	/**
	 * Prefix used to denote a constructor argument definition.
	 * <p>
	 *  前缀用于表示构造函数的参数定义
	 * 
	 */
	public static final String CONSTRUCTOR_ARG_PREFIX = "$";


	private String defaultParentBean;

	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();


	/**
	 * Create new PropertiesBeanDefinitionReader for the given bean factory.
	 * <p>
	 *  为给定的bean工厂创建新的PropertiesBeanDefinitionReader
	 * 
	 * 
	 * @param registry the BeanFactory to load bean definitions into,
	 * in the form of a BeanDefinitionRegistry
	 */
	public PropertiesBeanDefinitionReader(BeanDefinitionRegistry registry) {
		super(registry);
	}


	/**
	 * Set the default parent bean for this bean factory.
	 * If a child bean definition handled by this factory provides neither
	 * a parent nor a class attribute, this default value gets used.
	 * <p>Can be used e.g. for view definition files, to define a parent
	 * with a default view class and common attributes for all views.
	 * View definitions that define their own parent or carry their own
	 * class can still override this.
	 * <p>Strictly speaking, the rule that a default parent setting does
	 * not apply to a bean definition that carries a class is there for
	 * backwards compatibility reasons. It still matches the typical use case.
	 * <p>
	 * 设置此bean工厂的默认父bean如果由此工厂处理的子bean定义不提供父类和类属性,则使用此默认值<p>可以使用例如视图定义文件来定义父对象所有视图的默认视图类和公共属性查看定义自己的父级或携带自己的
	 * 类的定义仍然可以覆盖此<p>严格来说,默认父设置不适用于携带类的bean定义的规则是有向后兼容性原因它仍然符合典型的用例。
	 * 
	 */
	public void setDefaultParentBean(String defaultParentBean) {
		this.defaultParentBean = defaultParentBean;
	}

	/**
	 * Return the default parent bean for this bean factory.
	 * <p>
	 *  返回此bean工厂的默认父bean
	 * 
	 */
	public String getDefaultParentBean() {
		return this.defaultParentBean;
	}

	/**
	 * Set the PropertiesPersister to use for parsing properties files.
	 * The default is DefaultPropertiesPersister.
	 * <p>
	 *  设置PropertiesPersister用于解析属性文件默认值为DefaultPropertiesPersister
	 * 
	 * 
	 * @see org.springframework.util.DefaultPropertiesPersister
	 */
	public void setPropertiesPersister(PropertiesPersister propertiesPersister) {
		this.propertiesPersister =
				(propertiesPersister != null ? propertiesPersister : new DefaultPropertiesPersister());
	}

	/**
	 * Return the PropertiesPersister to use for parsing properties files.
	 * <p>
	 * 返回PropertiesPersister用于解析属性文件
	 * 
	 */
	public PropertiesPersister getPropertiesPersister() {
		return this.propertiesPersister;
	}


	/**
	 * Load bean definitions from the specified properties file,
	 * using all property keys (i.e. not filtering by prefix).
	 * <p>
	 *  从指定的属性文件中加载bean定义,使用所有属性键(即不使用前缀过滤)
	 * 
	 * 
	 * @param resource the resource descriptor for the properties file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource, String)
	 */
	@Override
	public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource), null);
	}

	/**
	 * Load bean definitions from the specified properties file.
	 * <p>
	 *  从指定的属性文件加载bean定义
	 * 
	 * 
	 * @param resource the resource descriptor for the properties file
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or {@code null})
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(Resource resource, String prefix) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource), prefix);
	}

	/**
	 * Load bean definitions from the specified properties file.
	 * <p>
	 *  从指定的属性文件加载bean定义
	 * 
	 * 
	 * @param encodedResource the resource descriptor for the properties file,
	 * allowing to specify an encoding to use for parsing the file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(encodedResource, null);
	}

	/**
	 * Load bean definitions from the specified properties file.
	 * <p>
	 *  从指定的属性文件加载bean定义
	 * 
	 * 
	 * @param encodedResource the resource descriptor for the properties file,
	 * allowing to specify an encoding to use for parsing the file
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or {@code null})
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(EncodedResource encodedResource, String prefix)
			throws BeanDefinitionStoreException {

		Properties props = new Properties();
		try {
			InputStream is = encodedResource.getResource().getInputStream();
			try {
				if (encodedResource.getEncoding() != null) {
					getPropertiesPersister().load(props, new InputStreamReader(is, encodedResource.getEncoding()));
				}
				else {
					getPropertiesPersister().load(props, is);
				}
			}
			finally {
				is.close();
			}
			return registerBeanDefinitions(props, prefix, encodedResource.getResource().getDescription());
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException("Could not parse properties from " + encodedResource.getResource(), ex);
		}
	}

	/**
	 * Register bean definitions contained in a resource bundle,
	 * using all property keys (i.e. not filtering by prefix).
	 * <p>
	 *  包含在资源包中的bean定义使用所有属性键(即不使用前缀过滤)
	 * 
	 * 
	 * @param rb the ResourceBundle to load from
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #registerBeanDefinitions(java.util.ResourceBundle, String)
	 */
	public int registerBeanDefinitions(ResourceBundle rb) throws BeanDefinitionStoreException {
		return registerBeanDefinitions(rb, null);
	}

	/**
	 * Register bean definitions contained in a ResourceBundle.
	 * <p>Similar syntax as for a Map. This method is useful to enable
	 * standard Java internationalization support.
	 * <p>
	 *  包含在ResourceBundle中的注册bean定义<p>与Map类似的语法该方法对于启用标准Java国际化支持非常有用
	 * 
	 * 
	 * @param rb the ResourceBundle to load from
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or {@code null})
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int registerBeanDefinitions(ResourceBundle rb, String prefix) throws BeanDefinitionStoreException {
		// Simply create a map and call overloaded method.
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration<String> keys = rb.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			map.put(key, rb.getObject(key));
		}
		return registerBeanDefinitions(map, prefix);
	}


	/**
	 * Register bean definitions contained in a Map,
	 * using all property keys (i.e. not filtering by prefix).
	 * <p>
	 *  包含在Map中的注册bean定义,使用所有属性键(即不使用前缀过滤)
	 * 
	 * 
	 * @param map Map: name -> property (String or Object). Property values
	 * will be strings if coming from a Properties file etc. Property names
	 * (keys) <b>must</b> be Strings. Class keys must be Strings.
	 * @return the number of bean definitions found
	 * @throws BeansException in case of loading or parsing errors
	 * @see #registerBeanDefinitions(java.util.Map, String, String)
	 */
	public int registerBeanDefinitions(Map<?, ?> map) throws BeansException {
		return registerBeanDefinitions(map, null);
	}

	/**
	 * Register bean definitions contained in a Map.
	 * Ignore ineligible properties.
	 * <p>
	 * 包含在Map中的注册bean定义忽略不符合条件的属性
	 * 
	 * 
	 * @param map Map name -> property (String or Object). Property values
	 * will be strings if coming from a Properties file etc. Property names
	 * (keys) <b>must</b> be Strings. Class keys must be Strings.
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or {@code null})
	 * @return the number of bean definitions found
	 * @throws BeansException in case of loading or parsing errors
	 */
	public int registerBeanDefinitions(Map<?, ?> map, String prefix) throws BeansException {
		return registerBeanDefinitions(map, prefix, "Map " + map);
	}

	/**
	 * Register bean definitions contained in a Map.
	 * Ignore ineligible properties.
	 * <p>
	 *  包含在Map中的注册bean定义忽略不符合条件的属性
	 * 
	 * 
	 * @param map Map name -> property (String or Object). Property values
	 * will be strings if coming from a Properties file etc. Property names
	 * (keys) <b>must</b> be strings. Class keys must be Strings.
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or {@code null})
	 * @param resourceDescription description of the resource that the
	 * Map came from (for logging purposes)
	 * @return the number of bean definitions found
	 * @throws BeansException in case of loading or parsing errors
	 * @see #registerBeanDefinitions(Map, String)
	 */
	public int registerBeanDefinitions(Map<?, ?> map, String prefix, String resourceDescription)
			throws BeansException {

		if (prefix == null) {
			prefix = "";
		}
		int beanCount = 0;

		for (Object key : map.keySet()) {
			if (!(key instanceof String)) {
				throw new IllegalArgumentException("Illegal key [" + key + "]: only Strings allowed");
			}
			String keyString = (String) key;
			if (keyString.startsWith(prefix)) {
				// Key is of form: prefix<name>.property
				String nameAndProperty = keyString.substring(prefix.length());
				// Find dot before property name, ignoring dots in property keys.
				int sepIdx = -1;
				int propKeyIdx = nameAndProperty.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX);
				if (propKeyIdx != -1) {
					sepIdx = nameAndProperty.lastIndexOf(SEPARATOR, propKeyIdx);
				}
				else {
					sepIdx = nameAndProperty.lastIndexOf(SEPARATOR);
				}
				if (sepIdx != -1) {
					String beanName = nameAndProperty.substring(0, sepIdx);
					if (logger.isDebugEnabled()) {
						logger.debug("Found bean name '" + beanName + "'");
					}
					if (!getRegistry().containsBeanDefinition(beanName)) {
						// If we haven't already registered it...
						registerBeanDefinition(beanName, map, prefix + beanName, resourceDescription);
						++beanCount;
					}
				}
				else {
					// Ignore it: It wasn't a valid bean name and property,
					// although it did start with the required prefix.
					if (logger.isDebugEnabled()) {
						logger.debug("Invalid bean name and property [" + nameAndProperty + "]");
					}
				}
			}
		}

		return beanCount;
	}

	/**
	 * Get all property values, given a prefix (which will be stripped)
	 * and add the bean they define to the factory with the given name
	 * <p>
	 *  获取所有属性值,给定一个前缀(将被剥离),并使用给定的名称将其定义的bean添加到工厂
	 * 
	 * 
	 * @param beanName name of the bean to define
	 * @param map Map containing string pairs
	 * @param prefix prefix of each entry, which will be stripped
	 * @param resourceDescription description of the resource that the
	 * Map came from (for logging purposes)
	 * @throws BeansException if the bean definition could not be parsed or registered
	 */
	protected void registerBeanDefinition(String beanName, Map<?, ?> map, String prefix, String resourceDescription)
			throws BeansException {

		String className = null;
		String parent = null;
		String scope = GenericBeanDefinition.SCOPE_SINGLETON;
		boolean isAbstract = false;
		boolean lazyInit = false;

		ConstructorArgumentValues cas = new ConstructorArgumentValues();
		MutablePropertyValues pvs = new MutablePropertyValues();

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String key = StringUtils.trimWhitespace((String) entry.getKey());
			if (key.startsWith(prefix + SEPARATOR)) {
				String property = key.substring(prefix.length() + SEPARATOR.length());
				if (CLASS_KEY.equals(property)) {
					className = StringUtils.trimWhitespace((String) entry.getValue());
				}
				else if (PARENT_KEY.equals(property)) {
					parent = StringUtils.trimWhitespace((String) entry.getValue());
				}
				else if (ABSTRACT_KEY.equals(property)) {
					String val = StringUtils.trimWhitespace((String) entry.getValue());
					isAbstract = TRUE_VALUE.equals(val);
				}
				else if (SCOPE_KEY.equals(property)) {
					// Spring 2.0 style
					scope = StringUtils.trimWhitespace((String) entry.getValue());
				}
				else if (SINGLETON_KEY.equals(property)) {
					// Spring 1.2 style
					String val = StringUtils.trimWhitespace((String) entry.getValue());
					scope = ((val == null || TRUE_VALUE.equals(val) ? GenericBeanDefinition.SCOPE_SINGLETON :
							GenericBeanDefinition.SCOPE_PROTOTYPE));
				}
				else if (LAZY_INIT_KEY.equals(property)) {
					String val = StringUtils.trimWhitespace((String) entry.getValue());
					lazyInit = TRUE_VALUE.equals(val);
				}
				else if (property.startsWith(CONSTRUCTOR_ARG_PREFIX)) {
					if (property.endsWith(REF_SUFFIX)) {
						int index = Integer.parseInt(property.substring(1, property.length() - REF_SUFFIX.length()));
						cas.addIndexedArgumentValue(index, new RuntimeBeanReference(entry.getValue().toString()));
					}
					else {
						int index = Integer.parseInt(property.substring(1));
						cas.addIndexedArgumentValue(index, readValue(entry));
					}
				}
				else if (property.endsWith(REF_SUFFIX)) {
					// This isn't a real property, but a reference to another prototype
					// Extract property name: property is of form dog(ref)
					property = property.substring(0, property.length() - REF_SUFFIX.length());
					String ref = StringUtils.trimWhitespace((String) entry.getValue());

					// It doesn't matter if the referenced bean hasn't yet been registered:
					// this will ensure that the reference is resolved at runtime.
					Object val = new RuntimeBeanReference(ref);
					pvs.add(property, val);
				}
				else {
					// It's a normal bean property.
					pvs.add(property, readValue(entry));
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Registering bean definition for bean name '" + beanName + "' with " + pvs);
		}

		// Just use default parent if we're not dealing with the parent itself,
		// and if there's no class name specified. The latter has to happen for
		// backwards compatibility reasons.
		if (parent == null && className == null && !beanName.equals(this.defaultParentBean)) {
			parent = this.defaultParentBean;
		}

		try {
			AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(
					parent, className, getBeanClassLoader());
			bd.setScope(scope);
			bd.setAbstract(isAbstract);
			bd.setLazyInit(lazyInit);
			bd.setConstructorArgumentValues(cas);
			bd.setPropertyValues(pvs);
			getRegistry().registerBeanDefinition(beanName, bd);
		}
		catch (ClassNotFoundException ex) {
			throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
		}
		catch (LinkageError err) {
			throw new CannotLoadBeanClassException(resourceDescription, beanName, className, err);
		}
	}

	/**
	 * Reads the value of the entry. Correctly interprets bean references for
	 * values that are prefixed with an asterisk.
	 * <p>
	 *  读取条目的值正确解释带有前缀为星号的值的bean引用
	 */
	private Object readValue(Map.Entry<? ,?> entry) {
		Object val = entry.getValue();
		if (val instanceof String) {
			String strVal = (String) val;
			// If it starts with a reference prefix...
			if (strVal.startsWith(REF_PREFIX)) {
				// Expand the reference.
				String targetName = strVal.substring(1);
				if (targetName.startsWith(REF_PREFIX)) {
					// Escaped prefix -> use plain value.
					val = targetName;
				}
				else {
					val = new RuntimeBeanReference(targetName);
				}
			}
		}
		return val;
	}

}
