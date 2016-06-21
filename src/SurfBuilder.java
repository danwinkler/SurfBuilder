import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class SurfBuilder 
{
	public static class Point implements Primitive
	{
		Point3f p;
		
		public float distance( Point3f p )
		{
			return this.p.distance( p );
		}

		public float distance2( Point3f p )
		{
			return this.p.distanceSquared( p );
		}		
	}
	
	public static class Line implements Primitive
	{
		Point3f p0;
		Point3f p1;
		
		public Line( Point3f p0, Point3f p1 )
		{
			this.p0 = p0;
			this.p1 = p1;
		}
		
		public float distance( Point3f p )
		{
			return (float) Math.sqrt( distance2( p ) );
		}

		public float distance2( Point3f p )
		{
			Vector3f v = new Vector3f( p1 );
			v.sub( p0 );
			Vector3f w = new Vector3f( p );
			w.sub( p0 );
			
			float c1 = w.dot( v );
			if( c1 < 0 )
			{
				return p.distanceSquared( p0 );
			}
			
			float c2 = v.dot( v );
			if( c2 < c1 )
			{
				return p.distanceSquared( p1 );
			}
			
			float b = c1 / c2;
			Point3f pb = new Point3f( v );
			pb.scale( b );
			pb.add( p0 );
			return p.distanceSquared( pb );
		}
	}
	
	public static class InverseSquareSurface extends Surface
	{
		float crossover;
		
		public InverseSquareSurface( float crossover )
		{
			super();
			this.crossover = crossover;
		}
		
		@Override
		public float compute2( float distance2 )
		{
			return (1.f / distance2) - crossover;
		}		
	}
	
	public static void main( String[] args )
	{
		Surface s = new InverseSquareSurface( 3 );
		MarchingSolver solver = new MarchingSolver( s, new Vector3f( -1, -1, -1 ), new Vector3f( 2, 2, 2 ), .1f );
		
		Point3f p0 = new Point3f();
		Point3f p1 = new Point3f( 1, 0, 0 );
		Point3f p2 = new Point3f( 0, 1, 0 );
		Point3f p3 = new Point3f( 0, 0, 1 );
		
		solver.addPrimitive( new Line( p0, p1 ) );
		solver.addPrimitive( new Line( p0, p2 ) );
		solver.addPrimitive( new Line( p0, p3 ) );
		
		ArrayList<Point3f> points = solver.solve();
		
		StringBuilder sb = new StringBuilder();
		for( Point3f p : points )
		{
			sb.append( p.x );
			sb.append( " " );
			sb.append( p.y );
			sb.append( " " );
			sb.append( p.z );
			sb.append( "\n" );
		}

		try 
		{
			Files.write( FileSystems.getDefault().getPath( "test.xyz" ), sb.toString().getBytes() );
		} 
		catch( IOException e ) 
		{
			e.printStackTrace();
		}
	}
}
