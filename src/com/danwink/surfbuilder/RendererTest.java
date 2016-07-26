package com.danwink.surfbuilder;

import java.util.ArrayList;

import com.danwink.surfbuilder.fields.ConvolutionFieldBuilder;
import com.danwink.surfbuilder.fields.FieldBuilder;
import com.danwink.surfbuilder.fields.FieldBuilder.Field;
import com.danwink.surfbuilder.fields.SumFieldBuilder;
import com.danwink.surfbuilder.fields.SuperEllipticalFieldBuilder;
import com.danwink.surfbuilder.polygonize.MarchingCubePolygonizer;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class RendererTest
{
	public static class TwoLine extends Renderable
	{
		public TwoLine()
		{
			var( "isolevel", 0, 5 );
			var( "a", .5f, 3 );
			var( "b", 1, 5 );
			var( "r1", 0, 5 );
			var( "r2", 0, 5 );
			var( "t", 1, 5 );
		}
		
		public ArrayList<Triangle> getTris()
		{
			//MarchingSolver solver = new MarchingSolver( new Vector3f( -3, -3, -3 ), new Vector3f( 13, 13, 13 ), .5f, get( "isolevel" ) );
			
			Preset.FieldFunction ff = Preset.MakeSoftLineFF( get( "a" ), get( "b" ) );
			
			Point3f p0 = new Point3f( 0, 0, 0 );
			Point3f p1 = new Point3f( 10, 0, 0 );
			Point3f p2 = new Point3f( 0, 0, 10 );
			
			ArrayList<Primitive> primitives = new ArrayList<Primitive>();
			Primitive pr0 = new Preset.Line( p0, p1, ff );
			Primitive pr1 = new Preset.Line( p0, p2, ff );
			
			
			primitives.add( pr0 );
			primitives.add( pr1 );
			
			SuperEllipticalFieldBuilder fb = new SuperEllipticalFieldBuilder();
			fb.t = get( "t" );
			Field f = fb.buildField( primitives, new Vector3f( -3, -3, -3 ), new Vector3f( 13, 13, 13 ), .5f );
			
			MarchingCubePolygonizer mc = new MarchingCubePolygonizer();
			
			return mc.polygonize( f, get( "isolevel" ) );
		}
	}
	
	public static class PullConeTest extends Renderable
	{
		public PullConeTest()
		{
			var( "isolevel", 0, 1 );
			var( "strength", 0, 10 );
			var( "variance", 0, 1 );
		}
		
		public ArrayList<Triangle> getTris()
		{
			MarchingSolver solver = new MarchingSolver( new Vector3f( -3, -3, -3 ), new Vector3f( 13, 13, 13 ), .5f, get( "isolevel" ) );
			
			Preset.FieldFunction ff = Preset.MakeSoftLineFF( 1, 3 );
			
			Point3f p0 = new Point3f( 0, 0, 0 );
			Point3f p1 = new Point3f( 10, 0, 0 );
			Point3f p2 = new Point3f( 0, 10, 0 );
			Vector3f up = new Vector3f( 0, 0, 1 );
			
			solver.addPrimitive( new Preset.Line( p0, p1, ff ) );
			solver.addPrimitive( new Preset.Line( p0, p2, ff ) );
			solver.addPrimitive( new Preset.PullCone( p0, up, get( "strength" ), get( "variance" ) ) );
			
			return solver.solve();
		}
	}
	
	public static class Test2 extends Renderable
	{
		public Test2()
		{
			var( "isolevel", 0, 2f, .5f );
			var( "angle", 0, (float)Math.PI/2.f, 0 );
		}
		
		public ArrayList<Triangle> getTris() 
		{			
			Point3f p0 = new Point3f( 5, 0, 0 );
			Point3f p1 = new Point3f( 0, 0, 0 );
			Point3f p2 = new Point3f( 5 + (float)Math.cos( get( "angle" ) ) * 5, (float)Math.sin( get( "angle" ) ) * 5, 0 );
			
			Preset.ConvLine l0 = new Preset.ConvLine( p0, p1, 1 );
			Preset.ConvLine l1 = new Preset.ConvLine( p0, p2, 1 );
			
			ArrayList<Primitive> primitives = new ArrayList<Primitive>();
			
			primitives.add( l0 );
			primitives.add( l1 );
			
			SumFieldBuilder fb = new SumFieldBuilder();
			Field f = fb.buildField( primitives, new Vector3f( -3, -3, -3 ), new Vector3f( 13, 13, 3 ), .5f );
			
			MarchingCubePolygonizer mc = new MarchingCubePolygonizer();
			
			return mc.polygonize( f, get( "isolevel" ) );
		}
	}
	
	public static void main( String[] args )
	{
		//Renderable obj = new PullConeTest();
		//Renderable obj = new TwoLine();
		Renderable obj = new Test2();
		
		Renderer r = new Renderer( obj );
		r.run();
	}
}
