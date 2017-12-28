/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.rse.subsystem.resources;

import biz.rapidfire.core.model.IRapidFireChildResource;
import biz.rapidfire.core.model.IRapidFireJobResource;
import biz.rapidfire.rse.Messages;

public class LibrariesNode extends AbstractNodeResource implements IRapidFireChildResource<IRapidFireJobResource> {

    public LibrariesNode(IRapidFireJobResource job) {
        super(job, Messages.NodeText_Libraries);
    }

    public IRapidFireJobResource getParentJob() {
        return super.getJob();
    }

    public IRapidFireJobResource getParent() {
        return super.getJob();
    }
}
