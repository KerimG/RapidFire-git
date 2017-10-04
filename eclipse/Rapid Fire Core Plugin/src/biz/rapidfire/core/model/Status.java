/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core.model;

import java.util.HashMap;
import java.util.Map;

public enum Status {
    RDY ("*RDY"), //$NON-NLS-1$
    RUN_PENDING ("*RUN-PND"), //$NON-NLS-1$
    RUN ("*RUN"), //$NON-NLS-1$
    END_PND ("*END-PND"), //$NON-NLS-1$
    END ("*END"); //$NON-NLS-1$

    public String label;

    private static Map<String, Status> statuses;

    static {
        statuses = new HashMap<String, Status>();
        for (Status status : Status.values()) {
            statuses.put(status.label, status);
        }
    }

    private Status(String label) {
        this.label = label;
    }

    public static Status find(String label) {
        return statuses.get(label);
    }
}