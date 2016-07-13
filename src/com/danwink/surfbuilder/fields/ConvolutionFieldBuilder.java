package com.danwink.surfbuilder.fields;

import java.util.ArrayList;

import com.danwink.surfbuilder.Primitive;
import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class ConvolutionFieldBuilder extends FieldBuilder
{
	public float v;

	public Field buildField( ArrayList<Primitive> primitives, Vector3f min, Vector3f max, float res )
	{
		Field field = new Field( min, max, res );
		
		//Build out field
		Point3f point = new Point3f();
		for( int x = 0; x < field.xSize; x++ )
		{
			point.x = x * res + min.x;
			for( int y = 0; y < field.ySize; y++ )
			{
				point.y = y * res + min.y;
				for( int z = 0; z < field.zSize; z++ )
				{
					point.z = z * res + min.z;
					
					float f = Float.MAX_VALUE;
					f = 0;
					for( Primitive p : primitives )
					{
						//f = Math.min( fixReturn( p.compute( point ) ), f );
						//f += fixReturn( p.compute( point ) );
						//f += (float)1 - Math.exp( -fixReturn( p.compute( point ) ) / 2.f );
						//f += (float)Math.pow( Math.max( 0, 1 - (fixReturn( p.compute( point ) ) / v) ), 2 );
						//float v = (float)(Math.sqrt( Math.PI / 2 ) * -point.x * erf( (point.x - closePoint.x) / Math.sqrt( 2 ) ) * Math.exp( .5f * Math.pow((point.x - closePoint.x), 2) ));
						//v *= (float)(Math.sqrt( Math.PI / 2 ) * -point.y * erf( (point.y - closePoint.y) / Math.sqrt( 2 ) ) * Math.exp( .5f * Math.pow((point.y - closePoint.y), 2) ));
						//v *= (float)(Math.sqrt( Math.PI / 2 ) * -point.z * erf( (point.z - closePoint.z) / Math.sqrt( 2 ) ) * Math.exp( .5f * Math.pow((point.z - closePoint.z), 2) ));
						
						
						
						//f += v;
						float d = fixReturn( p.compute( point ) );
						//f += (float)(Math.sqrt( Math.PI / 2 ) * 1 * erf( Math.sqrt(d) / Math.sqrt( 2 ) ) * Math.exp( .5f * d ) );
						f += Math.exp( d );
					}
					
					//f = 1 - f;
									
					field.field[x][y][z] = f;
				}
			}
		}
		
		return field;
	}
	//http://introcs.cs.princeton.edu/java/21function/ErrorFunction.java.html
	public static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                            t * ( 1.00002368 +
                                            t * ( 0.37409196 + 
                                            t * ( 0.09678418 + 
                                            t * (-0.18628806 + 
                                            t * ( 0.27886807 + 
                                            t * (-1.13520398 + 
                                            t * ( 1.48851587 + 
                                            t * (-0.82215223 + 
                                            t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    }
}