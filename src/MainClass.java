import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.StringBuffer;

public class MainClass
{
	static boolean needsort=false;

	public static void main(String[] args)
	{
		Step2();
	}
	
	public static void Step2()
	{
		File             fnin1;
		File             fnin2;
		File             fnout1;
		File             fnout2;
		FileInputStream  fin1;
		FileInputStream  fin2;
		FileOutputStream fout1;
		FileOutputStream fout2;
		
		StringBuffer buffer1;
		StringBuffer buffer2;
		
		try
		{
			fnin1 =new File("C:\\Users\\Randall Hedglin\\Desktop\\Word Lists\\Input\\acceptable-unsort.txt");
			fnin2 =new File("C:\\Users\\Randall Hedglin\\Desktop\\Word Lists\\Input\\preferred-uncull.txt");
			fnout1=new File("C:\\Users\\Randall Hedglin\\Desktop\\Word Lists\\Output\\acceptable-sort.txt");
			fnout2=new File("C:\\Users\\Randall Hedglin\\Desktop\\Word Lists\\Output\\preferred-cull.txt");

			fnout1.createNewFile();
			fnout2.createNewFile();

			fin1 =new FileInputStream(fnin1);
			fin2 =new FileInputStream(fnin2);
			fout1=new FileOutputStream(fnout1);
			fout2=new FileOutputStream(fnout2);
			
			buffer1=new StringBuffer();
			buffer2=new StringBuffer();
		}
		catch(Throwable e)
		{
			System.out.println("Failed to open files.");
			e.printStackTrace();
			return;
		}
		
		char string[]=new char[64];
		int  strpos  =0;
		int  strlen  =0;
		
		string[strpos]=0;

		boolean output=false;
		boolean eof   =false;

		int loaded1=0;
		int loaded2=0;

		int c=-1;

		while(true)
		{
			try
	    	{
	    		c=fin1.read();
	    	}
			catch(Throwable e)
			{
				System.out.println("File input failed.");
				e.printStackTrace();
				return;
			}

    		if(c<0)
    		{
    			output=true;
    			eof   =true;
    		}
    		else if((c=='\n')||
    				(c=='\r'))
    		{
    			output=true;
    		}
    		else
    		{
    			string[strpos++]=(char)c;
    			string[strpos]  =0;

    			strlen++;
    		}
    		
    		if(output)
    		{
    			if(strlen>0)
    			{
	    			buffer1.append("<");
	    			buffer1.append(String.valueOf(string,
	    										  0,
	    										  strlen));
	    			buffer1.append(">");
    				
    				loaded1++;
    			}
    			
    			strpos=0;
    			strlen=0;
    			
    			output=false;
    		}
    		
    		if(eof)
    			break;
		}
		
    	System.out.println("Loaded "+String.valueOf(loaded1)+" items (acceptable).");

    	needsort=true;

		while(needsort)
    		buffer1=SortBuffer(buffer1);
		
    	eof=false;
    	
    	int cull=0;

		while(true)
		{
			try
	    	{
	    		c=fin2.read();
	    	}
			catch(Throwable e)
			{
				System.out.println("File input failed.");
				e.printStackTrace();
				return;
			}

    		if(c<0)
    		{
    			output=true;
    			eof   =true;
    		}
    		else if((c=='\n')||
    				(c=='\r'))
    		{
    			output=true;
    		}
    		else
    		{
    			string[strpos++]=(char)c;
    			string[strpos]  =0;

    			strlen++;
    		}
    		
    		if(output)
    		{
    			if(strlen>0)
    			{
    				String tempstr=new String("<"+String.valueOf(string,
							  					  0,
							  					  strlen)+">");
    				
    				if(!buffer1.toString().contains(tempstr))
    				{
    					cull++;
    				}
    				else
    				{
		    			buffer2.append(tempstr);
	    				
	    				loaded2++;
    				}
    			}
				
    			strpos=0;
    			strlen=0;
    			
    			output=false;
    		}
    		
    		if(eof)
    			break;
		}
		
    	System.out.println("Loaded "+String.valueOf(loaded2)+" items (preferred), culled "+String.valueOf(cull)+" non-words.");

    	needsort=true;

		while(needsort)
    		buffer2=SortBuffer(buffer2);
		
		OutputWordList(buffer1,
				       fout1);
		OutputWordList(buffer2,
				       fout2);

		try
    	{
    		fin1.close();
    		fin2.close();
    		fout1.close();
    		fout2.close();
    	}
		catch(Throwable e)
		{
			System.out.println("Failed to close files.");
			e.printStackTrace();
			return;
		}
    	
   	   	System.out.println("Operation complete.");
	}
	
