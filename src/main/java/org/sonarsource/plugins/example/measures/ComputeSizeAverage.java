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
package org.sonarsource.plugins.example.measures;

import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import static org.sonarsource.plugins.example.measures.ExampleMetrics.FILENAME_SIZE;

public class ComputeSizeAverage implements MeasureComputer {

    private static final Logger LOGGER = Loggers.get(ComputeSizeAverage.class);

    @Override
    public MeasureComputerDefinition define(MeasureComputerDefinitionContext def) {
        LOGGER.info("--- ComputeSizeAverage.define");
        return def.newDefinitionBuilder()
            .setOutputMetrics(FILENAME_SIZE.key())
            .build();
    }

    @Override
    public void compute(MeasureComputerContext context) {
        LOGGER.info("--- ComputeSizeAverage.compute");
        // measure is already defined on files by {@link SetSizeOnFilesSensor}
        // in scanner stack
        if (context.getComponent().getType() != Component.Type.FILE) {
            int sum = 0;
            int count = 0;
            for (Measure child : context.getChildrenMeasures(FILENAME_SIZE.key())) {
                sum += child.getIntValue();
                count++;
            }
            int average = count == 0 ? 0 : sum / count;
            context.addMeasure(FILENAME_SIZE.key(), average);
        }
    }
}
