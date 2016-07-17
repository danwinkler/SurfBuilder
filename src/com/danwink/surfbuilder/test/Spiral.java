package com.danwink.surfbuilder.test;

import java.util.ArrayList;

import com.danwink.dlib2.processing.PRenderer;
import com.danwink.surfbuilder.MarchingSolver;
import com.danwink.surfbuilder.Preset;
import com.danwink.surfbuilder.Primitive;
import com.danwink.surfbuilder.Triangle;
import com.danwink.surfbuilder.fields.FieldBuilder;
import com.danwink.surfbuilder.fields.SumFieldBuilder;
import com.danwink.surfbuilder.fields.FieldBuilder.Field;
import com.danwink.surfbuilder.polygonize.MarchingCubePolygonizer;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;
import processing.core.PShape;

public class Spiral extends PRenderer
{
	PShape shape;
	
	public void begin()
	{
		size( 800, 600, P3D );
		
		var( "max_rad", 0, 10, 5 );
		var( "min_rad", 0, 10, 1 );
		var( "d_angle", .01f, .5f, .23f );
		var( "h_parts", 10, 100, 50 );
		var( "height", 5, 10, 8 );
		var( "isolevel", 0, 1, .32f );
		var( "s", 0, 5, 2 );
	}

	public void render2D()
	{
		
	}

	public void render3D()
	{
		if( keyPressed ) 
		{
		    if( key == 's' )
		    {
		    	save();
		    }
		}
		
		shape.draw( g );
	}
	
	public void save()
	{
		ArrayList<Triangle> tris = buildTris();
		MarchingSolver.saveTriangles( tris, "test.scad" );
	}
	
	public ArrayList<Triangle> buildTris()
	{
		ArrayList<Primitive> primitives = new ArrayList<Primitive>();
		
		int h_parts = (int)get( "h_parts" );
		float d_angle = get( "d_angle" );
		float maxRad = get( "max_rad" );
		float minRad = get( "min_rad" );
		
		
		float h_inc = get( "height" ) / h_parts;
		for( int h = 0; h < h_parts-1; h++ )
		{
			float d = h / (float)h_parts;
			float angle = d_angle * h;
			float height = h_inc * h;
			float rad = lerp( minRad, maxRad, d );
			
			float n_d = (h+1) / (float)h_parts;
			float n_angle = d_angle * (h+1);
			float n_height = h_inc * (h+1);
			float n_rad = lerp( minRad, maxRad, n_d );
			
			
			Point3f p0 = new Point3f( cos(angle) * rad, sin(angle) * rad, height );
			Point3f p1 = new Point3f( cos(n_angle) * n_rad, sin(n_angle) * n_rad, n_height );
			
			Point3f p3 = new Point3f( cos(-angle) * rad, sin(-angle) * rad, height );
			Point3f p4 = new Point3f( cos(-n_angle) * n_rad, sin(-n_angle) * n_rad, n_height );
			
			primitives.add( new Preset.ConvLine( p0, p1, get( "s" ) ) );
			primitives.add( new Preset.ConvLine( p3, p4, get( "s" ) ) );
		}
		
		SumFieldBuilder fb = new SumFieldBuilder();
		Field f = fb.buildField( primitives, new Vector3f( -10, -10, -3 ), new Vector3f( 10, 10, 13 ), .2f );
		
		MarchingCubePolygonizer mc = new MarchingCubePolygonizer();
		
		return mc.polygonize( f, get( "isolevel" ) );
	}
	
	public void valueChanged()
	{			
		ArrayList<Triangle> tris = buildTris();
		
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
		Spiral sp = new Spiral();
		sp.run();
	}
}