	public static void OutputWordList(StringBuffer     buffer,
		       						  FileOutputStream fout)
	{
		int pos=0;

		while(pos<buffer.length())
		{
			char c=buffer.charAt(pos);
			
			if(c=='<')
			{
				// ignore
			}
			else if(c=='>')
			{
		    	try
		    	{
		    		fout.write('\r');
		    		fout.write('\n');
		    	}
				catch(Throwable e)
				{
					System.out.println("File output failed.");
					e.printStackTrace();
					return;
				}
			}
			else
			{
		    	try
		    	{
		    		fout.write(c);
		    	}
				catch(Throwable e)
				{
					System.out.println("File output failed.");
					e.printStackTrace();
					return;
				}
			}
			
			pos++;
		}
	}
	
	public static StringBuffer SortBuffer(StringBuffer buffer1)
	{
		needsort=false;
		
		StringBuffer buffer2=new StringBuffer();
		
		StringBuffer string1=new StringBuffer();
		StringBuffer string2=new StringBuffer();
		
		int swap=0;
		int cull=0;
		
		int pos=0;

		while(pos<buffer1.length())
		{
			char c=buffer1.charAt(pos);
			
			if(c=='<')
			{
				string2.setLength(0);
			}
			else if(c=='>')
			{
				if(string1.length()>0)
				{
					int compare=string1.toString().compareTo(string2.toString());
					
					if(compare>0)
					{
						StringBuffer string3;
						
						string3=string1;
						string1=string2;
						string2=string3;
						
						swap++;
						
						buffer2.append("<");
						buffer2.append(string1.toString());
						buffer2.append(">");
						
						string1.setLength(0);
					}
					else if(compare==0)
					{
						cull++;
					}
					else
					{
						buffer2.append("<");
						buffer2.append(string1.toString());
						buffer2.append(">");
					}
				}
				
				string1.setLength(0);
				string1.append(string2.toString());
			}
			else
			{
				string2.append(c);
			}
			
			pos++;
		}
		
		buffer2.append("<");
		buffer2.append(string2.toString());
		buffer2.append(">");

		if((swap>0)||
		   (cull>0))
			needsort=true;
		
		System.out.println("Swapped "+String.valueOf(swap)+" items, culled "+String.valueOf(cull)+" items.");
		
		buffer1.setLength(0);
		buffer1.append(buffer2.toString());
		
		return(buffer1);
	}
	
	public static void Step1()
	{
		File             fnin;
		File             fnout;
		FileInputStream  fin;
		FileOutputStream fout;

		try
		{
			fnin =new File("C:\\Users\\Randall Hedglin\\Desktop\\Word Lists\\Input\\coca-3sp.txt");
			fnout=new File("C:\\Users\\Randall Hedglin\\Desktop\\Word Lists\\Output\\coca-310.txt");

			fnout.createNewFile();

			fin =new FileInputStream(fnin);
			fout=new FileOutputStream(fnout);
		}
		catch(Throwable e)
		{
			System.out.println("Failed to open files.");
			e.printStackTrace();
			return;
		}
		
		char string[]=new char[64];
		int  strpos  =0;
		int  strlen  =0;
		
		string[strpos]=0;

		boolean invalid=false;
		boolean output =false;
		boolean eof    =false;
		
		int c=-1;

		while(true)
		{
			try
	    	{
	    		c=fin.read();
	    	}
			catch(Throwable e)
			{
				System.out.println("File input failed.");
				e.printStackTrace();
				return;
			}

    		if(c<0)
    		{
    			output=true;
    			eof   =true;
    		}
    		else if((c=='\n')||
    				(c=='\r'))
    		{
    			output=true;
    		}
    		else if((c<'a')||
    				(c>'z'))
    		{
        		if(c!=0xA0)
        			invalid=true;
    		}
    		else
    		{
    			if(strpos<63)
    			{
	    			string[strpos++]=(char)c;
	    			string[strpos]  =0;
	
	    			strlen++;
    			}
    			else
    			{
    				invalid=true;
    			}
    		}
    		
    		if(output)
    		{
    			if(!invalid)
    			{
    				if((strlen>=3)&&
    				   (strlen<=10))
    				{
	    				for(int n=0;n<strlen;n++)
	    				{
	    			    	try
	    			    	{
	    			    		fout.write(string[n]);
	    			    	}
	    					catch(Throwable e)
	    					{
	    						System.out.println("File output failed.");
	    						e.printStackTrace();
	    						return;
	    					}
	    				}
	    				
	    				if(!eof)
	    				{
	    			    	try
	    			    	{
	    			    		fout.write('\r');
	    			    		fout.write('\n');
	    			    	}
	    					catch(Throwable e)
	    					{
	    						System.out.println("File output failed.");
	    						e.printStackTrace();
	    						return;
	    					}
	    				}
    				}
    			}
    			
    			strpos=0;
    			strlen=0;
    			
    			output =false;
    			invalid=false;
    		}
    		
    		if(eof)
    			break;
		}
		
    	try
    	{
    		fin.close();
    		fout.close();
    	}
		catch(Throwable e)
		{
			System.out.println("Failed to close files.");
			e.printStackTrace();
			return;
		}
    	
    	System.out.println("Operation complete.");
	}
}