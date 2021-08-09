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
 * A label represents a position in the bytecode of a method. Labels are used
 * for jump, goto, and switch instructions, and for try catch blocks. A label
 * designates the <i>instruction</i> that is just after. Note however that there
 * can be other elements between a label and the instruction it designates (such
 * as other labels, stack map frames, line numbers, etc.).
 *
 * <p>
 * 标签表示方法的字节码中的位置标签用于跳转,转到和切换指令以及try catch块标签指定刚刚注册的<i>指令</i>,但是可以有标签与其指定的指令之间的其他元素(如其他标签,堆栈地图帧,行号等)
 * 
 * 
 * @author Eric Bruneton
 */
public class Label {

    /**
     * Indicates if this label is only used for debug attributes. Such a label
     * is not the start of a basic block, the target of a jump instruction, or
     * an exception handler. It can be safely ignored in control flow graph
     * analysis algorithms (for optimization purposes).
     * <p>
     *  指示此标签是否仅用于调试属性这样的标签不是基本块的开始,跳转指令的目标或异常处理程序在控制流图分析算法中可以安全地忽略(为了优化目的)
     * 
     */
    static final int DEBUG = 1;

    /**
     * Indicates if the position of this label is known.
     * <p>
     *  指示此标签的位置是否已知
     * 
     */
    static final int RESOLVED = 2;

    /**
     * Indicates if this label has been updated, after instruction resizing.
     * <p>
     *  指示调整大小后是否更新了此标签
     * 
     */
    static final int RESIZED = 4;

    /**
     * Indicates if this basic block has been pushed in the basic block stack.
     * See {@link MethodWriter#visitMaxs visitMaxs}.
     * <p>
     * 指示这个基本块是否被推入基本块堆栈请参阅{@link MethodWriter#visitMaxs visitMaxs}
     * 
     */
    static final int PUSHED = 8;

    /**
     * Indicates if this label is the target of a jump instruction, or the start
     * of an exception handler.
     * <p>
     *  指示此标签是跳转指令的目标还是异常处理程序的开始
     * 
     */
    static final int TARGET = 16;

    /**
     * Indicates if a stack map frame must be stored for this label.
     * <p>
     *  指示是否必须为此标签存储堆栈映射帧
     * 
     */
    static final int STORE = 32;

    /**
     * Indicates if this label corresponds to a reachable basic block.
     * <p>
     *  指示此标签是否对应于可达到的基本块
     * 
     */
    static final int REACHABLE = 64;

    /**
     * Indicates if this basic block ends with a JSR instruction.
     * <p>
     *  指示此基本块是否以JSR指令结束
     * 
     */
    static final int JSR = 128;

    /**
     * Indicates if this basic block ends with a RET instruction.
     * <p>
     *  指示此基本块是否以RET指令结束
     * 
     */
    static final int RET = 256;

    /**
     * Indicates if this basic block is the start of a subroutine.
     * <p>
     *  指示此基本块是否是子程序的开始
     * 
     */
    static final int SUBROUTINE = 512;

    /**
     * Indicates if this subroutine basic block has been visited by a
     * visitSubroutine(null, ...) call.
     * <p>
     *  指示此子程序基本块是否已由访问子程序(null,)调用访问
     * 
     */
    static final int VISITED = 1024;

    /**
     * Indicates if this subroutine basic block has been visited by a
     * visitSubroutine(!null, ...) call.
     * <p>
     *  指示该子程序基本块是否已被访问子程序(！null,)调用访问
     * 
     */
    static final int VISITED2 = 2048;

    /**
     * Field used to associate user information to a label. Warning: this field
     * is used by the ASM tree package. In order to use it with the ASM tree
     * package you must override the
     * {@code org.objectweb.asm.tree.MethodNode#getLabelNode} method.
     * <p>
     * 用于将用户信息与标签关联的字段警告：此字段由ASM树包使用为了将其与ASM树包一起使用,您必须覆盖{@code orgobjectwebasmtreeMethodNode#getLabelNode}方法
     * 。
     * 
     */
    public Object info;

