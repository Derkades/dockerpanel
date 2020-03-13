package xyz.derkades.dockerpanel;

public class ThreadBlocker {
	
	boolean block = true;
	
	public void block() {
		block = true;
		while (block) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void done() {
		block = false;
	}

}
