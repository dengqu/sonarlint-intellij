/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015-2021 SonarSource
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
package org.sonarlint.intellij.clion;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.sonarlint.intellij.common.analysis.AnalysisConfigurator;

import java.util.Collection;

public class CFamilyAnalysisConfigurator implements AnalysisConfigurator {

  @Override
  public AnalysisConfiguration configure(Module module, Collection<VirtualFile> filesToAnalyze) {
    AnalysisConfiguration result = new AnalysisConfiguration();
    AnalyzerConfiguration analyzerConfiguration = new AnalyzerConfiguration(module.getProject());
    BuildWrapperJsonGenerator buildWrapperJsonGenerator = new BuildWrapperJsonGenerator();
    filesToAnalyze.stream()
      .map(analyzerConfiguration::getConfiguration)
      .filter(AnalyzerConfiguration.ConfigurationResult::hasConfiguration)
      .map(AnalyzerConfiguration.ConfigurationResult::getConfiguration)
      .forEach(configuration -> {
        buildWrapperJsonGenerator.add(configuration);
        if (configuration.sonarLanguage != null) {
          result.forcedLanguages.put(configuration.virtualFile, configuration.sonarLanguage);
        }
      });
    result.extraProperties.put("sonar.cfamily.build-wrapper-content", buildWrapperJsonGenerator.build());
    return result;
  }

}