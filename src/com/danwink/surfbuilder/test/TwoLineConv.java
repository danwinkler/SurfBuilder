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

public class TwoLineConv extends PRenderer
{
	PShape shape;
	
	public void begin()
	{
		size( 800, 600, P3D );
		
		var( "angle", 0, PI/2, 0 );
		var( "isolevel", 0, 1, .5f );
		var( "s", 0, 5, 2 );
	}

	public void render2D()
	{
		
	}

	public void render3D()
	{
		shape.draw( g );
	}
	
	public void valueChanged()
	{
		Point3f p0 = new Point3f( 5, 0, 0 );
		Point3f p1 = new Point3f( 0, 0, 0 );
		Point3f p2 = new Point3f( 5 + (float)Math.cos( get( "angle" ) ) * 5, (float)Math.sin( get( "angle" ) ) * 5, 0 );
		
		Preset.ConvLine l0 = new Preset.ConvLine( p0, p1, get( "s" ) );
		Preset.ConvLine l1 = new Preset.ConvLine( p0, p2, get( "s" ) );
		
		ArrayList<Primitive> primitives = new ArrayList<Primitive>();
		
		primitives.add( l0 );
		primitives.add( l1 );
		
		SumFieldBuilder fb = new SumFieldBuilder();
		Field f = fb.buildField( primitives, new Vector3f( -3, -3, -3 ), new Vector3f( 13, 13, 3 ), .5f );
		
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
	}
	
	public static void main( String[] args )
	{
		TwoLineConv tlc = new TwoLineConv();
		tlc.run();
	}
}