    /**
     * Flags that indicate the status of this label.
     *
     * <p>
     *  指示此标签状态的标志
     * 
     * 
     * @see #DEBUG
     * @see #RESOLVED
     * @see #RESIZED
     * @see #PUSHED
     * @see #TARGET
     * @see #STORE
     * @see #REACHABLE
     * @see #JSR
     * @see #RET
     */
    int status;

    /**
     * The line number corresponding to this label, if known. If there are
     * several lines, each line is stored in a separate label, all linked via
     * their next field (these links are created in ClassReader and removed just
     * before visitLabel is called, so that this does not impact the rest of the
     * code).
     * <p>
     *  对应于该标签的行号,如果已知如果有几行,每行都存储在一个单独的标签中,全部通过其下一个字段链接(这些链接是在ClassReader中创建的,并且在调用visitLabel之前删除,因此这样做不影响其余
     * 代码)。
     * 
     */
    int line;

    /**
     * The position of this label in the code, if known.
     * <p>
     *  该标签在代码中的位置,如果已知
     * 
     */
    int position;

    /**
     * Number of forward references to this label, times two.
     * <p>
     *  此标签的前向引用次数,次数为2
     * 
     */
    private int referenceCount;

    /**
     * Informations about forward references. Each forward reference is
     * described by two consecutive integers in this array: the first one is the
     * position of the first byte of the bytecode instruction that contains the
     * forward reference, while the second is the position of the first byte of
     * the forward reference itself. In fact the sign of the first integer
     * indicates if this reference uses 2 or 4 bytes, and its absolute value
     * gives the position of the bytecode instruction. This array is also used
     * as a bitset to store the subroutines to which a basic block belongs. This
     * information is needed in {@link MethodWriter#visitMaxs}, after all
     * forward references have been resolved. Hence the same array can be used
     * for both purposes without problems.
     * <p>
     * 关于前向引用的信息每个前向引用由该数组中的两个连续整数描述：第一个是包含前向引用的字节码指令的第一个字节的位置,而第二个字节是前向的第一个字节的位置引用本身实际上第一个整数的符号表示此引用是使用2或4个
     * 字节,其绝对值给出了字节码指令的位置此数组还用作存储基本块所属的子例程的位组所有前向引用都被解析后,需要{@link MethodWriter#visitMaxs}中的信息。
     * 因此,同一个数组可以用于两个目的而没有问题。
     * 
     */
    private int[] srcAndRefPositions;

    // ------------------------------------------------------------------------

    /*
     * Fields for the control flow and data flow graph analysis algorithms (used
     * to compute the maximum stack size or the stack map frames). A control
     * flow graph contains one node per "basic block", and one edge per "jump"
     * from one basic block to another. Each node (i.e., each basic block) is
     * represented by the Label object that corresponds to the first instruction
     * of this basic block. Each node also stores the list of its successors in
     * the graph, as a linked list of Edge objects.
     *
     * The control flow analysis algorithms used to compute the maximum stack
     * size or the stack map frames are similar and use two steps. The first
     * step, during the visit of each instruction, builds information about the
     * state of the local variables and the operand stack at the end of each
     * basic block, called the "output frame", <i>relatively</i> to the frame
     * state at the beginning of the basic block, which is called the "input
     * frame", and which is <i>unknown</i> during this step. The second step, in
     * {@link MethodWriter#visitMaxs}, is a fix point algorithm that computes
     * information about the input frame of each basic block, from the input
     * state of the first basic block (known from the method signature), and by
     * the using the previously computed relative output frames.
     *
     * The algorithm used to compute the maximum stack size only computes the
     * relative output and absolute input stack heights, while the algorithm
     * used to compute stack map frames computes relative output frames and
     * absolute input frames.
     * <p>
     * 控制流和数据流图分析算法的领域(用于计算最大堆栈大小或堆栈映射帧)控制流程图包含一个节点,每个"基本块",每个"跳"从一个基本块到一个边另一个每个节点(即每个基本块)由对应于该基本块的第一个指令的Lab
     * el对象表示。
     * 每个节点还将其后继列表存储在图中,作为Edge对象的链接列表。
     * 
     * 用于计算最大堆栈大小或堆栈映射帧的控制流分析算法是相似的,并且使用两个步骤第一步,在每个指令的访问期间,建立关于局部变量的状态和操作数堆栈的信息的每个基本块,被称为"输出帧",<i>相对于基本块的开始处
     * 的帧状态,其被称为"输入帧",并且其是<i>未知</i> i>在此步骤中第二步,在{@link MethodWriter#visitMaxs}中,是从第一个基本块的输入状态(从方法签名中已知的)计算每个
     * 基本块的输入帧的信息的修正点算法),并通过使用先前计算的相对输出帧。
     * 
     * 用于计算最大堆栈大小的算法仅计算相对输出和绝对输入堆栈高度,而用于计算堆栈映射帧的算法计算相对输出帧和绝对输入帧
     * 
     */

