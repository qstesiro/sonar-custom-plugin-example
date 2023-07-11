/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.plugins.example.languages;

import java.util.Map;

import org.sonar.api.config.Configuration;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import static org.sonarsource.plugins.example.rules.HmmLintRulesDefinition.REPO_KEY;
import static org.sonarsource.plugins.example.settings.HmmLanguageProperties.FILE_SUFFIXES_KEY;

/**
 * Default, BuiltIn Quality Profile for the projects having files of the language "hmm"
 */
public final class HmmQualityProfile implements BuiltInQualityProfilesDefinition {

    private static final Logger LOGGER = Loggers.get(HmmQualityProfile.class);

    private final Configuration config;

    public HmmQualityProfile(Configuration config) {
        LOGGER.info("--- HmmQualityProfile.HmmQualityProfile");
        this.config = config;
    }

    @Override
    public void define(Context context) {
        LOGGER.info("--- HmmQualityProfile.define");
        LOGGER.info("--- {}: {}",
                    FILE_SUFFIXES_KEY,
                    config.get(FILE_SUFFIXES_KEY)); // debug ???
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("HmmLint Rules", HmmLanguage.KEY);
        profile.setDefault(true);
        NewBuiltInActiveRule rule1 = profile.activateRule(REPO_KEY, "HmmRule1");
        rule1.overrideSeverity("BLOCKER");
        NewBuiltInActiveRule rule2 = profile.activateRule(REPO_KEY, "HmmRule2");
        rule2.overrideSeverity("CRITICAL");
        NewBuiltInActiveRule rule3 = profile.activateRule(REPO_KEY, "HmmRule3");
        rule3.overrideSeverity("MAJOR");
        profile.done();
    }
}
