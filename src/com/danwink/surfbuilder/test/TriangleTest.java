package com.danwink.surfbuilder.test;

import java.util.ArrayList;

import com.danwink.dlib2.processing.PRenderer;
import com.danwink.surfbuilder.Preset;
import com.danwink.surfbuilder.Primitive;
import com.danwink.surfbuilder.Triangle;
import com.danwink.surfbuilder.fields.SumFieldBuilder;
import com.danwink.surfbuilder.fields.FieldBuilder.Field;
import com.danwink.surfbuilder.polygonize.MarchingCubePolygonizer;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;
import processing.core.PShape;

public class TriangleTest extends PRenderer
{
	PShape shape;
	
	public void begin()
	{
		size( 800, 600, P3D );
		
		var( "angle", 0, PI/2, 0 );
		var( "isolevel", 0, 10, .5f );
		var( "s", 0, 10, 2 );
		var( "c", 0, 10, 0 );
	}

	public void render2D()
	{
		
	}

	public void render3D()
	{
		stroke( 255, 255, 255 );
		shape.draw( g );
	}
	
	public void valueChanged()
	{
		Point3f p0 = new Point3f( 1, 1, .1f );
		Point3f p1 = new Point3f( 5, 1, .1f );
		Point3f p2 = new Point3f( 1, 8, .1f );
		
		Preset.ConvTri tri = new Preset.ConvTri( p0, p1, p2, get( "s" ) );
		tri.c = get( "c" );
		
		ArrayList<Primitive> primitives = new ArrayList<Primitive>();
		
		primitives.add( tri );
		
		SumFieldBuilder fb = new SumFieldBuilder();
		Field f = fb.buildField( primitives, new Vector3f( -10, -10, -10 ), new Vector3f( 10, 10, 10 ), .5f );
		
		MarchingCubePolygonizer mc = new MarchingCubePolygonizer();
		
		ArrayList<Triangle> tris =  mc.polygonize( f, get( "isolevel" ) );
		
		shape = createShape();
		shape.beginShape( TRIANGLES );
		for( Triangle t : tris )
		{
			shape.vertex( t.a.x, t.a.y, t.a.z );
			shape.vertex( t.b.x, t.b.y, t.b.z );
			shape.vertex( t.c.x, t.c.y, t.c.z );
		}
		shape.endShape();
		
		shape.beginShape( POINTS );
		shape.strokeWeight( 5 );
		shape.stroke( 255, 0, 0 );
		shape.vertex( tri.p0.x, tri.p0.y, tri.p0.z );
		
		shape.stroke( 0, 255, 0 );
		shape.vertex( tri.p1.x, tri.p1.y, tri.p1.z );
		
		shape.stroke( 0, 0, 255 );
		shape.vertex( tri.p2.x, tri.p2.y, tri.p2.z );
		
		shape.stroke( 255, 255, 255 );
		shape.vertex( tri.b.x, tri.b.y, tri.b.z );
		shape.endShape();
		
	}
	
	public static void main( String[] args )
	{
		TriangleTest test = new TriangleTest();
		test.run();
	}
}

