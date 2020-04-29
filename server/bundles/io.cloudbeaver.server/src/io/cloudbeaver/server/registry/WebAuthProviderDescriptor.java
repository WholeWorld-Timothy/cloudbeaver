/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2020 DBeaver Corp and others
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
package io.cloudbeaver.server.registry;

import io.cloudbeaver.DBWAuthProvider;
import org.eclipse.core.runtime.IConfigurationElement;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.impl.AbstractDescriptor;
import org.jkiss.dbeaver.model.impl.PropertyDescriptor;
import org.jkiss.dbeaver.model.preferences.DBPPropertyDescriptor;
import org.jkiss.utils.ArrayUtils;
import org.jkiss.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Auth service descriptor
 */
public class WebAuthProviderDescriptor extends AbstractDescriptor {

    private final IConfigurationElement cfg;

    private ObjectType implType;
    private DBWAuthProvider instance;
    private final List<DBPPropertyDescriptor> properties = new ArrayList<>();

    public WebAuthProviderDescriptor(IConfigurationElement cfg) {
        super(cfg);
        this.cfg = cfg;
        this.implType = new ObjectType(cfg, "class");

        for (IConfigurationElement propGroup : ArrayUtils.safeArray(cfg.getChildren(PropertyDescriptor.TAG_PROPERTY_GROUP))) {
            String category = propGroup.getAttribute(PropertyDescriptor.ATTR_LABEL);
            IConfigurationElement[] propElements = propGroup.getChildren(PropertyDescriptor.TAG_PROPERTY);
            for (IConfigurationElement prop : propElements) {
                properties.add(new WebAuthProviderPropertyDescriptor(category, prop));
            }
        }
    }

    @NotNull
    public String getId() {
        return cfg.getAttribute("id");
    }

    public String getLabel() {
        return cfg.getAttribute("label");
    }

    public String getDescription() {
        return cfg.getAttribute("description");
    }

    public String getIcon() {
        return cfg.getAttribute("icon");
    }

    @NotNull
    public DBWAuthProvider<?> getInstance() {
        if (instance == null) {
            try {
                instance = implType.createInstance(DBWAuthProvider.class);
            } catch (DBException e) {
                throw new IllegalStateException("Can not instantiate auth provider '" + implType.getImplName() + "'", e);
            }
        }
        return instance;
    }

}
