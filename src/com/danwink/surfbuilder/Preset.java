package com.danwink.surfbuilder;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class Preset 
{
	public static class Sphere extends Primitive
	{
		Point3f p;

		public float compute( Point3f p )
		{
			return 1.f / this.p.distanceSquared( p );
		}		
	}
	
	public static class PullCone extends Primitive
	{
		Point3f base;
		Vector3f dir;
		float strength;
		float variance;
		
		public PullCone( Point3f base, Vector3f dir, float strength, float variance )
		{
			this.base = base;
			this.dir = dir;
			this.strength = strength;
			this.variance = variance;
		}
		
		public float compute( Point3f p )
		{
			Vector3f a = new Vector3f( p );
			a.sub( base );
			
			Vector3f an = new Vector3f( a );
			an.normalize();
			
			float adotdir = a.dot( dir );
			float dirdotdir = dir.dot( dir );
			
			Vector3f b = new Vector3f( dir );
			b.scale( adotdir / dirdotdir );
			
			return -(float)(strength * Math.abs( an.dot( dir ) ) * Math.exp( -variance * a.lengthSquared() )); 
			
			//return -(strength /* * Math.abs( an.dot( dir ) )*/ / a.lengthSquared());
		}
	}
	
	public static interface DistanceModifier
	{
		public float compute( float d );
	}
	
	public static interface FieldFunction
	{
		public float compute( float r2 );
	}
	
	public static interface NormalFunction
	{
		public float compute( Vector3f normal, Vector3f v );
	}
	
	public static FieldFunction MakeSoftLineFF( float a, float b )
	{
		float b2 = b*b;
		float b4 = b2*b2;
		float b6 = b4*b2;
		return r2 -> {
			if( r2 > b2 ) return 0;
			float r4 = r2*r2;
			float r6 = r2*r4;
			return a * (1 - ((4*r6)/(9*b6)) + ((17*r4)/(9*b4)) - ((22*r2)/(9*b2)) );
		};
	}
	
	public static final FieldFunction INVERSE_SQUARE = r2 -> 1.f / r2;
	
	public static class Line extends Primitive
	{
		Point3f p0;
		Point3f p1;
		Vector3f normal;
		float normalLength;
		
		FieldFunction ff;
		DistanceModifier dm;
		NormalFunction nf;
		
		public Line( Point3f p0, Point3f p1, Vector3f normal, FieldFunction ff, DistanceModifier dm, NormalFunction nf )
		{
			this.p0 = p0;
			this.p1 = p1;
			this.normal = normal;
			
			this.ff = ff;
			this.dm = dm;
			this.nf = nf;
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
				Vector3f p0p = new Vector3f( p );
				p0p.sub( p0 );
				return ff.compute( p.distanceSquared( p0 ) ) * dm.compute( 0 ) * nf.compute( normal, p0p );
			}
			
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				Vector3f p1p = new Vector3f( p );
				p1p.sub( p1 );
				return ff.compute( p.distanceSquared( p1 ) ) * dm.compute( 1 ) * nf.compute( normal, p1p );
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0 );
			Vector3f pbp = new Vector3f( p );
			pbp.sub( pb );
			float ret = ff.compute( p.distanceSquared( pb ) ) * dm.compute( b ) * nf.compute( normal, pbp ); 
			return ret;
		}
	}
	
	public static class InvExpLine extends Primitive
	{
		Point3f p0;
		Point3f p1;
		DistanceModifier dm;
		
		public InvExpLine( Point3f p0, Point3f p1, DistanceModifier dm )
		{
			this.p0 = p0;
			this.p1 = p1;
			this.dm = dm;
		}
		
		public InvExpLine( Point3f p0, Point3f p1 )
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
				return 1.f/ p.distanceSquared( p0 ) * dm.compute( 0 );
			}
			
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				return 1.f / p.distanceSquared( p1 ) * dm.compute( 1 ) ;
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0 );
			float ret = (1.f / p.distanceSquared( pb )) * dm.compute( b ); 
			return ret;
		}
	}
	
	public static class SoftLine extends Primitive
	{
		Point3f p0;
		Point3f p1;
		float a, b;
		float b2, b4, b6;
		
		DistanceModifier dm;
		
		public SoftLine( Point3f p0, Point3f p1, float a, float b, DistanceModifier dm )
		{
			this.p0 = p0;
			this.p1 = p1;
			this.a = a;
			this.b = b;
			this.b2 = b*b;
			this.b4 = b2*b2;
			this.b6 = b4*b2;
			this.dm = dm;
		}
		
		public SoftLine( Point3f p0, Point3f p1, float a, float b )
		{
			this( p0, p1, a, b, d -> 1 );
		}
		
		private float retFromD2( float r2 )
		{
			if( r2 > b2 ) return 0;
			float r4 = r2*r2;
			float r6 = r2*r4;
			return a * (1 - ((4*r6)/(9*b6)) + ((17*r4)/(9*b4)) - ((22*r2)/(9*b2)) );
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
				return retFromD2( p.distanceSquared( p0 ) ) * dm.compute( 0 );
			}
			
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				return retFromD2( p.distanceSquared( p1 ) ) * dm.compute( 1 );
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0 );
			float ret = retFromD2( p.distanceSquared( pb ) ) * dm.compute( b ); 
			return ret;
		}
	}
}
