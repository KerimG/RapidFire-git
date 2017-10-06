/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core.handlers;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import biz.rapidfire.core.model.IRapidFireResource;

public class EndJobHandler extends AbstractJobHandler implements IHandler {

    public EndJobHandler() {
        super();
    }

    protected Object executeWithResource(IRapidFireResource job) throws ExecutionException {

        System.out.println("Ending Rapid Fire job ... " + job.getName());

        return null;
    }

}
