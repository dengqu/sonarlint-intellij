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
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.changes.ChangeList
import com.intellij.openapi.vcs.changes.ChangeListListener
import org.sonarlint.intellij.common.ui.SonarLintConsole
import org.sonarlint.intellij.common.util.SonarLintUtils.getService

class TriggerServerBranchResolutionOnVcsChange(private val project: Project) : ChangeListListener {
    override fun changeListChanged(list: ChangeList) {
        print("changeListChanged" + list.changes)
    }

    override fun changeListAdded(list: ChangeList) {
        print("changeListAdded" + list.changes)
    }

    override fun changeListRemoved(list: ChangeList) {
        print("changeListRemoved" + list.changes)
    }

    override fun changeListDataChanged(list: ChangeList) {
        print("changeListDataChanged" + list.changes)
    }

    override fun changeListRenamed(list: ChangeList, oldName: String) {
        print("changeListRenamed" + list.changes)
    }

    override fun changeListCommentChanged(list: ChangeList, oldComment: String) {
        print("changeListCommentChanged" + list.changes)
    }

    override fun defaultListChanged(oldDefaultList: ChangeList, newDefaultList: ChangeList) {
        print("defaultListChanged" + newDefaultList.changes)
    }

    override fun defaultListChanged(oldDefaultList: ChangeList, newDefaultList: ChangeList, automatic: Boolean) {
        print("defaultListChanged" + newDefaultList.changes)
    }

    override fun changesAdded(changes: Collection<Change>, toList: ChangeList) {
        print("changesAdded" + toList.changes)
    }

    override fun changesRemoved(changes: Collection<Change>, fromList: ChangeList) {
        print("changesRemoved" + fromList.changes)
    }

    override fun changesMoved(changes: Collection<Change>, fromList: ChangeList, toList: ChangeList) {
        print("changesMoved" + toList.changes)
    }

    override fun allChangeListsMappingsChanged() {
        print("allChangeListsMappingsChanged")
        getService(project, VcsService::class.java).resolveServerBranch()
    }

    override fun unchangedFileStatusChanged() {
        print("unchangedFileStatusChanged")
    }

    override fun changeListUpdateDone() {
        print("changeListUpdateDone")
    }

    private fun print(s: String) {
        SonarLintConsole.get(project).info("VCS !!!!!!!!!!!!!!! $s")
    }
}
