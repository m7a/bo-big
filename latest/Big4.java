import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.channels.SeekableByteChannel;

public class Big4 {

	private static final int BUFSIZ   = 1024 * 512; // Newly 512 KiB buffer
	private static final int CAPACITY = 48;
	private static final byte[] TEXT = new String("abzdefghijklmnopqrstuv" +
				"wxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789").
				getBytes(Charset.forName("UTF-8"));
	private static final byte NL = (byte)'\n';

	private final char mode;
	private final String file;
	private final long size;
	private final int threads;

	private final long start;
	private long wb;
	private long wbs;

	private Big4(String[] args) {
		super();

		start = System.currentTimeMillis();
		wb = 0;
		wbs = 0;

		copyright();
		if(args.length < 3) {
			help();
			mode = '0';
			file = null;
			size = 0;
			threads = 0;
		} else {
			int shift;
			if(args[0].charAt(0) == '-' && args[0].length() == 2) {
				mode = args[0].charAt(1);
				shift = 1;
			} else {
				mode = 'z';
				shift = 0;
			}
			file = args[shift++];
			size = getsize(args[shift], args[shift + 1]);
			if(shift + 3 == args.length)
				threads = Integer.parseInt(args[shift + 2]);
			else
				threads = Runtime.getRuntime().
							availableProcessors();
		}
	}

	private static void copyright() {
		System.out.println("Ma_Sys.ma Big 4.0.2, Copyright (c) 2014, " +
						"2019, 2020 Ma_Sys.ma.");
		System.out.println(
			"For further info send an e-mail to Ma_Sys.ma@web.de.");
		System.out.println();
	}

	private static void help() {
		System.out.println("USAGE: big4 [-z|-t|-b] <file> " +
						"<size> <unit> [threads]");
		System.out.println();
		System.out.println("[-z|-t|-b] defaults to -z");
		System.out.println("\t-z  Writes zero bytes only");
		System.out.println("\t-t  Writes random text");
		System.out.println("\t-b  Writes random binary data");
		System.out.println("<file>");
		System.out.println("\tAn output file to use");
		System.out.println("<size>");
		System.out.println("\tNumber of bytes to write");
		System.out.println("<unit>");
		System.out.println("\tConfigures a multipliactor for <size>");
		System.out.println("\tB    1           = 2^00");
		System.out.println("\tKiB  1024        = 2^10");
		System.out.println("\tMiB  1024²       = 2^20");
		System.out.println("\tGiB  1024³       = 2^30");
		System.out.println("\tTiB  1024*1024³  = 2^40");
		System.out.println("\tPiB  1024²*1024³ = 2^50");
	}

	private static long getsize(String nrS, String unit) {
		long nr = Long.parseLong(nrS);
		switch(unit.charAt(0)) {
		case 'E': nr <<= 10;
		case 'P': nr <<= 10;
		case 'T': nr <<= 10;
		case 'G': nr <<= 10;
		case 'M': nr <<= 10;
		case 'K': nr <<= 10;
		}
		return nr;
	}

	private void run() throws Exception {
		if(mode == '0')
			return;

		Path fp = Paths.get(file);
		// Newly without StandardOpenOption.TRUNCATE_EXISTING because
		// this way one can specify target file NUL on Windows to
		// use it for benchmark purposes.
		try(SeekableByteChannel sb = Files.newByteChannel(fp,
					StandardOpenOption.WRITE,
					StandardOpenOption.CREATE)) {
			run(sb);
		}

		long end = System.currentTimeMillis();

		System.out.println();
		long delta = end - start;
		System.out.println(String.format(
			"Wrote %d MiB in %d s @ %.3f MiB/s",
			size / 1024 / 1024,
			delta / 1000,
			(double)size / 1024 * 1000 / 1024 / delta
		));
	}

	private void run(SeekableByteChannel sb) throws Exception {
		LinkedBlockingQueue<ByteBuffer> q =
				new LinkedBlockingQueue<ByteBuffer>(CAPACITY);

		Thread s = createStatusThread();
		final Thread[] t = startWritingToQueue(q);
		s.start();

		long rem = size;
		while(rem >= BUFSIZ) {
			sb.write(q.take());
			wb += BUFSIZ;
			wbs += BUFSIZ;
			rem -= BUFSIZ;
		}

		ByteBuffer b = q.take();
		byte[] data = new byte[(int)rem];
		b.get(data);

		stopWritingToQueue(t);

		sb.write(ByteBuffer.wrap(data));
		wb += rem;
		wbs += rem;
		rem = 0;

		s.interrupt();
		try {
			s.join();
		} catch(InterruptedException ex) {
			// expected
		}
	}

