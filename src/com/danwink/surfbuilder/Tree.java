package com.danwink.surfbuilder;

import java.util.ArrayList;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

import com.danwink.surfbuilder.MarchingSolver.Triangle;
import com.phyloa.dlib.util.DMath;

public class Tree 
{
	public static class Branch extends Primitive
	{
		Point3f p0;
		Point3f p1;
		int depth;
		
		ArrayList<Branch> children = new ArrayList<Branch>();

		public Branch( Point3f p0, Point3f p1, int depth )
		{
			this.p0 = p0;
			this.p1 = p1;
			this.depth = depth;
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
				return fixReturn( 1.f/ p.distanceSquared( p0 ) * lengthFactor( 0 ) );
			}
			
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				return fixReturn( 1.f / p.distanceSquared( p1 ) * lengthFactor( 1 ) );
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0 );
			float ret = (1.f / p.distanceSquared( pb )) * lengthFactor( b ); 
			return fixReturn( ret );
		}
		
		public float lengthFactor( float d )
		{
			float scale = 1.f / (depth+1);
			float lFactor = .4f + (float)Math.sin( d*Math.PI ) * .6f;
			return scale * lFactor;
		}
		
		private float fixReturn( float v )
		{
			if( Float.isNaN( v ) ) return 0;
			if( Float.isInfinite( v ) ) return Float.MAX_VALUE;
			return v;
		}
		
		public void collect( ArrayList<Branch> branches )
		{
			branches.add( this );
			for( Branch b : children ) 
			{
				b.collect( branches );
			}
		}
	}
	
	public static void build( Branch b, int depthLimit )
	{
		if( b.depth == depthLimit ) return;
		
		Vector3f v = new Vector3f( b.p1 );
		v.sub( b.p0 );
		v.scale( .8f );
		
		float scale = v.length() * .8f;
		
		int count = DMath.randomi( 1, 4 );
		for( int i = 0; i < count; i++ )
		{
			Point3f np = new Point3f();
			np.x = b.p1.x + v.x + DMath.randomf( -scale, scale );
			np.y = b.p1.y + v.y + DMath.randomf( -scale, scale );
			np.z = b.p1.z + v.z + DMath.randomf( -scale, scale );
			Branch nb = new Branch( b.p1, np, b.depth + 1 );
			b.children.add( nb );
			build( nb, depthLimit );
		}
	}
	
	public static void main( String[] args )
	{
		MarchingSolver solver = new MarchingSolver( new Vector3f( -3, -3, -1 ), new Vector3f( 3, 3, 8 ), .03f, 30 );
		Branch base = new Branch( new Point3f( 0, 0, 0 ), new Point3f( 0, 0, 1 ), 0 );
		build( base, 2 );
		
		ArrayList<Branch> branches = new ArrayList<Branch>();
		base.collect(branches);
		for( Branch b : branches ) 
			solver.addPrimitive( b );
		
		//To make base
		solver.addPrimitive( new Branch( new Point3f(), new Point3f( .3f, 0, 0 ), 2 ) );
		solver.addPrimitive( new Branch( new Point3f(), new Point3f( 0, .3f, 0 ), 2 ) );
		solver.addPrimitive( new Branch( new Point3f(), new Point3f( -.2f, -.2f, 0 ), 2 ) );
		
		
		ArrayList<Triangle> triangles = solver.solve();
		
		MarchingSolver.saveTriangles( triangles, "test.scad" );
	}
}
