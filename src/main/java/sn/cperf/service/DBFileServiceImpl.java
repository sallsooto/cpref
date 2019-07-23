package sn.cperf.service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sn.cperf.dao.DBFileRepository;
import sn.cperf.exception.StorageException;
import sn.cperf.model.DBFile;
import sn.cperf.model.Task;

@Service
public class DBFileServiceImpl implements DBFileService {

	@Autowired
	DBFileRepository dbFileRepository;

	@Override
	public DBFile storeOrUpdateFile(MultipartFile file, Long dbFileId, boolean isUserDefaultAvatar) {
		try {
			System.err.println("j'arrive jusqu'à la");
			if (file != null) {
				// Check if the file's name contains invalid characters
				if (file.getOriginalFilename() != null && file.getOriginalFilename().contains("..")) {
					throw new StorageException("Désolé! le nom du fchier contient une séquence de chemin invalide"
							+ file.getOriginalFilename());
				} else {
					DBFile dbFile = null;
					if (dbFileId != null && dbFileId > 0) {
						try {
							dbFile = dbFileRepository.getOne(dbFileId);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					if (dbFile == null)
						dbFile = new DBFile();
					dbFile.setName(file.getOriginalFilename());
					dbFile.setType(file.getContentType());
					dbFile.setData(file.getBytes());
					dbFile.setDefaultUserAvatar(isUserDefaultAvatar);
					return dbFileRepository.save(dbFile);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<Resource> downloadImageFile(DBFile file) {
		if (file != null) {
			try {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getType()))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
						.body(new ByteArrayResource(file.getData()));
			} catch (Exception e) {
			}
		}
		return null;
	}

	@Override
	public DBFile getDefaultUserAvatar() {
		try {
			return dbFileRepository.findTop1ByDefaultUserAvatarIsTrueOrderByIdDesc();
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public ResponseEntity<InputStreamResource> readStreamOnBrowser(DBFile file) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=" + file.getName());
		try {
			InputStream is = new ByteArrayInputStream(file.getData());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType(file.getType()))
					.body(new InputStreamResource(is));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean checkExtensions(String filename, String[] exts) {
		String fileExtension = null;
		try {
			fileExtension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fileExtension != null) {
			for (int i = 0; i < exts.length; i++) {
				if (exts[i].toLowerCase().equals(fileExtension.toLowerCase()))
					return true;
			}
		}
		return false;
	}

	@Override
	public DBFile find(Long id) {
		try {return dbFileRepository.getOne(id);} catch (Exception e) {}
		return null;
	}

	@Override
	public void delete(DBFile dbFile) {
		try {dbFileRepository.delete(dbFile);} catch (Exception e) {}
	}

}
