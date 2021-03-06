/*
 * Copyright (C) 2016 Square, Inc.
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
package com.squareup.sqldelight.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.squareup.sqldelight.SqliteLexer
import com.squareup.sqldelight.lang.SqliteTokenTypes
import com.squareup.sqldelight.util.SqlitePsiUtils

sealed class SqliteElement(node: ASTNode) : ASTWrapperPsiElement(node), PsiNamedElement {
  protected var hardcodedName: String? = null

  abstract protected val ruleRefType: IElementType

  val id: IdentifierElement?
    get() = PsiTreeUtil.findChildOfType(this, IdentifierElement::class.java)

  override fun getTextOffset() = id?.textOffset ?: super.getTextOffset()
  override fun getName() = hardcodedName ?: id?.text ?: "unknown-name"
  override fun setName(name: String): PsiElement {
    id?.replace(SqlitePsiUtils.createLeafFromText(project, context, name, ruleRefType))
    hardcodedName = name
    return this
  }

  override fun subtreeChanged() {
    super.subtreeChanged()
    hardcodedName = null
  }

  internal class TableNameElement(node: ASTNode) : SqliteElement(node) {
    override val ruleRefType = SqliteTokenTypes.TOKEN_ELEMENT_TYPES[SqliteLexer.IDENTIFIER]

    fun isSameTable(other: TableNameElement?) = id?.name?.equals(other?.id?.name) ?: false
  }

  internal class ColumnNameElement(node: ASTNode) : SqliteElement(node) {
    override val ruleRefType = SqliteTokenTypes.TOKEN_ELEMENT_TYPES[SqliteLexer.IDENTIFIER]
  }

  internal class SqlStmtNameElement(node: ASTNode) : SqliteElement(node) {
    override val ruleRefType = SqliteTokenTypes.TOKEN_ELEMENT_TYPES[SqliteLexer.IDENTIFIER]
  }
}
