/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.jmx.export.metadata;

import org.springframework.jmx.support.MetricType;

/**
 * Metadata that indicates to expose a given bean property as a JMX attribute,
 * with additional descriptor properties that indicate that the attribute is a
 * metric. Only valid when used on a JavaBean getter.
 *
 * <p>
 *  指定将给定的bean属性公开为JMX属性的元数据,其中附加的描述符属性指示该属性是一个度量仅当在JavaBean getter上使用时才有效
 * 
 * 
 * @author Jennifer Hickey
 * @since 3.0
 * @see org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler
 */
public class ManagedMetric extends AbstractJmxAttribute {

	private String category = "";

	private String displayName = "";

	private MetricType metricType = MetricType.GAUGE;

	private int persistPeriod = -1;

	private String persistPolicy = "";

	private String unit = "";


	/**
	 * The category of this metric (ex. throughput, performance, utilization).
	 * <p>
	 * 该度量的类别(吞吐量,性能,利用率)
	 * 
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * The category of this metric (ex. throughput, performance, utilization).
	 * <p>
	 *  该度量的类别(吞吐量,性能,利用率)
	 * 
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * A display name for this metric.
	 * <p>
	 *  此度量的显示名称
	 * 
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * A display name for this metric.
	 * <p>
	 *  此度量的显示名称
	 * 
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * A description of how this metric's values change over time.
	 * <p>
	 *  关于这个度量值如何随时间变化的描述
	 * 
	 */
	public void setMetricType(MetricType metricType) {
		this.metricType = metricType;
	}

	/**
	 * A description of how this metric's values change over time.
	 * <p>
	 *  关于这个度量值如何随时间变化的描述
	 * 
	 */
	public MetricType getMetricType() {
		return this.metricType;
	}

	/**
	 * The persist period for this metric.
	 * <p>
	 *  该指标的持续时间
	 * 
	 */
	public void setPersistPeriod(int persistPeriod) {
		this.persistPeriod = persistPeriod;
	}

	/**
	 * The persist period for this metric.
	 * <p>
	 *  该指标的持续时间
	 * 
	 */
	public int getPersistPeriod() {
		return this.persistPeriod;
	}

	/**
	 * The persist policy for this metric.
	 * <p>
	 *  该指标的持续策略
	 * 
	 */
	public void setPersistPolicy(String persistPolicy) {
		this.persistPolicy = persistPolicy;
	}

	/**
	 * The persist policy for this metric.
	 * <p>
	 *  该指标的持续策略
	 * 
	 */
	public String getPersistPolicy() {
		return this.persistPolicy;
	}

	/**
	 * The expected unit of measurement values.
	 * <p>
	 *  预期的测量单位值
	 * 
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * The expected unit of measurement values.
	 * <p>
	 *  预期的测量单位值
	 */
	public String getUnit() {
		return this.unit;
	}

}
