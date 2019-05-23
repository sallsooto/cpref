package sn.cperf.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	public String storeFile(MultipartFile file,String[] extensions);
	public String storeAvatar(MultipartFile file,String[] extensions);
	public Path getDefaultDir(String path_name);
	public boolean checkExtensions(String filePathName, String[] extensions);
	public String normalizeTargetFileName(MultipartFile file);
	public boolean checkFileExists(String path);
	public boolean ckeckValidePath(MultipartFile file);
	public Path getFilePathInUploadDir(String fileName);
	public Path getResolveFilePathWithEnDirConfig(String envConfigDirPropertyName,String fileName);
	public boolean createDirectoryByEnvironnementConfig(String envDirKey);
}
