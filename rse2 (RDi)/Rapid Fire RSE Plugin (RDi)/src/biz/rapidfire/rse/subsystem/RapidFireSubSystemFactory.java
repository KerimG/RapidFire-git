/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.rapidfire.rse.subsystem;

import java.util.Vector;

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolManager;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystemConfiguration;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class RapidFireSubSystemFactory extends SubSystemConfiguration {

    public static final String ID = "biz.rapidfire.rse.subsystem.RapidFireSubSystemFactory"; //$NON-NLS-1$

    public RapidFireSubSystemFactory() {
        super();
    }

    @Override
    public ISubSystem createSubSystemInternal(IHost host) {
        RapidFireSubSystem subSystem = new RapidFireSubSystem(host, getConnectorService(host));
        return subSystem;
    }

    @Override
    protected void removeSubSystem(ISubSystem subSystem) {
        getSubSystems(false);
        super.removeSubSystem(subSystem);
    }

    @Override
    public String getTranslatedFilterTypeProperty(ISystemFilter selectedFilter) {
        return "Messages.Message_Filter";
        // return null;
    }

    @Override
    protected ISystemFilterPool createDefaultFilterPool(ISystemFilterPoolManager mgr) {

        ISystemFilterPool defaultPool = super.createDefaultFilterPool(mgr);
        Vector<String> strings = new Vector<String>();

        // RapidFireFilter messageFilter =
        // RapidFireFilter.getDefaultFilter();
        // strings.add(messageFilter.getFilterString());
        strings.add("*");
        try {
            ISystemFilter filter = mgr.createSystemFilter(defaultPool, "Messages.My_Messages", strings);
            filter.setType("Messages.Message_Filter");
        } catch (Exception exc) {
        }
        return defaultPool;
        // return null;
    }

    @Override
    public boolean supportsFilters() {
        return true;
    }

    @Override
    public boolean supportsNestedFilters() {
        return false;
    }

    /*
     * Start of RDi/WDSCi specific methods.
     */

    @Override
    public IConnectorService getConnectorService(IHost host) {

        ISubSystem[] subSystems = host.getSubSystems();
        for (int i = 0; i < subSystems.length; i++) {
            ISubSystem subSystem = subSystems[i];
            if ((subSystem instanceof QSYSObjectSubSystem)) {
                return subSystem.getConnectorService();
            }
        }

        return null;
    }
}