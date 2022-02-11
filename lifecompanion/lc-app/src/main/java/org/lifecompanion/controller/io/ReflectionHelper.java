/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.controller.io;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ReflectionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionHelper.class);


    public static <T> List<Class<? extends T>> findImplementationsInModules(Class<T> type) {
        long start = System.currentTimeMillis();
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .whitelistModules("org.lifecompanion.app")
                .disableJarScanning()
                .scan()
        ) {
            String className = type.getName();
            LOGGER.debug("Search for {}", className);
            ClassInfoList infoList = scanResult.getClassesImplementing(className).filter(classInfo -> !classInfo.isAbstract() && !classInfo.isInterface());
            List<Class<?>> classes = infoList.loadClasses();
            LOGGER.info("{} implementations detected in {} ms, found {} types", className, System.currentTimeMillis() - start, classes.size());

            return classes.stream().map(c -> (Class<? extends T>) c).collect(Collectors.toList());
        }
    }
}
