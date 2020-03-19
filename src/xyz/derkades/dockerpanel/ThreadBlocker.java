package xyz.derkades.dockerpanel;

@Deprecated
public class ThreadBlocker {

	boolean block = true;

	public void block() {
		this.block = true;
		while (this.block) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void done() {
		this.block = false;
	}

}
