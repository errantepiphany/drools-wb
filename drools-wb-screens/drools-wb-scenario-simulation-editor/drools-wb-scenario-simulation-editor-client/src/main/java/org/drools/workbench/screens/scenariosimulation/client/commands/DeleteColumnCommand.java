/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.Date;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;

/**
 * <code>Command</code> to <b>delete</b> a column. <b>Eventually</b> add a ne column if the deleted one is the last of its group.
 */
@Dependent
public class DeleteColumnCommand extends AbstractCommand {

    private ScenarioGridModel model;
    private int columnIndex;
    private String columnGroup;
    private ScenarioGridPanel scenarioGridPanel;
    private ScenarioGridLayer scenarioGridLayer;

    public DeleteColumnCommand() {
    }

    /**
     * @param model
     * @param columnIndex
     * @param columnGroup
     * @param scenarioGridPanel
     * @param scenarioGridLayer
     */
    public DeleteColumnCommand(ScenarioGridModel model, int columnIndex, String columnGroup, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer scenarioGridLayer) {
        this.model = model;
        this.columnIndex = columnIndex;
        this.columnGroup = columnGroup;
        this.scenarioGridPanel = scenarioGridPanel;
        this.scenarioGridLayer = scenarioGridLayer;
    }

    @Override
    public void execute() {
        model.deleteColumn(columnIndex);
        if (model.getGroupSize(columnGroup) < 1) {
            FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
            Map.Entry<String, String> validPlaceholders = model.getValidPlaceholders();
            String instanceTitle = validPlaceholders.getKey();
            String propertyTitle = validPlaceholders.getValue();
            model.insertColumn(columnIndex, getScenarioGridColumnLocal(instanceTitle,
                                                                       propertyTitle,
                                                                       String.valueOf(new Date().getTime()),
                                                                       columnGroup,
                                                                       factMappingType,
                                                                       scenarioGridPanel,
                                                                       scenarioGridLayer,
                                                                       ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        }
    }
}
