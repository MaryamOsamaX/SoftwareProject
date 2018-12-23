package fci.software.project.item;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import fci.software.project.classes.SelectModel;
import fci.software.project.classes.Send;
import fci.software.project.classes.UploadData;
import fci.software.project.question.*;
import fci.software.project.user.User;
import fci.software.project.user.UserController;
import fci.software.project.user.UserRepo;
import fci.software.project.blockeditems.*;

@Controller
public class ItemController {
	static long counter = 0;
	String SaveCorrect = "";
	String SaveItemId = "";
	int v = 0;
	@Autowired
	private ItemRepo itemRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private BlockedRepo blockedRepo;
	@Autowired
	private QuestionRepo questionRepo;
	// Save the uploaded file to this folder
	// private static String UPLOADED_FOLDER = "D://Storageimages//";
	@Autowired
	private EntityManagerFactory emf = null;

	public EntityManager getEntityManager() {
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
			if (v == 0) {
				counter += itemRepo.count() + 1;
				v = 1;
			}

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

	@RequestMapping(method = RequestMethod.GET, value = "/search")
	public String searchView(Model model) {
		Item searchData = new Item();
		model.addAttribute("searchData", searchData);
		return "search";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/search")
	public String search(Model model, @ModelAttribute("searchData") Item searchData) throws Exception {
		int photocnt = 0;
		UserController temp = new UserController();
		/***************** `to get all the avail ***************/
		String query = "SELECT * FROM Item i WHERE";
		query += (" type=\'" + searchData.getType() + "\'");
		query += (" AND color = \'" + searchData.getColor() + "\' ; ");
		EntityManager em = getEntityManager();
		javax.persistence.Query q = em.createNativeQuery(query, Item.class);
		List<Item> a = q.getResultList();
		/**************** get the blocked *******************/
		String block = "SELECT * FROM blocked_items WHERE user_id=\'" + temp.saveUserId + "\';";
		q = em.createNativeQuery(block, BlockedItems.class);
		List<BlockedItems> b = q.getResultList();
		/****************** filter data ***************************/
		List<Item> filterA = new ArrayList<Item>();
		List<Send> filter = new ArrayList<Send>();

		int flag;
		for (int l = 0; l < a.size(); l++) {
			Send c = new Send();
			flag = 0;
			for (int j = 0; j < b.size(); j++) {
				if (a.get(l).getItemId().equals(b.get(j).getItemKey().itemId)) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				c.item = a.get(l);
				byte[] photo = a.get(l).getImage();
				File file = new File("");
				String path = file.getAbsolutePath();
				FileOutputStream outputStream = new FileOutputStream(
						path + "\\src\\main\\resources\\uploadedphotos\\photo" + photocnt + ".jpg");
				outputStream.write(photo);
				c.path = (path + "\\src\\main\\resources\\uploadedphotos\\photo" + photocnt + ".jpg");
				outputStream.close();
				photocnt++;
				filter.add(c);
			}
		}
		// model.addAttribute("showData", filterA);
		// ImageIcon filearray[]=new ImageIcon [a.size()];
		// List<String> paths = new ArrayList<String>();
		// byte[] photo = filterA.get(0).getImage();
		/*
		 * File file = new File(""); String path = file.getAbsolutePath();
		 */
		// System.out.println(path);
		/*
		 * FileOutputStream outputStream = new FileOutputStream( path +
		 * "\\src\\main\\resources\\uploadedphotos\\photo" + photocnt + ".jpg");
		 * outputStream.write(photo); paths.add(path +
		 * "\\src\\main\\resources\\uploadedphotos\\photo" + photocnt + ".jpg");
		 */ // temp.getImage();
		model.addAttribute("photoData", filter);
		// outputStream.close();
		// photocnt++;

		return "search";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/select")
	public String selectView(Model model, HttpServletRequest request) {
		SelectModel selectData = new SelectModel();
		String arr[] = new String[5];
		String itemId = request.getParameter("id");
		SaveItemId = itemId;
		Optional<Question> question = questionRepo.findById(itemId);
		if (question.isPresent()) {
			SaveCorrect = question.get().getCorrect();
			arr[0] = question.get().getCorrect();
			arr[1] = question.get().getC1();
			arr[2] = question.get().getC2();
			arr[3] = question.get().getC3();
			arr[4] = question.get().getC4();
			arr = Randomize(arr);
			model.addAttribute("f", question.get().getQuestion());
			model.addAttribute("x", arr[0]);
			model.addAttribute("y", arr[1]);
			model.addAttribute("z", arr[2]);
			model.addAttribute("w", arr[3]);
			model.addAttribute("u", arr[4]);
			model.addAttribute("selectData", selectData);

		}

		return "select";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/select")
	public String select(Model model, @ModelAttribute("selectData") SelectModel selectData) {
		String user = itemRepo.findById(SaveItemId).get().getUserId();

		if (selectData.getResult().equals(SaveCorrect)) {
			String phone = userRepo.findById(user).get().getPhoneNumber();
			model.addAttribute("correctmessage", "that's the uploader number call him :" + phone);
		} else {
			BlockedItems m = new BlockedItems();
			Key k = new Key();
			k.itemId = SaveItemId;
			k.userId = user;
			m.setItemKey(k);
			blockedRepo.save(m);
			model.addAttribute("failmessage", "Wrong answer you blocked from that item");

		}

		return "select";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/posts")
	public String postsView(Model model) throws Exception {
		int photocnt = 0;
		UserController x = new UserController();

		String query = "SELECT * FROM Item i WHERE user_id=\'" + x.saveUserId + "\' ;";
		EntityManager em = getEntityManager();
		javax.persistence.Query q = em.createNativeQuery(query, Item.class);
		List<Item> a = q.getResultList();
		List<Send> arr = new ArrayList<Send>();
		for (int l = 0; l < a.size(); l++) {
			Send c = new Send();

			c.item = a.get(l);
			byte[] photo = a.get(l).getImage();
			File file = new File("");
			String path = file.getAbsolutePath();
			FileOutputStream outputStream = new FileOutputStream(
					path + "\\src\\main\\resources\\uploadedphotos\\photo" + photocnt + ".jpg");
			outputStream.write(photo);
			c.path = (path + "\\src\\main\\resources\\uploadedphotos\\photo" + photocnt + ".jpg");
			outputStream.close();
			photocnt++;
			arr.add(c);

		}
		model.addAttribute("photoData", arr);

		return "posts";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/delete")
	public String delete(Model model, HttpServletRequest request) throws Exception {
		/********************* delete **********************/

		String itemId = request.getParameter("id");
		itemRepo.deleteById(itemId);
		model.addAttribute("message", "item deleted successfully!");
		/******************* posts again **********************/
		int photocnt = 0;
		UserController x = new UserController();

		String query = "SELECT * FROM Item i WHERE user_id=\'" + x.saveUserId + "\' ;";
		EntityManager em = getEntityManager();
		javax.persistence.Query q = em.createNativeQuery(query, Item.class);
		List<Item> a = q.getResultList();
		List<Send> arr = new ArrayList<Send>();
		for (int l = 0; l < a.size(); l++) {
			Send c = new Send();

			c.item = a.get(l);
			byte[] photo = a.get(l).getImage();
			File file = new File("");
			String path = file.getAbsolutePath();
			FileOutputStream outputStream = new FileOutputStream(
					path + "\\src\\main\\resources\\uploadedphotos\\photo" + photocnt + ".jpg");
			outputStream.write(photo);
			c.path = (path + "\\src\\main\\resources\\uploadedphotos\\photo" + photocnt + ".jpg");
			outputStream.close();
			photocnt++;
			arr.add(c);

		}
		model.addAttribute("photoData", arr);

		return "posts";
	}

	public String[] Randomize(String[] arr) {
		String[] randomizedArray = new String[arr.length];
		System.arraycopy(arr, 0, randomizedArray, 0, arr.length);
		Random rgen = new Random();

		for (int i = 0; i < randomizedArray.length; i++) {
			int randPos = rgen.nextInt(randomizedArray.length);
			String tmp = randomizedArray[i];
			randomizedArray[i] = randomizedArray[randPos];
			randomizedArray[randPos] = tmp;
		}

		return randomizedArray;
	}

}
