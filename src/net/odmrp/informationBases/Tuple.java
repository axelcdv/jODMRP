package net.odmrp.informationBases;

public class Tuple {
	
	protected long _timeout;
	
	public Tuple(long timeout) {
		this._timeout = timeout;
	}
	
	public long getTimeout() {
		return _timeout;
	}
	
	public void setTimeout(long timeout) {
		_timeout = timeout;
	}
}
