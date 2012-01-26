/**
 * 
 */
package ca.eandb.jmist.framework.loader.openexr;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author brad
 *
 */
@OpenEXRAttributeType("v2i")
public final class V2i implements Attribute {

	private final int x;
	private final int y;
	
	public V2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static V2i read(DataInput in, int size) throws IOException {
		return new V2i(in.readInt(), in.readInt());
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.loader.openexr.Attribute#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
	}

}
