package com.danwink.surfbuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.danwink.surfbuilder.MarchingSolver.Triangle;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class RodExperiments
{	
	public static ArrayList<Triangle> experiment3Line( float backOff, float isoLevel, Preset.DistanceModifier dm )
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -1, -1, -1 ), new Vector3f( 2, 2, 2 ), .03f, isoLevel );
		
		Preset.InvExpLine a = new Preset.InvExpLine( new Point3f( backOff, 0, 0 ), new Point3f( 1, 0, 0 ), dm );
		Preset.InvExpLine b = new Preset.InvExpLine( new Point3f( 0, backOff, 0 ), new Point3f( 0, 1, 0 ), dm );
		Preset.InvExpLine c = new Preset.InvExpLine( new Point3f( 0, 0, backOff ), new Point3f( 0, 0, 1 ), dm );
		
		solver.addPrimitive( a, b, c );
		
		return solver.solve();
	}
	
	public static ArrayList<Triangle> experiment2Line( float backOff, float isoLevel, Preset.DistanceModifier dm )
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -2, -1, -1 ), new Vector3f( 2, 1, 1 ), .03f, isoLevel );
		
		Preset.InvExpLine a = new Preset.InvExpLine( new Point3f( backOff, 0, 0 ), new Point3f( 1, 0, 0 ), dm );
		Preset.InvExpLine b = new Preset.InvExpLine( new Point3f( -backOff, 0, 0 ), new Point3f( -1, 0, 0 ), dm );
		
		solver.addPrimitive( a, b );
		
		return solver.solve();
	}
	
	public static ArrayList<Triangle> makeExample( Preset.DistanceModifier dm )
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -1, -1, -1 ), new Vector3f( 2, 2, 2 ), .03f, 40 );
		Preset.InvExpLine a = new Preset.InvExpLine( new Point3f( 0, 0, 0 ), new Point3f( 1, 0, 0 ), dm );
		solver.addPrimitive( a );
		return solver.solve();
	}
	
	public static void createExperiment( String name, Preset.DistanceModifier dm )
	{
		float[] backOffs = new float[] { 0f, .05f, .1f, .15f, .2f };
		float[] isoLevels = new float[] { 10, 20, 40, 80, 160 };
		
		ArrayList<Triangle> exampleTriangles = makeExample( dm );
		String nameExample = "exp" + File.separator + name + File.separator + "example.scad";
		MarchingSolver.saveTriangles( exampleTriangles, nameExample );
		getPng( nameExample, "--camera=.5,5,0,.5,0,0" );
		
		int i = 0;
		for( int backoffIndex = 0; backoffIndex < 5; backoffIndex++ )
		{
			for( int isoLevelIndex = 0; isoLevelIndex < 5; isoLevelIndex++ )
			{
				System.out.println( i );
				
				//3 Line
				ArrayList<Triangle> triangles = experiment3Line( backOffs[backoffIndex], isoLevels[isoLevelIndex], dm );
				String name3Line = "exp" + File.separator + name + File.separator + "3line" + File.separator + String.format( "%03d", i ) + ".scad";
				MarchingSolver.saveTriangles( triangles, name3Line );
				getPng( name3Line, "--camera=1,5,5,.25,0,0" );
				
				
				//2 Line
				triangles = experiment2Line( backOffs[backoffIndex], isoLevels[isoLevelIndex], dm );
				String name2Line = "exp" + File.separator + name + File.separator + "2line" + File.separator + String.format( "%03d", i ) + ".scad";
				MarchingSolver.saveTriangles( triangles, name2Line );
				getPng( name2Line, "--camera=0,7,0,0,0,0" );
				
				i++;
			}
		}
	}
	
	public static void getPng( String file, String args )
	{
		try
		{
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec( "C:\\Program Files\\OpenSCAD\\openscad.exe -o " + file + ".png " + file + " " + args );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main( String[] args )
	{
		//Line Type A (Rounded end cylinder)
		//createExperiment( "linea", d -> 1 );
		createExperiment( "lineb", d -> .4f + (float)Math.sin( d*Math.PI ) * .6f );
	}
}
