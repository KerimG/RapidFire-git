/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.rse.model;

import biz.rapidfire.core.model.IRapidFireLibraryResource;
import biz.rapidfire.core.subsystem.IRapidFireSubSystem;

import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.impl.AbstractResource;

public class RapidFireLibraryResource extends AbstractResource implements IRapidFireLibraryResource, Comparable<IRapidFireLibraryResource> {

    private String dataLibrary;
    private String job;
    private String library;
    private String shadowLibrary;

    public RapidFireLibraryResource(String dataLibrary, String job, String library) {

        this.dataLibrary = dataLibrary;
        this.job = job;
        this.library = library;
    }

    /*
     * IRapidFireFileResource methods
     */

    public String getDataLibrary() {
        return dataLibrary;
    }

    public String getJob() {
        return job;
    }

    public String getLibrary() {
        return library;
    }

    public String getShadowLibrary() {
        return shadowLibrary;
    }

    public void setShadowLibrary(String shadowLibrary) {
        this.shadowLibrary = shadowLibrary;
    }

    public int compareTo(IRapidFireLibraryResource resource) {

        if (resource == null) {
            return 1;
        }

        int result = resource.getDataLibrary().compareTo(getDataLibrary());
        if (result != 0) {
            return result;
        }

        result = resource.getJob().compareTo(getJob());
        if (result != 0) {
            return result;
        }

        return getLibrary().compareTo(resource.getLibrary());
    }

    public void setParentSubSystem(IRapidFireSubSystem subSystem) {
        super.setSubSystem((SubSystem)subSystem);
    }

    public IRapidFireSubSystem getParentSubSystem() {
        return (IRapidFireSubSystem)super.getSubSystem();
    }

    @Override
    public String toString() {
        return getLibrary();
    }

}
