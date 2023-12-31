/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core.maintenance.notification.shared;

import java.util.HashMap;
import java.util.Map;

public enum User {
    SYSOPR ("*SYSOPR"); //$NON-NLS-1$

    private String label;

    private static Map<String, User> users;

    static {
        users = new HashMap<String, User>();
        for (User status : User.values()) {
            users.put(status.label, status);
        }
    }

    private User(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static String[] labels() {
        return users.keySet().toArray(new String[users.size()]);
    }

    public static User find(String label) {
        // TODO: remove debug code
        if (label.length() != label.trim().length()) {
            throw new IllegalArgumentException("Expect to see a trimmed value here."); //$NON-NLS-1$
        }
        return users.get(label);
    }

}