    /**
     * Start of the output stack relatively to the input stack. The exact
     * semantics of this field depends on the algorithm that is used.
     *
     * When only the maximum stack size is computed, this field is the number of
     * elements in the input stack.
     *
     * When the stack map frames are completely computed, this field is the
     * offset of the first output stack element relatively to the top of the
     * input stack. This offset is always negative or null. A null offset means
     * that the output stack must be appended to the input stack. A -n offset
     * means that the first n output stack elements must replace the top n input
     * stack elements, and that the other elements must be appended to the input
     * stack.
     * <p>
     *  相对于输入堆栈的输出堆栈的开始此字段的确切语义取决于所使用的算法
     * 
     *  当仅计算最大堆栈大小时,该字段是输入堆栈中的元素数量
     * 
     * 当堆栈映射帧被完全计算时,该字段是第一个输出堆栈元素相对于输入堆栈顶部的偏移量此偏移量始终为负值或空值零偏移意味着输出堆栈必须附加到输入堆栈A-n偏移意味着前n个输出堆栈元素必须替换前n个输入堆栈元素,
     * 而其他元素必须附加到输入堆栈。
     * 
     */
    int inputStackTop;

    /**
     * Maximum height reached by the output stack, relatively to the top of the
     * input stack. This maximum is always positive or null.
     * <p>
     *  输出堆栈达到的最大高度,相对于输入堆栈顶部的最大高度此最大值始终为正或为零
     * 
     */
    int outputStackMax;

    /**
     * Information about the input and output stack map frames of this basic
     * block. This field is only used when {@link ClassWriter#COMPUTE_FRAMES}
     * option is used.
     * <p>
     *  有关此基本块的输入和输出堆栈映射帧的信息此字段仅在使用{@link ClassWriter#COMPUTE_FRAMES}选项时使用
     * 
     */
    Frame frame;

    /**
     * The successor of this label, in the order they are visited. This linked
     * list does not include labels used for debug info only. If
     * {@link ClassWriter#COMPUTE_FRAMES} option is used then, in addition, it
     * does not contain successive labels that denote the same bytecode position
     * (in this case only the first label appears in this list).
     * <p>
     * 此标签的后继者按访问顺序此链接列表不包括仅用于调试信息的标签如果使用{@link ClassWriter#COMPUTE_FRAMES}选项,此外,它不包含表示相同的连续标签字节码位置(在这种情况下,只
     * 有第一个标签出现在此列表中)。
     * 
     */
    Label successor;

    /**
     * The successors of this node in the control flow graph. These successors
     * are stored in a linked list of {@link Edge Edge} objects, linked to each
     * other by their {@link Edge#next} field.
     * <p>
     *  控制流图中此节点的后继者这些后继存储在{@link Edge Edge}对象的链接列表中,通过其{@link Edge#next}字段彼此链接
     * 
     */
    Edge successors;

