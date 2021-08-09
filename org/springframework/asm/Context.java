/***** Lobxxx Translate Finished ******/
/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.springframework.asm;

/**
 * Information about a class being parsed in a {@link ClassReader}.
 * 
 * <p>
 * 有关在{@link ClassReader}中解析课程的信息
 * 
 * 
 * @author Eric Bruneton
 */
class Context {

    /**
     * Prototypes of the attributes that must be parsed for this class.
     * <p>
     *  必须为此类解析的属性的原型
     * 
     */
    Attribute[] attrs;

    /**
     * The {@link ClassReader} option flags for the parsing of this class.
     * <p>
     *  用于解析此类的{@link ClassReader}选项标志
     * 
     */
    int flags;

    /**
     * The buffer used to read strings.
     * <p>
     *  用于读取字符串的缓冲区
     * 
     */
    char[] buffer;

    /**
     * The start index of each bootstrap method.
     * <p>
     *  每个引导方法的起始索引
     * 
     */
    int[] bootstrapMethods;

    /**
     * The access flags of the method currently being parsed.
     * <p>
     *  当前正在解析的方法的访问标志
     * 
     */
    int access;

    /**
     * The name of the method currently being parsed.
     * <p>
     *  当前正在解析的方法的名称
     * 
     */
    String name;

    /**
     * The descriptor of the method currently being parsed.
     * <p>
     *  目前正在解析的方法的描述符
     * 
     */
    String desc;

    /**
     * The label objects, indexed by bytecode offset, of the method currently
     * being parsed (only bytecode offsets for which a label is needed have a
     * non null associated Label object).
     * <p>
     *  由当前正在解析的方法(仅需要标签的字节码偏移量具有非空关联的Label对象)的由字节码偏移量索引的标签对象
     * 
     */
    Label[] labels;

    /**
     * The target of the type annotation currently being parsed.
     * <p>
     *  目前正在解析的注释类型的目标
     * 
     */
    int typeRef;

    /**
     * The path of the type annotation currently being parsed.
     * <p>
     *  当前正在解析的类型注释的路径
     * 
     */
    TypePath typePath;

    /**
     * The offset of the latest stack map frame that has been parsed.
     * <p>
     *  已解析的最新堆栈映射帧的偏移量
     * 
     */
    int offset;

    /**
     * The labels corresponding to the start of the local variable ranges in the
     * local variable type annotation currently being parsed.
     * <p>
     * 与局部变量开始对应的标签在当前正在解析的局部变量类型注释中区分
     * 
     */
    Label[] start;

    /**
     * The labels corresponding to the end of the local variable ranges in the
     * local variable type annotation currently being parsed.
     * <p>
     *  与局部变量的结尾相对应的标签在当前正在解析的局部变量类型注释中有所区别
     * 
     */
    Label[] end;

    /**
     * The local variable indices for each local variable range in the local
     * variable type annotation currently being parsed.
     * <p>
     *  当前正在解析的局部变量类型注释中的每个局部变量范围的局部变量索引
     * 
     */
    int[] index;

    /**
     * The encoding of the latest stack map frame that has been parsed.
     * <p>
     *  已解析的最新堆栈映射帧的编码
     * 
     */
    int mode;

    /**
     * The number of locals in the latest stack map frame that has been parsed.
     * <p>
     *  已解析的最新堆栈映射帧中的本地人数
     * 
     */
    int localCount;

    /**
     * The number locals in the latest stack map frame that has been parsed,
     * minus the number of locals in the previous frame.
     * <p>
     *  已解析的最新堆栈映射帧中的本地号码减去前一帧中的本地人数
     * 
     */
    int localDiff;

    /**
     * The local values of the latest stack map frame that has been parsed.
     * <p>
     *  已解析的最新堆栈映射帧的本地值
     * 
     */
    Object[] local;

    /**
     * The stack size of the latest stack map frame that has been parsed.
     * <p>
     *  已解析的最新堆栈映射帧的堆栈大小
     * 
     */
    int stackCount;

    /**
     * The stack values of the latest stack map frame that has been parsed.
     * <p>
     * 已解析的最新堆栈映射帧的堆栈值
     */
    Object[] stack;
}
