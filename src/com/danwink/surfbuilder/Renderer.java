package com.danwink.surfbuilder;

import java.util.ArrayList;
import com.danwink.surfbuilder.MarchingSolver.Triangle;
import com.danwink.surfbuilder.Renderable.Var;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PShape;

public class Renderer extends PApplet implements ControlListener
{
	Renderable r;
	PShape shape;
	
	PeasyCam cam;
	ControlP5 cp5;
	
	boolean rebuild = true;
		
	public Renderer( Renderable r )
	{
		this.r = r;
	}
	
	public void settings()
	{
		size( 800, 600, "processing.opengl.PGraphics3D" );
	}
	
	public void setup()
	{
		cam = new PeasyCam( this, 50 );
		cam.setMinimumDistance( 1 );
		cam.setMaximumDistance( 100 );
		
		cp5 = new ControlP5( this );
		cp5.setAutoDraw( false );
		
		for( int i = 0; i < r.varList.size(); i++ )
		{
			Var v = r.varList.get( i );
			cp5.addSlider( v.name ).setPosition( 5, 25*i + 5 ).setRange( v.min, v.max ).setValue( r.vars.get( v.name ) );
		}
	}
	
	public void controlEvent( ControlEvent e )
	{
		r.vars.put( e.getName(), e.getValue() );
		rebuild = true;
	}
	
	public void buildShape()
	{
		ArrayList<Triangle> tris = r.getTris();
		
		shape = createShape();
		shape.beginShape( TRIANGLES );
		for( Triangle t : tris )
		{
			shape.vertex( t.a.x, t.a.y, t.a.z );
			shape.vertex( t.b.x, t.b.y, t.b.z );
			shape.vertex( t.c.x, t.c.y, t.c.z );
		}
		shape.endShape();
	}
	
	public void draw()
	{
		background( 0 );
		
		perspective( PI/3.f, (float)width/height, .1f, 100 );
		
		if( rebuild )
		{
			buildShape();
			rebuild = false;
		}
		
		if( shape != null )
			shape.draw( g );
		
		cam.beginHUD();
		ortho();
		camera();
		cp5.draw();
		cam.endHUD();
	}
	
	public void mouseMoved()
	{
		if( mouseX < 150 )
		{
			cam.setActive( false );
		}
		else
		{
			cam.setActive( true );
		}
	}
	
	public void run()
	{
		PApplet.runSketch( new String[] { "com.danwink.surfbuilder.Renderer" }, this );
	}
}