import java.io.*;
import java.util.*;

public class Big3 {

	private static final int BUFFER_SIZE = 1024 * 1024 * 16;

	private static final char[] LETTERS = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	};
	
	private int lastStat = 0;

	public Big3(String[] args) {
		super();
		long beginTime = System.currentTimeMillis();
		if(args.length != 4) {
			help(); return;
		}
		long size = Long.parseLong(args[2]);
		if(args[3].equals("KiB")) {
			size = size * 1024;
		} else if(args[3].equals("MiB")) {
			size = size * 1024 * 1024;
		} else if(args[3].equals("GiB")) {
			size = size * 1024 * 1024 * 1024;
		} else if(args[3].equals("TiB")) {
			size = size * 1024 * 1024 * 1024 * 1024;
		}
		File file = new File(args[1]);
		if(size == -1) {
			System.out.println("Warning: Running until cancelling.");
			size = Long.MAX_VALUE;
		} else if(size < -1) {
			System.out.println("Error: Negative sizes are not allowed.");
			return;
		}
		long remaining = size;
		long written   = 0;
		if(args[0].equals("zeros")) {
			byte[] buffer = new byte[BUFFER_SIZE];
			for(int i = 0; i < buffer.length; i++) {
				buffer[i] = 0;
			}
			try {
				FileOutputStream out = new FileOutputStream(file);
				while(remaining > 0) {
					int write = 0;
					if(remaining > BUFFER_SIZE) {
						write     = BUFFER_SIZE;
						remaining = remaining - BUFFER_SIZE;
					} else {
						write     = (int)remaining;
						remaining = 0;
					}
					out.write(buffer, 0, write);
					written += write;
					stat(written, size);
				}
				out.close();
			} catch(IOException ex) {
				System.out.println();
				ex.printStackTrace();
			}
		} else if(args[0].equals("rtext")) {
			Random rand = new Random();
			try {
				PrintWriter out = new PrintWriter(file);
				while(remaining > 0) {
					char[] buffer = new char[BUFFER_SIZE];
					for(int i = 0; i < buffer.length; i++) {
						if(((i+1) % 80) == 0) {
							buffer[i] = '\n';
						} else {
							buffer[i] = LETTERS[rand.nextInt(LETTERS.length)];
						}
					}
					int write = 0;
					if(remaining > BUFFER_SIZE) {
						write     = BUFFER_SIZE;
						remaining = remaining - BUFFER_SIZE;
					} else {
						write     = (int)remaining;
						remaining = 0;
					}
					out.write(buffer, 0, write);
					written += write;
					stat(written, size);
				}
				out.close();
			} catch(IOException ex) {
				System.out.println();
				ex.printStackTrace();
			}
		} else if(args[0].equals("rbin")) {
			Random rand = new Random();
			try {
				FileOutputStream out = new FileOutputStream(file);
				while(remaining > 0) {
					byte[] buffer = new byte[BUFFER_SIZE];
					rand.nextBytes(buffer);
					int write = 0;
					if(remaining > BUFFER_SIZE) {
						write     = BUFFER_SIZE;
						remaining = remaining - BUFFER_SIZE;
					} else {
						write     = (int)remaining;
						remaining = 0;
					}
					out.write(buffer, 0, write);
					written += write;
					stat(written, size);
				}
				out.close();
			} catch(IOException ex) {
				System.out.println();
				ex.printStackTrace();
			}
		}
		System.out.println();
		System.out.println();
		long time = System.currentTimeMillis() - beginTime;
		System.out.println("Wrote " + size + " bytes in " + time + " milliseconds, that is " + (size / time) + " b/ms.");
		float timeF = (int)time / 1000.0f;
		float sizeM = (int)(size / 1024.0f / 1024.0f);
		System.out.println("Wrote " + sizeM + " MiB in " + timeF + " seconds, that is " + (sizeM / timeF) + " MiB/s.");
		System.out.println();
		System.out.print("Done.");
	}

	private void stat(long written, long of) {
		char[] del = new char[lastStat];
		for(int i = 0; i < del.length; i++) { del[i] = '\b'; }
		int percent = (int)((written * 100) / of); 
		written     = written / 1024 / 1024;
		of          = of / 1024 / 1024;
		String stat = written + "/" + of + " MiB (" + percent +" %) written.";
		lastStat = stat.length();
		System.out.print(new String(del) + stat);
	}

	private void help() {
		System.out.println("Usage: java Big3 <method> <file> <size> <unit>");
		System.out.println();
		System.out.println("<method>");
		System.out.println("   zeros  Writes '0' bytes into <file>.");
		System.out.println("   rtext  Writes random text to <file>.");
		System.out.println("   rbin   Writes random bytes to <file>.");
		System.out.println("<file>");
		System.out.println("   The file to write the data to.");
		System.out.println("<size>");
		System.out.println("   A figure which gives a size.");
		System.out.println("<unit>");
		System.out.println("   B    Writes <size> bytes to <file>.");
		System.out.println("   KiB  Writes <size>*1024 bytes to <file>.");
		System.out.println("   MiB  Writes <size>*1024² bytes to <file>.");
		System.out.println("   GiB  Writes <size>*1024³ bytes to <file>.");
	}

	public static void main(String[] args) {
		System.out.println("Big 3.0.0.1, Copyright (c) 2011 Ma_Sys.ma.");
		System.out.println("For further info send an e-mail to Ma_Sys.ma@web.de.");
		System.out.println();
		new Big3(args);
		System.out.println();
		System.exit(0);
	}

}
