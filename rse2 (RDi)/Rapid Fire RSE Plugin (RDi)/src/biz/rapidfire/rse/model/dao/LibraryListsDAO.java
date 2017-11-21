/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.rse.model.dao;

import biz.rapidfire.core.model.IRapidFireLibraryListResource;
import biz.rapidfire.core.model.dao.AbstractLibraryListsDAO;
import biz.rapidfire.core.model.dao.ILibraryListsDAO;
import biz.rapidfire.core.model.dao.JDBCConnectionManager;
import biz.rapidfire.rse.subsystem.resources.RapidFireLibraryListResource;

public class LibraryListsDAO extends AbstractLibraryListsDAO implements ILibraryListsDAO {

    public LibraryListsDAO(String connectionName, String libraryName) throws Exception {
        super(JDBCConnectionManager.getInstance().getConnection(connectionName, libraryName, false));
    }

    @Override
    protected IRapidFireLibraryListResource createLibraryListInstance(String dataLibrary, String job, String library) {
        return new RapidFireLibraryListResource(dataLibrary, job, library);
    }
}