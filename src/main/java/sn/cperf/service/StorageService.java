package sn.cperf.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	public String storeFile(MultipartFile file);
	public String storeAvatar(MultipartFile file);
	public Path getDefaultDir();
	public boolean checkExtensions(String filePathName, String[] extensions);
	public String normalizeTargetFileName(MultipartFile file);
	public boolean checkFileExists(String path);
	
}
