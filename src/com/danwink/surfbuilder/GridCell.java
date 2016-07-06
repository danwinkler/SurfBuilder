package com.danwink.surfbuilder;

import jp.objectclub.vecmath.Point3f;

public class GridCell
{
	public float[] val = new float[8];
	public Point3f[] p = new Point3f[8];
	
	public GridCell()
	{
		for( int i = 0; i < p.length; i++ )
		{
			p[i] = new Point3f();
		}
	}
}