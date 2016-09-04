/*
 *  Copyright 2013~2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.xdocreport.dom.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.xdocreport.dom.example.models.Developer;
import org.isisaddons.module.xdocreport.dom.example.models.Project;
import org.isisaddons.module.xdocreport.dom.example.models.ProjectDevelopersModel;

import fr.opensagres.xdocreport.core.io.IOUtils;

public class XDocReportServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    ConfigurationService mockConfigurationService;

    XDocReportService service;

    @Before
    public void setUp() throws Exception {
        service = new XDocReportService();
    }

    @Test
    public void simple() throws Exception {

        // given
        InputStream in= new FileInputStream(new File("src/test/java/org/isisaddons/module/xdocreport/dom/example/template/Project-template.docx"));
        final byte[] templateBytes = IOUtils.toByteArray(in);

        Project project = new Project("XDocReport");
        List<Developer> developers = new ArrayList<>();
        developers.add(new Developer("ZERR", "Angelo", "angelo.zerr@gmail.com"));
        developers.add(new Developer("Leclercq", "Pascal", "pascal.leclercq@gmail.com"));
        final ProjectDevelopersModel dataModel = new ProjectDevelopersModel(project, developers);

        // when
        final byte[] docxBytes = service.render(templateBytes, dataModel, OutputType.DOCX);

        // then
        IOUtils.write(docxBytes,new FileOutputStream(new File("target/Project.docx")));
    }

}
