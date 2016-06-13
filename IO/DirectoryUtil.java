package wanghao.util.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public final class DirectoryUtil {
	/**
	 * 根据目录名称和匹配文件名称的正则表达式获取文件列表
	 * @param dir 目录
	 * @param regex 匹配文件名的正则表达式
	 * @return 目录的File数组
	 */
	public static File[] local(File dir, final String regex){
		return dir.listFiles(new FilenameFilter() {//目录过滤器
			private Pattern pattern = Pattern.compile(regex);
			@Override
			public boolean accept(File dir, String name) {
				return pattern.matcher(new File(name).getName()).matches();
			}
		});
	}
	
	/**
	 * 根据路径名称和匹配文件名称的正则表达式获取文件列表
	 * @param path 路径名称
	 * @param regex 匹配文件名的正则表达式
	 * @return 目录的File数组
	 */
	public static File[] local(String path, final String regex){
		return local(new File(path),regex);
	}
	
	/**
	 * 创建用于存储目录和文件的类
	 * @author wh
	 *
	 */
	public static class TreeInfo implements Iterable<File>{
		public List<File> files = new ArrayList<File>();
		public List<File> dirs = new ArrayList<File>();
		
		@Override
		public Iterator<File> iterator() {
			return files.iterator();
		}
		
		void addAll(TreeInfo other) {
			files.addAll(other.files);
			dirs.addAll(other.dirs);
		}
		
		@Override
		public String toString() {
			return "dirs: "+PPrint.pformat(dirs)+
					"\n\nfiles: "+PPrint.pformat(files);
		}
	}
	
	public static TreeInfo walk(String start, String regex){
		return recurseDirs(new File(start), regex);
	}
	
	public static TreeInfo walk(File start, String regex){
		return recurseDirs(start, regex);
	}
	
	public static TreeInfo walk(String start){
		return recurseDirs(new File(start), ".*");
	}
	
	public static TreeInfo walk(File start){
		return recurseDirs(start, ".*");
	}
	
	/**
	 * 根据文件名的正则表达式从当前目录递归访问所有文件
	 * @param startDir
	 * @param regex
	 * @return
	 */
	static TreeInfo recurseDirs(File startDir, String regex){
		TreeInfo result = new TreeInfo();
		for(File item:startDir.listFiles()){
			if(item.isDirectory()){
				result.dirs.add(item);
				result.addAll(recurseDirs(item, regex));
			}else{
				if(item.getName().matches(regex))
					result.files.add(item);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(walk("."));
//		File[] file=local(".", ".*");
//		for(File f:file){
//			System.out.println(f.getName()+" ");
//		}
	}

}
