package com.danwink.surfbuilder;

import java.util.ArrayList;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class SoftLineExp
{	
	public static void main( String[] args )
	{	
		MarchingSolver solver = new MarchingSolver( new Vector3f( -30, -30, -10 ), new Vector3f( 30, 30, 10 ), .6f, .3f );
		solver.verbose = true;
		
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
		
		Vector3f up = new Vector3f( 0, 0, 1 );
		/*
		int num = 3;
		for( int i = 0; i < 6; i++ )
		{
			float a = (float)((Math.PI*2 / num) * i);
			float na = (float)((Math.PI*2 / num) * ((i+1)%num));
			float offset = .5f;
			float a2 = a + offset;
			float na2 = na + offset;
			Point3f p0 = new Point3f( (float)Math.cos( a ) * 10, (float)Math.sin( a ) * 10, 0 );
			Point3f p1 = new Point3f( (float)Math.cos( na ) * 10, (float)Math.sin( na ) * 10, 0 );
			Point3f p2 = new Point3f( (float)Math.cos( na2 )*2 * 10, (float)Math.sin( na2 )*2 * 10, 0 );
			Point3f p3 = new Point3f( (float)Math.cos( a2 )*2 * 10, (float)Math.sin( a2 )*2 * 10, 0 );
			solver.addPrimitive( new Preset.Line( p0, p1, up, ff, dm, nf ) );
			solver.addPrimitive( new Preset.Line( p0, p3, up, ff, dm, nf ) );
			solver.addPrimitive( new Preset.Line( p3, p2, up, ff, dm, nf ) );
		}*/
		
		Point3f p0 = new Point3f( 0, 0, 0 );
		Point3f p1 = new Point3f( 10, 0, 0 );
		Point3f p2 = new Point3f( 0, 10, 0 );
		
		Vector3f puller = new Vector3f( 0, 0, 1 );
		
		solver.addPrimitive( new Preset.Line(p0, p1, up, ff, dm, nf) );
		solver.addPrimitive( new Preset.Line(p0, p2, up, ff, dm, nf) );
		solver.addPrimitive( new Preset.PullCone( p0, puller, .5f, .1f ) );
		
		ArrayList<Triangle> triangles = solver.solve();
		
		MarchingSolver.saveTriangles( triangles, "test.scad" );
	}
}
