package de.uni_stuttgart.ipvs.ids.communication;

import java.io.Serializable;


public class ValueResponseMessage<T> implements Serializable {

	private static final long serialVersionUID = 209947072618173490L;

	final protected T value;
	
	public ValueResponseMessage(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return this.value;
	}

}
