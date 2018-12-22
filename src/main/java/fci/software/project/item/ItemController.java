package fci.software.project.item;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import javax.persistence.*;
import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import fci.software.project.classes.UploadData;
import fci.software.project.question.*;
import fci.software.project.user.UserController;


@Controller
public class ItemController {
	static int counter = 1;
	
	@Autowired
	private ItemRepo itemRepo;
	@Autowired
	private QuestionRepo questionRepo;
	// Save the uploaded file to this folder
	// private static String UPLOADED_FOLDER = "D://Storageimages//";
	@Autowired
	private EntityManagerFactory emf=null;
	
	public EntityManager getEntityManager(){
		return emf.createEntityManager();
		
	}

	@RequestMapping(method = RequestMethod.GET, value = "/upload")
	public String uploadView(Model model)

	{
		UploadData uploadData = new UploadData();
		model.addAttribute("uploadData", uploadData);
		return "upload";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public String uploadItem(@ModelAttribute("file") MultipartFile file, Model model,
			@ModelAttribute("uploadData") UploadData uploadData) {

		if (file.isEmpty()) {
			model.addAttribute("failmessage", "Please select a file to upload");
			return "upload";
		}

		try {

			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			UserController temp = new UserController();
			Item item = new Item();
			Question q = new Question();
			item.setColor(uploadData.getColor());
			item.setType(uploadData.getType());
			item.setItemName(uploadData.getItemName());
			item.setImage(bytes);
			item.setUserId(temp.saveUserId);
			item.setItemId(String.valueOf(counter));
			q.setQuestion(uploadData.getQuestion());
			q.setCorrect(uploadData.getCorrect());
			q.setC1(uploadData.getC1());
			q.setC2(uploadData.getC2());
			q.setC3(uploadData.getC3());
			q.setC4(uploadData.getC4());
			q.setItemId(String.valueOf(counter));
			q.setQuestionId(String.valueOf(counter));
			counter++;
			itemRepo.save(item);
			questionRepo.save(q);
			/*
			 * Path path = Paths.get(UPLOADED_FOLDER +
			 * file.getOriginalFilename()); Files.write(path, bytes);
			 */
			model.addAttribute("correctmessage", "You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "upload";
	}

}