    /**
     * The next basic block in the basic block stack. This stack is used in the
     * main loop of the fix point algorithm used in the second step of the
     * control flow analysis algorithms. It is also used in
     * {@link #visitSubroutine} to avoid using a recursive method, and in
     * ClassReader to temporarily store multiple source lines for a label.
     *
     * <p>
     * 基本块堆栈中的下一个基本块该堆栈用于控制流分析算法第二步中使用的固定点算法的主循环。
     * 它还用于{@link #visitSubroutine}以避免使用递归方法,并在ClassReader中临时存储一个标签的多个源行。
     * 
     * 
     * @see MethodWriter#visitMaxs
     */
    Label next;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new label.
     * <p>
     *  构建一个新标签
     * 
     */
    public Label() {
    }

    // ------------------------------------------------------------------------
    // Methods to compute offsets and to manage forward references
    // ------------------------------------------------------------------------

    /**
     * Returns the offset corresponding to this label. This offset is computed
     * from the start of the method's bytecode. <i>This method is intended for
     * {@link Attribute} sub classes, and is normally not needed by class
     * generators or adapters.</i>
     *
     * <p>
     *  返回与此标签相对应的偏移量此偏移量从方法字节码开始计算<i>此方法适用于{@link Attribute}子类,通常不需要类生成器或适配器</i>
     * 
     * 
     * @return the offset corresponding to this label.
     * @throws IllegalStateException
     *             if this label is not resolved yet.
     */
    public int getOffset() {
        if ((status & RESOLVED) == 0) {
            throw new IllegalStateException(
                    "Label offset position has not been resolved yet");
        }
        return position;
    }

    /**
     * Puts a reference to this label in the bytecode of a method. If the
     * position of the label is known, the offset is computed and written
     * directly. Otherwise, a null offset is written and a new forward reference
     * is declared for this label.
     *
     * <p>
     * 在方法的字节码中引用此标签如果标签的位置已知,则偏移量被计算并直接写入否则,写入零偏移并为该标签声明新的转发参考
     * 
     * 
     * @param owner
     *            the code writer that calls this method.
     * @param out
     *            the bytecode of the method.
     * @param source
     *            the position of first byte of the bytecode instruction that
     *            contains this label.
     * @param wideOffset
     *            <tt>true</tt> if the reference must be stored in 4 bytes, or
     *            <tt>false</tt> if it must be stored with 2 bytes.
     * @throws IllegalArgumentException
     *             if this label has not been created by the given code writer.
     */
    void put(final MethodWriter owner, final ByteVector out, final int source,
            final boolean wideOffset) {
        if ((status & RESOLVED) == 0) {
            if (wideOffset) {
                addReference(-1 - source, out.length);
                out.putInt(-1);
            } else {
                addReference(source, out.length);
                out.putShort(-1);
            }
        } else {
            if (wideOffset) {
                out.putInt(position - source);
            } else {
                out.putShort(position - source);
            }
        }
    }

    /**
     * Adds a forward reference to this label. This method must be called only
     * for a true forward reference, i.e. only if this label is not resolved
     * yet. For backward references, the offset of the reference can be, and
     * must be, computed and stored directly.
     *
     * <p>
     *  向此标签添加前向引用此方法必须仅用于正向引用,即仅当此标签尚未解析时才会调用此方法对于反向引用,引用的偏移量可以是且必须直接计算和存储
     * 
     * 
     * @param sourcePosition
     *            the position of the referencing instruction. This position
     *            will be used to compute the offset of this forward reference.
     * @param referencePosition
     *            the position where the offset for this forward reference must
     *            be stored.
     */
    private void addReference(final int sourcePosition,
            final int referencePosition) {
        if (srcAndRefPositions == null) {
            srcAndRefPositions = new int[6];
        }
        if (referenceCount >= srcAndRefPositions.length) {
            int[] a = new int[srcAndRefPositions.length + 6];
            System.arraycopy(srcAndRefPositions, 0, a, 0,
                    srcAndRefPositions.length);
            srcAndRefPositions = a;
        }
        srcAndRefPositions[referenceCount++] = sourcePosition;
        srcAndRefPositions[referenceCount++] = referencePosition;
    }

