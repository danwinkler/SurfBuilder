package com.danwink.surfbuilder;

import java.io.IOException;
import java.util.ArrayList;

import com.danwink.surfbuilder.MarchingSolver.Triangle;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class Animator
{	
	public static void main( String[] args )
	{
		Preset.DistanceModifier dm = d -> {
			return 1f - (float)Math.sin( d*Math.PI ) * .5f;
		};
		
		dm = d -> 1;
		
		Preset.FieldFunction ff = Preset.MakeSoftLineFF( 1, 3 );
		//ff = Preset.INVERSE_SQUARE;
		
		Preset.NormalFunction nf = (n, v) -> {
			v.normalize();
			float a = 1;
			float b = 20;
			float k = 7;
			float lin = Math.abs( v.dot( n ) );
			float ret = (float)(a / (1.f + b * Math.pow( Math.E, -k*lin )));
			return 1f - ret*.5f;
		};
		nf = (n, v) -> 1;
		
		Vector3f up = new Vector3f( 0, 0, 1 );
		
		for( float i = 1f; i < 3; i += .03f )
		{
			MarchingSolver solver = new MarchingSolver( new Vector3f( -3, -3, -3 ), new Vector3f( 13, 13, 13 ), .5f, .3f );
			
			Point3f p0 = new Point3f( 0, 0, 0 );
			Point3f p1 = new Point3f( 10, 0, 0 );
			Point3f p2 = new Point3f( 0, 0, 10 );
			
			solver.addPrimitive( new Preset.Line( p0, p1, ff ) );
			solver.addPrimitive( new Preset.Line( p0, p2, ff ) );
			
			//solver.addPrimitive( new Preset.PullCone( p0, up, i*10f, 1f, true ) );
			
			ArrayList<Triangle> triangles = solver.solve();
			
			String file = "exp/anim/d/" + String.format("%.2f", i) + ".scad";
			MarchingSolver.saveTriangles( triangles, file );
			getPng( file, "--camera=30,-50,10,3,0,3" );
			System.out.println( i );
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
}
