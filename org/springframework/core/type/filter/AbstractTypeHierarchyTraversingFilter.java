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

package org.springframework.core.type.filter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/**
 * Type filter that is aware of traversing over hierarchy.
 *
 * <p>This filter is useful when matching needs to be made based on potentially the
 * whole class/interface hierarchy. The algorithm employed uses a succeed-fast
 * strategy: if at any time a match is declared, no further processing is
 * carried out.
 *
 * <p>
 *  注意遍历层次结构的类型过滤器
 * 
 * <p>当基于潜在的整个类/接口层次结构进行匹配时,该过滤器很有用。所使用的算法使用成功的快速策略：如果在任何时候声明匹配,则不进行进一步的处理
 * 
 * 
 * @author Ramnivas Laddad
 * @author Mark Fisher
 * @since 2.5
 */
public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter {

	protected final Log logger = LogFactory.getLog(getClass());

	private final boolean considerInherited;

	private final boolean considerInterfaces;


	protected AbstractTypeHierarchyTraversingFilter(boolean considerInherited, boolean considerInterfaces) {
		this.considerInherited = considerInherited;
		this.considerInterfaces = considerInterfaces;
	}


	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {

		// This method optimizes avoiding unnecessary creation of ClassReaders
		// as well as visiting over those readers.
		if (matchSelf(metadataReader)) {
			return true;
		}
		ClassMetadata metadata = metadataReader.getClassMetadata();
		if (matchClassName(metadata.getClassName())) {
			return true;
		}

		if (this.considerInherited) {
			if (metadata.hasSuperClass()) {
				// Optimization to avoid creating ClassReader for super class.
				Boolean superClassMatch = matchSuperClass(metadata.getSuperClassName());
				if (superClassMatch != null) {
					if (superClassMatch.booleanValue()) {
						return true;
					}
				}
				else {
					// Need to read super class to determine a match...
					try {
						if (match(metadata.getSuperClassName(), metadataReaderFactory)) {
							return true;
						}
					}
					catch (IOException ex) {
						logger.debug("Could not read super class [" + metadata.getSuperClassName() +
								"] of type-filtered class [" + metadata.getClassName() + "]");
					}
 				}
			}
		}

		if (this.considerInterfaces) {
			for (String ifc : metadata.getInterfaceNames()) {
				// Optimization to avoid creating ClassReader for super class
				Boolean interfaceMatch = matchInterface(ifc);
				if (interfaceMatch != null) {
					if (interfaceMatch.booleanValue()) {
						return true;
					}
				}
				else {
					// Need to read interface to determine a match...
					try {
						if (match(ifc, metadataReaderFactory)) {
							return true;
						}
					}
					catch (IOException ex) {
						logger.debug("Could not read interface [" + ifc + "] for type-filtered class [" +
								metadata.getClassName() + "]");
					}
				}
			}
		}

		return false;
	}

	private boolean match(String className, MetadataReaderFactory metadataReaderFactory) throws IOException {
		return match(metadataReaderFactory.getMetadataReader(className), metadataReaderFactory);
	}

	/**
	 * Override this to match self characteristics alone. Typically,
	 * the implementation will use a visitor to extract information
	 * to perform matching.
	 * <p>
	 *  覆盖此以匹配自身特性通常,实现将使用访问者提取信息来执行匹配
	 * 
	 */
	protected boolean matchSelf(MetadataReader metadataReader) {
		return false;
	}

	/**
	 * Override this to match on type name.
	 * <p>
	 *  覆盖此类型以匹配类型名称
	 * 
	 */
	protected boolean matchClassName(String className) {
		return false;
	}

	/**
	 * Override this to match on super type name.
	 * <p>
	 *  覆盖此项以匹配超类型名称
	 * 
	 */
	protected Boolean matchSuperClass(String superClassName) {
		return null;
	}

	/**
	 * Override this to match on interface type name.
	 * <p>
	 *  覆盖此项以匹配接口类型名称
	 */
	protected Boolean matchInterface(String interfaceName) {
		return null;
	}

}