    /**
     * Resolves all forward references to this label. This method must be called
     * when this label is added to the bytecode of the method, i.e. when its
     * position becomes known. This method fills in the blanks that where left
     * in the bytecode by each forward reference previously added to this label.
     *
     * <p>
     *  解决对此标签的所有前向引用当将此标签添加到方法的字节码时,即当其位置变为已知时,必须调用此方法。此方法将填充字节码中剩余的每个前缀所指向的前缀的空白标签
     * 
     * 
     * @param owner
     *            the code writer that calls this method.
     * @param position
     *            the position of this label in the bytecode.
     * @param data
     *            the bytecode of the method.
     * @return <tt>true</tt> if a blank that was left for this label was to
     *         small to store the offset. In such a case the corresponding jump
     *         instruction is replaced with a pseudo instruction (using unused
     *         opcodes) using an unsigned two bytes offset. These pseudo
     *         instructions will need to be replaced with true instructions with
     *         wider offsets (4 bytes instead of 2). This is done in
     *         {@link MethodWriter#resizeInstructions}.
     * @throws IllegalArgumentException
     *             if this label has already been resolved, or if it has not
     *             been created by the given code writer.
     */
    boolean resolve(final MethodWriter owner, final int position,
            final byte[] data) {
        boolean needUpdate = false;
        this.status |= RESOLVED;
        this.position = position;
        int i = 0;
        while (i < referenceCount) {
            int source = srcAndRefPositions[i++];
            int reference = srcAndRefPositions[i++];
            int offset;
            if (source >= 0) {
                offset = position - source;
                if (offset < Short.MIN_VALUE || offset > Short.MAX_VALUE) {
                    /*
                     * changes the opcode of the jump instruction, in order to
                     * be able to find it later (see resizeInstructions in
                     * MethodWriter). These temporary opcodes are similar to
                     * jump instruction opcodes, except that the 2 bytes offset
                     * is unsigned (and can therefore represent values from 0 to
                     * 65535, which is sufficient since the size of a method is
                     * limited to 65535 bytes).
                     * <p>
                     * 改变跳转指令的操作码,以便稍后找到它(参见MethodWriter中的resizeInstructions)这些临时操作码类似于跳转指令操作码,除了2个字节的偏移是无符号的(因此可以表示从0到65535
                     * ,这是足够的,因为方法的大小限制为65535字节)。
                     * 
                     */
                    int opcode = data[reference - 1] & 0xFF;
                    if (opcode <= Opcodes.JSR) {
                        // changes IFEQ ... JSR to opcodes 202 to 217
                        data[reference - 1] = (byte) (opcode + 49);
                    } else {
                        // changes IFNULL and IFNONNULL to opcodes 218 and 219
                        data[reference - 1] = (byte) (opcode + 20);
                    }
                    needUpdate = true;
                }
                data[reference++] = (byte) (offset >>> 8);
                data[reference] = (byte) offset;
            } else {
                offset = position + source + 1;
                data[reference++] = (byte) (offset >>> 24);
                data[reference++] = (byte) (offset >>> 16);
                data[reference++] = (byte) (offset >>> 8);
                data[reference] = (byte) offset;
            }
        }
        return needUpdate;
    }

    /**
     * Returns the first label of the series to which this label belongs. For an
     * isolated label or for the first label in a series of successive labels,
     * this method returns the label itself. For other labels it returns the
     * first label of the series.
     *
     * <p>
     *  返回此标签所属系列的第一个标签对于隔离标签或一系列连续标签中的第一个标签,此方法返回标签本身对于其他标签,它返回该系列的第一个标签
     * 
     * 
     * @return the first label of the series to which this label belongs.
     */
    Label getFirst() {
        return !ClassReader.FRAMES || frame == null ? this : frame.owner;
    }

    // ------------------------------------------------------------------------
    // Methods related to subroutines
    // ------------------------------------------------------------------------

    /**
     * Returns true is this basic block belongs to the given subroutine.
     *
     * <p>
     *  返回true是这个基本块属于给定的子例程
     * 
     * 
     * @param id
     *            a subroutine id.
     * @return true is this basic block belongs to the given subroutine.
     */
    boolean inSubroutine(final long id) {
        if ((status & Label.VISITED) != 0) {
            return (srcAndRefPositions[(int) (id >>> 32)] & (int) id) != 0;
        }
        return false;
    }

