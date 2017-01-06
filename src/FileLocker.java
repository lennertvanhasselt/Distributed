
public class FileLocker implements Runnable{
	
	public int indexFileToLock;
	
	FileLocker(int index){
		indexFileToLock=index;
	}

	@Override
	public void run() {
		
		synchronized(this){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
