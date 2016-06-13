package wanghao.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class FileUtil {

	/**
	 * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件
	 * 
	 * @param filePath
	 *            文件全路径名
	 * @param byteLength
	 *            一次读取文件的最大字节数
	 */
	public static void readFileByBytes(String filePath, int byteLength) {
		File file = new File(filePath).getAbsoluteFile();
		byte[] tmpBytesArray = new byte[byteLength];
		int len;
		
		//使用带资源的try构造，会自动调用close()，只要对象实现了Closeable接口都可以使用带资源的try
		try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
			while ((len = bis.read(tmpBytesArray)) != -1) {
				// 这里可以对每次读取的byteLength长字节数据进行相应的处理
				// 每次打印实际返回的长度,否则当最后一次小于byteLength,上次的数据会追加到末尾
				System.out.print(new String(tmpBytesArray, 0, len));
			}
		}catch (IOException e) {
			System.out.println(e.getMessage());
		}							
	}

	/**
	 * 一次性读取所有二进制数据
	 * 
	 * @param filePath
	 * @return 二进制数据
	 */
	public static byte[] readFileByBytes(String filePath) {
		File file = new File(filePath).getAbsoluteFile();
		BufferedInputStream bis = null;
		byte[] data = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			data = new byte[bis.available()];
			bis.read(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * 以字符为单位读取文件，常用于读文本，数字等类型的文件
	 * 
	 * @param filePath
	 *            文件全路径名
	 * @param charLength
	 *            一次读取文件的最大字符数
	 */
	public static void readFileByChars(String filePath, int charLength) {
		File file = new File(filePath).getAbsoluteFile();
		InputStreamReader reader = null;
		char[] tmpCharsArray = new char[charLength];
		int len;
		try {
			reader = new InputStreamReader(new FileInputStream(file));
			while ((len = reader.read(tmpCharsArray)) != -1) {
				// 这里可以对每次读取的charLength长字符数据进行相应的处理
				// 每次打印实际返回的长度,否则当最后一次小于charLength,上次的数据会追加到末尾
				System.out.print(new String(tmpCharsArray, 0, len));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 * 
	 * @param filePath
	 *            文件全路径名
	 * @return 整个文件的String字符串
	 */
	public static String readFileByLine(String filePath) {
		File file = new File(filePath).getAbsoluteFile();
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(file));
			String str;
			int lineNum = 1;
			while ((str = reader.readLine()) != null) {
				// 这里可以对每行数据进行相应的处理
				sb.append(str + "\n");// 加上回车符，readLine()已将其删掉
				System.out.println("line " + lineNum + ": " + str);
				lineNum++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * RandomAccessFile所用示例
	 * 使用RandomAccessFile类似于组合使用DataInputStream和DataOutputStream
	 * ,都实现了相同的接口:DataInput和DataOutput 使用RandomAccessFile之前必须要知道文件排版布局，才能正确操作
	 * 通过seek()方法在文件中导出移动，修改某个特定值
	 * 
	 * @param filePath
	 */
	public static void testRandomAccess(String filePath) {
		RandomAccessFile rf = null;
		// 写入指定数据
		try {
			rf = new RandomAccessFile(filePath, "rw");
			for (int i = 0; i < 5; i++) {
				rf.writeDouble(i * 1.414);// 不同的类型都可以
			}
			rf.writeUTF("The end of file...");// 写入字符串类型
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 读取已写入的数据
		try {
			rf = new RandomAccessFile(filePath, "r");
			for (int i = 0; i < 5; i++) {
				System.out.println("value " + i + ": " + rf.readDouble());
			}
			System.out.println(rf.readUTF());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 使用RandomAccess向文件写入字符串
	 * 
	 * @param filePath
	 *            文件全路径名
	 * @param content
	 *            字符串内容
	 * @param isAppend
	 *            是否追加写，若不追加写则会将content覆盖到文件前面,注意不会重写文件
	 */
	public static void writeFileByRandomAccess(String filePath, String content,
			boolean isAppend) {
		RandomAccessFile rf = null;
		try {
			// 按读写方式打开一个随机访问文件流
			rf = new RandomAccessFile(filePath, "rw");
			// 获取文件长度，字节数
			long fileLength = rf.length();
			if (isAppend) {
				// 将写文件指针移到文件尾
				rf.seek(fileLength);
			} else {
				// 将写文件指针移到文件首
				rf.seek(0);
			}
			rf.writeBytes(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 使用FileOutputStream向文件写入字符串,重写文件
	 * 
	 * @param filePath
	 *            文件全路径名
	 * @param content
	 *            字符串内容
	 */
	public static void writeFileByFileOutputStream(String filePath,
			String content) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			try {
				fos.write(content.getBytes());
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用FileWriter向文件写入字符串
	 * 
	 * @param filePath
	 *            文件全路径名
	 * @param content
	 *            字符串内容
	 * @param isAppend
	 *            是否追加写，若不追加写则会重写文件,将content入到文件
	 */
	public static void writeFileByFileWriter(String filePath, String content,
			boolean isAppend) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(filePath, isAppend);
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 使用BufferedWriter向文件写入字符串
	 * 
	 * @param filePath
	 *            文件全路径名
	 * @param content
	 *            字符串内容
	 * @param isAppend
	 *            是否追加写，若不追加写则会重写文件,将content入到文件
	 */
	public static void writeFileByBufferedWriter(String filePath,
			String content, boolean isAppend) {
		BufferedWriter bw = null;
		try {
			// 包装FileWriter类
			bw = new BufferedWriter(new FileWriter(filePath, isAppend));
			bw.write(content);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 使用PrintWriter向文件写入字符串
	 * 
	 * @param filePath
	 *            文件全路径名
	 * @param content
	 *            字符串内容
	 * @param isAppend
	 *            是否追加写，若不追加写则会重写文件,将content入到文件
	 */
	public static void writeByPrintWriter(String filePath, String content,
			boolean isAppend) {
		File file = new File(filePath).getAbsoluteFile();
		PrintWriter pw = null;
		try {
			// 包装FileWriter类
			pw = new PrintWriter(new FileWriter(file, isAppend), true);// 自动刷新缓冲区
			pw.write(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}

	public static void main(String[] args) {
//		 String filePath = "data";
//		 readFileByBytes(filePath, 512);
		// System.out.println(new String(readFileByBytes(filePath)));
		// readFileByChars(filePath, 512);
		// System.out.println(readFileByLine(filePath));
		// testRandomAccess(filePath);
		// writeFileByRandomAccess(filePath, "good morning!",true);
		// writeFileByFileOutputStream(filePath, "good morning!");
		// writeFileByFileWriter(filePath, "good afternoon!",false);
		// writeFileByBufferedWriter(filePath, new String("wwww"), true);
		// writeByPrintWriter(filePath,new String("hahah"),false);
	}
}
