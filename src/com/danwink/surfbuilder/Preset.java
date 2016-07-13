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
	
	public static class ConvLine extends Primitive
	{
		Point3f p0, p1;
		Vector3f v, vn;
		float l, l2;
		float s = 1;
		float s2 = s*s;
		
		public ConvLine( Point3f p0, Point3f p1 )
		{
			this.p0 = p0;
			this.p1 = p1;
			
			v = new Vector3f();
			v.sub( p1, p0 );
			
			l = v.length();
			l2 = l*l;
			
			vn = new Vector3f( v );
			vn.scale( 1.f / l );
		}

		public float compute( Point3f p )
		{
			Vector3f d = new Vector3f( p );
			d.sub( p0 );
			
			float dl2 = d.lengthSquared();
			float dl = (float)Math.sqrt( dl2 );
			
			float x = d.dot( vn );
			float x2 = x*x;
			float p2 = 1 + s2 * (dl2 - x2);
			float pl = (float)Math.sqrt( p2 );
			float q2 = 1 + s2 * (dl2 + l2 - 2*l*x);
			
			float t1 = x / (2*p2 * (p2 + s2*x2));
			float t2 = (l-x)/(2*p2*q2);
			float t3 = (1/(2*s*p2*pl)) * (float)(Math.atan((s*x)/pl) + Math.atan((s*(l-x))/pl));
			
			return t1 + t2 + t3;
		}
	}
	
	public static class PullCone extends Primitive
	{
		Point3f base;
		Vector3f dir;
		float strength;
		float variance;
		boolean twoSided;
		
		public PullCone( Point3f base, Vector3f dir, float strength, float variance, boolean twoSided )
		{
			this.base = base;
			this.dir = dir;
			this.strength = strength;
			this.variance = variance;
			this.twoSided = twoSided;
		}
		
		public PullCone( Point3f base, Vector3f dir, float strength, float variance )
		{
			this( base, dir, strength, variance, true );
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
			float bscale = adotdir / dirdotdir; 
			b.scale( bscale );
			
			Vector3f bp = new Vector3f( p );
			bp.sub( b );
			bp.sub( base );
			
			float andotdir = (twoSided ? (float)Math.acos( Math.abs( an.dot( dir ) ) ) : an.dot( dir ) );
			
			return (float)(-strength * Math.abs( an.dot( dir ) ) * Math.exp( -variance * a.lengthSquared() )); //blob
			//return -(float)(strength * Math.exp( -variance * (((twoSided || bscale > 0 ? bscale : bscale*bscale) + bp.lengthSquared())))); // Cone
			//return -(float)((twoSided || bscale > 0 ? bscale : bscale*bscale) * (1.f / bp.lengthSquared()));
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
		
		float p0Pullback = 0;
		float p1Pullback = 1;
		Point3f p0Computed;
		Point3f p1Computed;
		
		FieldFunction ff;
		DistanceModifier dm;
		NormalFunction nf;
		
		public Line( Point3f p0, Point3f p1, Vector3f normal, FieldFunction ff, DistanceModifier dm, NormalFunction nf )
		{
			this.p0 = p0;
			this.p1 = p1;
			 
			this.p0Computed = new Point3f( p0 );
			this.p1Computed = new Point3f( p1 );
			
			this.normal = normal;
			
			this.ff = ff;
			this.dm = dm;
			this.nf = nf;
		}
		
		public Line( Point3f p0, Point3f p1, FieldFunction ff )
		{
			this( p0, p1, new Vector3f(0, 0, 1), ff, d -> 1, (n, v) -> 1 );
		}
		
		public float compute( Point3f p )
		{
			Vector3f v = new Vector3f( p1Computed );
			v.sub( p0Computed );
			Vector3f w = new Vector3f( p );
			w.sub( p0Computed );
			
			float c1 = w.dot( v );
			if( c1 < 0 )
			{
				Vector3f p0p = new Vector3f( p );
				p0p.sub( p0Computed );
				return ff.compute( p.distanceSquared( p0Computed ) ) * dm.compute( 0 ) * nf.compute( normal, p0p );
			}
			
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				Vector3f p1p = new Vector3f( p );
				p1p.sub( p1Computed );
				return ff.compute( p.distanceSquared( p1Computed ) ) * dm.compute( 1 ) * nf.compute( normal, p1p );
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0Computed );
			Vector3f pbp = new Vector3f( p );
			pbp.sub( pb );
			float ret = ff.compute( p.distanceSquared( pb ) ) * dm.compute( b ) * nf.compute( normal, pbp ); 
			return ret;
		}
		
		public void setPullback( float a, float b )
		{
			p0Pullback = a;
			p1Pullback = b;
			Vector3f v0 = new Vector3f( p1 );
			v0.sub( p0 );
			Vector3f v1 = new Vector3f( v0 );
			
			v0.scale( p0Pullback );
			v1.scale( 1 - p1Pullback );
			
			p0Computed.set( p0 );
			p0Computed.add( v0 );
			
			p1Computed.set( p0 );
			p1Computed.add( v1 );
		}

		public Point3f getClosestPoint( Point3f p )
		{
			Vector3f v = new Vector3f( p1Computed );
			v.sub( p0Computed );
			Vector3f w = new Vector3f( p );
			w.sub( p0Computed );
			
			float c1 = w.dot( v );
			if( c1 < 0 )
			{
				Vector3f p0p = new Vector3f( p );
				p0p.sub( p0Computed );
				return new Point3f( p0p );
			}
			
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				Vector3f p1p = new Vector3f( p );
				p1p.sub( p1Computed );
				return new Point3f( p1p );
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0Computed );
			Vector3f pbp = new Vector3f( p );
			pbp.sub( pb );
			float ret = ff.compute( p.distanceSquared( pb ) ) * dm.compute( b ) * nf.compute( normal, pbp ); 
			return new Point3f( pbp );
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