	private Thread[] startWritingToQueue(LinkedBlockingQueue<ByteBuffer>
									q) {
		Thread[] ret = new Thread[threads];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = createWritingThread(q);
			ret[i].start();
		}
		return ret;
	}

	private Thread createWritingThread(final LinkedBlockingQueue<ByteBuffer>
									q) {
		return new Thread() {
			public void run() {
				final HighQualityRandom r =
							new HighQualityRandom();
				try {
					while(!isInterrupted())
						q.put(createBuffer(r));
				} catch(InterruptedException ex) {
					// expected
				}
			}
		};
	}

	private ByteBuffer createBuffer(final HighQualityRandom r) {
		final ByteBuffer ret = ByteBuffer.allocate(BUFSIZ);
		if(mode == 'z') {
			ret.position(BUFSIZ - 1);
		} else {
			int rem = BUFSIZ;
			while(rem > 0) {
				long rnd = r.nextLong();
				if(mode == 'b') {
					ret.putLong(rnd);
				} else {
					long w = 0;
					final int lim = (int)(rem % 80);
					for(int i = 0; i < 8; i++) {
						w <<= 8;
						if(i == lim) {
							w |= NL;
						} else {
							w |= TEXT[(int)(rnd &
								0xff) %
								TEXT.length];
							rnd >>= 2;
						}
					}
					ret.putLong(w);
				}
				rem -= 8;
			}
		}
		ret.flip();
		return ret;
	}

	private Thread createStatusThread() {
		return new Thread() {
			public void run() {
				try {
					while(!isInterrupted()) {
						displayStatus();
						sleep(1000);
					}
				} catch(InterruptedException ex) {
					// expected
				}
			}
		};
	}

	private void displayStatus() {
		long tms = System.currentTimeMillis() - start;
		System.out.println(String.format(
			"%3.2f%% +%d MiB %d MiB/s %d/%d MiB",
			(float)((double)wb*100/(double)size),
			wbs / 1024 / 1024,
			wb / 1024 * 1000 / 1024 / tms,
			wb / 1024 / 1024,
			size / 1024 / 1024
		));
		wbs = 0;
	}

	private void stopWritingToQueue(Thread[] t) {
		for(Thread i: t)
			i.interrupt();
	}

	public static void main(String[] args) {
		try {
			new Big4(args).run();
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

}

// Der Java Zufallsgenerator "ThreadLocalRandom" funktionierte nicht immer
// zuverlässig. Insbesondere unter hoher Last versagte er und spuckte nur noch
// gleiche Werte aus, sodass das Programm manchmal in eine Endlosschleife
// geriet. Deshalb wurde hier ein anderer Zufallsgenerator verwendet.
// 
// cf. http://www.javamex.com/tutorials/random_numbers/numerical_recipes.shtml
class HighQualityRandom extends Random { // ---------------- Zufallsgenerator --

	private static final int REPRODUCIBILITY_MINUTES = 5;

	private final long seed;
	private long u;
	private long v = 4101842887655102017l;
	private long w = 1;

	public HighQualityRandom() {
		this((long)(System.nanoTime() * hqdouble()));
	}

	private static double hqdouble() {
		// Für den Fall, dass der Java-Zufallsgenerator mal wieder
		// spinnt wollen wir wenigstens die nanoTime() als Seed
		// verwenden
		double a = ThreadLocalRandom.current().nextDouble();
		return a == 0d? 1: a;
	}

	public HighQualityRandom(long seed) {
		this.seed = seed;
		u = seed ^ v;
		nextLong();
		v = u;
		nextLong();
		w = v;
		nextLong();
	}

	public long nextLong() {
		u = u * 2862933555777941757l + 7046029254386353087l;
		v ^= v >>> 17;
		v ^= v << 31;
		v ^= v >>> 8;
		w = 4294957665l * (w & 0xffffffff) + (w >>> 32);
		long x = u ^ (u << 21);
		x ^= x >>> 35;
		x ^= x << 4;
		long ret = (x + v) ^ w;
		return ret;
	}

	protected int next(int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}

}
