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

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import org.sonarsource.plugins.example.settings.HmmLanguageProperties;

/**
 * This class defines the fictive Hmm language.
 */
public final class HmmLanguage extends AbstractLanguage {

    private static final Logger LOGGER = Loggers.get(HmmLanguage.class);

    public static final String NAME = "Hmm";
    public static final String KEY = "hmm";

    private final Configuration config;

    public HmmLanguage(Configuration config) {
        super(KEY, NAME);
        LOGGER.info("--- HmmLanguage.HmmLanguage");
        this.config = config;
    }

    @Override
    public String[] getFileSuffixes() {
        LOGGER.info("--- HmmLanguage.getFileSuffixes");
        return config.getStringArray(HmmLanguageProperties.FILE_SUFFIXES_KEY);
    }
}
