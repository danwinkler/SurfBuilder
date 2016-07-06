package com.danwink.surfbuilder.fields;

import java.util.ArrayList;

import com.danwink.surfbuilder.Primitive;

import jp.objectclub.vecmath.Vector3f;

public abstract class FieldBuilder
{
	public abstract Field buildField( ArrayList<Primitive> primitives, Vector3f min, Vector3f max, float res );
	
	public static float fixReturn( float v )
	{
		if( Float.isNaN( v ) ) return 0;
		if( Float.isInfinite( v ) ) return Float.MAX_VALUE;
		return v;
	}
	
	public static class Field
	{
		public Vector3f min;
		public Vector3f max;
		public float res;
		public float[][][] field;
		public int xSize, ySize, zSize;
		
		public Field( Vector3f min, Vector3f max, float res )
		{
			this.min = min;
			this.max = max;
			this.res = res;
			
			this.xSize = (int)(( max.x - min.x ) / res) + 1;
			this.ySize = (int)(( max.y - min.y ) / res) + 1;
			this.zSize = (int)(( max.z - min.z ) / res) + 1;
			
			this.field = new float[xSize][ySize][zSize];
		}
		
	}
}
