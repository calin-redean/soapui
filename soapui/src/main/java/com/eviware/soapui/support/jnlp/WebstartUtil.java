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

package com.eviware.soapui.support.jnlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class WebstartUtil
{

	protected static String createWebStartDirectory( String name, String jarUrl ) throws IOException
	{

		String deploymentUserTmp = System.getProperty( "deployment.user.tmp" );
		JarFile jar = getJar( jarUrl );
		String dir = createDirectory( deploymentUserTmp, name );
		extract( jar, dir );
		return dir;
	}

	private static void extract( JarFile jar, String dir ) throws IOException, FileNotFoundException
	{
		makeDirectories( jar, dir );
		extractFiles( jar, dir );
	}

	@SuppressWarnings( "unchecked" )
	private static void extractFiles( JarFile jar, String eviwareDir ) throws IOException, FileNotFoundException
	{
		Enumeration entries = jar.entries();
		while( entries.hasMoreElements() )
		{
			JarEntry file = ( JarEntry )entries.nextElement();
			File f = new File( eviwareDir + File.separator + file.getName() );

			if( file.isDirectory() )
			{ // if its a directory, skip it
				continue;
			}
			// System.out.println(f);
			InputStream is = jar.getInputStream( file );
			FileOutputStream fos = new FileOutputStream( f );

			while( is.available() > 0 )
			{
				fos.write( is.read() );
			}

			fos.close();
			is.close();
		}
	}

	@SuppressWarnings( "unchecked" )
	private static void makeDirectories( JarFile jar, String eviwareDir )
	{
		Enumeration entries = jar.entries();
		while( entries.hasMoreElements() )
		{
			JarEntry file = ( JarEntry )entries.nextElement();
			File f = new File( eviwareDir + File.separator + file.getName() );
			if( file.isDirectory() )
			{ // if its a directory, create it
				f.mkdir();
				// System.out.println(f);
			}
		}
	}

	private static JarFile getJar( String jarUrl ) throws MalformedURLException, IOException
	{
		// String reportsJarUrl = System.getProperty("reports.jar.url");
		URL url = new URL( "jar:" + jarUrl + "!/" );
		JarURLConnection jarConnection = ( JarURLConnection )url.openConnection();
		JarFile jar = jarConnection.getJarFile();
		return jar;
	}

	private static String createDirectory( String deploymentUserTmp, String folderName )
	{
		File folder = new File( deploymentUserTmp + File.separator + folderName );
		folder.mkdir();
		// System.out.println(folder.getAbsolutePath());
		return folder.getAbsolutePath();
	}

	protected static boolean isWebStart()
	{
		String webstart = System.getProperty( "com.eviware.soapui.webstart", "false" );
		if( "true".equalsIgnoreCase( webstart ) )
		{
			return true;
		}
		else
			return false;

	}

}
