package sn.cperf.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sn.cperf.config.Helpers;
import sn.cperf.dao.DBFileRepository;
import sn.cperf.dao.ProcedureRepository;
import sn.cperf.model.DBFile;
import sn.cperf.model.Procedure;
import sn.cperf.service.DBFileService;
import sn.cperf.service.StorageService;

@Controller
@Secured(value= {"ROLE_admin","ROLE_procedure_explorer","ROLE_sadmin"})
@RequestMapping("/Procedure")
public class ProcedureController {

	@Autowired
	ProcedureRepository procedureRepository;
	@Autowired
	StorageService storageService;
	@Autowired
	DBFileService dbFileService;
	@Autowired DBFileRepository dBFileRepository;

	@GetMapping("/")
	public String getListProceduresView() {
		return "procedures";
	}

	@GetMapping("/getProceduresJson")
	@ResponseBody
	public Page<Procedure> getProceduresJson(@RequestParam(name = "name", defaultValue = "") String procedureName,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "7") int size) {
		try {
			if (procedureName != null && !procedureName.equals(""))
				return procedureRepository.findByNameLikeIgnoreCaseOrderByIdDesc("%" + procedureName + "%",
						new PageRequest(page, size));
			else
				return procedureRepository.findByOrderByIdDesc(new PageRequest(page, size));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/Edit")
	public String getEditProcedureVeiw(@RequestParam(name = "pid", defaultValue = "0") Long procedureId, Model model) {
		Procedure procedure = new Procedure();
		try {
			if (procedureId != null && procedureId > 0) {
				Optional<Procedure> opp = procedureRepository.findById(procedureId);
				if (opp.isPresent())
					procedure = opp.get();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("procedure", procedure);
		return "procedure";
	}

	@PostMapping("/Edit")
	public String editProcedure(@RequestParam(name = "pid", defaultValue = "0") Long procedureId,
			@RequestParam(name = "filePaths") List<MultipartFile> files,
			@Valid @ModelAttribute("procedure") Procedure procedure, BindingResult bind, Model model) {
		try {
			boolean isUpdateOperation = false;
			String fileName = "";
			if (procedure.getId() != null)
				isUpdateOperation = true;
			else
				procedure.setStoreAt(new Date());
			if (files != null && !files.isEmpty()) {
				List<DBFile> dbFiles = procedure.getFiles();
				for(MultipartFile file : files) {
					try {
						if (dbFileService.checkExtensions(file.getOriginalFilename(),Helpers.getAFileExtensions())) {
//							procedure.setDbFile(dbFileService.storeOrUpdateFile(file,
//									procedure.getDbFile() != null ? procedure.getDbFile().getId() : null, false));
//							
//							if (procedure.getDbFile() == null)
//								fileName = storageService.storeFile(file, new String[] { "pdf","doc","docx","ppt","pptx","xls","xlsx","text","txt","png","jpeg","jif","ico","jpg","svg" });
							DBFile dbFile = dbFileService.storeOrUpdateFile(file, null, false);
							if(dbFile != null)
								dbFiles.add(dbFile);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(!dbFiles.isEmpty()) {
					procedure.setFiles(dbFiles);
					fileName = "fichiers enregistrées";
				}
			}
//			if (fileName != null && !fileName.equals(""))
//				procedure.setFilePath(fileName);
			if (procedureRepository.save(procedure) != null) {
				String message = (isUpdateOperation) ? " Procedure modifié" : "Procedure enregistré";
				message = ((fileName != null && !fileName.equals("")) || procedure.getDbFile() != null) ? message + " !"
						: message
								+ ", sans le(s) fichier(s), assurez-vous de charger des fichiers "+Helpers.getHtmlInputFileExentions() +" taille chacun inférieur ou égale à 200MB !";
				model.addAttribute("successMsg", message);
			} else {
				model.addAttribute("errorMsg", "Opération échouée, veuillez recommencer !");
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Erreur de traitement de données, veuillez recommencer !");
			e.printStackTrace();
		}
		model.addAttribute("procedure", procedure);
		return "procedure";
	}

	@RequestMapping(value = "/Show/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	@ResponseBody
	public ResponseEntity<InputStreamResource> showProceduresPDf(@PathVariable(name = "id") Long procedureId) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=procedure.pdf");
		try {
			Procedure p = procedureRepository.getOne(procedureId);
			if(p.getDbFile() != null)
				return  dbFileService.readStreamOnBrowser(p.getDbFile());
			// show file on disk if exist
			InputStream is = new FileInputStream(storageService.getFilePathInUploadDir(p.getFilePath()).toFile());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(is));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		System.out.println("Fichier introuvable");
		return null;
	}	
	
	@RequestMapping(value = "/Show/File/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> showProceduresFile(@PathVariable(name = "id") Long fileId) {
		try {
			DBFile file = dBFileRepository.getOne(fileId);
			if(file != null)
				return  dbFileService.readStreamOnBrowser(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		System.out.println("Fichier introuvable");
		return null;
	}

	@GetMapping("/{id}/del")
	public String delProcedure(@PathVariable("id") Long id) {
		try {
			Procedure p = procedureRepository.getOne(id);
			DBFile dbFile = p.getDbFile();
			p.setDbFile(null);
			List<DBFile> files = p.getFiles();
			p.setFiles(null);
			procedureRepository.delete(p);
			if(files != null) {
				for(DBFile f : files)
					dbFileService.delete(f);
			}
			if(dbFile != null)
				dbFileService.delete(dbFile);
		} catch (Exception e) {
		}
		return "redirect:/Procedure/";
	}
	@GetMapping("/deleteFile/")
	@ResponseBody
	public void deleteFile(@RequestParam("fid") Long fileId, @RequestParam("procid") Long procedureId) {
		try {
			DBFile file = dBFileRepository.getOne(fileId);
			Procedure p = procedureRepository.getOne(procedureId);
			if(p != null && file != null) {
				List<DBFile> files = new ArrayList<DBFile>();
				for(DBFile f : p.getFiles()) {
					if(f.getId() != file.getId())
						files.add(f);
				}
				p.setFiles(files);
				procedureRepository.save(p);
				dBFileRepository.delete(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
