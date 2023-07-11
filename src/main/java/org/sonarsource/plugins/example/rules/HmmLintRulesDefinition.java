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
package org.sonarsource.plugins.example.rules;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import org.sonarsource.plugins.example.languages.HmmLanguage;

public final class HmmLintRulesDefinition implements RulesDefinition {

    private static final Logger LOGGER = Loggers.get(HmmLintRulesDefinition.class);

    private static final String PATH_TO_RULES_XML = "/example/hmmlint-rules.xml";

    protected static final String KEY = "hmmlint";
    protected static final String NAME = "HmmLint";

    public static final String REPO_KEY = HmmLanguage.KEY + "-" + KEY;
    protected static final String REPO_NAME = HmmLanguage.KEY + "-" + NAME;

    @Override
    public void define(Context context) {
        LOGGER.info("--- HmmLintRulesDefinition.define");
        defineRulesForLanguage(context, REPO_KEY, REPO_NAME, HmmLanguage.KEY);
    }

    private void defineRulesForLanguage(Context context,
                                        String repositoryKey,
                                        String repositoryName,
                                        String languageKey) {
        LOGGER.info("--- HmmLintRulesDefinition.defineRulesForLanguage");
        // repository页面部分无法显示完整列表(只显示前10个),可以通过规则key进行筛选 ???
        NewRepository repository = context
            .createRepository(repositoryKey, languageKey)
            .setName(repositoryName);
        InputStream rulesXml = this
            .getClass()
            .getResourceAsStream(rulesDefinitionFilePath());
        if (rulesXml != null) {
            RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
            rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
        }
        repository.done();
    }

    protected String rulesDefinitionFilePath() {
        LOGGER.info("--- HmmLintRulesDefinition.rulesDefinitionFilePath");
        return PATH_TO_RULES_XML;
    }
}
