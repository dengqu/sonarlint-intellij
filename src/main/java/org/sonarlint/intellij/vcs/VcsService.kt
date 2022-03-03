/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015-2022 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.vcs

import com.intellij.openapi.project.Project
import org.sonarlint.intellij.common.util.SonarLintUtils
import org.sonarlint.intellij.core.ProjectBindingManager
import org.sonarsource.sonarlint.core.vcs.GitUtils
import java.nio.file.Paths

class VcsService(val project: Project) {
    private var resolvedServerBranchName: String? = null

    fun resolveServerBranch(): String? {
        val previousResolvedBranchName = resolvedServerBranchName
        resolvedServerBranchName = null
        val bindingManager = SonarLintUtils.getService(project, ProjectBindingManager::class.java)
        if (!bindingManager.isBindingValid) return null
        val basePath = project.basePath ?: return null
        val repository = GitUtils.getRepositoryForDir(Paths.get(basePath)) ?: return null
        val validConnectedEngine = bindingManager.validConnectedEngine ?: return null
        val serverBranches = validConnectedEngine.getServerBranches(bindingManager.mainProjectKey!!)
        resolvedServerBranchName = GitUtils.electBestMatchingServerBranchForCurrentHead(
            repository, serverBranches.branchNames, serverBranches.mainBranchName.orElse(null)
        )
        if (previousResolvedBranchName != resolvedServerBranchName) {
            project.messageBus.syncPublisher(VCS_LISTENER_TOPIC).onResolvedServerBranchChanged(resolvedServerBranchName)
        }
        return resolvedServerBranchName
    }
}

