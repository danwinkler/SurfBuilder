package com.danwink.surfbuilder;

import java.util.ArrayList;

import com.danwink.surfbuilder.MarchingSolver.Triangle;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class WebLike
{
	public static class AwareLine extends Primitive
	{
		Point3f p0;
		Point3f p1;
		
		public AwareLine( Point3f p0, Point3f p1 )
		{
			this.p0 = p0;
			this.p1 = p1;
		}

		public float compute( Point3f p )
		{
			Vector3f main = getVector( p );
			float mLength = main.length();
			float field = 1.f / main.lengthSquared();
			main.scale( 1.f/ mLength );
			
			float sumDot = 0;
			for( Primitive prim : owner.primitives )
			{
				if( prim == this ) continue;
				AwareLine al = (AwareLine)prim;
				Vector3f v = al.getVector( p );
				float vLength = v.length();
				v.scale( 1.f / vLength );
				sumDot += MarchingSolver.fixReturn( (float)Math.acos( v.dot( main ) ) ) * (1.f / vLength*2);
			}
			
			return field*.5f + sumDot * .1f;
		}
		
		public Vector3f getVector( Point3f p )
		{
			Vector3f v = new Vector3f( p1 );
			v.sub( p0 );
			Vector3f w = new Vector3f( p );
			w.sub( p0 );
			
			Vector3f retV = new Vector3f();
			
			float c1 = w.dot( v );
			if( c1 < 0 )
			{
				retV.set( p0 );
				retV.sub( p );
				return retV;
			}
			
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				retV.set( p1 );
				retV.sub( p );
				return retV;
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0 );
			
			retV.set( pb );
			retV.sub( p );
			return retV;
		}
	}
	
	public static void main( String[] args )
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -3, -3, -1 ), new Vector3f( 3, 3, 1 ), .03f, 120 );
		solver.verbose = true;
		
		for( int i = 0; i < 6; i++ )
		{
			float a = (float)((Math.PI*2 / 6) * i);
			float na = (float)((Math.PI*2 / 6) * ((i+1)%6));
			float offset = .5f;
			float a2 = a + offset;
			float na2 = na + offset;
			Point3f p0 = new Point3f( (float)Math.cos( a ), (float)Math.sin( a ), 0 );
			Point3f p1 = new Point3f( (float)Math.cos( na ), (float)Math.sin( na ), 0 );
			Point3f p2 = new Point3f( (float)Math.cos( na2 )*2, (float)Math.sin( na2 )*2, 0 );
			Point3f p3 = new Point3f( (float)Math.cos( a2 )*2, (float)Math.sin( a2 )*2, 0 );
			solver.addPrimitive( new AwareLine( p0, p1 ) );
			solver.addPrimitive( new AwareLine( p0, p3 ) );
			solver.addPrimitive( new AwareLine( p3, p2 ) );
		}
		
		
		ArrayList<Triangle> triangles = solver.solve();
		
		MarchingSolver.saveTriangles( triangles, "awaretest.scad" );
	}
}
