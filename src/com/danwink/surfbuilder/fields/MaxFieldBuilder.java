package com.danwink.surfbuilder.fields;

import java.util.ArrayList;

import com.danwink.surfbuilder.Primitive;
import com.danwink.surfbuilder.fields.FieldBuilder.Field;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

/**
 * Improved Super-elliptical Blending
 * 
 * @author Daniel Winkler
 *
 */
public class MaxFieldBuilder extends FieldBuilder
{
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
					
					float f = Float.MIN_VALUE;
					
					for( Primitive p : primitives )
					{
						f = Math.max( fixReturn( p.compute( point ) ), f );
					}
										
					field.field[x][y][z] = f;
				}
			}
		}
		
		return field;
	}
}
