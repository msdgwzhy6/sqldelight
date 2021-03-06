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
package com.squareup.sqldelight.generating

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.squareup.sqldelight.SqliteCompiler.Status
import com.squareup.sqldelight.lang.SqliteFile

internal class SqlDocumentAnnotator : ExternalAnnotator<Status<PsiElement>, Status<PsiElement>>() {
  override fun collectInformation(file: PsiFile) = (file as SqliteFile).status
  override fun doAnnotate(status: Status<PsiElement>) = status
  override fun apply(file: PsiFile, status: Status<PsiElement>, holder: AnnotationHolder) {
    if (status.result == Status.Result.FAILURE) {
      holder.createErrorAnnotation(status.originatingElement, status.errorMessage)
    } else {
      val generatedFile = (file as SqliteFile).generatedFile ?: return
      val document = FileDocumentManager.getInstance().getDocument(generatedFile.virtualFile)
      document?.createGuardedBlock(0, document.textLength)
    }
  }
}
