
public abstract class Surface
{
	Surface.Method m;
	Surface.Type type;
	
	public Surface()
	{
		if( !Float.isNaN( compute( 0 ) ) )
		{
			m = d -> compute( d );
			type = Type.LINEAR;
		}
		else if( !Float.isNaN( compute2( 0 ) ) )
		{
			m = d -> compute2( d );
			type = Type.SQUARED;
		}
		else
		{
			System.out.println( "Didn't override a method in Surface subclass" );
		}
	}
	
	public float getDistance( float d )
	{
		return m.m( d );
	}
	
	public float compute( float distance )
	{
		return Float.NaN;
	};
	
	public float compute2( float distance2 )
	{
		return Float.NaN;
	};
	
	public interface Method
	{
		public float m( float d ); 
	}
	
	public enum Type 
	{
		LINEAR,
		SQUARED;
	}
}