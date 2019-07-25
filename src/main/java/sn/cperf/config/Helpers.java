package sn.cperf.config;

import org.springframework.stereotype.Component;

@Component
public class Helpers {
	public static String getFileFontAwesomeTextClass(String filename) {
		if(filename != null && !filename.equals("")) {
			String ext = "";
			try { ext = filename.substring(filename.lastIndexOf("."), filename.length()).toLowerCase();} catch (Exception e) {}
			if(ext.equals(".xls") || ext.equals(".xlsx"))
				return "fas fa-file-excel";
			else if(ext.equals(".csv") || ext.equals(".csvx"))
				return "fas fa-file-csv";
			else if(ext.equals(".doc") || ext.equals(".docx"))
				return "fas fa-file-word";
			else if(ext.equals(".ppt") || ext.equals(".pptx"))
				return "fas fa-file-powerpoint";
			else if(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg")  || ext.equals(".ico")
					|| ext.equals(".gif") || ext.equals(".svg")  || ext.equals(".svgz")  || ext.equals(".bmp"))
				return "fas fa-file-image";
			else if(ext.equals(".pdf"))
				return "fas fa-file-pdf";
			else if(ext.equals(".text") || ext.equals(".txt"))
				return "fas fa-file";
			else
				return "fas fa-file";
		}
		return "";
	}
	public static String[] getAFileExtensions() {
		return  new String[] { "pdf","doc","docx","ppt","pptx","xls","xlsx","text","txt","png","jpeg","gif","ico","jpg","svg","svgz","zip","csv","csvx"};
	}
	
	public static String getHtmlInputFileExentions() {
		String ext = "";
		if(getAFileExtensions().length >0) {
			for(int i=0; i<getAFileExtensions().length;i++) {
				ext = ext + "."+getAFileExtensions()[i];
				if(i < getAFileExtensions().length-1)
					ext = ext + ", ";
			}
		}
		return ext;
	}
}
