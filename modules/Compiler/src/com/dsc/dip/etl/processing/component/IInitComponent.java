package com.dsc.dip.etl.processing.component;

public interface IInitComponent {
	
	public boolean init() throws ComponentInitException;

	public boolean complete() throws ComponentInitException;
	
}
