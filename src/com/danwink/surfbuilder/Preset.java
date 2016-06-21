package com.danwink.surfbuilder;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class Preset 
{
	public static class Sphere implements Primitive
	{
		Point3f p;

		public float compute( Point3f p )
		{
			return 1.f / this.p.distanceSquared( p );
		}		
	}
	
	public static class Line implements Primitive
	{
		Point3f p0;
		Point3f p1;
		DistanceModifier dm;
		
		public Line( Point3f p0, Point3f p1, DistanceModifier dm )
		{
			this.p0 = p0;
			this.p1 = p1;
			this.dm = dm;
		}
		
		public Line( Point3f p0, Point3f p1 )
		{
			this( p0, p1, d -> 1 );
		}

		public float compute( Point3f p )
		{
			Vector3f v = new Vector3f( p1 );
			v.sub( p0 );
			Vector3f w = new Vector3f( p );
			w.sub( p0 );
			
			float c1 = w.dot( v );
			if( c1 < 0 )
			{
				return fixReturn( 1.f/ p.distanceSquared( p0 ) * dm.compute( 0 ) );
			}
			
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				return fixReturn( 1.f / p.distanceSquared( p1 ) * dm.compute( 1 ) );
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0 );
			float ret = (1.f / p.distanceSquared( pb )) * dm.compute( b ); 
			return fixReturn( ret );
		}
		
		private float fixReturn( float v )
		{
			if( Float.isNaN( v ) ) return 0;
			if( Float.isInfinite( v ) ) return Float.MAX_VALUE;
			return v;
		}
		
		public static interface DistanceModifier
		{
			public float compute( float d );
		}
	}
}
