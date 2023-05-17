package com.github.icoder.wizardgpt.actions.lang

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.tree.ICompositeElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

/**
 * single-line
 * - element1#startOffset, element2#endOffset equals commonParentElement#startOffset,#endOffset.
 * multi-line
 * - element2 is compositeElement (e.g. LOCAL_VARIABLE, etc) && commonParentElement is CODE_BLOCK.
 * method-signature
 *
 */
@Suppress("UnstableApiUsage")
class JavaElementTypeMatcher : ElementTypeMatcher {
    private fun isErrorElement(element: PsiElement) = element is PsiErrorElement
    private fun isCommentElement(element: PsiElement) = element is PsiComment

    override fun isCodeBlock(ele: PsiElement): Boolean = isErrorElement(ele).not()
            && ele.elementType?.toString() == "CODE_BLOCK"

    override fun isStatement(ele: PsiElement): Boolean = isErrorElement(ele).not()
            && (isCommentElement(ele)
            || when (ele.elementType.takeIf { it is ICompositeElementType }?.toString() ?: false)
            {
                "LOCAL_VARIABLE", "EXPRESSION_STATEMENT", "DECLARATION_STATEMENT", "BLOCK_STATEMENT" -> true
                "IF_STATEMENT", "SWITCH_STATEMENT", "SWITCH_LABEL_STATEMENT" -> true
                "FOR_STATEMENT", "FOREACH_STATEMENT", "WHILE_STATEMENT", "DO_WHILE_STATEMENT" -> true
                "BREAK_STATEMENT", "YIELD_STATEMENT", "CONTINUE_STATEMENT", "RETURN_STATEMENT" -> true
                "TRY_STATEMENT", "CATCH_SECTION", "THROW_STATEMENT" -> true
                "THIS_EXPRESSION", "SUPER_EXPRESSION" -> true
                "SYNCHRONIZED_STATEMENT" -> true
                else -> false
            })

    /**
     * Example:
     *
     * [[start]  parent  [end]]
     * String userName = "abc";
     */
    override fun isStatement(start: PsiElement, end: PsiElement, parent: PsiElement): Boolean = isStatement(parent)
            && start.startOffset == parent.startOffset
            && end.endOffset == parent.endOffset

    /**
     * Example:
     *
     * [[start]  parent]
     * String a = "abc";
     * [parent [end]]
     * int b = 1000;
     */
    override fun isCodeBlock(start: PsiElement, end: PsiElement, parent: PsiElement): Boolean = isCodeBlock(parent)
            && PsiTreeUtil.findFirstParent(start)   { isStatement(it)}?.startOffset == start.startOffset
            && PsiTreeUtil.findFirstParent(end)     { isStatement(it)}?.endOffset   == end.endOffset
}