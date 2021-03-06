/*
 *  soapUI, copyright (C) 2004-2012 smartbear.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.impl.wsdl.support.xsd;

import java.util.ArrayList;

/**
 * Exception thrown by schema-related operations
 * 
 * @author Ole.Matzura
 */

public class SchemaException extends Exception
{
	private ArrayList<?> errorList;

	public SchemaException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public SchemaException( Exception e, ArrayList<?> errorList )
	{
		this.errorList = errorList;
	}

	public ArrayList<?> getErrorList()
	{
		return errorList;
	}
}
