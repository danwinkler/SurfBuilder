package com.danwink.surfbuilder;

import java.util.ArrayList;
import java.util.HashMap;

import com.danwink.surfbuilder.MarchingSolver.Triangle;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class RendererTest
{
	public static class TwoLine extends Renderable
	{
		public TwoLine()
		{
			var( "isolevel", 0, 1 );
		}
		
		public ArrayList<Triangle> getTris()
		{
			MarchingSolver solver = new MarchingSolver( new Vector3f( -3, -3, -3 ), new Vector3f( 13, 13, 13 ), .5f, get( "isolevel" ) );
			
			Preset.FieldFunction ff = Preset.MakeSoftLineFF( 1, 3 );
			
			Point3f p0 = new Point3f( 0, 0, 0 );
			Point3f p1 = new Point3f( 10, 0, 0 );
			Point3f p2 = new Point3f( 0, 0, 10 );
			
			solver.addPrimitive( new Preset.Line( p0, p1, ff ) );
			solver.addPrimitive( new Preset.Line( p0, p2, ff ) );
			
			return solver.solve();
		}
	}
	
	public static void main( String[] args )
	{
		Renderable obj = new TwoLine();
		
		Renderer r = new Renderer( obj );
		r.run();
	}
}
