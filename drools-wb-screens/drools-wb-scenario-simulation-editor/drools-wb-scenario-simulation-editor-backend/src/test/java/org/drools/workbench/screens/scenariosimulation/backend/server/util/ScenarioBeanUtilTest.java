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

package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.workbench.screens.scenariosimulation.backend.server.model.Dispute;
import org.drools.workbench.screens.scenariosimulation.backend.server.model.NotEmptyConstructor;
import org.drools.workbench.screens.scenariosimulation.backend.server.model.Person;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioException;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelperTest;
import org.junit.Test;

import static org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanUtil.convertValue;
import static org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanUtil.loadClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ScenarioBeanUtilTest {

    private static String FIRST_NAME = "firstNameToSet";
    private static int AGE = 10;
    private static ClassLoader classLoader = ScenarioBeanUtilTest.class.getClassLoader();

    @Test
    public void fillBeanTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("creator", "firstName"), FIRST_NAME);
        paramsToSet.put(Arrays.asList("creator", "age"), AGE);

        Object result = ScenarioBeanUtil.fillBean(Dispute.class.getCanonicalName(), paramsToSet, classLoader);

        assertTrue(result instanceof Dispute);

        Dispute dispute = (Dispute) result;
        assertEquals(dispute.getCreator().getFirstName(), FIRST_NAME);
        assertEquals(dispute.getCreator().getAge(), AGE);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanLoadClassTest() {
        ScenarioBeanUtil.fillBean("FakeCanonicalName", new HashMap<>(), classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailNotEmptyConstructorTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("name"), null);

        ScenarioBeanUtil.fillBean(NotEmptyConstructor.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("fakeField"), null);

        ScenarioBeanUtil.fillBean(Dispute.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test(expected = ScenarioException.class)
    public void fillBeanFailNullClassTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("fakeField"), null);

        ScenarioBeanUtil.fillBean(null, paramsToSet, classLoader);
    }

    @Test
    public void fillBeanSimpleObjectTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Collections.emptyList(), "Test");

        ScenarioBeanUtil.fillBean(String.class.getCanonicalName(), paramsToSet, classLoader);
    }

    @Test
    public void navigateToObjectTest() {
        Dispute dispute = new Dispute();
        Person creator = new Person();
        creator.setFirstName(FIRST_NAME);
        dispute.setCreator(creator);
        List<String> pathToProperty = Arrays.asList("creator", "firstName");

        Object targetObject = ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, true);

        assertEquals(targetObject, FIRST_NAME);
    }

    @Test
    public void navigateToObjectNoStepTest() {
        String message = "Invalid path to a property, no steps provided";
        Assertions.assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(new Dispute(), new ArrayList<>(), true))
                .isInstanceOf(ScenarioException.class)
                .hasMessage(message);
    }

    @Test
    public void navigateToObjectFakeFieldTest() {
        Dispute dispute = new Dispute();
        List<String> pathToProperty = Arrays.asList("fakeField");

        String message = "Impossible to find field with name 'fakeField' in class " + Dispute.class.getCanonicalName();
        Assertions.assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, true))
                .isInstanceOf(ScenarioException.class)
                .hasMessage(message);
    }

    @Test
    public void navigateToObjectNoStepCreationTest() {
        Dispute dispute = new Dispute();
        List<String> pathToProperty = Arrays.asList("creator", "firstName");

        String message = "Impossible to reach field firstName because a step is not instantiated";
        Assertions.assertThatThrownBy(() -> ScenarioBeanUtil.navigateToObject(dispute, pathToProperty, false))
                .isInstanceOf(ScenarioException.class)
                .hasMessage(message);
    }

    @Test
    public void convertValueTest() {
        assertEquals("Test", convertValue(String.class.getCanonicalName(), "Test", classLoader));
        assertEquals(false, convertValue(boolean.class.getCanonicalName(), "false", classLoader));
        assertEquals(true, convertValue(Boolean.class.getCanonicalName(), "true", classLoader));
        assertEquals(1, convertValue(int.class.getCanonicalName(), "1", classLoader));
        assertEquals(1, convertValue(Integer.class.getCanonicalName(), "1", classLoader));
        assertEquals(1L, convertValue(long.class.getCanonicalName(), "1", classLoader));
        assertEquals(1L, convertValue(Long.class.getCanonicalName(), "1", classLoader));
        assertEquals(1.0D, convertValue(double.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0D, convertValue(Double.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0F, convertValue(float.class.getCanonicalName(), "1.0", classLoader));
        assertEquals(1.0F, convertValue(Float.class.getCanonicalName(), "1.0", classLoader));
        assertNull(convertValue(Float.class.getCanonicalName(), null, classLoader));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertValueFailLoadClassTest() {
        convertValue("my.NotExistingClass", "Test", classLoader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertValueFailUnsupportedTest() {
        convertValue(ScenarioRunnerHelperTest.class.getCanonicalName(), "Test", classLoader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertValueFailPrimitiveNullTest() {
        convertValue("int", null, classLoader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertValueFailNotStringOrTypeTest() {
        convertValue(ScenarioRunnerHelperTest.class.getCanonicalName(), 1, classLoader);
    }

    @Test
    public void loadClassTest() {
        assertEquals(String.class, loadClass(String.class.getCanonicalName(), classLoader));
        assertEquals(int.class, loadClass(int.class.getCanonicalName(), classLoader));

        Assertions.assertThatThrownBy(() -> loadClass(null, classLoader))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Impossible to load class null");

        Assertions.assertThatThrownBy(() -> loadClass("NotExistingClass", classLoader))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Impossible to load class NotExistingClass");
    }
}