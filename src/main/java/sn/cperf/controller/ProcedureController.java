package sn.cperf.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import sn.cperf.dao.ProcedureRepository;
import sn.cperf.model.Procedure;
import sn.cperf.service.StorageService;

@Controller
@RequestMapping("/Procedure")
public class ProcedureController {
	
	@Autowired ProcedureRepository procedureRepository;
	@Autowired StorageService storageService;
	@GetMapping("/")
	public String getListProceduresView() {
		return "procedures";
	}
	
	@GetMapping("/getProceduresJson")
	@ResponseBody
	public Page<Procedure> getProceduresJson(@RequestParam(name="name", defaultValue="") String procedureName,
			@RequestParam(name="page", defaultValue="0") int page, @RequestParam(name="size", defaultValue="7") int size){
		try {
			if(procedureName != null && !procedureName.equals(""))
				return procedureRepository.findByNameLikeIgnoreCaseOrderByIdDesc("%"+procedureName+"%", new PageRequest(page, size));
			else
				return procedureRepository.findByOrderByIdDesc(new PageRequest(page, size));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/Edit")
	public String getEditProcedureVeiw(@RequestParam(name="pid",defaultValue="0") Long procedureId, Model model) {
		Procedure procedure = new Procedure();
		try {
			if(procedureId != null && procedureId >0) {
				Optional<Procedure> opp = procedureRepository.findById(procedureId);
				if(opp.isPresent())
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
	public String editProcedure(@RequestParam(name="pid",defaultValue="0") Long procedureId,
			@RequestParam(name="filePath") MultipartFile file,@Valid @ModelAttribute("procedure") Procedure procedure, BindingResult bind,Model model) {
		try {
			boolean isUpdateOperation = false;
			String fileName="";
			if(procedure.getId() != null)
				isUpdateOperation = true;
			else
				procedure.setStoreAt(new Date());
			if(file != null) {
				try {
					fileName = storageService.storeFile(file, new String[] {"pdf"});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(fileName != null && !fileName.equals(""))
				procedure.setFilePath(fileName);
			if(procedureRepository.save(procedure)!=null) {
				String message = (isUpdateOperation) ? " Procedure modifié" : "Procedure enregistré";
				message = (fileName != null && !fileName.equals("")) ? message + " !" : message +", sans le fichier(assurez-vous de charger un fichier pdf de taille inférieur ou égale à 200MB) !";
				model.addAttribute("successMsg", message);
			}else {
				model.addAttribute("errorMsg", "Opération échouée, veuillez recommencer !");
			}
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Erreur de traitement de données, veuillez recommencer !");
			e.printStackTrace();
		}
		model.addAttribute("procedure", procedure);
		return "procedure";
	}

    
    @RequestMapping(value = "/Show/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> showProceduresPDf(@PathVariable(name = "id") Long procedureId) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=procedure.pdf");
    	try {
			Procedure p = procedureRepository.getOne(procedureId);
			InputStream is = new FileInputStream(storageService.getFilePathInUploadDir(p.getFilePath()).toFile());

			return ResponseEntity
			        .ok()
			        .headers(headers)
			        .contentType(MediaType.APPLICATION_PDF)
			        .body(new InputStreamResource(is));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    	System.out.println("Fichier introuvable");
    	return null;
    }
}
