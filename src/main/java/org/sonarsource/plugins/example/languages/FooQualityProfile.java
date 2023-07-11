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

import static org.sonarsource.plugins.example.rules.FooLintRulesDefinition.REPO_KEY;
import static org.sonarsource.plugins.example.settings.FooLanguageProperties.FILE_SUFFIXES_KEY;

/**
 * Default, BuiltIn Quality Profile for the projects having files of the language "foo"
 */
public final class FooQualityProfile implements BuiltInQualityProfilesDefinition {

    private static final Logger LOGGER = Loggers.get(FooQualityProfile.class);

    private final Configuration config;

    public FooQualityProfile(Configuration config) {
        LOGGER.info("--- FooQualityProfile.FooQualityProfile");
        this.config = config;
    }

    @Override
    public void define(Context context) {
        LOGGER.info("--- FooQualityProfile.define");
        LOGGER.info("--- {}: {}",
                    FILE_SUFFIXES_KEY,
                    config.get(FILE_SUFFIXES_KEY)); // debug ???
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("FooLint Rules", FooLanguage.KEY);
        profile.setDefault(true);
        NewBuiltInActiveRule rule1 = profile.activateRule(REPO_KEY, "ExampleRule1");
        rule1.overrideSeverity("BLOCKER");
        NewBuiltInActiveRule rule2 = profile.activateRule(REPO_KEY, "ExampleRule2");
        rule2.overrideSeverity("MAJOR");
        NewBuiltInActiveRule rule3 = profile.activateRule(REPO_KEY, "ExampleRule3");
        rule3.overrideSeverity("CRITICAL");
        profile.done();
        // debug ???
        Map<String, Map<String, BuiltInQualityProfile>> profiles = context.profilesByLanguageAndName();
        for (String key : profiles.keySet()) {
            LOGGER.info("--- {}: {}", key, profiles.get(key));
        }
    }

}
