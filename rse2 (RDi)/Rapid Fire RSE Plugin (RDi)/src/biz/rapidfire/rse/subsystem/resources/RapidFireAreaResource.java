/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.rse.subsystem.resources;

import org.eclipse.rse.core.subsystems.AbstractResource;
import org.eclipse.rse.core.subsystems.ISubSystem;

import biz.rapidfire.core.exceptions.IllegalParameterException;
import biz.rapidfire.core.model.IRapidFireAreaResource;
import biz.rapidfire.core.model.IRapidFireFileResource;
import biz.rapidfire.core.model.IRapidFireJobResource;
import biz.rapidfire.core.subsystem.IRapidFireSubSystem;
import biz.rapidfire.core.subsystem.resources.RapidFireAreaResourceDelegate;

public class RapidFireAreaResource extends AbstractResource implements IRapidFireAreaResource, Comparable<IRapidFireAreaResource> {

    private IRapidFireJobResource parentJob;
    private RapidFireAreaResourceDelegate delegate;

    public static RapidFireAreaResource createEmptyInstance(IRapidFireFileResource file) {
        return new RapidFireAreaResource(file, ""); //$NON-NLS-1$
    }

    public RapidFireAreaResource(IRapidFireFileResource file, String area) {

        if (file == null) {
            throw new IllegalParameterException("file", null); //$NON-NLS-1$
        }

        this.parentJob = file.getParentJob();
        this.delegate = new RapidFireAreaResourceDelegate(parentJob.getDataLibrary(), parentJob.getName(), file.getPosition(), area);
        super.setSubSystem((ISubSystem)file.getParentSubSystem());
    }

    /*
     * IRapidFireResource methods
     */

    public String getDataLibrary() {
        return delegate.getDataLibrary();
    }

    public IRapidFireSubSystem getParentSubSystem() {
        return (IRapidFireSubSystem)super.getSubSystem();
    }

    public IRapidFireJobResource getParentJob() {
        return this.parentJob;
    }

    /*
     * IRapidFireAreaResource methods
     */

    public String getJob() {
        return delegate.getJob();
    }

    public int getPosition() {
        return delegate.getPosition();
    }

    public String getName() {
        return delegate.getName();
    }

    public String getLibrary() {
        return delegate.getLibrary();
    }

    public void setLibrary(String library) {
        delegate.setLibrary(library);
    }

    public String getLibraryList() {
        return delegate.getLibraryList();
    }

    public void setLibraryList(String libraryList) {
        delegate.setLibraryList(libraryList);
    }

    public String getLibraryCcsid() {
        return delegate.getLibraryCcsid();
    }

    public void setLibraryCcsid(String libraryCcsid) {
        delegate.setLibraryCcsid(libraryCcsid);
    }

    public String getCommandExtension() {
        return delegate.getCommandExtension();
    }

    public void setCommandExtension(String commandExtension) {
        delegate.setCommandExtension(commandExtension);
    }

    public int compareTo(IRapidFireAreaResource resource) {
        return delegate.compareTo(resource);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}