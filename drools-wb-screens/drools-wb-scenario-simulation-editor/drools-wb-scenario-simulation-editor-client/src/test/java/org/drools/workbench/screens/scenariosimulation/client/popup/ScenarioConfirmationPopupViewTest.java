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

package org.drools.workbench.screens.scenariosimulation.client.popup;

import com.google.gwt.dom.client.ParagraphElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class ScenarioConfirmationPopupViewTest extends AbstractDeletePopupViewTest {

    protected ScenarioConfirmationPopupView popupView;

    @Mock
    protected ParagraphElement text1Mock;

    @Mock
    protected ParagraphElement textQuestionMock;

    @Mock
    protected Button cancelButtonMock;

    @Mock
    protected Button okDeleteButtonMock;

    @Mock
    protected MouseEvent mouseEventMock;

    @Mock
    protected HTMLElement elementMock;

    @Mock
    protected TranslationService translationServiceMock;

    @Before
    public void setup() {
        super.commonSetup();
//        popupView = spy(new ScenarioConfirmationPopupView() {
//            {
//                this.mainTitle = (HeadingElement) mainTitleMock;
//                this.mainQuestion = mainQuestionMock;
//                this.text1 = text1Mock;
//                this.textQuestion = textQuestionMock;
//                this.cancelButton = cancelButtonMock;
//                this.okDeleteButton = okDeleteButtonMock;
//                this.modal = modalMock;
//                this.translationService = translationServiceMock;
//            }
//        });
    }

    @Test
    public void init() {
        popupView.init();
        verify(cancelButtonMock, times(1)).setText(anyString());
    }

    @Test
    public void show() {
        popupView.show(MAIN_TITLE_TEXT,
                       MAIN_QUESTION_TEXT,
                       TEXT1_TEXT,
                       TEXT_QUESTION_TEXT,
                       OKDELETE_BUTTON_TEXT,
                       okDeleteCommandMock);
        verifyShow();
        assertEquals(okDeleteCommandMock, popupView.okDeleteCommand);
    }

    @Test
    public void getElement() {
        final HTMLElement retrieved = popupView.getElement();
        assertNotNull(retrieved);
    }

    @Test
    public void hide() {
        popupView.hide();
        verify(modalMock, times(1)).hide();
    }

    @Test
    public void onOkDeleteClick() {
        popupView.okDeleteCommand = null;
        popupView.onOkDeleteClick(mouseEventMock);
        verify(okDeleteCommandMock, never()).execute();
        verify(popupView, times(1)).hide();
        reset(popupView);
        popupView.okDeleteCommand = okDeleteCommandMock;
        popupView.onOkDeleteClick(mouseEventMock);
        verify(okDeleteCommandMock, times(1)).execute();
        verify(popupView, times(1)).hide();
    }

    @Test
    public void onCancelClick() {
        popupView.onCancelClick(mouseEventMock);
        verify(popupView, times(1)).hide();
    }

    protected void verifyShow() {
        verify(mainTitleMock, times(1)).setInnerHTML(eq(MAIN_TITLE_TEXT));
        verify(mainQuestionMock, times(1)).setInnerHTML(eq(MAIN_QUESTION_TEXT));
        verify(text1Mock, times(1)).setInnerText(eq(TEXT1_TEXT));
        verify(textQuestionMock, times(1)).setInnerText(eq(TEXT_QUESTION_TEXT));
        verify(okDeleteButtonMock, times(1)).setText(eq(OKDELETE_BUTTON_TEXT));
        verify(modalMock, times(1)).show();
    }
}