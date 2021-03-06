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

package org.drools.workbench.screens.scenariosimulation.client.renderers;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridColumnRendererTest {

    private static final String PLACEHOLDER = "PLACEHOLDER";
    private static final String VALUE = "VALUE";
    @Mock
    private GridBodyCellRenderContext contextMock;
    @Mock
    private GridRenderer rendererMock;
    @Mock
    private ScenarioGridRendererTheme themeMock;
    @Mock
    private Text textMock;
    @Mock
    private Text placeholderMock;
    @Mock
    private Text errorTextMock;
    @Mock
    private Rectangle rectangle;
    @Mock
    private ScenarioGridCell scenarioGridCell;
    @Mock
    private Node<?> nodeMock;

    private ScenarioGridColumnRenderer scenarioGridColumnRenderer;

    @Before
    public void setUp() {
        doReturn(nodeMock).when(textMock).asNode();
        doReturn(nodeMock).when(placeholderMock).asNode();
        doReturn(nodeMock).when(errorTextMock).asNode();
        doReturn(nodeMock).when(rectangle).asNode();
        when(themeMock.getPlaceholderText()).thenReturn(placeholderMock);
        when(themeMock.getBodyText()).thenReturn(textMock);
        when(rendererMock.getTheme()).thenReturn(themeMock);
        when(contextMock.getRenderer()).thenReturn(rendererMock);
        when(themeMock.getErrorText()).thenReturn(errorTextMock);
        when(themeMock.getBodyErrorBackground(any())).thenReturn(rectangle);
        scenarioGridColumnRenderer = spy(new ScenarioGridColumnRenderer());
    }

    @Test
    public void renderCell() {
        GridCell<String> cell = null;
        Group retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNull(retrieved);
        verify(scenarioGridColumnRenderer, never()).internalRenderCell(any(), eq(contextMock), any(), anyString());

        cell = new ScenarioGridCell(null);
        retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNull(retrieved);
        verify(scenarioGridColumnRenderer, times(1)).internalRenderCell(any(), eq(contextMock), eq(textMock), eq(null));
        reset(scenarioGridColumnRenderer);

        cell = new ScenarioGridCell(new ScenarioGridCellValue(null));
        retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNull(retrieved);
        verify(scenarioGridColumnRenderer, times(1)).internalRenderCell(any(), eq(contextMock), eq(textMock), eq(null));
        reset(scenarioGridColumnRenderer);

        cell = new ScenarioGridCell(new ScenarioGridCellValue(VALUE));
        retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNotNull(retrieved);
        verify(scenarioGridColumnRenderer, times(1)).internalRenderCell(any(), eq(contextMock), eq(textMock), eq(VALUE));
        reset(scenarioGridColumnRenderer);

        ScenarioGridCell scenarioGridCell = new ScenarioGridCell(new ScenarioGridCellValue(VALUE, PLACEHOLDER));
        retrieved = scenarioGridColumnRenderer.renderCell(scenarioGridCell, contextMock);
        assertNotNull(retrieved);
        verify(scenarioGridColumnRenderer, times(1)).internalRenderCell(any(), eq(contextMock), eq(textMock), eq(VALUE));
        reset(scenarioGridColumnRenderer);

        scenarioGridCell = new ScenarioGridCell(new ScenarioGridCellValue(null, PLACEHOLDER));
        retrieved = scenarioGridColumnRenderer.renderCell(scenarioGridCell, contextMock);
        assertNotNull(retrieved);
        verify(scenarioGridColumnRenderer, times(1)).internalRenderCell(any(), eq(contextMock), eq(placeholderMock), eq(PLACEHOLDER));
        reset(scenarioGridColumnRenderer);

        cell = new ScenarioGridCell(new ScenarioGridCellValue(VALUE));
        ((ScenarioGridCell) cell).setError(true);
        retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNotNull(retrieved);
        verify(scenarioGridColumnRenderer, times(1)).internalRenderCell(any(), eq(contextMock), eq(errorTextMock), eq(VALUE));
        reset(scenarioGridColumnRenderer);
    }

    @Test
    public void internalRenderCell() {
        assertNull(scenarioGridColumnRenderer.internalRenderCell(null, null, null, null));
        assertNull(scenarioGridColumnRenderer.internalRenderCell(scenarioGridCell, null, null, null));
        when(scenarioGridCell.getValue()).thenReturn(mock(GridCellValue.class));
        assertNull(scenarioGridColumnRenderer.internalRenderCell(scenarioGridCell, null, null, null));
    }

    @Test
    public void applyBackgroundColor() {
        Group group = mock(Group.class);

        when(scenarioGridCell.isError()).thenReturn(true);
        scenarioGridColumnRenderer.applyBackgroundColor(scenarioGridCell, contextMock, group, themeMock);
        verify(group, times(1)).add(any());

        reset(group);

        when(scenarioGridCell.isError()).thenReturn(false);
        scenarioGridColumnRenderer.applyBackgroundColor(scenarioGridCell, contextMock, group, themeMock);
        verify(group, never()).add(any());

    }
}