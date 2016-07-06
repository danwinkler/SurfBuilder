package com.danwink.surfbuilder.polygonize;

import java.util.ArrayList;

import com.danwink.surfbuilder.Triangle;
import com.danwink.surfbuilder.fields.FieldBuilder.Field;

public abstract class Polygonizer
{
	public abstract ArrayList<Triangle> polygonize( Field f, float isoLevel );
}
