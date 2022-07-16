package de.uni_stuttgart.ipvs.ids.communication;

import de.uni_stuttgart.ipvs.ids.replication.VersionedValue;

public class WriteRequestMessage<T> extends VersionedValue<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3310552644845307035L;

	public WriteRequestMessage(VersionedValue<T> versionedValue) {
		super(versionedValue);
	}
	
	public WriteRequestMessage(int version, T value) {
		super(version, value);
	}

}
