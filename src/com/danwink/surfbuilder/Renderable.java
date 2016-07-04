package com.danwink.surfbuilder;

import java.util.ArrayList;
import java.util.HashMap;

import com.danwink.surfbuilder.MarchingSolver.Triangle;

public abstract class Renderable
{
	HashMap<String, Float> vars = new HashMap<String, Float>();
	ArrayList<Var> varList = new ArrayList<Var>();
	
	public void var( String name, float min, float max )
	{
		vars.put( name, (min+max)/2.f );
		varList.add( new Var( name, min, max ) );
	}
	
	public float get( String name )
	{
		return vars.get( name );
	}
	
	public abstract ArrayList<Triangle> getTris();
	
	public class Var
	{
		String name;
		float min;
		float max;
		
		public Var( String name, float min, float max )
		{
			this.name = name;
			this.min = min;
			this.max = max;
		}
	}
}