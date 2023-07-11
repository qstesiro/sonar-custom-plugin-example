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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.xml.stream.XMLStreamException;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Configuration;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.example.languages.HmmLanguage;

/**
 * The goal of this Sensor is to load the results of an analysis performed by a fictive external tool named: HmmLint
 * Results are provided as an xml file and are corresponding to the rules defined in 'rules.xml'.
 * To be very abstract, these rules are applied on source files made with the fictive language Hmm.
 */
public class HmmLintIssuesLoaderSensor implements Sensor {

    private static final Logger LOGGER = Loggers.get(FooLintIssuesLoaderSensor.class);

    protected static final String REPORT_PATH_KEY = "sonar.hmmlint.reportPath";

    protected final Configuration config;
    protected final FileSystem fileSystem;
    protected SensorContext context;

    /**
     * Use of IoC to get Settings, FileSystem, RuleFinder and ResourcePerspectives
     */
    public HmmLintIssuesLoaderSensor(final Configuration config, final FileSystem fileSystem) {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.HmmLintIssuesLoaderSensor");
        this.config = config;
        this.fileSystem = fileSystem;
    }

    @Override
    public void describe(final SensorDescriptor descriptor) {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.describe");
        descriptor.name("HmmLint Issues Loader Sensor");
        descriptor.onlyOnLanguage(HmmLanguage.KEY);
    }

    @Override
    public void execute(final SensorContext context) {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.execute");
        String reportPath = getReportPath();
        if (reportPath != null) {
            this.context = context;
            File analysisResultsFile = new File(reportPath);
            try {
                parseAndSaveResults(analysisResultsFile);
            } catch (XMLStreamException e) {
                throw new IllegalStateException("Unable to parse the provided FooLint file", e);
            }
        }
    }

    protected String getReportPath() {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.getReportPath");
        Optional<String> o = config.get(reportPathKey());
        if (o.isPresent()) {
            return o.get();
        }
        return null;
    }

    protected String reportPathKey() {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.reportPathKey");
        return REPORT_PATH_KEY;
    }

    protected void parseAndSaveResults(final File file) throws XMLStreamException {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.parseAndSaveResults");
        LOGGER.info("(mock) Parsing 'HmmLint' Analysis Results");
        HmmLintAnalysisResultsParser parser = new HmmLintAnalysisResultsParser();
        List<ErrorDataFromExternalLinter> errors = parser.parse(file);
        for (ErrorDataFromExternalLinter error : errors) {
            getResourceAndSaveIssue(error);
        }
    }

    private void getResourceAndSaveIssue(final ErrorDataFromExternalLinter error) {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.getResourceAndSaveIssue");
        LOGGER.debug(error.toString());

        InputFile inputFile = fileSystem.inputFile(
            fileSystem.predicates().and(
                fileSystem.predicates().hasRelativePath(error.getFilePath()),
                fileSystem.predicates().hasType(InputFile.Type.MAIN)
                )
            );

        LOGGER.debug("inputFile null ? " + (inputFile == null));

        if (inputFile != null) {
            saveIssue(inputFile, error.getLine(), error.getType(), error.getDescription());
        } else {
            LOGGER.error("Not able to find a InputFile with " + error.getFilePath());
        }
    }

    private void saveIssue(final InputFile inputFile, int line, final String externalRuleKey, final String message) {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.saveIssue");
        RuleKey ruleKey = RuleKey.of(getRepositoryKeyForLanguage(inputFile.language()), externalRuleKey);
        NewIssue newIssue = context.newIssue().forRule(ruleKey);
        NewIssueLocation primaryLocation = newIssue.newLocation().on(inputFile).message(message);
        if (line > 0) {
            primaryLocation.at(inputFile.selectLine(line));
        }
        newIssue.at(primaryLocation);

        newIssue.save();
    }

    // 获取规则key(规则key正常是repository:key)
    private static String getRepositoryKeyForLanguage(String languageKey) {
        LOGGER.info("--- HmmLintIssuesLoaderSensor.getRepositoryKeyForLanguage");
        return languageKey.toLowerCase() + "-" + HmmLintRulesDefinition.KEY;
    }

    @Override
    public String toString() {
        return "HmmLintIssuesLoaderSensor";
    }

    private class HmmLintAnalysisResultsParser {

        public List<ErrorDataFromExternalLinter> parse(final File file) throws XMLStreamException {
            LOGGER.info("--- HmmLintIssuesLoaderSensor.FooLintAnalysisResultsParser.parse");
            LOGGER.info("Parsing file {}", file.getAbsolutePath());

            // as the goal of this example is not to demonstrate how to parse an xml file we return an hard coded list of FooError

            ErrorDataFromExternalLinter hmmError1 = new ErrorDataFromExternalLinter("HmmRule1", "More precise description of the error", "src/MyClass.hmm", 5);
            ErrorDataFromExternalLinter hmmError2 = new ErrorDataFromExternalLinter("HmmRule2", "More precise description of the error", "src/MyClass.hmm", 9);

            return Arrays.asList(hmmError1, hmmError2);
        }
    }

    private class ErrorDataFromExternalLinter {

        private final String externalRuleId;
        private final String issueMessage;
        private final String filePath;
        private final int line;

        public ErrorDataFromExternalLinter(final String externalRuleId, final String issueMessage, final String filePath, final int line) {
            LOGGER.info("--- HmmLintIssuesLoaderSensor.ErrorDataFromExternalLinter.ErrorDataFromExternalLinter");
            this.externalRuleId = externalRuleId;
            this.issueMessage = issueMessage;
            this.filePath = filePath;
            this.line = line;
        }

        public String getType() {
            LOGGER.info("--- HmmLintIssuesLoaderSensor.ErrorDataFromExternalLinter.getType");
            return externalRuleId;
        }

        public String getDescription() {
            LOGGER.info("--- HmmLintIssuesLoaderSensor.ErrorDataFromExternalLinter.getDescription");
            return issueMessage;
        }

        public String getFilePath() {
            LOGGER.info("--- HmmLintIssuesLoaderSensor.ErrorDataFromExternalLinter.getFilePath");
            return filePath;
        }

        public int getLine() {
            LOGGER.info("--- HmmLintIssuesLoaderSensor.ErrorDataFromExternalLinter.getLine");
            return line;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append(externalRuleId);
            s.append("|");
            s.append(issueMessage);
            s.append("|");
            s.append(filePath);
            s.append("(");
            s.append(line);
            s.append(")");
            return s.toString();
        }
    }
}
