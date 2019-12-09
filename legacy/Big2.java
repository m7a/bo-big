import java.io.*;
import java.util.*;

public class Big2 {
	
	private static final String[] HELP_OPTIONS = {
		"--help", "-help", "-h", "-?", "--usage", "-usage",
		"/?", "/help", "/usage",
	};
	
	private static final char[] LETTERS = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'-'
	};
	
	public Big2(String[] args) {
		copyright();
		if(args.length != 3) {
			if(args.length == 0) {
				help();
			} else {
				for(int i = 0; i < HELP_OPTIONS.length; i++) {
					if(args[0].equals(HELP_OPTIONS[i])) {
						help();
					}
				}
			}
		} else {
			String modus = args[0];
			String output_file = args[1];
			String filesize_string = args[2];
			long filesize = -2;
			Random r = new Random();
			
			try {
				filesize = Long.parseLong(filesize_string);
			} catch (NumberFormatException ex) {
				err("The number you entered is not a valid long number", ex);
			}
			
			if(filesize < -1) {
				System.err.println("Filesize lower than -1 is not allowed.");
				System.exit(1);
			} else if(filesize != -1) {
				//         MB       KB   B
				filesize = filesize*1024*1024;
			}
			
			try {
				if(modus.equals("rbin")) {
					FileOutputStream out = new FileOutputStream(output_file);
					for(int i = 0;;i++) {	
						out.write(r.nextInt(255));
						
						if(i % (1024*1024) == 0) {
							System.out.println(i/1024/1024 + " MB written.");
						}
						
						if(filesize != -1 && i >= filesize) {
							break;
						}
					}
					out.close();
				} else {
					PrintWriter out = new PrintWriter(output_file);
					for(int i = 0;;i++) {
						if(modus.equals("zeros")) {
							out.print(0);
						} else if(modus.equals("rtext")) {
							out.print(LETTERS[r.nextInt(LETTERS.length-1)]);
						}
						
						if(i % (1024*1024) == 0 && i != 0) {
							System.out.println(i/1024/1024 + " MB written.");
						}
						
						if(filesize != -1 && i >= filesize) {
							break;
						}
					}
					out.close();
				}
			} catch (Throwable t) {
				err("Cannot create file.", t);
			}
			
			System.out.println();
		}
	}
	
	private void err(String dsc, Throwable e) {
		System.err.println(dsc);
		System.err.println("ERROR : " + e.toString());
		System.err.println("Stack Trace (don't worry if you don't know what this means) : ");
		e.printStackTrace();
		System.exit(1);
	}
		
	private void copyright() {
		System.out.println("-------------------------------------------------------------------");
		System.out.println(" Big 2.0, Copyright (c) Ma_Sys.ma, Further Info : Ma_Sys.ma@gmx.de ");
		System.out.println("-------------------------------------------------------------------");
	}
	
	private void help() {
		System.out.println();
		System.out.println("USAGE : java Big2 <modus> <destination_file> <filesize_mb>");
		System.out.println("Avariable Options for <modus> : ");
		System.out.println(" rtext - <destination_file> is filled with random text");
		System.out.println(" zeros - <destination_file> is filled with lots of zeros");
		System.out.println(" rbin  - <destination_file> is filled with random bytes");
		System.out.println("The output will sent to <destination_file>");
		System.out.println("The size of <destination_file> will be <filesize_mb> in MB.");
		System.out.println("Enter -1 as filesize for infinite Size, the Program then never exits.");
		System.out.println();
		System.out.println("WARNING! ");
		System.out.println(" This program is only for testing some things with big files.");
		System.out.println(" Use it only if you know, what you do. Don't spam people with this program.");
		System.out.println();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		new Big2(args);
	}
}