    /**
     * Returns true if this basic block and the given one belong to a common
     * subroutine.
     *
     * <p>
     *  如果此基本块和给定的属性属于一个公共子例程,则返回true
     * 
     * 
     * @param block
     *            another basic block.
     * @return true if this basic block and the given one belong to a common
     *         subroutine.
     */
    boolean inSameSubroutine(final Label block) {
        if ((status & VISITED) == 0 || (block.status & VISITED) == 0) {
            return false;
        }
        for (int i = 0; i < srcAndRefPositions.length; ++i) {
            if ((srcAndRefPositions[i] & block.srcAndRefPositions[i]) != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Marks this basic block as belonging to the given subroutine.
     *
     * <p>
     * 将此基本块标记为属于给定子程序
     * 
     * 
     * @param id
     *            a subroutine id.
     * @param nbSubroutines
     *            the total number of subroutines in the method.
     */
    void addToSubroutine(final long id, final int nbSubroutines) {
        if ((status & VISITED) == 0) {
            status |= VISITED;
            srcAndRefPositions = new int[nbSubroutines / 32 + 1];
        }
        srcAndRefPositions[(int) (id >>> 32)] |= (int) id;
    }

    /**
     * Finds the basic blocks that belong to a given subroutine, and marks these
     * blocks as belonging to this subroutine. This method follows the control
     * flow graph to find all the blocks that are reachable from the current
     * block WITHOUT following any JSR target.
     *
     * <p>
     *  找到属于给定子程序的基本块,并将这些块标记为属于此子程序此方法遵循控制流程图,以查找从当前块可访问的所有块,不跟随任何JSR目标
     * 
     * 
     * @param JSR
     *            a JSR block that jumps to this subroutine. If this JSR is not
     *            null it is added to the successor of the RET blocks found in
     *            the subroutine.
     * @param id
     *            the id of this subroutine.
     * @param nbSubroutines
     *            the total number of subroutines in the method.
     */
    void visitSubroutine(final Label JSR, final long id, final int nbSubroutines) {
        // user managed stack of labels, to avoid using a recursive method
        // (recursivity can lead to stack overflow with very large methods)
        Label stack = this;
        while (stack != null) {
            // removes a label l from the stack
            Label l = stack;
            stack = l.next;
            l.next = null;

            if (JSR != null) {
                if ((l.status & VISITED2) != 0) {
                    continue;
                }
                l.status |= VISITED2;
                // adds JSR to the successors of l, if it is a RET block
                if ((l.status & RET) != 0) {
                    if (!l.inSameSubroutine(JSR)) {
                        Edge e = new Edge();
                        e.info = l.inputStackTop;
                        e.successor = JSR.successors.successor;
                        e.next = l.successors;
                        l.successors = e;
                    }
                }
            } else {
                // if the l block already belongs to subroutine 'id', continue
                if (l.inSubroutine(id)) {
                    continue;
                }
                // marks the l block as belonging to subroutine 'id'
                l.addToSubroutine(id, nbSubroutines);
            }
            // pushes each successor of l on the stack, except JSR targets
            Edge e = l.successors;
            while (e != null) {
                // if the l block is a JSR block, then 'l.successors.next' leads
                // to the JSR target (see {@link #visitJumpInsn}) and must
                // therefore not be followed
                if ((l.status & Label.JSR) == 0 || e != l.successors.next) {
                    // pushes e.successor on the stack if it not already added
                    if (e.successor.next == null) {
                        e.successor.next = stack;
                        stack = e.successor;
                    }
                }
                e = e.next;
            }
        }
    }

    // ------------------------------------------------------------------------
    // Overriden Object methods
    // ------------------------------------------------------------------------

    /**
     * Returns a string representation of this label.
     *
     * <p>
     *  返回此标签的字符串表示形式
     * 
     * @return a string representation of this label.
     */
    @Override
    public String toString() {
        return "L" + System.identityHashCode(this);
    }
}
