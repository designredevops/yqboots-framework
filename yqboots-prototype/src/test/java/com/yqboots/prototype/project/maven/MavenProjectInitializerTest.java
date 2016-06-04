/*
 *
 *  * Copyright 2015-2016 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.yqboots.prototype.project.maven;

import com.yqboots.prototype.Application;
import com.yqboots.prototype.project.ProjectContext;
import com.yqboots.prototype.project.core.ProjectMetadata;
import com.yqboots.prototype.project.core.ProjectType;
import com.yqboots.prototype.project.core.theme.Theme;
import com.yqboots.prototype.project.core.theme.ThemeColor;
import com.yqboots.prototype.project.core.theme.ThemeSkin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class MavenProjectInitializerTest {
    @Autowired
    private MavenProjectInitializer initializer;

    @Test
    public void testStartup() throws Exception {
        ProjectContext context = new ProjectContext();

        ProjectMetadata metadata = new ProjectMetadata();
        metadata.setGroupId("com.yqboots.test");
        metadata.setArtifactId("test-core");
        metadata.setType(ProjectType.MAVEN);
        metadata.setName("Test Project");

        context.setMetadata(metadata);

        Theme theme = new Theme();
        theme.setSkin(ThemeSkin.DARK);
        theme.setColor(ThemeColor.GREEN);
        context.setTheme(theme);

        initializer.startup(context);
    }
}