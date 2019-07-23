package sn.cperf.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import sn.cperf.model.DBFile;

public interface DBFileService {
	DBFile storeOrUpdateFile(MultipartFile file, Long DBfileId, boolean isUserDefaultAvatar);
	DBFile getDefaultUserAvatar();
	ResponseEntity<Resource> downloadImageFile(DBFile file);
	ResponseEntity<InputStreamResource> readStreamOnBrowser(DBFile file);
	boolean checkExtensions(String filename, String[] exts);
	DBFile find(Long id);
	void delete(DBFile dbFile);
	
}
