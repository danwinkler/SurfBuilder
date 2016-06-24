package com.danwink.surfbuilder;
import java.util.ArrayList;

import com.danwink.surfbuilder.MarchingSolver.Triangle;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class SurfBuilder 
{
	public static void main( String[] args )
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -1, -1, -1 ), new Vector3f( 3, 3, 3 ), .03f, 30 );
		
		Point3f p0 = new Point3f();
		Point3f p1 = new Point3f( 1, 0, 0 );
		Point3f p2 = new Point3f( 0, 1, 0 );
		Point3f p3 = new Point3f( 0, 0, 2 );
		Preset.Line.DistanceModifier dm = d -> {
			return .2f + (float)Math.sin( d*Math.PI + .1f ) * .6f;
		};
		
		solver.addPrimitive( new Preset.Line( p0, p1, dm ) );
		solver.addPrimitive( new Preset.Line( p0, p2, dm ) );
		solver.addPrimitive( new Preset.Line( p0, p3, dm ) );
		
		ArrayList<Triangle> triangles = solver.solve();
		
		MarchingSolver.saveTriangles( triangles, "test.scad" );
	}
}
