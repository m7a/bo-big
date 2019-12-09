import java.io.*;

public class Big extends Thread
        {
        private static int size;
        private static String file;

        public static void main(String[] args)
                {
                if(args.length != 2)
                        {
                        System.err.println("USAGE : ");
                        System.err.println("java Big <file> <size> ");
                        System.err.println("size : Defines the Size in MB of the rubbish-file to create");
                        System.err.println("file : Defines the name of the file to create");
                        System.err.println("\nTis program makes very big file ");
                        System.err.println("\nWarning : ");
                        System.err.println("THIS PROGRAM IS ONLY FOR TO LEARN NOT TO SPAM SOME OTHERS AS YOU!");
                        System.exit(1);
                        }
        
                file = new String(args[0]);

                try
                        {
                        size = Integer.parseInt(args[1]);
                        }
                catch(NumberFormatException ex)
                        {
                        System.err.println("There was an error:");
                        System.err.println("\n");
                        ex.printStackTrace();
                        System.exit(2);
                        }

                Thread make = new Big();
                make.start();
                }

        public void run()
                {
                System.out.println("Creating File...\n");
                BufferedWriter datei;
                
                try
                        {
                        datei = new BufferedWriter(new FileWriter(file));
                        datei.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                        datei.write("<?ma version=\"1.0\"?>\n");
                        datei.write("<mafile>\n");
                        datei.write("   <macontents>\n");
                        datei.write("\n");
                        for(int i = 1 ; i <= size ; i++)
                                {
                                datei.write(retMB());
                                System.out.println("MB " + i + " writed");
                                }
                        datei.write("\n");
                        datei.write("   </macontents>\n");
                        datei.write("</mafile>\n");
                        datei.close();
                        }
                catch(IOException ex)
                        {
                        System.err.println("There was an error:");
                        System.err.println("\n");
                        ex.printStackTrace();
                        System.exit(2);
                        }

                System.out.println("\nDone!");
                System.exit(0);
                }

        private String retMB()
                {
                String ret = "";
                StringBuffer puffer = new StringBuffer(ret);
                for(int i = 0 ; i < 77240 ; i++)
                        {
                        puffer.append("+++rubbish+++\n");
                        }
                return puffer.toString();
                }
        } 
