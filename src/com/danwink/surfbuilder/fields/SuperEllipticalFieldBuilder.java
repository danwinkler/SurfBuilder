package com.danwink.surfbuilder.fields;

import java.util.ArrayList;

import com.danwink.surfbuilder.Primitive;
import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class SuperEllipticalFieldBuilder extends FieldBuilder
{
	public float t;

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
					
					float f = 0;
					
					for( Primitive p : primitives )
					{
						//f += (float)Math.pow( Math.max( (1.f - (fixReturn( p.compute( point ) )) ) / p.r, 0 ), t );
						f += (float)Math.exp( -fixReturn( p.compute( point ) )/2.f );
					}
										
					field.field[x][y][z] = f;
				}
			}
		}
		
		return field;
	}
}
