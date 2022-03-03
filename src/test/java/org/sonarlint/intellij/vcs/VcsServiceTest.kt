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

import com.intellij.openapi.util.io.FileUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.sonarlint.intellij.AbstractSonarLintLightTests
import org.sonarlint.intellij.config.global.ServerConnection
import org.sonarsource.sonarlint.core.client.api.connected.ConnectedSonarLintEngine
import org.sonarsource.sonarlint.core.client.api.connected.ProjectBranches
import java.nio.file.Paths
import java.util.Optional

internal class VcsServiceTest : AbstractSonarLintLightTests() {

    private val connectedEngine = mock(ConnectedSonarLintEngine::class.java)
    private lateinit var vcsService: VcsService
    
    @Before
    fun prepare() {
        vcsService = VcsService(project)
        FileUtil.delete(Paths.get(project.basePath!!))
    }

    @Test
    fun should_not_resolve_in_standalone_mode() {
        val resolvedBranchName = vcsService.resolveServerBranch()

        assertThat(resolvedBranchName).isNull()
    }

    @Test
    fun should_resolve_server_branch_in_connected_mode() {
        connectProjectTo(ServerConnection.newBuilder().setName("connection").build(), "projectKey")
        `when`(connectedEngine.getServerBranches("projectKey")).thenReturn(ProjectBranches(setOf("master", "branch1"), Optional.of("master")))
        engineManager.registerEngine(connectedEngine, "connection")
        FileUtil.copyDir(Paths.get(testDataPath, "git").toFile(), Paths.get(project.basePath!!, ".git").toFile())
        val resolvedBranchName = vcsService.resolveServerBranch()

        assertThat(resolvedBranchName).isEqualTo("branch1")
    }

    @Test
    fun should_notify_listeners_when_resolved_server_branch_changes() {
        var resolvedBranchName: String? = null
        project.messageBus.connect(project).subscribe(VCS_LISTENER_TOPIC, object : VcsListener {
            override fun onResolvedServerBranchChanged(name: String?) {
                resolvedBranchName = name
            }
        })
        connectProjectTo(ServerConnection.newBuilder().setName("connection").build(), "projectKey")
        `when`(connectedEngine.getServerBranches("projectKey")).thenReturn(ProjectBranches(setOf("master", "branch1"), Optional.of("master")))
        engineManager.registerEngine(connectedEngine, "connection")
        FileUtil.copyDir(Paths.get(testDataPath, "git").toFile(), Paths.get(project.basePath!!, ".git").toFile())

        vcsService.resolveServerBranch()

        assertThat(resolvedBranchName).isEqualTo("branch1")
    }
}
