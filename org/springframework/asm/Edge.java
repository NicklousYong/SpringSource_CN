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
 * An edge in the control flow graph of a method body. See {@link Label Label}.
 * 
 * <p>
 * 方法体的控制流图中的边缘参见{@link Label Label}
 * 
 * 
 * @author Eric Bruneton
 */
class Edge {

    /**
     * Denotes a normal control flow graph edge.
     * <p>
     *  表示正常的控制流程图边
     * 
     */
    static final int NORMAL = 0;

    /**
     * Denotes a control flow graph edge corresponding to an exception handler.
     * More precisely any {@link Edge} whose {@link #info} is strictly positive
     * corresponds to an exception handler. The actual value of {@link #info} is
     * the index, in the {@link ClassWriter} type table, of the exception that
     * is catched.
     * <p>
     *  表示对应于异常处理程序的控制流图边缘更精确地说{@link #info}严格为正的任何{@link Edge}对应于异常处理程序{@link #info}的实际值是索引,在{@link ClassWriter}
     * 类型表,被捕获的异常。
     * 
     */
    static final int EXCEPTION = 0x7FFFFFFF;

    /**
     * Information about this control flow graph edge. If
     * {@link ClassWriter#COMPUTE_MAXS} is used this field is the (relative)
     * stack size in the basic block from which this edge originates. This size
     * is equal to the stack size at the "jump" instruction to which this edge
     * corresponds, relatively to the stack size at the beginning of the
     * originating basic block. If {@link ClassWriter#COMPUTE_FRAMES} is used,
     * this field is the kind of this control flow graph edge (i.e. NORMAL or
     * EXCEPTION).
     * <p>
     * 关于此控制流程图边缘的信息如果使用{@link ClassWriter#COMPUTE_MAXS},则此字段是此边沿起始的基本块中的(相对)堆栈大小此大小等于"跳转"指令到该边对应于相对于始发基本块开始
     * 处的堆栈大小如果使用{@link ClassWriter#COMPUTE_FRAMES},则该字段是此控制流图边缘的类型(即NORMAL或EXCEPTION)。
     * 
     */
    int info;

    /**
     * The successor block of the basic block from which this edge originates.
     * <p>
     *  该边缘起源的基本块的后继块
     * 
     */
    Label successor;

    /**
     * The next edge in the list of successors of the originating basic block.
     * See {@link Label#successors successors}.
     * <p>
     *  发起基本块的后继列表中的下一个边缘请参阅{@link标签#后继继承者}
     */
    Edge next;
}
