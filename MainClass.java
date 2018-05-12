
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class MainClass {
	public static final String ADDR="http://47.92.110.171:9800/dsi/core/h5/report/280011/2001525226616776/%d/%s/%s?_r=84785&_=1525485188283";
	public static final String TAG_NAME="stuAlias",TAG_SCORE="stuScore";
	public static final String[] XKNAME={"Chinese","Math","English","Physics"};
	public static final int[] CLZ={141,45,0,320,275,229,185,416,365,90};
	static ArrayList<Student> ALL=new ArrayList<>();
	static String NAME=null;
	static Scanner scan;
	public static void main(String[] args) throws Throwable {
		if (args==null||args.length==0) {
			System.out.print("Welcome to Doofen!\r\nMade By Xs.JIONG\r\n\r\nPlz input class number:");
			scan=new Scanner(System.in);
			int CN=scan.nextInt();
			while (CN<1||CN>10) {
				System.out.print("Plz input class number:");
				CN=scan.nextInt();
			}
			CN=CLZ[CN-1];
			System.out.print("Plz input class max id:");
			int ID=scan.nextInt();
			for (int i=1;i<=ID;i++) getOne(CN+i,i);
			rank();
		}
		int CN=CLZ[Integer.parseInt(args[0])-1];
		int ID=Integer.parseInt(args[1]);
		for (int i=1;i<=ID;i++) getOne(CN+i,i);
		rank();
	}
	static void rank() {
		for (int i=1;i<=4;i++) {
			final int q=i-1;
			Collections.sort(ALL, new Comparator<Student>() {
				@Override
				public int compare(Student a, Student b) {
					return (a.all[q]>b.all[q]?-1:1);
				}
			});
			System.out.println(XKNAME[q]+" Rank:");
			Student s;
			for (int j=1;j<=ALL.size();j++) {
				s=ALL.get(j-1);
				System.out.println(j+" "+s.name+" "+s.all[q]);
			}
			System.out.println();
		}
		Collections.sort(ALL, new Comparator<Student>() {
			@Override
			public int compare(Student a, Student b) {
				return (a.total>b.total?-1:1);
			}
		});
		System.out.println("Total Rank:");
		Student s;
		for (int i=1;i<=ALL.size();i++) {
			s=ALL.get(i-1);
			System.out.println(i+" "+s.name+" "+s.total);
		}
	}
	static void getOne(int id, int ori) throws Throwable {
		NAME=null;
		String res=String.valueOf(id);
		int l=4-res.length();
		for (int i=0;i<l;i++) res="0"+res;
		res="280011160000"+res;
		double total=0;
		double cu;
		System.out.println("ID:"+ori);
		Student s=new Student();
		for (int i=1;i<=4;i++) {
			cu=getXK(res, i);
			if (i==1) {
				System.out.println("Name:"+NAME);
				s.name=NAME;
			}
			System.out.println(XKNAME[i-1]+':'+cu);
			s.all[i-1]=cu;
			s.total+=cu;
		}
		System.out.println("Total:"+s.total+"\r\n");
		ALL.add(s);
	}
	static double getXK(String id, int xk) throws Throwable {
		HttpURLConnection con=(HttpURLConnection) new URL(String.format(ADDR, xk, id, id)).openConnection();
		con.setConnectTimeout(8000);
		con.setDoInput(true);
		con.connect();
		InputStream in=con.getInputStream();
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		int read;
		byte[] buff=new byte[1024];
		while ((read=in.read(buff))!=-1) out.write(buff, 0, read);
		in.close();
		con.disconnect();
		out.close();
		String s=new String(out.toByteArray(),"UTF-8");
		int i=0;
		double fs=-1;
		if (NAME==null)
			a:for (;i<s.length();i++) {
				for (int j=0;j<TAG_NAME.length();j++) if (s.charAt(i+j)!=TAG_NAME.charAt(j)) continue a;
				i+=TAG_NAME.length()+3;
				StringBuffer b=new StringBuffer();
				char c;
				while ((c=s.charAt(i++))!='\"') b.append(c);
				i--;
				NAME=b.toString();
				b.setLength(0);
				b=null;
				break;
			}
		a:for (i=0;i<s.length();i++) {
			for (int j=0;j<TAG_SCORE.length();j++) if (s.charAt(i+j)!=TAG_SCORE.charAt(j)) continue a;
			i+=TAG_SCORE.length()+2;
			StringBuffer b=new StringBuffer();
			char c;
			while ((c=s.charAt(i++))!=',') b.append(c);
			i--;
			fs=Double.valueOf(b.toString());
			b.setLength(0);
			b=null;
		}
		return fs;
	}
	static class Student {
		String name;
		double[] all=new double[4];
		double total=0;
		public Student() {}
	}
}
