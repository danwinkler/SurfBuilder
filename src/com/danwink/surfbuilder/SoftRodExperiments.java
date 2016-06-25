package com.danwink.surfbuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.danwink.surfbuilder.MarchingSolver.Triangle;
import com.danwink.surfbuilder.Preset.SoftLine;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class SoftRodExperiments
{	
	public static ArrayList<Triangle> experiment3Line( float backOff, float isoLevel, float a, float b )
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -5, -5, -5 ), new Vector3f( 15, 15, 15 ), .3f, isoLevel );
		
		SoftLine al = new SoftLine( new Point3f( backOff, 0, 0 ), new Point3f( 10, 0, 0 ), a, b );
		SoftLine bl = new SoftLine( new Point3f( 0, backOff, 0 ), new Point3f( 0, 10, 0 ), a, b );
		SoftLine cl = new SoftLine( new Point3f( 0, 0, backOff ), new Point3f( 0, 0, 10 ), a, b );
		
		solver.addPrimitive( al, bl, cl );
		
		return solver.solve();
	}
	
	public static ArrayList<Triangle> experiment2Line( float backOff, float isoLevel, float a, float b )
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -2, -1, -1 ), new Vector3f( 2, 1, 1 ), .03f, isoLevel );
		
		SoftLine al = new SoftLine( new Point3f( backOff, 0, 0 ), new Point3f( 1, 0, 0 ), a, b );
		SoftLine bl = new SoftLine( new Point3f( -backOff, 0, 0 ), new Point3f( -1, 0, 0 ), a, b );
		
		solver.addPrimitive( al, bl );
		
		return solver.solve();
	}
	
	public static ArrayList<Triangle> makeExample()
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -1, -1, -1 ), new Vector3f( 2, 2, 2 ), .03f, 40 );
		Preset.Line a = new Preset.Line( new Point3f( 0, 0, 0 ), new Point3f( 1, 0, 0 ) );
		solver.addPrimitive( a );
		return solver.solve();
	}
	
	public static void createExperiment( String name )
	{
		float[] backOffs = new float[] { 0f, .05f, .1f, .15f, .2f };
		float[] isoLevels = new float[] { .1f, .3f, .5f, .7f, .9f };
		float defaultIso = .8f;
		float[] as = new float[] { .5f, 1, 1.5f, 2.f, 2.5f };
		float defaultA = 1;
		float[] bs = new float[] { 3.f, 3.5f, 4, 4.5f, 5 };
		float defaultB = 3;
		
		ArrayList<Triangle> exampleTriangles = makeExample();
		String nameExample = "exp" + File.separator + name + File.separator + "example.scad";
		MarchingSolver.saveTriangles( exampleTriangles, nameExample );
		getPng( nameExample, "--camera=.5,5,0,.5,0,0" );
		
		int i = 0;
		for( int indexA = 0; indexA < 5; indexA++ )
		{
			for( int indexB = 0; indexB < 5; indexB++ )
			{
				System.out.println( i );
				
				//3 Line
				ArrayList<Triangle> triangles = experiment3Line( 0, isoLevels[indexB], defaultA, bs[indexA] );
				String name3Line = "exp" + File.separator + name + File.separator + "3line" + File.separator + String.format( "%03d", i ) + ".scad";
				MarchingSolver.saveTriangles( triangles, name3Line );
				getPng( name3Line, "--camera=10,50,50,2.5,0,0" );
				
				
				//2 Line
				triangles = experiment2Line( 0, isoLevels[indexB], defaultA, bs[indexA] );
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
		createExperiment( "softline" );
		//createExperiment( "lineb", d -> .4f + (float)Math.sin( d*Math.PI ) * .6f );
	}
}
