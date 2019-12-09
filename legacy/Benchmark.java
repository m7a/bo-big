import java.nio.ByteBuffer;

public class Benchmark {
	private static final int N = 1024 * 20;
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		for(int i = 0; i < N; i++) {
			byte[] b = new byte[1024 * 1024];
			b[0] = 0;
			new String(String.valueOf(b[4]));
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		start = System.currentTimeMillis();
		for(int i = 0; i < N; i++) {
			ByteBuffer b = ByteBuffer.allocate(1024 * 1024);
			b.putInt(1);
			new String(String.valueOf(b.get()));
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
