package sn.cperf.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import sn.cperf.dao.ParamRepository;
import sn.cperf.exception.StorageException;
import sn.cperf.model.Parametre;

@Service
public class StorageServiceImpl implements StorageService {

	@Autowired
	Environment env;
	@Autowired
	ParamRepository paramRepository;
	@Autowired
	CperfService cperfService;

	@Override
	public String storeFile(MultipartFile file,String extensions[]) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// check extensions
			if (!checkExtensions(fileName, extensions)) {
				throw new StorageException("Désolé! le fichier " + fileName
						+ " n'est prise en charge");
			}
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new StorageException(
						"Désolé! le nom du fchier contient une séquence de chemin invalide" + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			//Path targetLocation = this.getDefaultDir("/static/uploads/").resolve(normalizeTargetFileName(file));
			Path targetLocation = Paths.get(env.getProperty("spring.file.upload-dir")).resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation,StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException ex) {
			throw new StorageException("Impossible de stocker le fichier " + fileName + ". Veuillez réessayer!", ex);
		}
	}

	@Override
	public Path getDefaultDir(String path_name) {
		try {
			//spring.file.upload-dir
			return Paths.get(URI.create(getClass().getResource(path_name).toString())).toAbsolutePath().normalize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean checkExtensions(String filePathName, String[] extensions) {
		String fileExtension = null;
		try {
			System.out.println(filePathName);
			fileExtension = filePathName.substring(filePathName.lastIndexOf(".") + 1, filePathName.length());
			System.out.println(fileExtension);
		} catch (Exception e) {
			System.out.println("extension non chequée");
			e.printStackTrace();
		}
		if (fileExtension != null) {
			for (int i = 0; i < extensions.length; i++) {
				if (extensions[i].toLowerCase().equals(fileExtension.toLowerCase()))
					return true;
			}
		}
		return false;
	}

	@Override
	public String normalizeTargetFileName(MultipartFile file) {
		try {
			if (file != null) {
				//String addon_name = cperfService.getLoged() != null ? cperfService.getLoged().getFirstname() : "";
				// Date dt = new Date();
				// Calendar calander = Calendar.getInstance();
				// addon_name = addon_name + "_" + calander.DAY_OF_MONTH + "_" + calander.MONTH
				// + "_" + calander.YEAR + "_"
				// + calander.YEAR + "_" + file.getOriginalFilename();
				return (file.getOriginalFilename()).toLowerCase();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean checkFileExists(String path) {
		try {
			File f = new File(path);
			if (f.exists() && !f.isDirectory()) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String storeAvatar(MultipartFile file,String[] extensions) {// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// check extensions : new String[] { "jpg", "jpeg", "png", "gif", "svg","ico" }
			if (!checkExtensions(fileName, extensions)) {
				throw new StorageException("Désolé! le fichier " + fileName + " n'est pas prise en charge");
			}
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new StorageException(
						"Désolé! le nom de la photo contient une séquence de chemin invalide" + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = Paths.get(env.getProperty("spring.file.avatar-dir")).resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (IOException ex) {
			throw new StorageException("Impossible de stocker la photo " + fileName + ". Veuillez réessayer!", ex);
		}
	}

	@Override
	public boolean ckeckValidePath(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName != null && fileName.contains("..")) {
				throw new StorageException(
						"Désolé! le nom du fchier contient une séquence de chemin invalide" + fileName);
			}else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Path getFilePathInUploadDir(String fileName) {
		try {
			return Paths.get(env.getProperty("spring.file.upload-dir")).resolve(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Path getResolveFilePathWithEnDirConfig(String envConfigDirPropertyName, String fileName) {
		try {
			return Paths.get(env.getProperty(envConfigDirPropertyName)).resolve(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean createDirectoryByEnvironnementConfig(String envDirKey) {
		Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr--r--");
        FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);
		try {
			Files.createDirectory(Paths.get(env.getProperty(envDirKey)));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return false;
	}
}
